package com.stevedutch.intellectron.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.stevedutch.intellectron.service.ZettelService;

@Controller
public class ZettelController {
	
	@Autowired
	private ZettelService zettelService;
	
//	@PostMapping("/input")
//	public String getZettel(@RequestParam Zettel zettel) {
//		zettelService.saveZettel(zettel);
//		return "/input";
		

//}
}