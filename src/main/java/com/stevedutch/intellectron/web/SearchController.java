package com.stevedutch.intellectron.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.ZettelService;

@Controller
public class SearchController {
	
	private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	private TagService tagService;
	@Autowired
	private ZettelService zettelService;
	@Autowired
	private SearchService searchService;

	@GetMapping("/search")
	public String showSearchPage() {
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
		List<Tekst> texts = searchService.findTekstByTextFragment(textFragment);
		if (texts == null) {
			LOG.info("\n NO TEKST FOUND");
		} else {
			LOG.info("\n  found " + texts.size()+ " Texts: \n" + texts);
		}	
		model.addAttribute("textFragment",textFragment);
		model.addAttribute("texts", texts);
		return "/results";
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
