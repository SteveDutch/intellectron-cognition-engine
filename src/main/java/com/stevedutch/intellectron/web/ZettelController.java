package com.stevedutch.intellectron.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.service.AuthorService;
import com.stevedutch.intellectron.service.NoteService;
import com.stevedutch.intellectron.service.ReferenceService;
import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.TextService;
import com.stevedutch.intellectron.service.ValidationService;
import com.stevedutch.intellectron.service.ZettelService;
import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tekst;

import java.util.Map;
import com.stevedutch.intellectron.service.feedback.TextSavingFeedbackHolder;

@Controller
public class ZettelController {

	private static final Logger LOG = LoggerFactory.getLogger(ZettelController.class);
	@Autowired
	private ZettelService zettelService;
	@Autowired
	private NoteService noteService;
	@Autowired
	private TextService textService;
	@Autowired
	private TagService tagService;
	@Autowired
	private AuthorService authorService;
	@Autowired
	private ReferenceService refService;
	@Autowired
	private SearchService searchService;
	@Autowired
	private ValidationService validationService;
	@Autowired
	private TextSavingFeedbackHolder feedbackHolder;
	
		@GetMapping("/zettel/{zettelId}")
		public String showZettel(ModelMap model, @PathVariable Long zettelId) {
	
			Zettel zettel = searchService.findZettelById(zettelId);
	
	//		String formattedText = zettel.getTekst().getText();
	//		zettel.getTekst().setText(formattedText.replace("\n", "<br>"));
			model.put("zettel", zettel);
			model.put("note", zettel.getNote());
			model.put("tekst", zettel.getTekst());
			model.put("author", zettel.getTekst().getAssociatedAuthors());
			model.put("tags", zettel.getTags());
			model.put("references", zettel.getReferences());
			return "/zettel";
		}
	
		@PostMapping("/zettel/{zettelId}")
		public String updateOneZettel(@PathVariable Long zettelId, @RequestBody String json, RedirectAttributes redirectAttributes) 
				throws JsonMappingException, JsonProcessingException {
	
			LOG.info("\n im Controller updateOneZettel, JSON = " + json);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			ZettelDtoRecord changes = objectMapper.readValue(json, ZettelDtoRecord.class);
	
			LOG.info(" --> zettelController.updateOneZettel, nach json to zettelDTO, vorm saven: --> zettelDto = \n "
					+ changes);
	
			Author validatedAuthor = validationService.ensureAuthorNames(changes.author());
	
			Tekst resultingTekst = null;
			String textMessage = "";

			try {
				zettelService.updateOnlyZettel(zettelId, changes);
				noteService.updateNote(zettelId, changes.note());
				
				resultingTekst = textService.updateTekst(zettelId, changes.tekst());
				
				tagService.updateTags(zettelId, changes.tags());
				
				if (resultingTekst != null) {
					authorService.saveAuthorWithText(validatedAuthor, resultingTekst); 
				} else {
					 LOG.warn("Skipping author association for Zettel {} because text processing returned null.", zettelId);
				}
				
				refService.updateReferences(zettelId, changes.references());

			} catch (IllegalArgumentException e) {
				LOG.warn("Failed to update text for Zettel {}: {}", zettelId, e.getMessage());
				// Feedback holder should have been set by TextService
			} catch (Exception e) {
				LOG.error("Unexpected error updating Zettel {}", zettelId, e);
				feedbackHolder.setMessage("An unexpected error occurred during the update.");
			}

			// --- Retrieve feedback message from holder ---
			textMessage = feedbackHolder.getMessage();
			LOG.info("Retrieved feedback message from holder: '{}'", textMessage);

			// --- Construct final message ---
			String finalMessage = "Zettel updated successfully.";
			String messageType = "INFO"; 

			if (textMessage != null && !textMessage.isBlank()) { 
				 finalMessage += " " + textMessage;
				 if (textMessage.contains("similar")) {
					 messageType = "SIMILAR_MATCH";
				 } else if (textMessage.contains("identical")) {
					 messageType = "EXACT_MATCH";
				 } else if (textMessage.contains("new entry")) {
					 messageType = "NEW_ENTRY";
				 } else if (textMessage.contains("Error") || textMessage.contains("No text")) {
					 messageType = "ERROR";
				 }
			} else {
				 finalMessage += " (Text status unknown).";
				 messageType = "UNKNOWN";
			}

			LOG.info("Adding flash attribute 'updateMessage': {}", finalMessage);
			LOG.info("Adding flash attribute 'updateMessageType': {}", messageType);
			redirectAttributes.addFlashAttribute("updateMessage", finalMessage); 
			redirectAttributes.addFlashAttribute("updateMessageType", messageType); 

			return "redirect:/zettel/" + zettelId; 
		}

	@PostMapping("/zettel/{zettelId}/delete")
	public String deleteOneZettel(@PathVariable Long zettelId) {
		LOG.info("\n im deleteZettel = " + zettelId);
		zettelService.deleteOneZettelbyId(zettelId);

		return "redirect:/welcome";
	}

}