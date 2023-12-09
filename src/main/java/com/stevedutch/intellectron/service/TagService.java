package com.stevedutch.intellectron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.ZettelTag;
import com.stevedutch.intellectron.repository.TagRepository;

@Service
public class TagService {
	
	@Autowired
	private TagRepository tagRepository;
	
	public ZettelTag saveTag(ZettelTag zettelTag) {
		return tagRepository.save(zettelTag);
	}

}
