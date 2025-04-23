package com.stevedutch.intellectron.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.repository.TextRepository;
import java.util.AbstractMap.SimpleImmutableEntry;
import com.stevedutch.intellectron.service.feedback.TextSavingFeedbackHolder;

@Service
public class TextFingerprintService {

    private static final Logger LOG = LoggerFactory.getLogger(TextFingerprintService.class);
    private static final double SIMILARITY_THRESHOLD = 0.8; // 80% similarity threshold
    
    private final TextRepository textRepository;
    private final TextSavingFeedbackHolder feedbackHolder;

    // Inject value from application.properties/yml
    @Value("${similarity.check.max-recent-texts:100}") // Default to 100 if property not set
    private int maxRecentTextsToCheck; 

    @Autowired
    public TextFingerprintService(TextRepository textRepository, TextSavingFeedbackHolder feedbackHolder) {
        this.textRepository = textRepository;
        this.feedbackHolder = feedbackHolder;
    }
    
    /**
     * Generates a SHA-256 hash of the text content
     * @param text The text to hash
     * @return The hexadecimal representation of the hash
     */
    public String generateTextHash(String text) {
        if (text == null) {
             return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Error generating SHA-256 hash: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate text hash", e); 
        }
    }
    
    /**
     * Converts a byte array to a hexadecimal string
     * @param bytes The byte array to convert
     * @return The hexadecimal string representation
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    /**
     * Checks if an identical or similar text already exists in the database.
     * Prioritizes checks: 1. Exact Match, 2. Hash Match, 3. Similarity vs Recent Texts.
     * @param newText The text to check
     * @return An Optional containing the existing Tekst if found, otherwise Optional.empty().
     */
    @Transactional(readOnly = true)
    public Optional<Tekst> findExactOrSimilarText(String newText) {
        // Clear previous message at the start of a check
        feedbackHolder.clear();
        String messageForLogAndFeedback; 

        if (newText == null || newText.isBlank()) {
            messageForLogAndFeedback = "No text provided.";
            feedbackHolder.setMessage(messageForLogAndFeedback);
            LOG.info("Feedback Set: {}", messageForLogAndFeedback);
            return Optional.empty();
        }
        
        String normalizedText = newText.strip();
        
        // 1. Check for exact text match
        Tekst exactMatch = textRepository.findByText(normalizedText);
        if (exactMatch != null) {
            messageForLogAndFeedback = "Text matched an identical existing entry.";
            feedbackHolder.setMessage(messageForLogAndFeedback);
            LOG.info("Feedback Set: {} (ID: {})", messageForLogAndFeedback, exactMatch.getTextId());
            return Optional.of(exactMatch);
        }
        
        // 2. Generate hash and check for hash match
        String newTextHash = generateTextHash(normalizedText); 
        Optional<Tekst> hashMatch = textRepository.findByHash(newTextHash);
        if (hashMatch.isPresent()) {
            messageForLogAndFeedback = "Text matched an identical existing entry (hash).";
            feedbackHolder.setMessage(messageForLogAndFeedback);
            LOG.info("Feedback Set: {} (ID: {})", messageForLogAndFeedback, hashMatch.get().getTextId());
            return hashMatch;
        }
        
        // 3. Check for similarity against N most recent texts
        if (maxRecentTextsToCheck > 0) {
            LOG.info("No exact or hash match found. Checking similarity against the last {} texts.", maxRecentTextsToCheck);
            
            // Create a Pageable request for the first page with size N
            Pageable pageRequest = PageRequest.of(0, maxRecentTextsToCheck); 
            
            // Fetch only the recent texts instead of findAll()
            List<Tekst> recentTexts = textRepository.findByOrderByTextIdDesc(pageRequest);
            
            for (Tekst existingText : recentTexts) {
                 // Skip comparison if it somehow matched hash/exact earlier (shouldn't happen)
                 if (existingText.getHash() != null && existingText.getHash().equals(newTextHash)) continue;
                 if (existingText.getText() == null || existingText.getText().isBlank()) continue;
                
                 double similarity = calculateJaccardSimilarity(normalizedText, existingText.getText());
                 if (similarity >= SIMILARITY_THRESHOLD) {
                     LOG.info("Found SIMILAR text via Jaccard (within recent {}) with ID: {} (similarity: {})", 
                             maxRecentTextsToCheck, existingText.getTextId(), similarity);
                     messageForLogAndFeedback = String.format("Text matched a similar entry found within recent texts (ID: %d, Similarity: %.2f).", existingText.getTextId(), similarity);
                     feedbackHolder.setMessage(messageForLogAndFeedback);
                     LOG.info("Feedback Set: {}", messageForLogAndFeedback);
                     return Optional.of(existingText);
                 }
             }
             LOG.info("No similar text found within the last {}.", maxRecentTextsToCheck);
        } else {
             LOG.info("Similarity check against recent texts is disabled (max-recent-texts <= 0).");
        }

        // If we reach here, no exact, hash, or similar match was found (or similarity check was skipped)
        LOG.info("No exact, hash, or similar match found by fingerprint service.");
        LOG.info("No match found by fingerprint service, no feedback set here.");
        return Optional.empty(); 
    }
    
