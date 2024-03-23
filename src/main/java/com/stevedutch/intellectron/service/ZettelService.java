package com.stevedutch.intellectron.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.repository.ZettelRepository;

@Service
public class ZettelService {
	private static final Logger LOG = LoggerFactory.getLogger(ZettelService.class);

	@Autowired
	private ZettelRepository zettelRepo;

	@Autowired
	private TagService tagService;

	@Autowired
	private NoteService noteService;

	// ohne Lazy gibt 's einen  circular reference error, 
	// diesen mit Lazy zu beseitigen, feels like cheating
	// (führt dazu, dass betreffendes Feld nicht beim Start, sondern erst beim Auduf initialisiert wird)
	@Lazy
	@Autowired
	private TextService textService;

	@Autowired
	private AuthorService authorService;

	@Autowired
	private ReferenceService refService;

	// vXXX vielleicht ein bisschen groß, diese Funktion
	public ZettelDtoRecord createZettel(ZettelDtoRecord zettelDto) {

		if (zettelDto.zettel().getZettelId() == null) {
//				 id == null, create new Zettel;
			// save Note
			Note newNote = noteService.saveNotewithZettel(zettelDto.note(), zettelDto.zettel());
			// Zettel
			Zettel newZettel = setupZettel(zettelDto, newNote);

			ArrayList<Tag> newTags = tagService.saveTagsWithZettel(zettelDto.tags(), newZettel);
			zettelRepo.save(newZettel);
			LOG.info("\n ZettelService.createZettel,  just savedwithZettel LOGLOGLOG1st {}", newZettel);

			// Tekst
			Tekst newTekst = textService.saveTextwithZettel(zettelDto.tekst(), newZettel);
			// Author
			Author newAuthor = authorService.saveAuthorWithText(zettelDto.author(), newTekst);
			textService.saveTextWithAuthor(newTekst, newAuthor);

			// reference
			ArrayList<Reference> newRefs = new ArrayList<Reference>(zettelDto.references());
			setRelationsAndSaveRefsAndZettel(newZettel, newRefs);

			LOG.info(" \n --> ist in reference auch das target gespeichert? show referencE: \n" + newRefs);

			zettelDto = new ZettelDtoRecord(newZettel, newTekst, newNote, newAuthor, newTags, newRefs);

		} else {
			// because id !0 null: save/update Zettel;
			// Zettel updatedZettel = zettelRepo.findById(zettelId).orElse(new Zettel());
			zettelDto.zettel().setChanged(LocalDateTime.now());
			// Zettel updatedZettel = new Zettel(zettelDto);
			// zettelRepo.save(updatedZettel);
		}
		return zettelDto;
	}

	public Zettel setupZettel(ZettelDtoRecord zettelDto, Note newNote) {
		Zettel newZettel = zettelDto.zettel();
		newZettel.setNote(newNote);
		newZettel.setTekst(zettelDto.tekst());
		newZettel.setAdded(LocalDateTime.now());
		newZettel.getReferences().addAll(zettelDto.references());
		// TODO BUG 00:01 wird zu 1 -> added colon, (HH:mm ...) -> NumberFormatException
		// ggf. mit if-Klausel & length & vorne mit 0 auffüllen
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmddMMyyyy");
		newZettel.setSignature(Long.parseLong(newZettel.getAdded().format(formatter)));
		return newZettel;
	}

	public void setRelationsAndSaveRefsAndZettel(Zettel newZettel, ArrayList<Reference> newRefs) {
		newRefs.forEach(reference -> reference.setOriginZettel(newZettel.getSignature()));
		newRefs.forEach(reference -> refService.saveReferenceWithZettel(reference, newZettel));
	}

