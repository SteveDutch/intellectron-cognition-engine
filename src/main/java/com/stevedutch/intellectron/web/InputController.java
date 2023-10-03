package com.stevedutch.intellectron.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.service.AuthorService;

@Controller
public class InputController {
	
	@Autowired
	private AuthorService authorService;

	@GetMapping("/input")
	public String showInputMask(ModelMap model) {
		model.put("author", new Author());
		return "/input";
	}

	@PostMapping("/input")
	public String addAuthorName(Author author) {
		// TODO: check if names are empty
		authorService.saveAuthor(author);
		return "/input";
	}


}
