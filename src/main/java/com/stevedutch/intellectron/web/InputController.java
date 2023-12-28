package com.stevedutch.intellectron.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.service.AuthorService;
import com.stevedutch.intellectron.service.NoteService;
import com.stevedutch.intellectron.service.TextService;
import com.stevedutch.intellectron.service.ZettelService;

@Controller
public class InputController {
	
	@Autowired
	private AuthorService authorService;
	
	@Autowired
	private TextService textService;
	@Autowired
	private ZettelService zettelService;
	@Autowired
    private NoteService noteService;
	
	@GetMapping("/input")
	public String showInputMask(ModelMap model) {
		model.put("author", new Author());
		model.put("tekst", new Tekst());
		model.put("zettel", new Zettel());
		model.put("note", new Note());
		model.put("zetteltag", new Tag());
		return "/input";
	}
	
	@PostMapping("/input")
	public String postNewZettel(Zettel zettel, Tekst tekst, Note note, Tag tag, Author author) {
		// TODO: check if names are empty
//		System.out.println("\n Start of  InputController.postNewZettel()-->  Zettel: \n" + zettel);
		System.out.println("\n Start of  InputController.postNewZettel()-->  note/Kommentar: \n" + note);
		System.out.println("\n Start of  InputController.postNewZettel()-->   Text : \n" + tekst);
		System.out.println("\n Start of  InputController.postNewZettel()-->  Tag  :\n " + tag);
		System.out.println("\n Start of  InputController.postNewZettel()-->   Autor : \n" + author);
		ZettelDtoRecord zettelDtoRecord = new ZettelDtoRecord( zettel,  tekst,  note,  author);
//		System.out.println("\n Start of  addZettel()-->   ZettelDtoRecord : \n" + zettelDtoRecord);
		zettelService.createZettel(zettelDtoRecord);
		System.out.println("\n InputController.postNewZettel after createZettel()  -->  \n" + zettelService.createZettel(zettelDtoRecord));
		
		return "redirect:/input";
	}


}
