package com.stevedutch.intellectron.web;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.service.ZettelService;

@Controller
public class InputController {
	
	private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

	@Autowired
	private ZettelService zettelService;
	String unknownFamily = "Unbekannt";
	String unknownName = "Ignotus";

	@GetMapping("/input")
	public String showInputMask(ModelMap model) {
		model.put("author", new Author());
		model.put("tekst", new Tekst());
		model.put("zettel", new Zettel());
		model.put("note", new Note());
		model.put("tag", new Tag());
		model.put("tags", new ArrayList<Tag>());
		model.put("reference", new Reference());
		// falls ich mal die Verweise anzeigen will ... :)
//		List<Reference> references = refService.findAll();
//		model.put("references", references);
		List<Zettel> zettels = zettelService.findLast10Zettel();
		List<Zettel> randomZettels = zettelService.find10RandomZettel();
		LOG.info("\n Wieviel zufällige Zettel? --> " + randomZettels.size() + "\n " + "\n randomZettels = \n " + randomZettels);

		model.put("zettels", zettels);
		model.put("randomZettels", randomZettels);
		System.out.println("Anzahl der Zettel = " + zettels.stream().count() + " \n zettels= " + zettels);
		return "/input";
	}

	@PostMapping("/input")
	public String postNewZettel(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		System.out.println("JSON = \n " + json);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		ZettelDtoRecord zettelDto = objectMapper.readValue(json, ZettelDtoRecord.class);

		//  check if names are empty TODO:right place?
		if (zettelDto.author().getAuthorFamilyName() == null || zettelDto.author().getAuthorFamilyName() == "" 
				|| zettelDto.author().getAuthorFamilyName().trim().isBlank())  {
			zettelDto.author().setAuthorFamilyName(unknownFamily);
		}
		if (zettelDto.author().getAuthorFirstName() == null || zettelDto.author().getAuthorFirstName() == ""
				|| zettelDto.author().getAuthorFirstName().trim().isBlank())  {
			zettelDto.author().setAuthorFirstName(unknownName);
		}
		System.out.println("ZettelDtoRecord =  \n" + zettelDto + "\n");
//		ZettelDtoRecord zettelDtoRecord = new ZettelDtoRecord( zettel,  tekst,  note,  author, tags, reference);
		zettelService.createZettel(zettelDto);
		return "redirect:/input";
	}

}
