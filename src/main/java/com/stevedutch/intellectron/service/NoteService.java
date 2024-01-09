package com.stevedutch.intellectron.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.NoteRepository;

@Service
public class NoteService {
	
	
	// for junit testing
	public NoteService(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}

	@Autowired
	private NoteRepository noteRepository;
	
	public Note saveNote(Note note) {
        return noteRepository.save(note);
    }
	
	public Note saveNotewithZettel(Note note, Zettel zettel) {
		
		note.setZettel(zettel);
		note.setZettelId(zettel.getZettelId());
		System.out.println("imtest noteService.saveNotewithZettel;  note = " + Optional.ofNullable(note).isPresent());
		System.out.println("imtest noteService.saveNotewithZettel;  note.getZettelId() =  " + Optional.ofNullable(note.getZettelId()).isPresent());
        return noteRepository.save(note);

	}

}
