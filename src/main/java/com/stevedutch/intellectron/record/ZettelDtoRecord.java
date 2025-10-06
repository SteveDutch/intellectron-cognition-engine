package com.stevedutch.intellectron.record;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;

@Component
public record ZettelDtoRecord(Zettel zettel, Tekst tekst, Note note, Author author, 
		ArrayList<Tag> tags, ArrayList<Reference> references) {

}
