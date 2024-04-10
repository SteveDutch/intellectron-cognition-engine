package com.stevedutch.intellectron.service;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TextRepository;

@Service
public class TextService {
	
	private static final Logger LOG = LoggerFactory.getLogger(TextService.class);
	@Autowired
	private TextRepository textRepo;
	@Autowired
	private ZettelService zettelService;

	public Tekst saveText(Tekst tekst) {
		return textRepo.save(tekst);
	}
		
	public Tekst saveTextwithZettel (Tekst tekst, Zettel zettel) {
		tekst.getZettels().add(zettel);
        
        return textRepo.save(tekst);
    
	}

	public  Tekst saveTextWithAuthor(Tekst tekst, Author author) {
		// XXX wäre korrekt, wenn ich weitere Autoren hinzufügen wollte
		// for now I'm mocking that just one Author is valid
		// tekst.getAssociatedAuthors().add(author); Daher aber;
		tekst.setOneAssociatedAuthors(author);
		tekst = textRepo.save(tekst);
		return textRepo.save(tekst);
	}

	public Tekst updateTekst(Long zettelId, Tekst tekst) {
		
		Zettel zettel = zettelService.findZettelById(zettelId);
//		Tekst oldTekst = zettelService.findZettelById(zettelId).getTekst();
		// XXX Tekst vom front end kommt nur mit Text, daher anhand dessen den Tekst finden, oder -falls nicht existent -
		// oder als neues Text mit dem gegebenen Text einricfhten  --> Vermeiden von Doubletten in der Datenbank & Objekt
		Tekst updatedTekst = findByText(tekst.getText());
		LOG.info("\n -->TekstService.updateTekst, Tekst " + updatedTekst);
		if (updatedTekst == null) {
			updatedTekst = new Tekst();
            updatedTekst.setText(tekst.getText());
            updatedTekst.setTextDate(tekst.getTextDate());
            updatedTekst.setSource(tekst.getSource());
            textRepo.save(updatedTekst);
		}
		textRepo.save(updatedTekst);
		saveTextwithZettel(updatedTekst, zettelService.findZettelById(zettelId));
		zettel.setTekst(updatedTekst);
		LOG.info("\n -->TekstService.updateTekst, Tekst nachm Bearbeiten \n" +   "--->" + updatedTekst +"\n" + updatedTekst.getZettels());
		return updatedTekst;
		
	}

	public Tekst findById(Long textId) {
		return textRepo.findById(textId).orElseThrow(() -> new NoSuchElementException("Tekst mit dieser ID inexistent"));
		
	}
	
	public Tekst findByText(String text) {
		/// TODO null check 
		return textRepo.findByText(text);
	}
	
	

}
