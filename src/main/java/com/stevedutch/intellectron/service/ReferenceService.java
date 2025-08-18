package com.stevedutch.intellectron.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.ReferenceRepository;

@Service
public class ReferenceService {

	private static final Logger LOG = LoggerFactory.getLogger(ZettelService.class);

	@Autowired
	private ReferenceRepository refRepo;

	@Autowired
	private ZettelService zettelService;
	@Autowired
	private SearchService searchService;

	public Reference saveReference(Reference reference) {
		return refRepo.save(reference);
	}

	public void updateReferences(Long zettelId, ArrayList<Reference> references) {

		LOG.info("Start of updateReferences ; ---> " + references);
		
		// Get the source zettel
		Zettel sourceZettel = searchService.findZettelById(zettelId);
		
        // Load existing references for this source (active only due to @Where)
        List<Reference> existing = refRepo.findAllBySourceZettelId(sourceZettel.getZettelId());
        Map<Long, Reference> existingByTarget = new HashMap<>();
        for (Reference r : existing) {
            existingByTarget.put(r.getTargetZettelId(), r);
        }

        // Build desired map by target ID
        Map<Long, Reference> desiredByTarget = new HashMap<>();
        for (Reference reference : references) {
            if (reference.getTargetZettelId() == null) {
                LOG.warn("Skipping reference with null target zettel ID");
                continue;
            }
            desiredByTarget.put(reference.getTargetZettelId(), reference);
        }

        // Soft-delete removed references
        for (Reference oldRef : existing) {
            if (!desiredByTarget.containsKey(oldRef.getTargetZettelId())) {
                LOG.info("Soft-deleting removed reference: {}", oldRef);
                refRepo.delete(oldRef); // triggers @SQLDelete (soft)
            }
        }

        // Clear current associations in-memory to re-add desired ones
		sourceZettel.getReferences().clear();
		
        // Upsert desired references
		for (Reference reference : references) {
			// Set the source zettel
			reference.setSourceZettelId(sourceZettel.getZettelId());
			
            // Validate target exists
			try {
				Long targetZettelId = searchService.findZettelById(reference.getTargetZettelId()).getZettelId();
				reference.setTargetZettelId(targetZettelId);
			} catch (Exception e) {
				LOG.error("Target zettel not found with ID: " + reference.getTargetZettelId(), e);
				continue; // Skip this reference if target zettel doesn't exist
			}
			
			// Check if this reference already exists
			Reference existingReference = refRepo.findBySourceZettelIdAndTargetZettelId(
				sourceZettel.getZettelId(), reference.getTargetZettelId());
			
			if (existingReference != null) {
				// Update existing reference
				existingReference.setType(reference.getType());
				existingReference.setConnectionNote(reference.getConnectionNote());
				refRepo.save(existingReference);
				sourceZettel.getReferences().add(existingReference);
				LOG.info("Updated existing reference: " + existingReference);
			} else {
				// Save new reference
				Reference savedReference = refRepo.save(reference);
				sourceZettel.getReferences().add(savedReference);
				LOG.info("Saved new reference: " + savedReference);
			}
		}
		
		// Save the updated zettel
		zettelService.saveZettel(sourceZettel);
		LOG.info("updateReferences completed. Zettel has " + sourceZettel.getReferences().size() + " references");
	}

    public int purgeSoftDeletedReferencesOlderThanDays(int days) {
        return refRepo.purgeSoftDeletedOlderThanDays(days);
    }

}
