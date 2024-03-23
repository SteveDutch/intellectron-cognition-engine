package com.stevedutch.intellectron.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.service.NoteService;
import com.stevedutch.intellectron.service.TextService;
import com.stevedutch.intellectron.service.ZettelService;

@Controller
public class ZettelController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ZettelController.class);
	@Autowired
	private ZettelService zettelService;
	@Autowired
	private NoteService noteService;
	@Autowired
	private TextService textService;

	@GetMapping("/zettel/{zettelId}")
	public String showZettel(ModelMap model, @PathVariable Long zettelId) {
//		model.put("zettelDto", new ZettelDtoRecord(zettelDto.zettel(),zettelDto.tekst(), zettelDto.note(), zettelDto.author(), 
//				zettelDto.tags(), zettelDto.references()));
		
//		model.put("author", new Author());
//		model.put("tekst", new Tekst());
//		model.put("zettel", new Zettel());
//		model.put("note", new Note());
//		model.put("tag", new Tag());
//		model.put("tags", new ArrayList<Tag>());
//
//		model.put("reference", new Reference());
//		model.put("references", new ArrayList<Reference>());
		
		Zettel zettel = zettelService.findZettelById(zettelId);
		String formattedText = zettel.getTekst().getText();
		zettel.getTekst().setText(formattedText.replace("\n", "<br>"));
        model.put("zettel", zettel);
        model.put("note", zettel.getNote());
        model.put("tekst", zettel.getTekst());
        model.put("author", zettel.getTekst().getAssociatedAuthors());
        LOG.info("\n im showZettel, Authors= " + zettel.getTekst().getAssociatedAuthors());
        
        model.put("tags", zettel.getTags());
        model.put("references", zettel.getReferences());
        return "/zettel";
    }
	
	@PostMapping("/zettel/{zettelId}")
    public String updateOneZettel(@PathVariable Long zettelId, @RequestBody String json) throws JsonMappingException, JsonProcessingException {
		
		LOG.info("\n im Controller updateOneZettel, JSON = " + json);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		ZettelDtoRecord changes = objectMapper.readValue(json, ZettelDtoRecord.class);
//		Zettel updatedZettel = zettelService.findZettelById(zettelId);
		
//		zettelService.updateOneZettelbyId(zettelId, changes);
		LOG.info(" --> zettelController.updateOneZettel, nach json to zettelDTO, vorm saven: --> zettelDto = \n " + changes);
		zettelService.updateOnlyZettel(zettelId, changes);
		noteService.updateNote(zettelId, changes.note());
		textService.updateTekst(zettelId, changes.tekst());

//		LOG.info(" ---> zettelController.updateOneZettel, nachm saven: --> zettelDto = \n  " + changes);
		return "redirect:/zettel/";
	}
	
	@PostMapping("/zettel/{zettelId}/delete")
	public String deleteOneZettel(@PathVariable Long zettelId) {
		LOG.info("\n im deleteZettel = " + zettelId);
		zettelService.deleteOneZettelbyId(zettelId);
		
		return "redirect:/welcome";
	}
	

}