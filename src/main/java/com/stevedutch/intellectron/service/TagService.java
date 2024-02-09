package com.stevedutch.intellectron.service;

import java.util.ArrayList;

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
	
	public ArrayList<Tag> saveTagsWithZettel(ArrayList<Tag> tags, Zettel zettel) {
		
			ArrayList<Tag> newTags = new ArrayList<Tag>(tags);
			newTags.forEach(tag -> System.out.println("\n HIERHIERHIER   !!!   "  + tag));
			newTags.forEach(tag -> tagRepository.save(tag));
			newTags.forEach(tag -> tag.getZettels().add(zettel));
			
			zettel.getTags().addAll(newTags);
			
			newTags.forEach(tag -> tagRepository.save(tag));
			return newTags;
	}
	
	public Tag saveTagwithZettel(Tag tag, Zettel zettel) {
		
		tag.getZettels().add(zettel);
		return tagRepository.save(tag);
	}

}
