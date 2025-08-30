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

    // XXX:  for now, just a method to manually purge old references.
  // TODO: add UI-Button to trigger this method (delete orphans, show dustbin) or a cron job
    @PostMapping("/references/purge")
    public String purgeOldReferences(@RequestParam(defaultValue = "90") int days) {
        int deletedCount = refService.purgeSoftDeletedReferencesOlderThanDays(days);
        return "Purged " + deletedCount + " old references";
    }
}
    