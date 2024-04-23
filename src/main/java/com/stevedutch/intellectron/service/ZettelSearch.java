package com.stevedutch.intellectron.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TextRepository;
import com.stevedutch.intellectron.repository.ZettelRepository;

@Service
public class ZettelSearch {
	
	@Autowired
	private ZettelRepository zettelRepo;
	@Autowired
	private TextRepository textRepo;

	public List<Zettel> findZettelByNoteFragment(String noteFragment) {
		List<Zettel> result = zettelRepo.findZettelByNoteFragment(noteFragment);
		return result;
	}

	public List<Zettel> findZettelByTextFragment(String textFragment) {
		List<Zettel> result = zettelRepo.findZettelByTextFragment(textFragment);
		return result;
	}

	public List<Tekst> findTekstByTextFragment(String textFragment) {
		List<Tekst> result = textRepo.findTekstByTextFragment(textFragment);
		return result;
	}

 

}
