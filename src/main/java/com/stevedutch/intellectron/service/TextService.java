package com.stevedutch.intellectron.service;

import java.time.LocalDate;
import java.util.NoSuchElementException;

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
	@Autowired
	private TextRepository textRepo;
	@Autowired
	private ZettelService zettelService;

	public Tekst saveText(Tekst tekst) {
		Tekst existingText = textRepo.findByText(tekst.getText());
		if (existingText != null) {
			tekst.setTextId(existingText.getTextId());
		}
		checkTextDate(tekst);
		return textRepo.save(tekst);
	}

	public Tekst saveTextWithAuthor(Tekst tekst, Author author) {
		// XXX wäre korrekt, wenn ich weitere Autoren hinzufügen wollte
		// for now I'm mocking that just one Author is valid
		// tekst.getAssociatedAuthors().add(author); Daher aber;
		tekst.setOneAssociatedAuthors(author);
		checkTextDate(tekst);
		tekst = textRepo.save(tekst);
		return tekst;
	}

	public Tekst saveTextwithZettel(Tekst tekst, Zettel zettel) {
		Tekst actualTekst = textRepo.findByText(tekst.getText());
		if (actualTekst == null) {
			actualTekst = new Tekst(tekst.getText());
		}
		tekst.getZettels().add(zettel);
		checkTextDate(tekst);

		return textRepo.save(tekst);

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

		Zettel zettel = zettelService.findZettelById(zettelId);
//		Tekst oldTekst = zettelService.findZettelById(zettelId).getTekst();
		// XXX Tekst vom front end kommt nur mit Text, daher anhand dessen den Tekst
		// finden, oder -falls nicht existent -
		// oder als neues Text mit dem gegebenen Text einricfhten --> Vermeiden von
		// Doubletten in der Datenbank & Objekt
		Tekst updatedTekst = findByText(tekst.getText());
		LOG.info("\n -->TekstService.updateTekst, Tekst " + updatedTekst);
		if (updatedTekst == null) {
			updatedTekst = new Tekst(tekst.getText());
//            updatedTekst.setText(tekst.getText());
//            updatedTekst.setTitle(tekst.getTitle());
//            updatedTekst.setTextDate(tekst.getTextDate());
//            updatedTekst.setSource(tekst.getSource());
//            textRepo.save(updatedTekst);
		}
		updatedTekst.setTitle(tekst.getTitle());
		updatedTekst.setTextDate(tekst.getTextDate());
		checkTextDate(updatedTekst);
		updatedTekst.setSource(tekst.getSource());

		textRepo.save(updatedTekst);
		saveTextwithZettel(updatedTekst, zettelService.findZettelById(zettelId));
		zettel.setTekst(updatedTekst);
		LOG.info("\n -->TekstService.updateTekst, Tekst nachm Bearbeiten \n" + "--->" + updatedTekst + "\n"
				+ updatedTekst.getZettels());
		return updatedTekst;

	}

	public Tekst findById(Long textId) {
		return textRepo.findById(textId)
				.orElseThrow(() -> new NoSuchElementException("Tekst mit dieser ID inexistent"));

	}

	public Tekst findByText(String text) {
		/// TODO null check
		return textRepo.findByText(text);
	}

	/**
	 * checks if the text is already in the database, if yes, it returns it, if not,
	 * it returns the given one
	 * 
	 * @param tekst
	 * @return tekst (the given one or from db)
	 */
	public Tekst checkForExistingTekst(Tekst tekst) {
		Tekst existingText = textRepo.findByText(tekst.getText());
		if (existingText != null) {
			return existingText;
		}
		return tekst;

	}

}
