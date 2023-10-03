package com.stevedutch.intellectron.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResultsController {
	
	@GetMapping("/results")
	public String showSearchResults () {
		return "/results";
	}
	
}