package com.stevedutch.intellectron.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.repository.NoteRepository;
import com.stevedutch.intellectron.repository.TextRepository;
import com.stevedutch.intellectron.repository.ZettelRepository;

@Service
public class ZettelService {

	@Autowired
	private ZettelRepository zettelRepo;

	@Autowired
	private TextRepository textRepo;

	@Autowired
	private NoteService noteService;

	@Autowired
	private NoteRepository noteRepo;

	public Zettel createZettel(ZettelDtoRecord zettelDto) {
		System.out.println("\n Start of  createZettel()-->  note/Kommentar: \n" + zettelDto.note());
		if (zettelDto.zettel().getZettelId() == null) {
//				 id == null;

			Note newNote = zettelDto.note();
			newNote.setZettel(zettelDto.zettel());
			noteService.save(newNote);

			Zettel newZettel = zettelDto.zettel();
			newZettel.setNote(newNote);
			newZettel.setTekst(zettelDto.tekst());
//				
			zettelRepo.save(newZettel);
			System.out.println("\n ZettelService.createZettel ,  just saved: newZettel \n" + newZettel + "\n");

			Tekst newTekst = newZettel.getTekst();
			newTekst.getZettels().add(newZettel);
			textRepo.save(newTekst);

		} else {
			// TODO Überprüfen, ob der Zettel bereits vorhanden ist
			Optional<Zettel> actualZettel = zettelRepo.findById(zettelDto.zettel().getZettelId());
			if (actualZettel.isPresent()) {
				// Do something with the zettel

			} else {
				// id vorhanden

			}
		}
// XXX here eine klares TODO
		return zettelRepo.save(zettelDto.zettel());
	}

//		 TODO
	public Zettel updateZettel(Zettel zettel) {
		if (zettel.getZettelId() == null) {
			// wg, Optional erforderlich
			Zettel existingZettel = zettelRepo.findById(zettel.getZettelId()).orElse(new Zettel());
			// TODO Aktualisiere die relevanten Felder mit den neuen Werten, wenn sie nicht
			// null sind
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
			if (zettel.getNote() != null) {
				existingZettel.setNote(zettel.getNote());
			}

			// Aktualisiere die Beziehungen (z.B., Authors, Tags, Teksts)
			if (zettel.getTags() != null) {
				existingZettel.setTags(zettel.getTags());
			}
//                if (zettel.getTeksts() != null) {
//                    existingZettel.setTeksts(zettel.getTeksts());
//                }

		}

		return zettelRepo.save(zettel);
	}

}
