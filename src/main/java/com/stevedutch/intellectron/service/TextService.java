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
		tekst.getAssociatedAuthors().add(author);
		tekst = textRepo.save(tekst);
		return textRepo.save(tekst);
	}

	public void updateTekst(Long zettelId, Tekst tekst) {
		
		Tekst actualTekst = zettelService.findZettelById(zettelId).getTekst();
		Tekst updatedTekst = textRepo.findById(actualTekst.getTextId()).orElseThrow(() -> new NoSuchElementException("Zettel nicht gefunden"));
		LOG.info("\n -->TekstService.updateTekst, Tekst vorm Bearbeiten \n" +   "--->" + updatedTekst +"\n" + updatedTekst.getZettels());
		updatedTekst.setText(tekst.getText());
		updatedTekst.setTextDate(tekst.getTextDate());
		updatedTekst.setSource(tekst.getSource());
		textRepo.save(updatedTekst);
		LOG.info("\n -->TekstService.updateTekst, Tekst nachm Bearbeiten \n" +   "--->" + updatedTekst +"\n" + updatedTekst.getZettels());
		
	}
	
	
	
	

}
