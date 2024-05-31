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

	@Lazy
	@Autowired
	private TagService tagService;

	@Autowired
	private NoteService noteService;

	// ohne Lazy gibt 's einen circular reference error,
	// diesen mit Lazy zu beseitigen, feels like cheating
	// (führt dazu, dass betreffendes Feld nicht beim Start, sondern erst beim Auduf
	// initialisiert wird)
	@Lazy
	@Autowired
	private TextService textService;

	@Lazy
	@Autowired
	private AuthorService authorService;

	@Lazy
	@Autowired
	private ReferenceService refService; 
	
	@Lazy
    @Autowired
    private SearchService searchService;

	// vXXX vielleicht ein bisschen groß, diese Funktion
	/**
	 * takes a DTOrecord. First it checks (via zettel.topic & Note) 
	 * if an Zettel is already existing, if not it creates a new zettel. Afterwards it connects everything 
	 * and saves the zettel in the db afterwards.
	 *  
	 * @param zettelDto
	 * @return zettel Dto
	 */
	public ZettelDtoRecord createZettel(ZettelDtoRecord zettelDto) {

		Zettel newZettel = searchService.findOneZettelByNote(zettelDto.note().getNoteText());
		if (newZettel == null) {
			newZettel = new Zettel();
			
		}
			// connect to Note
			Note newNote = noteService.connectNotewithZettel(zettelDto.note(), newZettel);
		
			// check, if tekst is already existing 
			Tekst newTekst = textService.checkForExistingTekst(zettelDto.tekst());
			
			// Zettel
			newZettel = setupZettel(newZettel, zettelDto, newNote, newTekst);
				LOG.info("\n hat Tekst hier schon eine ID? (nach setUpZettel) \n" + newZettel);
				
			ArrayList<Tag> newTags = tagService.connectTagsWithZettel(zettelDto.tags(), newZettel);
			LOG.info("\n ZettelService.createZettel,  just savedTagswithZettel LOGLOGLOG1st {}", newZettel);

			// Tekst
			newTekst = textService.connectTextwithZettel(newTekst, newZettel);
			// Author
			Author newAuthor = authorService.connectAuthorWithText(zettelDto.author(), newTekst);
			
			textService.connectTextWithAuthor(newTekst, newAuthor);

			// reference
			ArrayList<Reference> newRefs = new ArrayList<Reference>(zettelDto.references());
			setRelationsRefsWithZettel(newZettel, newRefs);

			LOG.info(" \n --> ist in reference auch das target gespeichert? show referencE: \n" + newRefs);
			
			saveZettel(newZettel);
			authorService.saveAuthor(newAuthor);

			return zettelDto = new ZettelDtoRecord(newZettel, newTekst, newNote, newAuthor, newTags, newRefs);

		} 	 
	

	/**
	 * Takes a zettel object, a recordDTO & a note object.
	 * then it sets up the zettel object with the DTO-elements.
	 * @param zettelDto
	 * @param newNote
	 * @param newTekst 
	 * @return zettel object
	 */
	public Zettel setupZettel(Zettel zettel, ZettelDtoRecord zettelDto, Note newNote, Tekst newTekst) {
		
		zettel.setTopic(zettelDto.zettel().getTopic());
		zettel.setTags(zettelDto.tags());
		zettel.setNote(newNote);
		zettel.setTekst(newTekst);
		zettel.setAdded(LocalDateTime.now());
		zettel.setChanged(LocalDateTime.now());
		zettel.getReferences().addAll(zettelDto.references());
		// TODO BUG 00:01 wird zu 1 -> added colon, (HH:mm ...) -> NumberFormatException
		// ggf. mit if-Klausel & length & vorne mit 0 auffüllen
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmddMMyyyy");
		zettel.setSignature(Long.parseLong(zettel.getAdded().format(formatter)));
		return zettel;
	}

	public void setRelationsRefsWithZettel(Zettel newZettel, ArrayList<Reference> newRefs) {
		newRefs.forEach(reference -> reference.setOriginZettel(newZettel.getSignature()));
		newRefs.forEach(reference -> reference.getZettels().add(newZettel));
	}

	public Zettel updateOneZettelbyId(Long zettelId, ZettelDtoRecord zettelDto) {

		Zettel updatedZettel = zettelRepo.findById(zettelId)
				.orElseThrow(() -> new NoSuchElementException("Zettel nicht gefunden"));
		LOG.info("\n ZettelService.updateOneZettelbyId, Zettel & DTO vorm Bearbeiten \n" + "--->" + updatedZettel + "\n"
				+ "--->" + zettelDto);
//		
		// save Note
		Note newNote = noteService.saveNotewithZettel(zettelDto.note(), updatedZettel);
		// Zettel führt zu neuem Zettel
//		updatedZettel = setupZettel(zettelDto, newNote);

		ArrayList<Tag> newTags = zettelDto.tags();

		newTags.forEach(tag -> tagService.saveTag(tag));
		newTags.forEach(tag -> tag.getZettels().add(updatedZettel));
		saveZettel(updatedZettel);
		LOG.info("\n \n ZettelService.updateOneZettelbyId,  just savedwithZettel LOGLOGLOG1st {}", updatedZettel);

		// Tekst
		
		Tekst newTekst = textService.updateTekst(zettelId, zettelDto.tekst());
		textService.connectTextwithZettel(zettelDto.tekst(), updatedZettel);
		// Author
		Author newAuthor = authorService.connectAuthorWithText(zettelDto.author(), newTekst);
		textService.connectTextWithAuthor(newTekst, newAuthor);

		// reference
		ArrayList<Reference> newRefs = new ArrayList<Reference>(zettelDto.references());
		setRelationsRefsWithZettel(updatedZettel, newRefs);

		LOG.info(" \n --> ist in reference auch das target gespeichert? show referencE: \n" + newRefs);
		LOG.info("\n \n ZettelService.updateOneZettelbyId, am Ende .... LOGLOGLOG1st {}", updatedZettel);
		zettelDto = new ZettelDtoRecord(updatedZettel, newTekst, newNote, newAuthor, newTags, newRefs);
		return updatedZettel;

	}

	public void updateOnlyZettel(Long zettelId, ZettelDtoRecord changes) {
	
			Zettel updatedZettel = zettelRepo.findById(zettelId)
					.orElseThrow(() -> new NoSuchElementException("Zettel nicht gefunden"));
			LOG.info("\n --> ZettelService.updateOnlyZettel, Zettel vorm Bearbeiten \n" + "--->" + updatedZettel + "\n");
			updatedZettel.setTopic(changes.zettel().getTopic());
	//		updatedZettel.setSignature(changes.zettel().getSignature());
			updatedZettel = saveZettel(updatedZettel);
			LOG.info("\n --> ZettelService.updateOnlyZettel, Zettel nachm Bearbeiten \n" + "--->" + updatedZettel + "\n");
	
		}


	public void deleteOneZettelbyId(Long zettelId) {
		zettelRepo.deleteById(zettelId);
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
			}
		}
		return tenRandom;
	}

	/**
	 * Saves the zettel and sets its attribute changed to LocalDateTime.now()
	 * @param zettel
	 * @return zettel with set change date
	 */
	public Zettel saveZettel(Zettel zettel) {
		zettel.setChanged(LocalDateTime.now());
		return zettelRepo.save(zettel);
	}

	public List<Zettel> findZettelByTag(String tagText) {
			Tag searchTag = tagService.findTagByText(tagText);
			return zettelRepo.findZettelByTags(searchTag);
	}
	/**
	 * searches for Zettel by the given fragment of the topic
	 * @param topicFragment the fragment of the topic
	 * @return List of Zettel could include null!
	 */
	public List<Zettel> findZettelByTopicFragment(String topicFragment) {
		List<Zettel> result = zettelRepo.findZettelByTopicFragment(topicFragment);
		return result;
	}
}