    /**
     * Calculates Jaccard similarity coefficient between two text strings
     * (intersection size / union size)
     * @param text1 First text
     * @param text2 Second text
     * @return Similarity coefficient between 0.0 and 1.0
     */
    private double calculateJaccardSimilarity(String text1, String text2) {
        Set<String> words1 = tokenizeText(text1);
        Set<String> words2 = tokenizeText(text2);
        
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);
        
        if (union.isEmpty()) {
             return (words1.isEmpty() && words2.isEmpty()) ? 1.0 : 0.0; 
        }
        
        return (double) intersection.size() / union.size();
    }
    
    /**
     * Tokenizes a text string into a set of words
     * @param text The text to tokenize
     * @return A set of words from the text
     */
    private Set<String> tokenizeText(String text) {
        if (text == null || text.isBlank()) {
            return new HashSet<>();
        }
        
        String normalized = text.toLowerCase()
                .replaceAll("\\p{Punct}", " ")
                .replaceAll("\\s+", " ")
                .trim();
        
        return Arrays.stream(normalized.split("\\s+"))
                .filter(word -> word.length() > 2)
                .collect(Collectors.toSet());
    }
    
    /**
     * Alternative similarity method using Levenshtein distance
     * Useful for cases where the texts might be very similar with minor edits
     * @param text1 First text
     * @param text2 Second text
     * @return Normalized similarity score between 0.0 and 1.0
     */
    public double calculateLevenshteinSimilarity(String text1, String text2) {
        int[][] dp = new int[text1.length() + 1][text2.length() + 1];
        
        for (int i = 0; i <= text1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= text2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= text1.length(); i++) {
            for (int j = 1; j <= text2.length(); j++) {
                int cost = (text1.charAt(i - 1) == text2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        
        int maxLength = Math.max(text1.length(), text2.length());
        if (maxLength == 0) {
            return 1.0; // Both strings are empty
        }
        
        // Normalize the distance to get a similarity score between 0 and 1
        return 1.0 - (double) dp[text1.length()][text2.length()] / maxLength;
    }
    
    /**
     * Creates shingles (n-grams) from text for more effective comparison
     * @param text The text to process
     * @param n The size of each shingle
     * @return A list of n-grams
     */
    public List<String> createShingles(String text, int n) {
        if (text == null || text.length() < n) {
            return new ArrayList<>();
        }
        
        List<String> shingles = new ArrayList<>();
        for (int i = 0; i <= text.length() - n; i++) {
            shingles.add(text.substring(i, i + n));
        }
        return shingles;
    }
} 