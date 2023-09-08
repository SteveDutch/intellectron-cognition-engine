package com.stevedutch.intellectron.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping("/")
	public String showHomePage() {
		return "/welcome";
	}
	// TODO vermutlich nicht regelkonform (check: Ja) & abfangen von Typos
	@GetMapping("/welcome")
	public String showAlsoHomePage() {
		return "/welcome";
	}
	
}
