package com.stevedutch.intellectron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TagRepository;

@Service
public class TagService {
	
	@Autowired
	private TagRepository tagRepository;
	
	// for unit testing
	public TagService(TagRepository tagRepoMock) {
		this.tagRepository = tagRepoMock;
	}
	
	public Tag saveTag(Tag tag) {
		return tagRepository.save(tag);
	}
	
	public Tag saveTagwithZettel(Tag tag, Zettel zettel) {
		
		tag.getZettels().add(zettel);
		return tagRepository.save(tag);
	}

}
