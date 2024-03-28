package com.stevedutch.intellectron.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TagRepository;
import com.stevedutch.intellectron.repository.ZettelRepository;

@Service
public class TagService {
	
	private static final Logger LOG = LoggerFactory.getLogger(TagService.class);
	
	@Autowired
	private TagRepository tagRepo;
	@Autowired
	private ZettelService zettelService;
	@Autowired
	private ZettelRepository zettelRepo;
	
	
	public Tag saveTag(Tag tag) {
		System.out.println("\n saveTag");
		return tagRepo.save(tag);
	}
	
	public ArrayList<Tag> saveTagsWithZettel(ArrayList<Tag> tags, Zettel zettel) {
		
			tags.forEach(tag -> System.out.println("\n HIER erhaltene tags die sollen ihre ID kriegen oder neu kriegen  !!!   nämlich:  "  + tag));
			ArrayList<Tag> newTags = tags.stream().map(tag -> tagRepo.findByTagText(tag.getTagText())
					.orElse(new Tag(tag.getTagText())))
					.collect(Collectors.toCollection(ArrayList::new));
			newTags.forEach(tag -> System.out.println("\n HIER alle mit ID   !!!   nämlich:  "  + tag));
			newTags.forEach(tag -> tag.getZettels().add(zettel));
			newTags.forEach(tag -> System.out.println("\n HIER sollten alle verheiratet sein   !!!   nämlich:  "  + tag));
			newTags.forEach(tag -> tagRepo.save(tag));
			newTags.forEach(tag -> System.out.println("\n HIER alle gespeichert   !!!   nämlich:  "  + tag));
//			zettel.getTags().addAll(newTags);
			zettel.setChanged(LocalDateTime.now());
			zettelRepo.save(zettel);
			return newTags;
	}
	
	public Tag saveOneTagwithZettel(Tag tag, Zettel zettel) {
		
		tag.getZettels().add(zettel);
		// TODO check if tag already is connected to zettel, nur dann speichern (nächste Zeile), dann müßte ein doppeltes Zuweisen nicht mehr möglich sein
		zettel.getTags().add(tag);
		tagRepo.save(tag);
		zettel.setChanged(LocalDateTime.now());
		zettelRepo.save(zettel);
		return tag;
		
	}

	public void updateTags(Long zettelId, ArrayList<Tag> tags) {

		// neue Tags printen
		tags.forEach(tag -> LOG.info(" \n --> updated Tags  wie vom frontend erhalten= \n ID = " + tag.getId()+ "\n text = " + tag.getTagText()));
		Zettel zettel = zettelService.findZettelById(zettelId);
		// XXX tags vom front end kommen nur mit tagText, daher anhand dessen den Tag finden, oder -falls nicht existent -
		// oder als neues Tag mit dem gegebenen Text einrichten  --> Vermeiden von Doubletten in der Datenbank & Objekt
		ArrayList<Tag> newTags = tags.stream().map(tag -> tagRepo.findByTagText(tag.getTagText())
				.orElseGet(() -> new Tag(saveOneTagwithZettel(tag, zettel).getTagText())))
				.collect(Collectors.toCollection(ArrayList::new));
		newTags.forEach(tag -> LOG.info(" \n --> newTags, , gefeunden oder eingerichtet, als ArrayList  = ID = " + tag.getId() + " Text =  " + tag.getTagText()));
		// save Tags with Zettel
		LOG.info(" \n -->  Tags, vorm saven,  Objekte = " + newTags );
		saveTagsWithZettel(newTags, zettelService.findZettelById(zettelId));
		LOG.info("\n --> Tags gespeichert:");
		newTags.forEach(tag -> LOG.info(" \n --> new Tags  = ID = " + tag.getId() + " Text =  " + tag.getTagText()));
		LOG.info(" \n --> updated Tags,  Objekte = " + newTags );
		
	}

}
