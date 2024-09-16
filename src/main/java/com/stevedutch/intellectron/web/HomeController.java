package com.stevedutch.intellectron.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.stevedutch.intellectron.service.AuthorService;
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
	
	@GetMapping("/")
	public String showHomePage(Model model) {
		
		Long zettelNumber = zettelService.countZettel();
		Long textsNumber = textService.countText();
		Long tagsNumber = tagService.countTags();
		Long authorsNumber = authorService.countAuthors();
		
		model.addAttribute("zettelNumber", zettelNumber);
		model.addAttribute("textsNumber", textsNumber);
		model.addAttribute("tagsNumber", tagsNumber);
		model.addAttribute("authorsNumber", authorsNumber);
		return "/welcome";
	}
	// TODO vermutlich nicht regelkonform (check: Ja) & abfangen von Typos
	@GetMapping("/welcome")
	public String showAlsoHomePage() {
		return "/welcome";
	}
	
}
