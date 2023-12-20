package com.stevedutch.intellectron.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.NoteRepository;
import com.stevedutch.intellectron.repository.ZettelRepository;

@Service
public class ZettelService {
	
	@Autowired
	private ZettelRepository zettelRepo;
	
	@Autowired
	private NoteService noteService;
	
    @Autowired
    private NoteRepository noteRepo;
	
		public Zettel saveZettel(Zettel zettel) {
			System.out.println("\n Start of  save()-->  note/Kommentar: \n" + zettel.getNote());
//			System.out.println("\n Start of  saveZettel()-->   Text : \n" + tekst);
//			System.out.println("\n Start of  asaveZettel()-->   Autor : \n" + author);

//			return zettelRepo.save(zettel);
			if (zettel.getZettelId() == null) {
				// id == null;
				
//				Zettel newZettel = new Zettel();
//				newZettel.setNote(zettel.getNote());
//				newZettel.setTeksts(zettel.getTeksts());
//				System.out.println("\n ZettelService.saveZettel mit id = null, newZettel \n" + newZettel + "\n");
			} else {
				// Überprüfn, ob der Zettel bereits vorhanden ist
				Optional<Zettel> actualZettel = zettelRepo.findById(zettel.getZettelId());
				if (actualZettel.isPresent()) {
					// Do something with the zettel
					
				} else {
					// id vorhanden 
					
				}
			}

	            return zettelRepo.save(zettel);	
	    
	        }
	        

        
		
		public Zettel updateZettel(Zettel zettel) {
			if (zettel.getZettelId() == null) {
                // wg, Optional erforderlich
				Zettel existingZettel =  zettelRepo.findById(zettel.getZettelId()).orElse(new Zettel());
                // Aktualisiere die relevanten Felder mit den neuen Werten, wenn sie nicht null sind
                if (zettel.getTopic() == null) {
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
