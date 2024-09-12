package com.stevedutch.intellectron.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.service.SearchService;

@Controller
public class ResultsController {
	
	@Autowired
	Zettel zettel;
	@Autowired
	SearchService searchService;
	
	// TODO vermutlich nur für mustache, unfortunately i forgot it & didn'tmake a note :| 
	@GetMapping("/results")
	public String showSearchResults (ModelMap model) {
		
//		List<Zettel> zettels = Zettel.findZettels();
//		model.addAttribute("zettels", zettels);
		model.addAttribute("model", model);
		return "/results";
	}
	
	@GetMapping("/modal-content/{id}")
	@ResponseBody
	public Map<String, Object> getModalContent(@PathVariable Long id) {
 
	    // Add the content to the model
		zettel = searchService.findZettelById(id);
		Map<String, Object> response = new HashMap<>();
		response.put("modalContent", zettel.getTekst().getText());
		response.put("zettel", zettel.getZettelId());

	    return response;
	}
}