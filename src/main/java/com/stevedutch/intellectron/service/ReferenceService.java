package com.stevedutch.intellectron.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.ReferenceRepository;

@Service
public class ReferenceService {
	
	@Autowired
	private ReferenceRepository referenceRepo;

	public List<Reference> findAll() {
		return referenceRepo.findAll();
	}

	public Reference saveReferenceWithZettel(Reference reference, Zettel zettel) {
		reference.getZettels().add(zettel);
		return referenceRepo.save(reference);
	}

}
