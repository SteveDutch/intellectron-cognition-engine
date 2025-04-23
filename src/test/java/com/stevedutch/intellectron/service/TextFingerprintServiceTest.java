package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils; // For setting @Value field

import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.repository.TextRepository;
import com.stevedutch.intellectron.service.feedback.TextSavingFeedbackHolder;

@ExtendWith(MockitoExtension.class)
class TextFingerprintServiceTest {

    @Mock
    private TextRepository textRepository;

    @Mock
    private TextSavingFeedbackHolder feedbackHolder;

    // Use @Spy and @InjectMocks if you need to call real methods on the service
    // For this, @InjectMocks is sufficient if we mock all dependencies fully
    @InjectMocks
    private TextFingerprintService textFingerprintService;

    private Tekst existingTextExact;
    private Tekst existingTextHashMatch;
    private Tekst existingTextSimilar;
    private Tekst existingTextNotSimilar;

    private final String newTextContent = "This is the new text content.";
    private final String newTextHash = "a1b2c3d4e5f6"; // Dummy hash for newTextContent
    private final String similarTextContent = "This is the new text content, really.";
    private final String veryDifferentTextContent = "Completely unrelated information.";

    @BeforeEach
    void setUp() {
        // Initialize Tekst objects used in tests
        existingTextExact = new Tekst("This is the new text content.");
        existingTextExact.setTextId(1L);
        existingTextExact.setHash("a1b2c3d4e5f6"); // Same hash

        existingTextHashMatch = new Tekst("Slightly different content but same hash somehow"); // Simulate rare hash collision or pre-update state
        existingTextHashMatch.setTextId(2L);
        existingTextHashMatch.setHash("a1b2c3d4e5f6"); // Same hash

        existingTextSimilar = new Tekst(similarTextContent);
        existingTextSimilar.setTextId(3L);
        existingTextSimilar.setHash("f6e5d4c3b2a1"); // Different hash

        existingTextNotSimilar = new Tekst(veryDifferentTextContent);
        existingTextNotSimilar.setTextId(4L);
        existingTextNotSimilar.setHash("z9y8x7w6v5u4"); // Different hash

        // Mock the hash generation to return a predictable hash for the new text
        // Note: Direct mocking of private methods is hard. Instead, we can override generateTextHash
        // if needed or ensure our mocks cover the scenarios without needing the exact hash value internally.
        // For simplicity, let's assume generateTextHash works and focus on repository interactions.
        // We'll spy on generateTextHash later if specific hash logic testing is crucial.
    }

    // Helper to set the @Value field 'maxRecentTextsToCheck'
    private void setMaxRecentTextsToCheck(int value) {
        ReflectionTestUtils.setField(textFingerprintService, "maxRecentTextsToCheck", value);
    }

    @Test
    @DisplayName("Find Exact: Null or Blank Input")
    void findExactOrSimilarText_whenInputNullOrBlank_returnsEmpty() {
        setMaxRecentTextsToCheck(100); // Doesn't matter for this test

        Optional<Tekst> resultNull = textFingerprintService.findExactOrSimilarText(null);
        assertEquals(Optional.empty(), resultNull);

        Optional<Tekst> resultBlank = textFingerprintService.findExactOrSimilarText("   ");
        assertEquals(Optional.empty(), resultBlank);
    }

    @Test
    @DisplayName("Find Exact: Exact Text Match Found")
    void findExactOrSimilarText_whenExactTextMatch_returnsExisting() {
        setMaxRecentTextsToCheck(100);
        when(textRepository.findByText(newTextContent)).thenReturn(existingTextExact);

        Optional<Tekst> result = textFingerprintService.findExactOrSimilarText(newTextContent);

        assertTrue(result.isPresent());
        assertEquals(existingTextExact, result.get());
        verify(textRepository, never()).findByHash(anyString()); // Hash check skipped
        verify(textRepository, never()).findByOrderByTextIdDesc(any(Pageable.class)); // Similarity check skipped
    }

