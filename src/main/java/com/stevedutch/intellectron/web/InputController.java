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
import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.TextManipulationService;
import com.stevedutch.intellectron.service.ZettelService;

@Controller
public class InputController {

	private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

	@Autowired
	private ZettelService zettelService;
	@Autowired
	private SearchService searchService;
	@Autowired
	private TextManipulationService textManipulationService;

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
		List<Zettel> zettels = searchService.findLast10Zettel();
		List<Zettel> randomZettels = searchService.findRandomZettel(10);
		// TODO rename
		textManipulationService.reduceTekstStringListElements(zettels, 220);
		textManipulationService.reduceTekstStringListElements(randomZettels, 220);
		// TODO rename
		textManipulationService.reduceNoteStringListElements(zettels, 220);
		textManipulationService.reduceNoteStringListElements(randomZettels, 220);

		model.put("zettels", zettels);
		model.put("randomZettels", randomZettels);

		return "/input";
	}

	@PostMapping("/input")
	public String postNewZettel(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		System.out.println("JSON = \n " + json);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		ZettelDtoRecord zettelDto = objectMapper.readValue(json, ZettelDtoRecord.class);

		// check if names are empty TODO:right place? NO zu authorService
		if (zettelDto.author().getAuthorFamilyName() == null || zettelDto.author().getAuthorFamilyName() == ""
				|| zettelDto.author().getAuthorFamilyName().trim().isBlank()) {
			zettelDto.author().setAuthorFamilyName(unknownFamily);
		}
		if (zettelDto.author().getAuthorFirstName() == null || zettelDto.author().getAuthorFirstName() == ""
				|| zettelDto.author().getAuthorFirstName().trim().isBlank()) {
			zettelDto.author().setAuthorFirstName(unknownName);
		}
		System.out.println("\n ZettelDtoRecord =  \n" + zettelDto + "\n");
		ZettelDtoRecord newZettel = zettelService.createZettel(zettelDto);
		Long zettelId = newZettel.zettel().getZettelId();

		return "redirect:/zettel/" + zettelId;

	}
	// // XXX for historical & maybe learning reasons
	// @GetMapping("/400")
	// public String showErrorPage(RedirectAttributes attributes, Model model) {
	// // This method is not strictly necessary if you're only using flash
	// attributes, as Thymeleaf can access them directly.
	// // However, it's useful if you need to add more attributes or perform
	// additional logic.
	// return "400";
	// }

}
