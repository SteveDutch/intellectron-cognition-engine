package com.stevedutch.intellectron.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long>  {
	
	public Note findByZettelId(Long zettelId);

	public Note findOneNoteByNoteText(String noteText);

}
