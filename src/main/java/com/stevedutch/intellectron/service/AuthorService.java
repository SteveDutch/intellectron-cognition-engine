package com.stevedutch.intellectron.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.repository.AuthorRepository;

@Service
public class AuthorService  {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorService.class);

	@Autowired
	private AuthorRepository authorRepo;
	@Autowired
	private TextService textService;
	@Autowired
	private SearchService searchService;

	/**
	 * saves the author to the database after checking if Author with that names is
	 * in the database. If in db existing, the author will just be returned
	 * 
	 * @param author
	 * @return saved author if not found in db, otherwise the given author will just
	 *         be returned
	 */
	public Author saveAuthorIfUnknown(Author author) {

		if (authorRepo.findByAuthorFirstNameAndAuthorFamilyName(author.getAuthorFirstName(),
				author.getAuthorFamilyName()) == null) {
			author.setAuthorFirstName(author.getAuthorFirstName());
			author.setAuthorFamilyName(author.getAuthorFamilyName());
			return saveAuthor(author);
		}

		return author;

	}

	public Author saveAuthorWithText(Author author, Tekst tekst) {
		LOG.info(author.toString());
		tekst = searchService.findByText(tekst.getText());
		Author givenAuthor = authorRepo.findByAuthorFirstNameAndAuthorFamilyName(author.getAuthorFirstName(),
				author.getAuthorFamilyName());
		if (givenAuthor == null) {
			Optional.ofNullable(author).ifPresentOrElse(this::saveAuthorIfUnknown,
					() -> new Author("Ignotus", "Unbekannt"));
			author.getTexts().add(tekst);
			textService.saveTextWithAuthor(tekst, author);
			return saveAuthor(author);
		}
		givenAuthor.getTexts().add(tekst);
		textService.saveTextWithAuthor(tekst, givenAuthor);
		return authorRepo.save(givenAuthor);
	}

	/**
	 * saves the author to the database & strips the author's name
	 * 
	 * @param author
	 * @return saved author/new author
	 */
	public Author saveAuthor(Author author) {
		author.setAuthorFirstName(author.getAuthorFirstName().strip());
		author.setAuthorFamilyName(author.getAuthorFamilyName().strip());
		return authorRepo.save(author);
	}

	/**
	 * connects given author with given tekst. If
	 * 
	 * @param author
	 * @param tekst
	 * @return author - connected to tekst
	 */
	public Author connectAuthorWithText(Author author, Tekst tekst) {
		LOG.info(author.toString());
//    	tekst = textService.findByText(tekst.getText());
		Author givenAuthor = authorRepo.findByAuthorFirstNameAndAuthorFamilyName(author.getAuthorFirstName(),
				author.getAuthorFamilyName());
		if (givenAuthor == null) {
			givenAuthor = author;

		}
		givenAuthor.getTexts().add(tekst);
		tekst.setOneAssociatedAuthors(givenAuthor);
		return givenAuthor;
	}

	public Long countAuthors() {
		return authorRepo.count();
	}

}
