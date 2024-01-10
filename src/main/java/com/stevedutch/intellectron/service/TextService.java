package com.stevedutch.intellectron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TextRepository;

@Service
public class TextService {
	
	@Autowired
	private TextRepository textRepo;

	public Tekst saveText(Tekst tekst) {
		return textRepo.save(tekst);
	}
		
	public Tekst saveTextwithZettel (Tekst tekst, Zettel zettel) {
		tekst.getZettels().add(zettel);
        
        return textRepo.save(tekst);
    
	}
	
	
	
	

}
