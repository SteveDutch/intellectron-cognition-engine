package com.stevedutch.intellectron.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.ZettelSearch;
import com.stevedutch.intellectron.service.ZettelService;

@Controller
public class SearchController {
	
	private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	private TagService tagService;
	@Autowired
	private ZettelService zettelService;
	@Autowired
	private ZettelSearch zettelSearch;

	@GetMapping("/search")
	public String showSearchPage() {
		return "/search";
	}
	
	@GetMapping("/search/tag/{tagText}")
	public String searchByTag(@PathVariable String tagText) {
		LOG.info("\n  got tagText = " + tagText);
		tagService.findTagByText(tagText);
		List<Zettel> zettels = zettelService.findZettelByTag(tagText);
		LOG.info("\n  got zettels = " + zettels);
		return "/search";
	}
	
	@GetMapping("/search/topic/{topicFragment}")
	public String searchBytopicFragment(@PathVariable String topicFragment) {
		LOG.info("\n got topicFrgament = " + topicFragment);
		List<Zettel> zettels = zettelService.findZettelByTopicFragment(topicFragment);
		if (zettels == null) {
			LOG.info("\n NO ZETTEL FOUND");
		} else {
			LOG.info("\n  got " + zettels.size()+ " Zettels: \n" + zettels);
		}
		return "/search";
	}
	
	@GetMapping("/search/note/{noteFragment}")
	public String searchByNoteFragment(@PathVariable String noteFragment) {
		LOG.info("\n got noteFragment = " + noteFragment);
		List<Zettel> zettels = zettelSearch.findZettelByNoteFragment(noteFragment);		
		if (zettels == null) {
			LOG.info("\n NO ZETTEL FOUND");
		} else {
			LOG.info("\n  got " + zettels.size()+ " Zettels: \n" + zettels);
		}
		return "/search";
	}
	
	@GetMapping("/search/text/zettel/{textFragment}")
	public String searchZettelByTextFragment(@PathVariable String textFragment) {
		LOG.info("\n got textFragment = " + textFragment);
		List<Zettel> zettels = zettelSearch.findZettelByTextFragment(textFragment);
		if (zettels == null) {
			LOG.info("\n NO ZETTEL FOUND");
		} else {
			LOG.info("\n  got " + zettels.size()+ " Zettels: \n" + zettels);
		}
		return "/search";
	}
	
	@GetMapping("/search/text/{textFragment}")
	public String searchTextByTextFragment(@PathVariable String textFragment) {
		LOG.info("\n got textFragment = " + textFragment);
		List<Tekst> texts = zettelSearch.findTekstByTextFragment(textFragment);
		if (texts == null) {
			LOG.info("\n NO TEKST FOUND");
		} else {
			LOG.info("\n  found " + texts.size()+ " Texts: \n" + texts);
		}
		return "/search";
		}
	
}
