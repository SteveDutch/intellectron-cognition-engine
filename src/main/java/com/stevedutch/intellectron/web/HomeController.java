package com.stevedutch.intellectron.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.service.AuthorService;
import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.TextService;
import com.stevedutch.intellectron.service.ZettelService;

@Controller
public class HomeController {
	
	@Autowired
	private ZettelService zettelService;
	@Autowired
	private TextService textService;
	@Autowired
	private TagService tagService;
	@Autowired
	private AuthorService authorService;
	@Autowired
	private SearchService searchService;
	
	@GetMapping("/")
	public String showHomePage(Model model) {
		
		Long zettelNumber = zettelService.countZettel();
		Long textsNumber = textService.countText();
		Long tagsNumber = tagService.countTags();
		Long authorsNumber = authorService.countAuthors();
		
		List<Zettel> zettels = searchService.findLast10Zettel();
		zettelService.reduceTekstStringListElements(zettels, 23);
		zettelService.reduceNoteStringListElements(zettels, 23);
		zettelService.reduceTopicStringListElements(zettels, 23);
		
		List<Zettel> randomZettels = searchService.findRandomZettel(10);
		zettelService.reduceTekstStringListElements(randomZettels, 23);
		zettelService.reduceNoteStringListElements(randomZettels, 23);
		zettelService.reduceTopicStringListElements(randomZettels, 23);
		
		List<Zettel> fourRandomZettels = searchService.findRandomZettel(4);
		zettelService.reduceTekstStringListElements(fourRandomZettels, 23);
		zettelService.reduceNoteStringListElements(fourRandomZettels, 23);
		zettelService.reduceTopicStringListElements(fourRandomZettels, 23);
		
		List<Tekst> fourRandomTexts = searchService.findRandomText(5);
		textService.reduceTextStringListElements(fourRandomTexts, 23);
		textService.reduceTitleStringListElements(fourRandomTexts, 23);
		
		model.addAttribute("zettelNumber", zettelNumber);
		model.addAttribute("textsNumber", textsNumber);
		model.addAttribute("tagsNumber", tagsNumber);
		model.addAttribute("authorsNumber", authorsNumber);
		model.addAttribute("zettels", zettels);
		model.addAttribute("randomZettels", randomZettels);
		model.addAttribute("fourRandomZettels", fourRandomZettels);
		model.addAttribute("fourRandomTexts", fourRandomTexts);
		
		return "/welcome";
	}
	// TODO vermutlich nicht regelkonform (check: Ja) & abfangen von Typos
	@GetMapping("/welcome")
	public String showAlsoHomePage() {
		return "/welcome";
	}
	
}
