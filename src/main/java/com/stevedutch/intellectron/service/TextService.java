package com.stevedutch.intellectron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Tekst;

@Service
public class TextService {
	
	@Autowired
	private TextRepository textRepo;

	public Tekst saveText(Tekst tekst) {
		return textRepo.save(tekst);
		
	}
	
	
	
	

}
