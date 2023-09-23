package com.stevedutch.intellectron.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InputController {
	
	@GetMapping("/input")
	public String showInputMask() {
		return "/input";
	}

}
