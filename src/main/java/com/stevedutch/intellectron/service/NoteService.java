package com.stevedutch.intellectron.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.NoteRepository;

@Service
public class NoteService {
	
	private static final Logger LOG = LoggerFactory.getLogger(NoteService.class);
	// for junit testing TODO delete, no code for testing 
	public NoteService(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}

	@Autowired
	private NoteRepository noteRepository;
	
	public Note saveNote(Note note) {
		// XXX findOne... wirft keinen Fehler, wennn mehrere vorhanden sinf findBy ... schon
		Note existingNote = noteRepository.findOneNoteByNoteText(note.getNoteText());
		if (existingNote != null) {
			note.setZettelId(existingNote.getZettelId());
			
		}
		return noteRepository.save(note);
    }
	
	public Note saveNotewithZettel(Note note, Zettel zettel) {
		
		note.setZettel(zettel);
		note.setZettelId(zettel.getZettelId());
		System.out.println("imtest noteService.saveNotewithZettel;  note = " + Optional.ofNullable(note).isPresent());
		System.out.println("imtest noteService.saveNotewithZettel;  note.getZettelId() =  " + Optional.ofNullable(note.getZettelId()).isPresent());
        return noteRepository.save(note);

	}

	/**
	 * Checks if note is already in DB. if not, a new note is created.
	 * Thit connects note & zettel.
	 * 
	 * @param note
	 * @param zettel
	 * @return note
	 */
	public Note connectNotewithZettel(Note note, Zettel zettel) {
		Note newNote = noteRepository.findOneNoteByNoteText(note.getNoteText());
		if (newNote == null) {
			newNote =new Note(note.getNoteText());
			note = newNote;
		};
		note.setZettel(zettel);
		note.setZettelId(zettel.getZettelId());
		return note;
	}

	public void updateNote(Long zettelId, Note note) {
		Note updatedNote = noteRepository.findByZettelId(zettelId);
		LOG.info("\n --> NoteService.updateNote, Note vorm Bearbeiten \n" +   "--->" + updatedNote +"\n" + updatedNote.getZettel());
		updatedNote.setNoteText(note.getNoteText());
		noteRepository.save(updatedNote);
		LOG.info("\n --> NoteService.updateNote, Note nachm Bearbeiten \n" +   "--->" + updatedNote +"\n" + updatedNote.getZettel());
	}


}