	public Zettel updateOneZettelbyId(Long zettelId, ZettelDtoRecord zettelDto) {
		
		Zettel updatedZettel = zettelRepo.findById(zettelId).orElseThrow(() -> new NoSuchElementException("Zettel nicht gefunden"));
		LOG.info("\n ZettelService.updateOneZettelbyId, Zettel & DTO vorm Bearbeiten \n" +   "--->" + updatedZettel +"\n" +  "--->" + zettelDto);
//		
		// save Note
		Note newNote = noteService.saveNotewithZettel(zettelDto.note(), updatedZettel);
		// Zettel führt zu neuem Zettel
//		updatedZettel = setupZettel(zettelDto, newNote);

		ArrayList<Tag> newTags = zettelDto.tags();
		
		newTags.forEach(tag -> tagService.saveTag(tag));
		newTags.forEach(tag -> tag.getZettels().add(updatedZettel));
		zettelRepo.save(updatedZettel);
		LOG.info("\n \n ZettelService.updateOneZettelbyId,  just savedwithZettel LOGLOGLOG1st {}", updatedZettel);

		// Tekst
		Tekst newTekst = textService.saveTextwithZettel(zettelDto.tekst(), updatedZettel);
		// Author
		Author newAuthor = authorService.saveAuthorWithText(zettelDto.author(), newTekst);
		textService.saveTextWithAuthor(newTekst, newAuthor);

		// reference
		ArrayList<Reference> newRefs = new ArrayList<Reference>(zettelDto.references());
		setRelationsAndSaveRefsAndZettel(updatedZettel, newRefs);

		LOG.info(" \n --> ist in reference auch das target gespeichert? show referencE: \n" + newRefs);
		LOG.info("\n \n ZettelService.updateOneZettelbyId, am Ende .... LOGLOGLOG1st {}", updatedZettel);
		zettelDto = new ZettelDtoRecord(updatedZettel, newTekst, newNote, newAuthor, newTags, newRefs);
//		Zettel updatedZettel = zettelRepo.findById(zettelId).orElse(new Zettel());
//		Note updatedNote = noteService.saveNotewithZettel(changes.note(), updatedZettel);
//		LOG.info("\n ZettelService.updateOneZettelbyId, Zettel & DTO vorm Bearbeiten \n" +   "--->" + updatedZettel +"\n" +  "--->" + changes);
//		
//		updatedZettel.setChanged(LocalDateTime.now());
//		updatedZettel.setTopic(changes.zettel().getTopic());
//		LOG.info("\n ZettelService.updateOneZettelbyId, Zettel & DTO vorm Bearbeiten \n after setTopic \n --->" + updatedZettel +"\n --->" + changes);
//		updatedZettel.setNote(changes.note());
//		Tekst updatedTekst = changes.tekst();
//		LOG.info("\n ZettelService.updateOneZettelbyId, Zettel & DTO vorm Bearbeiten \n" +   "--->" + updatedZettel +"\n --->" + changes);
//		updatedTekst.addAssociatedAuthors(changes.author());	
//		LOG.info("\n ZettelService.updateOneZettelbyId, Zettel & DTO vorm Bearbeiten \n" +   "- -->" + updatedZettel +"\n --->" + changes);
//		updatedTekst.setTextDate(changes.tekst().getTextDate());
//		updatedTekst.setSource(changes.tekst().getSource());
////		textService.saveTextwithZettel(changes.tekst(), updatedZettel);
//		Author updatedAuthor = authorService.saveAuthorWithText(changes.author(), updatedTekst);
//		updatedZettel.setTekst(changes.tekst());
//		updatedZettel.setTags(changes.tags());
//		ArrayList<Reference> newRefs = new ArrayList<Reference>(changes.references());
//		setRelationsAndSaveRefsAndZettel(updatedZettel, newRefs);
//		LOG.info("\n ZettelService.updateOneZettelbyId, Zettel & DTO nachm Bearbeiten \n" + updatedZettel + "\n" + changes);
//		return (zettelRepo.save(updatedZettel));
//		
		return updatedZettel;

	}

	public List<Zettel> findAll() {
		return zettelRepo.findAllZettelWithTopic();
	}

	public List<Zettel> findLast10Zettel() {
		return zettelRepo.findLast10Zettel();
	}

	public Zettel findZettelById(Long zettelid) {
		return zettelRepo.findById(zettelid).orElse(new Zettel());
	}

	public List<Zettel> find10RandomZettel() {
		List<Zettel> tenRandom = new ArrayList<>();
		while (tenRandom.size() < 10) {
			Zettel randomZettel = zettelRepo.findOneRandomZettel();
			if (randomZettel != null) {
				tenRandom.add(randomZettel);
				System.out.println("\n in der while-Schleife ZettelService.find10RandomEteel: -->\n "
						+ "Größe der Liste, --> " + tenRandom.size() + "\n gefundener Zettel -->  " + randomZettel
						+ "\n Liste der zufälligenZettel --> " + tenRandom + "\n ");
			}
		}
		return tenRandom;
	}

	public void deleteOneZettelbyId(Long zettelId) {
		zettelRepo.deleteById(zettelId);
	}

	public void updateOnlyZettel(Long zettelId, ZettelDtoRecord changes) {

		Zettel updatedZettel = zettelRepo.findById(zettelId).orElseThrow(() -> new NoSuchElementException("Zettel nicht gefunden"));
		LOG.info("\n --> ZettelService.updateOnlyZettel, Zettel vorm Bearbeiten \n" +   "--->" + updatedZettel +"\n");
		updatedZettel.setTopic(changes.zettel().getTopic());
//		updatedZettel.setSignature(changes.zettel().getSignature());
		updatedZettel.setChanged(LocalDateTime.now());
		updatedZettel = zettelRepo.save(updatedZettel);
		LOG.info("\n --> ZettelService.updateOnlyZettel, Zettel nachm Bearbeiten \n" +   "--->" + updatedZettel +"\n");
		
	}

}
