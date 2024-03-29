package com.stevedutch.intellectron.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.ReferenceRepository;

@Service
public class ReferenceService {
	
	@Autowired
	private ReferenceRepository referenceRepo;
	
    @Autowired
    private ZettelService zettelService;

	public List<Reference> findAll() {
		return referenceRepo.findAll();
	}

	public Reference saveReferenceWithZettel(Reference reference, Zettel zettel) {
		reference.getZettels().add(zettel);
		return referenceRepo.save(reference);
	}

	public void updateReferences(Long zettelId, ArrayList<Reference> references) {
		
		Zettel zettel = zettelService.findZettelById(zettelId);
		zettel.setReferences(references.stream().collect(Collectors.toSet()));
		zettelService.setRelationsAndSaveRefsWithZettel(zettel, references);
		System.out.println(zettel.getReferences());
		zettelService.saveZettel(zettel);
		
	}

}
