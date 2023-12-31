package com.stevedutch.intellectron.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
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
//for testing
	ZettelService(NoteService noteServiceMock, ZettelRepository zettelRepoMock, NoteService noteServiceMock2, AuthorService authorServiceMock, TagService tagServiceMock, TextService tekstServiceMock, TextRepository tekstRepositoryMock) {
		// TODO Auto-generated constructor stub
		this.noteService = noteServiceMock;
        this.zettelRepo = zettelRepoMock;
        this.noteService = noteServiceMock2;
        this.authorService = authorServiceMock;
        this.tagService = tagServiceMock;
        this.textService = tekstServiceMock;
        this.textRepo = tekstRepositoryMock;
        
	}

	//	vXXX ielleicht ein bisschen groß, diese Funktion
	public ZettelDtoRecord createZettel(ZettelDtoRecord zettelDto) {
		System.out.println("\n Start of  createZettel()-->  note/Kommentar: \n" + zettelDto.note());
		if (zettelDto.zettel().getZettelId() == null) {
//				 id == null;

			Note newNote = zettelDto.note();
			newNote.setZettel(zettelDto.zettel());
			System.out.println("imtest noteService = " + Optional.ofNullable(noteService).isPresent());

			noteService.save(newNote);

			Zettel newZettel = zettelDto.zettel();
			newZettel.setNote(newNote);
			newZettel.setTekst(zettelDto.tekst());
			newZettel.getTags().add(zettelDto.tag());
			newZettel.setAdded(LocalDateTime.now());

			Tag newTag = zettelDto.tag();
			newTag.getZettels().add(newZettel);
			tagService.saveTag(newTag);
			System.out.println("\n ZettelService.createZettel ,  just saved: newTag \n" + newTag + "\n");
			
			zettelRepo.save(newZettel);
			System.out.println("\n ZettelService.createZettel ,  just saved: newZettel \n" + newZettel + "\n");

			Tekst newTekst = newZettel.getTekst();
			newTekst.getZettels().add(newZettel);
			textRepo.save(newTekst);
			System.out.println("\n ZettelService.createZettel ,  just saved: newTekst \n" + newTekst + "\n");

			Author newAuthor = zettelDto.author();
			newAuthor.getTexts().add(newTekst);
			authorService.saveAuthor(newAuthor);
			System.out.println("\n ZettelService.createZettel ,  just saved: newAuthor \n" + newAuthor + "\n");
			newTekst.getAuthors().add(newAuthor);
			System.out.println("\n ZettelService.createZettel ,  just updated: newTekst \n" + newTekst + "\n");
			zettelDto = new ZettelDtoRecord(newZettel, newTekst, newNote, newAuthor, newTag);
			
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

}
