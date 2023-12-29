package com.stevedutch.intellectron.record;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.domain.Tag;

public record ZettelDtoRecord(Zettel zettel, Tekst tekst, Note note, Author author, Tag tag) {
	
//	public ZettelDtoRecord(Zettel zettel, Tekst tekst, Note note, Tag tagText, Author author) {
//		this.author = new Author();
//		this.note = new Note();
//		this.tagText = new Tag();
//		this.tekst = new Tekst();
//		this.zettel = new Zettel();
//	}
}



