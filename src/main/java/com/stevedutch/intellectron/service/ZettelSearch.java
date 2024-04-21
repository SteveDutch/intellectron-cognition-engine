package com.stevedutch.intellectron.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.ZettelRepository;

@Service
public class ZettelSearch {
	
	@Autowired
	private ZettelRepository zettelRepo;

	public List<Zettel> findZettelByNoteFragment(String noteFragment) {
		List<Zettel> result = zettelRepo.findZettelByNoteFragment(noteFragment);
		return result;
	}

 

}
