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
import com.stevedutch.intellectron.repository.NoteRepository;
import com.stevedutch.intellectron.repository.TextRepository;
import com.stevedutch.intellectron.repository.ZettelRepository;


@Service
public class ZettelService {

	@Autowired
	private ZettelRepository zettelRepo;
	private static final Logger LOG = LoggerFactory.getLogger(ZettelService.class);
	@Autowired
	private TextRepository textRepo;

	@Autowired
	private TagService tagService;

	@Autowired
	private NoteService noteService;

	@Autowired
	private NoteRepository noteRepo;
	
	@Autowired
    private TextService textService;
	
	@Autowired
	private AuthorService authorService;
	
	@Autowired
	private ReferenceService refService;
	
	//for junit testing
	ZettelService(NoteService noteServiceMock, ZettelRepository zettelRepoMock, NoteService noteServiceMock2, AuthorService authorServiceMock, TagService tagServiceMock, TextService tekstServiceMock, TextRepository tekstRepositoryMock) {
		this.noteService = noteServiceMock;
        this.zettelRepo = zettelRepoMock;
        this.noteService = noteServiceMock2;
        this.authorService = authorServiceMock;
        this.tagService = tagServiceMock;
        this.textService = tekstServiceMock;
        this.textRepo = tekstRepositoryMock;
        
	}

	//	vXXX vielleicht ein bisschen groß, diese Funktion
	public ZettelDtoRecord createZettel(ZettelDtoRecord zettelDto) {
		
		if (zettelDto.zettel().getZettelId() == null) {
//				 id == null;
			// save Note
			Note newNote = noteService.saveNotewithZettel(zettelDto.note(), zettelDto.zettel());
			LOG.info("\n imtest noteService = " + Optional.ofNullable(noteService).isPresent());
			// Zettel
			Zettel newZettel = zettelDto.zettel();
			newZettel.setNote(newNote);
			newZettel.setTekst(zettelDto.tekst());
			newZettel.setAdded(LocalDateTime.now());
			newZettel.getReferences().addAll(zettelDto.references());
			// TODO BUG 00:01 wird zu 1
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(/* "yyyyMMddHHmm" */"HHmmddMMyyyy");
			newZettel.setSignature(Long.parseLong(newZettel.getAdded().format(formatter)));

			// tags 
//			ArrayList<Tag> newTags = new ArrayList<Tag>(zettelDto.tags());
//			newTags.forEach(tag -> System.out.println("\n HIERHIERHIER   !!!   "  + tag));
//			newTags.forEach(tag -> tagService.saveTag(tag));
//			
//			
//			newTags.forEach(tag -> tagService.saveTagwithZettel(tag, newZettel));
//			
//			newZettel.getTags().addAll(newTags);
			
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
			newRefs.forEach(reference ->reference.setOriginZettel(newZettel.getSignature()));
			newRefs.forEach(reference -> refService.saveReferenceWithZettel(reference, newZettel));
			
			zettelDto = new ZettelDtoRecord(newZettel, newTekst, newNote, newAuthor, newTags, newRefs);
			
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
		return zettelDto;
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

}
