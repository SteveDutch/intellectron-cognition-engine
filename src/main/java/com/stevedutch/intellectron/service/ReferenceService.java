package com.stevedutch.intellectron.service;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.ReferenceRepository;

@Service
public class ReferenceService {

	private static final Logger LOG = LoggerFactory.getLogger(ZettelService.class);

	@Autowired
	private ReferenceRepository refRepo;

	@Autowired
	private ZettelService zettelService;
	@Autowired
	private SearchService searchService;

	public Reference saveReferenceWithZettel(Reference reference, Zettel zettel) {
		reference.getZettels().add(zettel);
		return refRepo.save(reference);
	}

	public void updateReferences(Long zettelId, ArrayList<Reference> references) {

		LOG.info("Start of updateReferences ; ---> " + references);
		for (Reference reference : references) {
			reference.setOriginZettel(searchService.findZettelById(zettelId).getSignature());
			// for debugging
			refRepo.findByOriginZettelAndTargetZettel(reference.getOriginZettel(), reference.getTargetZettel());
			LOG.info("FOUND " + refRepo.findByOriginZettelAndTargetZettel(reference.getOriginZettel(),
					reference.getTargetZettel()));
			// klappt das jetzt schon?
			if (refRepo.findByOriginZettelAndTargetZettel(reference.getOriginZettel(),
					reference.getTargetZettel()) != null) {
				LOG.info("Loop-Start --> " + reference);
				reference.setReferenceId(refRepo
						.findByOriginZettelAndTargetZettel(reference.getOriginZettel(), reference.getTargetZettel())
						.getReferenceId());
				LOG.info(" --> im Loop, neues ID?" + reference);
			} else {
//				reference.setTargetZettel(zettelId);
				LOG.info("else/nicht in DB vorm save --> " + reference);
				refRepo.save(reference);
				LOG.info("else/nicht in DB nachm save --> " + reference);

			}
			// fürs deleten muss ich wohl erst den Zettel finden
			Zettel zettel = searchService.findZettelById(zettelId);
			zettel.setReferences(references.stream().collect(Collectors.toSet()));
			LOG.info("updateReferences ; am ende, hat zettel refs ---> " + zettel.getReferences());
			zettelService.saveZettel(zettel);

		}

	}

}
