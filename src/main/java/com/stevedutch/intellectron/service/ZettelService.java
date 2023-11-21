package com.stevedutch.intellectron.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.ZettelRepository;

public class ZettelService {
	
	@Autowired
	private ZettelRepository ZettelRepo;
	
		public Zettel saveZettel(Zettel zettel) {
            return ZettelRepo.save(zettel);
        }

}