    @Test
    @DisplayName("Find Exact: Hash Match Found (No Exact Text Match)")
    void findExactOrSimilarText_whenHashMatch_returnsExisting() {
        setMaxRecentTextsToCheck(100);
        when(textRepository.findByText(newTextContent)).thenReturn(null); // No exact text match
        // Assume generateTextHash(newTextContent) returns newTextHash
        // We need to ensure the spy/mock setup allows generateTextHash to be called or mock its behavior if private.
        // For now, let's assume it generates the hash correctly and mock the repo call.
        when(textRepository.findByHash(anyString())).thenReturn(Optional.of(existingTextHashMatch)); // Hash match found

        Optional<Tekst> result = textFingerprintService.findExactOrSimilarText(newTextContent);

        assertTrue(result.isPresent());
        assertEquals(existingTextHashMatch, result.get());
        verify(textRepository, times(1)).findByText(newTextContent);
        verify(textRepository, times(1)).findByHash(anyString()); // Hash check performed
        verify(textRepository, never()).findByOrderByTextIdDesc(any(Pageable.class)); // Similarity check skipped
    }
    
    @Test
    @DisplayName("Find Similar: Found Within Recent Texts")
    void findExactOrSimilarText_whenSimilarFoundInRecent_returnsSimilar() {
        setMaxRecentTextsToCheck(50); // Enable similarity check
        when(textRepository.findByText(newTextContent)).thenReturn(null);
        when(textRepository.findByHash(anyString())).thenReturn(Optional.empty());
        
        // Mock recent texts: one similar, one not
        List<Tekst> recentTexts = List.of(existingTextSimilar, existingTextNotSimilar);
        Pageable expectedPageable = PageRequest.of(0, 50);
        when(textRepository.findByOrderByTextIdDesc(expectedPageable)).thenReturn(recentTexts);

        Optional<Tekst> result = textFingerprintService.findExactOrSimilarText(newTextContent);

        assertTrue(result.isPresent());
        assertEquals(existingTextSimilar, result.get());
        verify(textRepository, times(1)).findByText(newTextContent);
        verify(textRepository, times(1)).findByHash(anyString());
        verify(textRepository, times(1)).findByOrderByTextIdDesc(expectedPageable); // Similarity check performed
    }

    @Test
    @DisplayName("Find Similar: None Found Within Recent Texts")
    void findExactOrSimilarText_whenNoSimilarFoundInRecent_returnsEmpty() {
         setMaxRecentTextsToCheck(50); // Enable similarity check
        when(textRepository.findByText(newTextContent)).thenReturn(null);
        when(textRepository.findByHash(anyString())).thenReturn(Optional.empty());
        
        // Mock recent texts: none are similar enough
        List<Tekst> recentTexts = List.of(existingTextNotSimilar); // Only contains a very different text
        Pageable expectedPageable = PageRequest.of(0, 50);
        when(textRepository.findByOrderByTextIdDesc(expectedPageable)).thenReturn(recentTexts);

        Optional<Tekst> result = textFingerprintService.findExactOrSimilarText(newTextContent);

        assertFalse(result.isPresent());
        verify(textRepository, times(1)).findByText(newTextContent);
        verify(textRepository, times(1)).findByHash(anyString());
        verify(textRepository, times(1)).findByOrderByTextIdDesc(expectedPageable); 
    }
    
     @Test
    @DisplayName("Find Similar: Check Disabled")
    void findExactOrSimilarText_whenSimilarityCheckDisabled_returnsEmpty() {
        setMaxRecentTextsToCheck(0); // Disable similarity check
        when(textRepository.findByText(newTextContent)).thenReturn(null);
        when(textRepository.findByHash(anyString())).thenReturn(Optional.empty());
        
        Optional<Tekst> result = textFingerprintService.findExactOrSimilarText(newTextContent);

        assertFalse(result.isPresent());
        verify(textRepository, times(1)).findByText(newTextContent);
        verify(textRepository, times(1)).findByHash(anyString());
        // Crucially, verify the similarity check was NOT performed
        verify(textRepository, never()).findByOrderByTextIdDesc(any(Pageable.class)); 
    }

    // TODO: Add tests for edge cases in Jaccard similarity if needed (e.g., empty token sets)
} 