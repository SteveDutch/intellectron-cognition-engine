package com.stevedutch.intellectron;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;

@Configuration
public class IntellectronApplicationConfiguration {
	
	@Bean
	public Zettel zettel() {
		return new Zettel();
	}
	
	@Bean
    public Tekst  tekst() {
        return new Tekst();
    }
	
	@Bean
    public Note	note() {
        return new Note();
    }
	
	@Bean
    public Author  author() {
        return new Author();
    }

}
