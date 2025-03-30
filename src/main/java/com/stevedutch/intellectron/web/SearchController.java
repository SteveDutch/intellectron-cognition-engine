package com.stevedutch.intellectron.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.TextManipulationService;

@Controller
public class SearchController {
	
	private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	private SearchService searchService;
	@Autowired
	private TextManipulationService textManipulationService;
	
	private static final int TITLE_STRING_LIMIT = 42;
	private static final int RANDOM_ZETTEL_NUMBER = 10;
	private static final int TRUNCATED_TEXTS_LIMIT = 555;
	private static final int ONE_TRUNCATED_TEXT_LIMIT = 1111;
	

	@GetMapping("/search")
	public String showSearchPage(ModelMap model) {
		
		List<Zettel> zettels = searchService.findLast10Zettel();
		textManipulationService.reduceZettelStrings(zettels, TITLE_STRING_LIMIT);

		List<Zettel> randomZettels = searchService.findRandomZettel(RANDOM_ZETTEL_NUMBER);
		textManipulationService.reduceZettelStrings(randomZettels, TITLE_STRING_LIMIT);
		
		
		model.put("zettels", zettels);
		model.put("randomZettels", randomZettels);
		return "/search";
	}
	
	@GetMapping("/search/tag/")
	public String searchZettelByTagFragment(@RequestParam("tagFragment") String tagFragment, ModelMap model) {
		LOG.info("Got & Searching for tag fragment: {}", tagFragment);
		List<Tag> wantedTags = searchService.findTagByTagFragment(tagFragment);
		LOG.info("\n  got " + wantedTags.size());

		model.addAttribute("wantedTags", wantedTags);
		model.addAttribute("tagFragment", tagFragment);
		return "/tags";
	}
	
	@GetMapping("/search/topic/")
	public String searchBytopicFragment(@RequestParam("topicFragment") String topicFragment, ModelMap model) {
		LOG.info("\n got topicFrgament = " + topicFragment);
		List<Zettel> zettels = searchService.findZettelByTopicFragment(topicFragment);
		if (zettels == null) {
			LOG.info("\n NO ZETTEL FOUND");
		} else {
			LOG.info("\n  got " + zettels.size()+ " Zettels: \n" + zettels);
		}
		model.addAttribute("topicFragment",topicFragment);
		model.addAttribute("zettels", zettels);
		return "/results";
	}
	
	@GetMapping("/search/note/")
	public String searchByNoteFragment(@RequestParam String noteFragment, ModelMap model) {
		LOG.info("\n got noteFragment = " + noteFragment);
		List<Zettel> zettels = searchService.findZettelByNoteFragment(noteFragment);		
		if (zettels == null) {
			LOG.info("\n NO ZETTEL FOUND");
		} else {
			LOG.info("\n  got " + zettels.size()+ " Zettels: \n" + zettels);
		}
		model.addAttribute("noteFragment",noteFragment);
        model.addAttribute("zettels", zettels);
		return "/results";
	}
	
	@GetMapping("/search/text/")
	public String searchZettelByTextFragment(@RequestParam String textFragment, ModelMap model) {
		LOG.info("\n got textFragment = " + textFragment);
		List<Zettel> zettels = searchService.findZettelByTextFragment(textFragment);
		if (zettels == null) {
			LOG.info("\n NO ZETTEL FOUND");
		} else {
			LOG.info("\n  got " + zettels.size()+ " Zettels: \n" + zettels);
		}
		model.addAttribute("textFragment",textFragment);
		model.addAttribute("zettels", zettels);
		return "/results";
	}

	@GetMapping("/search/text4tekst/")
	public String searchTextByTextFragment(@RequestParam String textFragment, ModelMap model) {
		LOG.info("\n got textFragment = " + textFragment);
		List<Tekst> texts = searchService.findTruncatedTekstByTextFragment(textFragment, TRUNCATED_TEXTS_LIMIT);
		LOG.info("\n  found " + texts.size()+ " Texts: \n" + texts);
		model.addAttribute("textFragment",textFragment);
		model.addAttribute("texts", texts);
		return "/texts";
		}
	

	@GetMapping("/search/text/truncatedText/{textId}")
	@ResponseBody
	public ResponseEntity<Map<String, String>> showTruncatedText(@PathVariable Long textId) {
		LOG.info("Got textId = {}", textId);  // Better logging practice using placeholder
		Tekst tekst = searchService.findTruncatedTekstById(textId, ONE_TRUNCATED_TEXT_LIMIT);
		if (tekst == null) {
			LOG.warn("No text found for id: {}", textId);
			 return ResponseEntity.notFound().build();
		}
		LOG.info("\n  found " + tekst);
	    Map<String, String> response = new HashMap<>();
	    response.put("title", tekst.getTitle());
	    response.put("content", tekst.getText());
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/search/text/{textId}")
	public String showfullText(ModelMap model, @PathVariable Long textId) {
		LOG.info("Got textId = {}", textId);  // Better logging practice using placeholder
		Tekst tekst = searchService.findById(textId);
		if (tekst == null) {
			LOG.warn("No text found for id: {}", textId);
			return "/";
		}
		String cleanedText = textManipulationService.brToNewLine(tekst.getText());
		tekst.setText(cleanedText);
		LOG.info("\n  found " + tekst.getTextId() + tekst.getTitle());
	    model.addAttribute("title", tekst.getTitle());
	    model.addAttribute("tekst", tekst);
	    return "/text-view";
	}
	
	@GetMapping("/search/author/")
	public String searchAuthor(@RequestParam String lastName, ModelMap model) {
		LOG.info("\n got author name = " + lastName);
		List<Author> authors = searchService.findAuthorByNameWithTruncatedTexts(lastName, TRUNCATED_TEXTS_LIMIT);
		LOG.info("\n  found " + authors.size()+ " Authors: \n" + authors);

		model.addAttribute("authors", authors);
		return "/authors";
		
	}
	
	/**
	 * Handles the GET request to display details of a specific tag and its associated Zettels.
	 * This endpoint retrieves a tag by its ID and finds all Zettels that are tagged with it.
	 * The results are added to the model for rendering in the tags view.
	 *
	 * @param tagId the unique identifier of the tag to display
	 * @param model the Spring ModelMap to which the tag and associated Zettels are added
	 * @return the name of the view template to render ("/tags")
	 * @throws TagNotFoundException if no tag is found with the given ID
	 * @see com.stevedutch.intellectron.domain.Tag
	 * @see com.stevedutch.intellectron.domain.Zettel
	 */
	@GetMapping("/search/tag/{tagId}")
	public String showTagDetails(@PathVariable Long tagId, ModelMap model) {
		LOG.info("Showing details for tag ID: {}", tagId);
		Tag selectedTag = searchService.findTagById(tagId);
		
		// Get the associated Zettels
		List<Zettel> zettels = searchService.findZettelByTag(selectedTag.getTagText());
		
		model.addAttribute("selectedTag", selectedTag);
		model.addAttribute("zettels", zettels);
		return "/tags";
	}
	
}
