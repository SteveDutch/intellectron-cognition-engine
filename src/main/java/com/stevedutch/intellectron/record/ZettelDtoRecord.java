package com.stevedutch.intellectron.record;

import java.util.ArrayList;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;

public record ZettelDtoRecord(Zettel zettel, Tekst tekst, Note note, Author author, 
		ArrayList<Tag> tags, Reference reference) {
	// You hierzu:Please note that the commented-out constructor in the code
    // snippet is not necessary since the record constructor handles the field initialization automatically.
//	public ZettelDtoRecord(Zettel zettel, Tekst tekst, Note note, Tag tagText, Author author) {
//		this.author = new Author();
//		this.note = new Note();
//		this.tagText = new Tag();
//		this.tekst = new Tekst();
//		this.zettel = new Zettel();
//	}
}



