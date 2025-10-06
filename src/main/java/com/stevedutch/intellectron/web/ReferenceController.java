package com.stevedutch.intellectron.web;

import com.stevedutch.intellectron.service.ReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReferenceController {

	private final ReferenceService refService;

	@Autowired
	public ReferenceController(ReferenceService refService) {
		this.refService = refService;
	}

	/**
	 * purge old references and orphans manually
	 * 
	 * @param days - delete orphans older than
	 * @return message with number of deleted references
	 */
	@PostMapping("/references/purge")
	public String purgeOldReferences(@RequestParam(defaultValue = "90") int days) {
		int deletedCount = refService.purgeSoftDeletedReferencesOlderThanDays(days);
		return "Purged " + deletedCount + " old references";
	}
}
