package com.stevedutch.intellectron.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.service.AuthorService;
import com.stevedutch.intellectron.service.ReferenceService;
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
    private ReferenceService refService;
	
	
	
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
		model.put("zettels" , zettels);
		System.out.println("Anzahl der Zettel = " + zettels.stream().count() + " \n zettels= "+  zettels );
		return "/input";
	}
	
	@PostMapping("/input")
	public String postNewZettel(Zettel zettel, Tekst tekst, Note note, ArrayList<Tag> tags, Tag tag, Author author, 
			Reference reference) {
		// TODO: check if names are empty
//		System.out.println("\n Start of  InputController.postNewZettel()-->  Zettel: \n" + zettel);
		System.out.println("\n Start of  InputController.postNewZettel()-->  note/Kommentar: \n" + note);
//		System.out.println("\n Start of  InputController.postNewZettel()-->   Text : \n" + tekst);
		System.out.println("\n Start of  InputController.postNewZettel()-->  Tags  :\n " + tags);
		System.out.println("\n Start of  InputController.postNewZettel()-->  Tas  :\n " + tag);
//		System.out.println("\n Start of  InputController.postNewZettel()-->   Autor : \n" + author);
		ZettelDtoRecord zettelDtoRecord = new ZettelDtoRecord( zettel,  tekst,  note,  author, tags, reference);
		zettelService.createZettel(zettelDtoRecord);
		System.out.println("\n InputController.postNewZettel after createZettel()  -->  \n" + zettelDtoRecord);
		System.out.println("\n InputController.postNewZettel after createZettel()  --> nur für mich: NOTE (text & id) \n" 
				+ zettelDtoRecord.note().getNoteText() + zettelDtoRecord.note().getZettelId());
//		System.out.println("\n InputController.postNewZettel after createZettel() \n --> nur für mich: NOTE \n" +note);
		System.out.println("\n InputController.postNewZettel after createZettel()  \n --> nur für mich: TAGs \n;" + tags);
//		System.out.println("\n InputController.postNewZettel after createZettel()  \n --> nur für mich: TEKST \n" + tekst);
		System.out.println("\n InputController.postNewZettel after createZettel() \n  --> nur für mich: ZETTEL \n" + zettel);
//		System.out.println("\n InputController.postNewZettel after createZettel()  \n --> nur für mich: AUTHOR \n" + author);
		return "redirect:/input";
	}


}
