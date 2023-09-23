package com.stevedutch.intellectron.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EntryController {
	
	@GetMapping("/entry")
	public String showEntry() {
		return ("/entry");
	}
	

}
