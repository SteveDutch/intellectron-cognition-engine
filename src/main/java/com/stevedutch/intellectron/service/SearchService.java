package com.stevedutch.intellectron.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.exception.SearchTermNotFoundException;
import com.stevedutch.intellectron.repository.TextRepository;
import com.stevedutch.intellectron.repository.ZettelRepository;
import com.stevedutch.intellectron.web.ZettelController;

@Service
public class SearchService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ZettelController.class);
	
	@Autowired
	private ZettelRepository zettelRepo;
	@Autowired
	private TextRepository textRepo;
	@Autowired
    private AuthorService authorService;

	public List<Zettel> findZettelByNoteFragment(String noteFragment) {

		validateSearchString(noteFragment);
		List<Zettel> result = zettelRepo.findZettelByNoteFragment(noteFragment);
		return result;
	}

	public List<Zettel> findZettelByTextFragment(String textFragment) {
		
		validateSearchString(textFragment);
		List<Zettel> result = zettelRepo.findZettelByTextFragment(textFragment);
		return result;
	}

	public List<Tekst> findTekstByTextFragment(String textFragment) {
		
		validateSearchString(textFragment);
		List<Tekst> result = textRepo.findTekstByTextFragment(textFragment);
		return result;
	}
	
	public Zettel findOneZettelByNote(String noteText) {
		validateSearchString(noteText);
		Zettel result = zettelRepo.findOneZettelByNote(noteText);
        return result;
	}
	
		/**
		 *Takes a String, and then uses the last element of the string as authors family name 
		 *to find authors with similar names.
		 * @param name String
		 * @return List<Author>
		 */
   public List<Author> findAuthorByName(String name) {
	   validateSearchString(name);
	   String[] nameParts = name.split("\\s+");
	   String lastName = nameParts[nameParts.length-1];
	   LOG.info("\n Now searching for author family name: " + lastName);
	   List<Author> result = authorService.findAuthorByLastNameLike(lastName);
	   return result;
	}
   	/**
   	 * Validates a search term, if it is null or empty it throws a SearchTermNotFoundException.
   	 * @param SearchTerm
   	 */
	public void validateSearchString(String SearchTerm) {
		if (SearchTerm == null || SearchTerm.isEmpty()) {
			LOG.info("\n NO SearchTerm  ");
			throw new SearchTermNotFoundException("no search term provided");
		}
	}

 

}
