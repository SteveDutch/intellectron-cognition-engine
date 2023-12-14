package com.stevedutch.intellectron.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
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
		return "/input";
	}
// TODO EXPERIMENT: PUT THIS TO aUTHORcONTROLLER, STILL WORKING THEN?
// TODO hnge name of function
	@PostMapping("/input")
	public String addZettel(Zettel zettel, Author author, Tekst tekst, Note note) {
		// TODO: check if names are empty
		System.out.println("\n Start of  addZettel()-->  Zettel: \n" + zettel);
		System.out.println("\n Start of  addZettel()-->  note/Kommentar: \n" + note);
		System.out.println("\n Start of  addZettel()-->   Text : \n" + tekst);
		System.out.println("\n Start of  addZettel()-->   Autor : \n" + author);
		zettelService.saveZettel(zettel);
		System.out.println("\n after addZettel()  -->  \n" +zettel);
//		authorService.saveAuthor(author);
//		textService.saveText(tekst);
//		noteService.save(note);
//		System.out.println(note);
		
		return "/input";
	}


}
