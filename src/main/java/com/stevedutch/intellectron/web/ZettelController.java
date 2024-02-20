//package com.stevedutch.intellectron.web;
//
//import java.util.ArrayList;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import com.stevedutch.intellectron.domain.Author;
//import com.stevedutch.intellectron.domain.Note;
//import com.stevedutch.intellectron.domain.Reference;
//import com.stevedutch.intellectron.domain.Tag;
//import com.stevedutch.intellectron.domain.Tekst;
//import com.stevedutch.intellectron.domain.Zettel;
//import com.stevedutch.intellectron.record.ZettelDtoRecord;
//import com.stevedutch.intellectron.service.ZettelService;
//
//@Controller
//public class ZettelController {
//	
//	@Autowired
//	private ZettelService zettelService;
////	@Autowired
////	private ZettelDtoRecord zettelDto;
//	
//	@GetMapping("/zettel")
//	public String showZettel(ModelMap model) {
////		model.put("zettelDto", new ZettelDtoRecord(zettelDto.zettel(),zettelDto.tekst(), zettelDto.note(), zettelDto.author(), 
////				zettelDto.tags(), zettelDto.references()));
//		model.put("author", new Author());
//		model.put("tekst", new Tekst());
//		model.put("zettel", new Zettel());
//		model.put("note", new Note());
//		model.put("tag", new Tag());
//		model.put("tags", new ArrayList<Tag>());
//
//		model.put("reference", new Reference());
//		model.put("references", new ArrayList<Reference>());
//        return "zettel";
//    }
//
//}