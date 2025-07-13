package com.stevedutch.intellectron.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.exception.SearchTermNotFoundException;
import com.stevedutch.intellectron.exception.TagNotFoundException;
import com.stevedutch.intellectron.repository.AuthorRepository;
import com.stevedutch.intellectron.repository.ReferenceRepository;
import com.stevedutch.intellectron.repository.TagRepository;
import com.stevedutch.intellectron.repository.TextRepository;
import com.stevedutch.intellectron.repository.ZettelRepository;
import com.stevedutch.intellectron.web.ZettelController;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SearchService {

	private static final Logger LOG = LoggerFactory.getLogger(ZettelController.class);
	private static final String WHITESPACE_REGEX = "\\\\s+|\\p{Zs}+";
	@Autowired
	private ZettelRepository zettelRepo;
	@Autowired
	private TextRepository textRepo;
	// XXX check this: repo-Zugriff außerhalb der betreffenden Entity-Serviceklasse?
	@Autowired
	private AuthorRepository authorRepo;
	@Autowired
	private ReferenceRepository refRepo;
	@Autowired
	private TagRepository tagRepo;

	public List<Zettel> findZettelByNoteFragment(String noteFragment) {
		validateSearchString(noteFragment);
		List<Zettel> result = zettelRepo.findZettelByNoteFragment(noteFragment);
		if (result.isEmpty()) {
			throw new SearchTermNotFoundException("No Zettel found with note: " + noteFragment);
		}
		return result;
	}

	public List<Zettel> findZettelByTextFragment(String textFragment) {
		validateSearchString(textFragment);
		List<Zettel> result = zettelRepo.findZettelByTextFragment(textFragment);
		if (result.isEmpty()) {
			throw new SearchTermNotFoundException("No Zettel found with text: " + textFragment);
		}
		return result;
	}
	
	public Zettel findOneZettelByNote(String noteText) {
		validateSearchString(noteText);
		Zettel result = zettelRepo.findOneZettelByNote(noteText);
		return result;
	}

	// XXX is this method ever used? NO - ausser im Test
	public List<Zettel> findAllZettelWithTopic() {
		return zettelRepo.findAllZettelWithTopic();
	}

	public Zettel findZettelById(Long zettelId) {
		return zettelRepo.findById(zettelId)
				.orElseThrow(() -> new EntityNotFoundException("Zettel not found with id " + zettelId));
	}

	/**
	 * searches for Zettel by the given fragment of the topic
	 * 
	 * @param topicFragment the fragment of the topic
	 * @return List of Zettel could include null!
	 */
	public List<Zettel> findZettelByTopicFragment(String topicFragment) {
	
		validateSearchString(topicFragment);
		List<Zettel> result = zettelRepo.findZettelByTopicFragment(topicFragment);
		if (result.isEmpty()) {
			throw new SearchTermNotFoundException("No Zettel found with topic: " + topicFragment);
		}
		return result;
	}

	public List<Zettel> findZettelByTag(String tagText) {
		Tag searchTag = findTagByText(tagText);
		return zettelRepo.findZettelByTags(searchTag);
	}

	/**
	 * Takes a String, and then uses the last element of the string as authors
	 * family name to find authors with similar names.
	 * 
	 * @param name String
	 * @param maxLength an int which determines the truncated length of the associated texts
	 * @return List<Author> with truncated texts
	 * @throws SearchTermNotFoundException if no author is found with the given name
	 */
	public List<Author> findAuthorByNameWithTruncatedTexts(String name, int maxLength) {
		validateSearchString(name);
		String[] nameParts = name.split("\\s+");
		String lastName = nameParts[nameParts.length - 1];
		LOG.info("\n Now searching for author family name: " + lastName);
		List<Author> result = findAuthorByLastNameLike(lastName);
		if (result.isEmpty()) {
            throw new SearchTermNotFoundException("No Author found with name: " + lastName);
		}
		result.forEach(author -> {
			author.getTexts().forEach(text -> {
				if (text.getText().length() > maxLength) {
                    text.setText(text.getText().substring(0, maxLength) + " . . . ");
                }
			});
		});
		return result;
	}

	/**
	 * searches for authors with last name containing the search term.
	 * 
	 * @param lastName
	 * @return List<Author>
	 */
	public List<Author> findAuthorByLastNameLike(String lastName) {
		List<Author> result = authorRepo.findByAuthorFamilyNameLike(lastName);
		return result;
	}
	/**
	 * searches text by id
	 * @param textId 
	 * @return
	 * @throws SearchTermNotFoundException if no tekst is found with the given id
	 */
	public Tekst findById(Long textId) {
		return textRepo.findById(textId)
				.orElseThrow(() -> new SearchTermNotFoundException("Tekst mit dieser ID inexistent " +"( " + textId + " )"));
	
	}
	// TODO scheint so, als würde mit dem ganzen String gesucht. Was bei großen Werten? Hash oder so nutzen?
	public Tekst findByText(String text) {
		/// TODO null check
		
		return textRepo.findByText(text);
	}

	/**
	 * searches for  Teksts by the given fragment, and truncates them to the given length
	 * 
	 * @param textFragment   the search term
	 * @param maxLength the maximum length of the returned text  
	 * @return List of Teksts 
	 * @throws SearchTermNotFoundException if no tekst is found with the search term
	 */
	public List<Tekst> findTruncatedTekstByTextFragment(String textFragment, int maxLength) {
	
		validateSearchString(textFragment);
		List<Tekst> result = textRepo.findTruncatedTekstByTextFragment(textFragment,  maxLength);
		if (result.isEmpty()) {
            throw new SearchTermNotFoundException("No Tekst found with text: " + textFragment);
		}
            return result;
	}
	/**
	 * searches for a Tekst by the Iand truncates it to the given length
	 * @param textId
	 * @param maxLength
	 * @return Tekst
	 * @throws SearchTermNotFoundException if no tekst is found with the given id
	 */
	public Tekst findTruncatedTekstById(Long textId, int maxLength) {
		Tekst resultTekst = textRepo.findTruncatedTextbyId(textId, maxLength);
		if (resultTekst == null) {
			throw new SearchTermNotFoundException("No Tekst found with id: " + textId);
		}
		return resultTekst;
	}

	// XXX brauche ich irgendwann eine genaue Tagsuche?
	public Tag findTagByText(String tagText) {
		return tagRepo.findByTagText(tagText)
				.orElseThrow(() -> new SearchTermNotFoundException("No Tag found with text: " + tagText));
	}

	/**
	 * throws a SearchTermNotFoundException if no Tag is found with the search term
	 * @param tagFragment - a String for the search term
	 * @return - List<Tag> of all tags that contain the search term
	 */
	public List<Tag> findTagByTagFragment(String tagFragment) {
		validateSearchString(tagFragment);
		List<Tag> result = tagRepo.findByTagFragment(tagFragment);
		if (result.isEmpty()) {
			throw new SearchTermNotFoundException("No Tag found with text: " + tagFragment);
		}
		return result;
	}

	/**
	 * Validates a search term, if it is null or empty it throws a
	 * SearchTermNotFoundException.
	 * 
	 * @param searchTerm
	 */
	public void validateSearchString(String searchTerm) {
		if (searchTerm == null || searchTerm.isEmpty() 
				|| searchTerm.matches(WHITESPACE_REGEX)) {
			LOG.info("\n NO SearchTerm  ");
			throw new SearchTermNotFoundException("no search term provided");
		}
	}

	public List<Zettel> findLast10Zettel() {
		return zettelRepo.findLast10Zettel();
	}
	
	//TODO rename tenRandom & Zettel & and not woking, 
	//stuck in loop when there are less than 10 Zettel
	/**
	 *finds x random Zettel
	 *
	 * @param x - number of Zettel to be found
	 * @return List<Zettel> of x random Zettel
	 */
	public List<Zettel> findRandomZettel(int x) {
		List<Zettel> tenRandom = new ArrayList<>();
		while (tenRandom.size() < x) {
			Zettel randomZettel = zettelRepo.findOneRandomZettel();
			if (randomZettel != null && !tenRandom.contains(randomZettel)) {
				tenRandom.add(randomZettel);
			}
		}
		return tenRandom;
	}
	
	public List<Tekst> findRandomText(int x) {
	    Set<Tekst> uniqueTexts = new HashSet<>();
	    int maxAttempts = x * 3;  
	    int attempts = 0;

	    while (uniqueTexts.size() < x && attempts < maxAttempts) {
	        Tekst randomTekst = textRepo.findOneRandomTekst();
	        if (randomTekst != null) {
	            uniqueTexts.add(randomTekst);
	        }
	        attempts++;
	    }

	    return new ArrayList<>(uniqueTexts);
	}

	public List<Tag> findRandomTag(int x) {
		Set<Tag> uniqueTags = new HashSet<>();
		int maxAttempts = x * 3;
		int attempts = 0;
		
		while (uniqueTags.size() < x && attempts < maxAttempts) {
			Tag randomTag = tagRepo.findOneRandomTag();
			if (randomTag != null) {
				uniqueTags.add(randomTag);
				LOG.info("\n SearchService.findRandomTag() --> Random Tag: " + randomTag.getId() + " / " + randomTag.getTagText());
			}
			
			attempts++;
		}
		return new ArrayList<>(uniqueTags);
	}

	public List<Author> findRandomAuthor(int x) {
		Set<Author> uniqueAuthors = new HashSet<>();
		int maxAttempts = x * 3;
		int attempts = 0;
		
		while (uniqueAuthors.size() < x && attempts < maxAttempts) {
			Author randomAuthor = authorRepo.findOneRandomAuthor();
			if (randomAuthor != null) {
				uniqueAuthors.add(randomAuthor);
				LOG.info("\n SearchService.findRandomAuthor() --> Random Author: " + randomAuthor.getAuthorId() + " / " + randomAuthor.getAuthorFamilyName());
			}
			attempts++;
		}
		return new ArrayList<>(uniqueAuthors);
	}

	/**
	 * Retrieves a Tag entity by its ID from the repository.
	 *
	 * @param tagId the unique identifier of the tag to retrieve
	 * @return the Tag entity if found
	 * @throws TagNotFoundException if no tag is found with the given ID
	 * @see com.stevedutch.intellectron.exception.TagNotFoundException
	 */
	public Tag findTagById(Long tagId) {
		return tagRepo.findById(tagId)
				.orElseThrow(() -> new TagNotFoundException("Tag not found with id " + tagId));
	}


	// XXX is this method ever used? NOPE
//	public List<Reference> findAll() {
//		return refRepo.findAll();
//	}

	public List<Zettel> findRelatedZettels(Long zettelId) {
		// TODO: Implement advanced relationship finding
		// For now, return empty list until repositories are extended
		return new ArrayList<>();
		
		// Future implementation:
		// List<Zettel> directLinks = refRepo.findDirectLinks(zettelId);
		// List<Zettel> similarTagged = zettelRepo.findBySharedTags(zettelId);
		// List<Zettel> similarTopics = zettelRepo.findSimilarTopics(zettelId);
		// return combineAndSortByRelevance(directLinks, similarTagged, similarTopics);
	}

}
