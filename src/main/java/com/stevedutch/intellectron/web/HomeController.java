package com.stevedutch.intellectron.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.service.AuthorService;
import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.TextService;
import com.stevedutch.intellectron.service.ZettelService;

@Controller
public class HomeController {

	@Autowired
	private ZettelService zettelService;
	@Autowired
	private TextService textService;
	@Autowired
	private TagService tagService;
	@Autowired
	private AuthorService authorService;
	@Autowired
	private SearchService searchService;

	private static final int TITLE_STRING_LIMIT = 23;
	private static final int TEXT_STRING_LIMIT = 55;
	private static final int RANDOM_ZETTEL_NUMBER = 4;
	private static final int RANDOM_TEKST_NUMBER = 5;
	private static final int RANDOM_TAG_NUMBER = 5;
	private static final int RANDOM_AUTHOR_NUMBER = 5;

	@GetMapping("/")
	public String showHomePage(Model model) {

		Long numberOfZettels = zettelService.countZettel();
		Long numberOfTexts = textService.countText();
		Long numberOfTags = tagService.countTags();
		Long numberOfAuthors = authorService.countAuthors();

		List<Zettel> zettels = searchService.findLast10Zettel();
		reduceZettelStrings(zettels, TITLE_STRING_LIMIT);
//		XXX ... brauche ich foch ga nicht, ich zeigged doch nur 4 oder? 
//		List<Zettel> randomZettels = searchService.findRandomZettel(RANDOM_ZETTEL_NUMBER);
//		zettelService.reduceTekstStringListElements(randomZettels, TITLE_STRING_LIMIT);
//		zettelService.reduceNoteStringListElements(randomZettels, TITLE_STRING_LIMIT);
//		zettelService.reduceTopicStringListElements(randomZettels, TITLE_STRING_LIMIT);

		List<Zettel> fourRandomZettels = searchService.findRandomZettel(RANDOM_ZETTEL_NUMBER);
		reduceZettelStrings(fourRandomZettels, TITLE_STRING_LIMIT);

		List<Tekst> fourRandomTexts = searchService.findRandomText(RANDOM_TEKST_NUMBER);
		reduceTekstStrings(fourRandomTexts, TITLE_STRING_LIMIT, TEXT_STRING_LIMIT);

		List<Tag> fiveRandomTags = searchService.findRandomTag(RANDOM_TAG_NUMBER);

		List<Author> fiveRandomAuthors = searchService.findRandomAuthor(RANDOM_AUTHOR_NUMBER);

		model.addAttribute("zettelNumber", numberOfZettels);
		model.addAttribute("textsNumber", numberOfTexts);
		model.addAttribute("tagsNumber", numberOfTags);
		model.addAttribute("authorsNumber", numberOfAuthors);
		model.addAttribute("zettels", zettels);
//		model.addAttribute("randomZettels", randomZettels);
		model.addAttribute("fourRandomZettels", fourRandomZettels);
		model.addAttribute("fourRandomTexts", fourRandomTexts);
		model.addAttribute("fiveRandomTags", fiveRandomTags);
		model.addAttribute("fiveRandomAuthors", fiveRandomAuthors);

		return "/welcome";
	}

	// TODO vermutlich nicht regelkonform (check: Ja) & abfangen von Typos
	@GetMapping("/welcome")
	public String redirectToHomePage() {
		return "/welcome";
	}

	public void reduceTekstStrings(List<Tekst> fourRandomTexts, int titleCharacterLimit, int textCharacterLimit) {
		textService.reduceTitleStringListElements(fourRandomTexts, titleCharacterLimit);
		textService.reduceTextStringListElements(fourRandomTexts, textCharacterLimit);
	}

	// XXX ev. unterschiedlich Anzahl
	public void reduceZettelStrings(List<Zettel> zettels, int numberOfCharacters) {
		zettelService.reduceTekstStringListElements(zettels, numberOfCharacters);
		zettelService.reduceNoteStringListElements(zettels, numberOfCharacters);
		zettelService.reduceTopicStringListElements(zettels, numberOfCharacters);
	}

}
