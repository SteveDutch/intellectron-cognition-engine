package com.stevedutch.intellectron.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;

// TODO rename functions
@Service
public class TextManipulationService {

	/**
	 *reduces the string of title & text of all elements of a list of tekst to the chosen number of characters
	 *
	 * @param texts
	 * @param titleCharacterLimit
	 * @param textCharacterLimit
	 */
	public void reduceTekstStrings(List<Tekst> texts, int titleCharacterLimit, int textCharacterLimit) {
		reduceTitleStringListElements(texts, titleCharacterLimit);
		reduceTextStringListElements(texts, textCharacterLimit);
	}

	// XXX ev. unterschiedliche Anzahl
	/**
	 * reduces the string size of zettel.Tekst.text, zettel.note, zettel.topic of  all elements of a list of zettel to chosen number
	 * of characters
	 * 
	 * @param zettels            - List of zettel objects
	 * @param numberOfCharacters - number of reduced characters
	 * 
	 */
	public void reduceZettelStrings(List<Zettel> zettels, int numberOfCharacters) {
		reduceTekstStringListElements(zettels, numberOfCharacters);
		reduceNoteStringListElements(zettels, numberOfCharacters);
		reduceTopicStringListElements(zettels, numberOfCharacters);
	}

	/**
	 * reduces the size of each Tekst.text element of a list of Zettel to chosen
	 * number of characters
	 * 
	 * @param zettels  List of zettel objects
	 * @param reducedLength  an int for the number of reduced characters
	 * 
	 */
	public void reduceTekstStringListElements(List<Zettel> zettels, int reducedLength) {
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

	public void reduceTopicStringListElements(List<Zettel> zettels, int reducedLength) {
		zettels.forEach(x -> {
			if (x.getTopic() != null) {
				if (x.getTopic().length() > reducedLength) {
					x.setTopic(x.getTopic().substring(0, reducedLength));
				}
			}
		});
	}

	/**
	 * reduces the size of each Tekst.text element of a list of Tekst to chosen
	 * length
	 * 
	 * @param tekster       - List of tekst objects
	 * @param reducedLength . number of reduced characters
	 */
	public void reduceTextStringListElements(List<Tekst> tekster, int reducedLength) {
		tekster.forEach(x -> {
			if (x.getText().length() > reducedLength) {
				x.setText(x.getText().substring(0, reducedLength));
			}
		});
	}

	public void reduceTitleStringListElements(List<Tekst> tekster, int reducedLength) {
		tekster.forEach(x -> {
			if (x.getTitle().length() > reducedLength) {
				x.setTitle(x.getTitle().substring(0, reducedLength));
			}
		});
	}

}
