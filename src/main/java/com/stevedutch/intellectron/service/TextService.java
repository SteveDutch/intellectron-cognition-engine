package com.stevedutch.intellectron.service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TextRepository;

@Service
public class TextService {

	private static final Logger LOG = LoggerFactory.getLogger(TextService.class);

	private static final String DUMMY_TITLE = "notext";
	private static final String DUMMY_TEXT_CONTENT = "placeholder for no text";
	private static final String DUMMY_SOURCE = "someone's brain / system generated";
	@Autowired
	private TextRepository textRepo;
	@Autowired
	private SearchService searchService;

	public Tekst saveText(Tekst tekst) {
		Tekst existingText = textRepo.findByText(tekst.getText());
		if (existingText != null) {
			tekst.setTextId(existingText.getTextId());
		}
		checkTextDate(tekst);
		tekst.setText(tekst.getText().strip());
		return textRepo.save(tekst);
	}

	public Tekst saveTextWithAuthor(Tekst tekst, Author author) {
		// XXX wäre korrekt, wenn ich weitere Autoren hinzufügen wollte
		// for now I'm mocking that just one Author is valid
		// tekst.getAssociatedAuthors().add(author); Daher aber;
		tekst.setOneAssociatedAuthors(author);
		tekst = saveText(tekst);
		return tekst;
	}

	public Tekst saveTextwithZettel(Tekst tekst, Zettel zettel) {
		Tekst actualTekst = textRepo.findByText(tekst.getText());
		if (actualTekst == null) {
			actualTekst = new Tekst(tekst.getText());
		}
		tekst.getZettels().add(zettel);
		return saveText(tekst);

	}

	/**
	 * Checks if textDate exists, if not, it sets LocalDate.EPOCH
	 * 
	 * @param tekst
	 */
	public void checkTextDate(Tekst tekst) {
		if (tekst.getTextDate() == null) {
			tekst.setTextDate(LocalDate.EPOCH);
		}
	}

	/**
	 * checks if the text is already in the database, if not, it creates a new one.
	 * Afterwards, it adds the zettel to the text.
	 * 
	 * @param tekst  object
	 * @param zettel object
	 * @return tekst object, with the zettel added
	 */
	public Tekst connectTextwithZettel(Tekst tekst, Zettel zettel) {
		Tekst actualTekst = textRepo.findByText(tekst.getText());
		if (actualTekst == null) {
			actualTekst = new Tekst(tekst.getText());
		}
		tekst.getZettels().add(zettel);

		return tekst;

	}

	public Tekst connectTextWithAuthor(Tekst tekst, Author author) {
		// XXX wäre korrekt, wenn ich weitere Autoren hinzufügen wollte
		// for now I'm mocking that just one Author is valid
		// tekst.getAssociatedAuthors().add(author); Daher aber;
		tekst.setOneAssociatedAuthors(author);
		tekst.setOneAssociatedAuthors(author);
		return tekst;
	}

	public Tekst updateTekst(Long zettelId, Tekst tekst) {

		Zettel zettel = searchService.findZettelById(zettelId);
//		Tekst oldTekst = zettelService.findZettelById(zettelId).getTekst();
		// XXX Tekst vom front end kommt nur mit Text, daher anhand dessen den Tekst
		// finden, oder -falls nicht existent -
		// oder als neues Text mit dem gegebenen Text einricfhten --> Vermeiden von
		// Doubletten in der Datenbank & Objekt
		Tekst updatedTekst = searchService.findByText(tekst.getText());
		LOG.info("\n -->TekstService.updateTekst, Tekst " + updatedTekst);
		if (updatedTekst == null) {
			updatedTekst = new Tekst(tekst.getText());
		}
		updatedTekst.setTitle(tekst.getTitle());
		updatedTekst.setTextDate(tekst.getTextDate());
		
		updatedTekst.setSource(tekst.getSource());
		
		saveText(updatedTekst);
		
		// saveTextwithZettel(updatedTekst, searchService.findZettelById(zettelId));
		saveTextwithZettel(updatedTekst, zettel);
		zettel.setTekst(updatedTekst);
		LOG.info("\n -->TekstService.updateTekst, Tekst nachm Bearbeiten \n" + "--->" + updatedTekst + "\n"
				+ updatedTekst.getZettels());
		return updatedTekst;

	}

	/**
	 * Checks if a text exists in the database and handles empty text cases.
	 * 
	 * If the input text is null or blank:
	 * - Returns an existing dummy text if found
	 * - Creates and returns a new dummy text if none exists
	 * 
	 * If the input text is not blank:
	 * - Returns an existing text if found in database
	 * - Returns the prepared (stripped) input text if it's new
	 * 
	 * @param tekst The text to check, can be null or contain blank text
	 * @return Tekst The existing text from database, the prepared input text, or a dummy text
	 */
	public Tekst checkForExistingTekst(Tekst tekst) {
		if (tekst == null || tekst.getText() == null || tekst.getText().isBlank()) {
			// Input is blank, try to find the specific dummy Tekst
			Tekst specificDummy = textRepo.findByTitleAndText(DUMMY_TITLE, DUMMY_TEXT_CONTENT);

			if (specificDummy != null) {
				return specificDummy; // Found our specific dummy
			} else {
				// Specific dummy Tekst not found, create and save it
				Tekst newDummy = new Tekst(DUMMY_TEXT_CONTENT);
				newDummy.setTitle(DUMMY_TITLE);
				newDummy.setSource(DUMMY_SOURCE);
				return saveText(newDummy);
			}
		} else {
			// Input is not blank
			String strippedText = tekst.getText().strip();
			Tekst existingText = textRepo.findByText(strippedText); // Find by actual content
			if (existingText != null) {
				return existingText;
			} else {
				// Tekst is new and not blank. Prepare it.
				tekst.setText(strippedText);
				checkTextDate(tekst); // Ensure date is set
				// This tekst (if new and non-blank) will be saved by a subsequent call 
				// in the service chain, e.g., through ZettelService calling saveTextWithAuthor or similar.
				// So, we return the modified, unsaved Tekst object here if it's a new, non-blank one.
				return tekst;
			}
		}
	}
	
	/**
	 * returns the number of all text in the database
	 * 
	 * @return   number of all texts
	 */
	public Long countAllText() {
		return textRepo.count();
	}





}
