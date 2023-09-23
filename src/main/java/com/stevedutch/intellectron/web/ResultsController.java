package com.stevedutch.intellectron.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResultsController {
	
	@GetMapping("/results")
	public String showSearchResults () {
		return "/results";
	}
	
}