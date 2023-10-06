package com.stevedutch.intellectron.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.service.AuthorService;
import com.stevedutch.intellectron.service.TextService;

@Controller
public class InputController {
	
	@Autowired
	private AuthorService authorService;
	
	@Autowired
	private TextService textService;


	@GetMapping("/input")
	public String showInputMask(ModelMap model) {
		model.put("author", new Author());
		model.put("tekst", new Tekst());
		return "/input";
	}

	@PostMapping("/input")
	public String addAuthorName(Author author, Tekst tekst) {
		// TODO: check if names are empty
		authorService.saveAuthor(author);
		textService.saveText(tekst);
		return "/input";
	}


}
