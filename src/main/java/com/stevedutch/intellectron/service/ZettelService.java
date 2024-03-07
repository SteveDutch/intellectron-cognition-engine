package com.stevedutch.intellectron.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.repository.ZettelRepository;


@Service
public class ZettelService {
	private static final Logger LOG = LoggerFactory.getLogger(ZettelService.class);
	
	@Autowired
	private ZettelRepository zettelRepo;

	@Autowired
	private TagService tagService;

	@Autowired
	private NoteService noteService;

	@Autowired
    private TextService textService;
	
	@Autowired
	private AuthorService authorService;
	
	@Autowired
	private ReferenceService refService;
	
	//	vXXX vielleicht ein bisschen groß, diese Funktion
	public ZettelDtoRecord createZettel(ZettelDtoRecord zettelDto) {
		
		if (zettelDto.zettel().getZettelId() == null) {
//				 id == null;
			// save Note
			Note newNote = noteService.saveNotewithZettel(zettelDto.note(), zettelDto.zettel());
			LOG.info("\n imtest noteService = " + Optional.ofNullable(noteService).isPresent());
			// Zettel
			Zettel newZettel = setupZettel(zettelDto, newNote);

			ArrayList<Tag> newTags = tagService.saveTagsWithZettel(zettelDto.tags(), newZettel);
			zettelRepo.save(newZettel);
			LOG.info("\n ZettelService.createZettel,  just savedwithZettel LOGLOGLOG1st {}", newZettel);
			
			//Tekst
			Tekst newTekst = textService.saveTextwithZettel(zettelDto.tekst(), newZettel);
			//Author
			Author newAuthor = authorService.saveAuthorWithText(zettelDto.author(), newTekst);
			LOG.info("\n Author nach Speichern in CreateZettel :  " + newAuthor);
			textService.saveTextWithAuthor(newTekst, newAuthor);
			LOG.info(" \n Tekst nachm Speichern in CreateZettel:   "+ newTekst);
		
  			// reference
			ArrayList<Reference> newRefs = new ArrayList<Reference>(zettelDto.references());
			setRelationsAndSaveRefsAndZettel(newZettel, newRefs);
			
			LOG.info(" \n --> ist in reference auch das target gespeichert? show referencE: \n" 
			+ newRefs);
			
			zettelDto = new ZettelDtoRecord(newZettel, newTekst, newNote, newAuthor, newTags, newRefs);
			
		} else {
			// TODO Überprüfen, ob der Zettel bereits vorhanden ist
//			Optional<Zettel> actualZettel = zettelRepo.findById(zettelDto.zettel().getZettelId());
//			if (actualZettel.isPresent()) {
//				// Do something with the zettel
//
//			} else {
//				// id vorhanden
//
//			}
		}
		return zettelDto;
	}

	public Zettel setupZettel(ZettelDtoRecord zettelDto, Note newNote) {
		Zettel newZettel = zettelDto.zettel();
		newZettel.setNote(newNote);
		newZettel.setTekst(zettelDto.tekst());
		newZettel.setAdded(LocalDateTime.now());
		newZettel.getReferences().addAll(zettelDto.references());
		// TODO BUG  00:01 wird zu 1 -> added colon, (HH:mm ...) -> NumberFormatException
		// ggf. mit if-Klausel & length & vorne mit 0 auffüllen
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(/* "yyyyMMddHHmm" */"HHmmddMMyyyy");
		newZettel.setSignature(Long.parseLong(newZettel.getAdded().format(formatter)));
		return newZettel;
	}

	public void setRelationsAndSaveRefsAndZettel(Zettel newZettel, ArrayList<Reference> newRefs) {
		newRefs.forEach(reference ->reference.setOriginZettel(newZettel.getSignature()));
		newRefs.forEach(reference -> refService.saveReferenceWithZettel(reference, newZettel));
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

	public List<Zettel> findAll() {
		return zettelRepo.findAllZettelWithTopic();
	}

	public List<Zettel> findLast10Zettel() {
		return zettelRepo.findLast10Zettel();
	}

	public Zettel findZettelById(Long zettelid) {
		return zettelRepo.findById(zettelid).orElse(new Zettel());
	}
	
	public void deleteOneZettelbyId (Long zettelId ) {
		zettelRepo.deleteById(zettelId);
	}

}
