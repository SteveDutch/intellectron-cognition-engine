package com.stevedutch.intellectron.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TagRepository;

@Service
public class TagService {
	
	private static final Logger LOG = LoggerFactory.getLogger(TagService.class);
	
	@Autowired
	private TagRepository tagRepo;
	@Autowired
	private ZettelService zettelService;

	
	public Tag saveTag(Tag tag) {

		if (tag.getId() == null) {
			Optional<Tag> existingTag = tagRepo.findByTagText(tag.getTagText());
			if (existingTag.isPresent()) {
				return (existingTag.get());
						//orElseGet(tagRepo.save(new Tag(tag.getTagText()))); // Verwenden Sie das existierende Tag / return just existingTag
														// laut phind
			} 
			//return tagRepo.save(tag); // Speichern Sie das neue Tag  //  als New Tag

		}
		return tagRepo.save(tag); // Speichern Sie das neue Tag

	}

	
	public ArrayList<Tag> saveTagsWithZettel(ArrayList<Tag> tags, Zettel zettel) {
		
			tags.forEach(tag -> System.out.println("\n HIER erhaltene tags die sollen ihre ID kriegen oder neu kriegen  !!!   nämlich:  " + tag.getId() +" Anzahl Zettel: " + tag.getZettels().size()));
			ArrayList<Tag> newTags = tags.stream().map(tag -> tagRepo.findByTagText(tag.getTagText())
					.orElse(saveTag(new Tag(tag.getTagText()))))
					.collect(Collectors.toCollection(ArrayList::new));
			newTags.forEach(tag -> System.out.println("\n HIER alle mit ID   !!!   nämlich:  "    + tag.getId() +" Anzahl Zettel: " + tag.getZettels().size()));
//			newTags.forEach(tag -> tag.getZettels().add(zettel));
//			newTags.forEach(tag -> System.out.println("\n HIER sollten alle verheiratet sein   !!!   nämlich:  "  + tag.getId() +" Anzahl Zettel: " + tag.getZettels().size()));
			newTags.forEach(tag -> saveTag(tag));
			newTags.forEach(tag -> System.out.println("\n HIER alle gespeichert   !!!   nämlich:  "  + tag.getId()+" Anzahl Zettel: "  + tag.getZettels().size()));
//			zettel.getTags().addAll(newTags);
			
			zettel.setChanged(LocalDateTime.now());
			zettelService.saveZettel(zettel);
			newTags.forEach(tag -> System.out.println("\n ---> nach saveZettel in saveTagsmitZettel: Tag: "  + tag.getId() +" Anzahl Zettel: " + tag.getTagText() 
			    + "\n zettels: " + tag.getZettels().size()));	
			return newTags; 
	}
	
	public void updateTags(Long zettelId, ArrayList<Tag> tags) {

		tags.forEach(tag -> LOG.info(" \n --> updated Tags  wie vom frontend erhalten= \n ID = " 
				+ tag.getId()+ "\n text = " + tag.getTagText()));
		Zettel zettel = zettelService.findZettelById(zettelId);
		// XXX tags vom front end kommen nur mit tagText, daher anhand dessen den Tag finden, oder -falls nicht existent -
		//  als neues Tag mit dem gegebenen Text einrichten  --> Vermeiden von Doubletten in der Datenbank & Objekt
		ArrayList<Tag> newTags = tags.stream().map(tag -> tagRepo.findByTagText(tag.getTagText())
				.orElseGet(() -> saveTag(new Tag(tag.getTagText()))))
				.collect(Collectors.toCollection(ArrayList::new));
//		newTags.forEach(tag -> System.out.println("\n HIER sollten alle tags EINE id HABEN ä" + tag.getId() + tag.getTagText()));
		// in DB vorhandene, aber zum Zettel neu hinzugefügte Tags mit Zettel verknüpfen
		for (Tag tag :newTags) {
			if (zettel.getTags().contains(tag)) {
				} else {zettel.getTags().add(tag);}
		}
		// löschen der Tags, die in der DB & Zettel, aber nicht mehr in der neuen Tagliste vorhanden sind (existed in old tag list, but not in new tag list)
		ArrayList<Tag> OriginalTagsCopy = new ArrayList<>(zettel.getTags());
		for (Tag tag :OriginalTagsCopy) {
			if (!newTags.contains(tag)) {
                zettel.getTags().remove(tag);
//                zettelService.saveZettel(zettel); XXX ist unnötig, wenn mir auch nicht klar ist, warum.
                
            }
		}

		LOG.info("\n --- Tags gespeichert mit am Ende von updateTags : ");
        newTags.forEach(tag -> LOG.info(" \n ---> new Tags  = ID = " + tag.getId() + " Text =  " + tag.getTagText()));
	}


    public Tag findTagByText(String tagText) {
        Optional<Tag> finding = tagRepo.findByTagText(tagText);
		finding.ifPresent(x -> LOG.info("\n  found tag = " + x.getTagText()));
		if (finding.isEmpty()) {
		
			LOG.info("\n  found nothing");
		}
		return finding.get();
	}

}
 