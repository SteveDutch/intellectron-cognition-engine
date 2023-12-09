package com.stevedutch.intellectron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.ZettelRepository;

@Service
public class ZettelService {
	
	@Autowired
	private ZettelRepository zettelRepo;
	
		public Zettel saveZettel(Zettel zettel) {
			if (zettelRepo.findByZettelId(zettel.getZettelId()) == null) {
				
			}

			return zettelRepo.save(zettel);
        }
		
		public Zettel updateZettel(Zettel zettel) {
			if (zettel.getZettelId() != null) {
                // wg. der voranstehenen Bedingung dürfte kein NULL auftreten können, aber trotzdem wg, Optional erforderlich
				Zettel existingZettel =  zettelRepo.findById(zettel.getZettelId()).orElse(new Zettel());
                // Aktualisiere die relevanten Felder mit den neuen Werten, wenn sie nicht null sind
                if (zettel.getTopic() != null) {
                    existingZettel.setTopic(zettel.getTopic());
                }
                if (zettel.getAdded() != null) {
                    existingZettel.setAdded(zettel.getAdded());
                }
                if (zettel.getChanged() != null) {
                    existingZettel.setChanged(zettel.getChanged());
                }
                if (zettel.getSignature() != null) {
                    existingZettel.setSignature(zettel.getSignature());
                }
                if (zettel.getNote()!= null) {
                    existingZettel.setNote(zettel.getNote());
                }	
                
                // Aktualisiere die Beziehungen (z.B., Authors, Tags, Teksts)
                if (zettel.getAuthors() != null) {
                    existingZettel.setAuthors(zettel.getAuthors());
                }
                if (zettel.getTags() != null) {
                    existingZettel.setTags(zettel.getTags());
                }
                if (zettel.getTeksts() != null) {
                    existingZettel.setTeksts(zettel.getTeksts());
                }

			}
			
			return zettelRepo.save(zettel);
		}
		
		

}
