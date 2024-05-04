package com.stevedutch.intellectron.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.stevedutch.intellectron.domain.Zettel;

@Controller
public class ResultsController {
	
	@GetMapping("/results")
	public String showSearchResults (ModelMap model) {
		
//		List<Zettel> zettels = Zettel.findZettels();
//		model.addAttribute("zettels", zettels);
		model.addAttribute("model", model);
		return "/results";
	}
	
}