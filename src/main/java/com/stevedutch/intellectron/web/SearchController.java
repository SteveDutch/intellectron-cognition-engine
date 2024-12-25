package com.stevedutch.intellectron.web;

import java.util.List;

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
	
	private static final int TITLE_STRING_LIMIT = 23;
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

		LOG.info("\n  got tagText = " + tagFragment);

		List<Tag> wantedTags = searchService.findTagByTagFragment(tagFragment);
		
//		List<Zettel> zettels = zettelService.findZettelByTag(wantedTag.getTagText()); // bei Ähnlichkeitssuche anhand eines Suchterms kann eine Liste zurückgebeen werden
//		if (zettels == null) {
//			LOG.info("\n NO ZETTEL FOUND"); // derzeit gehandelt über SearchTermNotFoundException
//		} else {
//			LOG.info("\n  got " + zettels.size()+ " Zettels: \n" + zettels);
//		}
		LOG.info("\n  got " + wantedTags.size()+ " Tags: \n" + wantedTags.iterator().next().getTagText());
		model.addAttribute("wantedTags",wantedTags);
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
		// TODO tekst not found exception window o.ä.
		if (texts == null) {
			LOG.info("\n NO TEKST FOUND");
		} else {
			LOG.info("\n  found " + texts.size()+ " Texts: \n" + texts);
		}	
		model.addAttribute("textFragment",textFragment);
		model.addAttribute("texts", texts);
		return "/texts";
		}
	

@GetMapping("/search/text/truncatedText/{textId}")
	public String showTruncatedText(ModelMap model, @PathVariable Long textId) {
	LOG.info("Got textId = {}", textId);  // Better logging practice using placeholder
		Tekst tekst = searchService.findTruncatedTekstById(textId, ONE_TRUNCATED_TEXT_LIMIT);
		// XXX TODO tekst not found exception window o.ä
		if (tekst == null) {
			LOG.info("No text found for id: {}", textId);
		} else {
			LOG.info("\n  found " + tekst);
		}
		model.addAttribute("tekst", tekst);
		  return "/texts";  
	}
	
	@GetMapping("/search/author/")
	public String searchAuthor(@RequestParam String lastName, ModelMap model) {
		LOG.info("\n got author name = " + lastName);
		List<Author> authors = searchService.findAuthorByName(lastName);
		if (authors == null) {
			LOG.info("\n NO Author FOUND");
		} else {
			LOG.info("\n  found " + authors.size()+ " Authors: \n" + authors);
		}

		model.addAttribute("authors", authors);
		return "/authors";
		
	}
	
}
