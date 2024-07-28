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
import com.stevedutch.intellectron.exception.EmptyZettelException;
import com.stevedutch.intellectron.exception.TopicTooLongException;
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

	private static final int MAX_LENGTH = 255;

	// vXXX vielleicht ein bisschen groß, diese Funktion
	/**
	 * takes a DTOrecord. First it checks (via zettel.topic & Note) if an Zettel is
	 * already existing, if not it creates a new zettel. Afterwards it connects
	 * everything and saves the zettel in the db afterwards.
	 * 
	 * @param zettelDto
	 * @return zettel Dto
	 */
	public ZettelDtoRecord createZettel(ZettelDtoRecord zettelDto) {

		noteService.noteEmptyOrBlankCheck(zettelDto.note());

		topicEmptyOrBlankCheck(zettelDto.zettel());

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
	 * checks if topic is empty or blank. If true, it throws an exception
	 * 
	 * @param zettelDto
	 */
	public void topicEmptyOrBlankCheck(Zettel zettel) {
		if ( zettel.getTopic() == null|| zettel.getTopic().isBlank()
				|| zettel.getTopic().isEmpty()) {
			throw new EmptyZettelException("this zettel's topic is empty");
		}
	}

	/**
	 * Checks if topic is longer than 255 characters. If true, it throws an
	 * exception
	 * 
	 * @param topic
	 */
	public void checkTopicLength(String topic) {
		if (topic.length() > MAX_LENGTH) {
			throw new TopicTooLongException("this zettel's topic is too long");
		}
	}

	/**
	 * Takes a zettel object, a recordDTO & a note object. then it sets up the
	 * zettel object with the DTO-elements.
	 * 
	 * @param zettelDto
	 * @param newNote
	 * @param newTekst
	 * @return zettel object
	 */
	public Zettel setupZettel(Zettel zettel, ZettelDtoRecord zettelDto, Note newNote, Tekst newTekst) {

		checkTopicLength(zettelDto.zettel().getTopic());
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

		topicEmptyOrBlankCheck(zettelDto.zettel());

		checkTopicLength(zettelDto.zettel().getTopic());
		Zettel updatedZettel = zettelRepo.findById(zettelId)
				.orElseThrow(() -> new NoSuchElementException("Zettel nicht gefunden"));
		LOG.info("\n ZettelService.updateOneZettelbyId, Zettel & DTO vorm Bearbeiten \n" + "--->" + updatedZettel + "\n"
				+ "--->" + zettelDto);
		// save Note
		Note newNote = noteService.saveNotewithZettel(zettelDto.note(), updatedZettel);

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
		topicEmptyOrBlankCheck(changes.zettel());
		checkTopicLength(changes.zettel().getTopic());
		Zettel updatedZettel = zettelRepo.findById(zettelId)
				.orElseThrow(() -> new NoSuchElementException("Zettel nicht gefunden"));
		LOG.info("\n --> ZettelService.updateOnlyZettel, Zettel vorm Bearbeiten \n" + "--->" + updatedZettel + "\n");
		updatedZettel.setTopic(changes.zettel().getTopic());
		updatedZettel = saveZettel(updatedZettel);
		LOG.info("\n --> ZettelService.updateOnlyZettel, Zettel nachm Bearbeiten \n" + "--->" + updatedZettel + "\n");

	}

	public void deleteOneZettelbyId(Long zettelId) {
		zettelRepo.deleteById(zettelId);
	}

	/**
	 * Saves the zettel and sets its attribute changed to LocalDateTime.now() and
	 * strips the topic of leading and trailing whitespaces
	 * 
	 * @param zettel
	 * @return zettel with set change date
	 */
	public Zettel saveZettel(Zettel zettel) {
		zettel.setChanged(LocalDateTime.now());
		zettel.setTopic(zettel.getTopic().strip());
		return zettelRepo.save(zettel);
	}

	/**
	 * reduces the size of each Tekst.text element of a list of Zettel to chosen
	 * characters
	 * 
	 * @param zettels - List of zettel objects
	 * @param int     - number of reduced characters
	 * 
	 */
	public void reduceStringListElements(List<Zettel> zettels, int reducedLength) {
		zettels.forEach(x -> {
			if (x.getTekst().getText().length() > reducedLength) {
				x.getTekst().setText(x.getTekst().getText().substring(0, reducedLength));
			}
		});
	}

	/**
	 * reduces the size of each zettel.topic element of a list of Zettel to chosen
	 * number of characters
	 * 
	 * @param zettels - List of zettel objects
	 * @param int     - number of reduced characters
	 * 
	 */
	public void reduceNoteStringListElements(List<Zettel> zettels, int reducedLength) {
		zettels.forEach(x -> {
			if (x.getNote() != null) {
				if (x.getNote().getNoteText().length() > reducedLength) {
					x.getNote().setNoteText(x.getNote().getNoteText().substring(0, reducedLength));
				}
			}
		});
	}
}
