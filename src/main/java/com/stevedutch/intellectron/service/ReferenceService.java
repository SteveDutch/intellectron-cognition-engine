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
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public void updateReferences(Long zettelId, ArrayList<Reference> references) {

		LOG.info("Start of updateReferences ; ---> " + references);

		// Get the source zettel
		Zettel sourceZettel = searchService.findZettelById(zettelId);

		// Load existing references for this source (active only due to @SQLRestriction)
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

		// 1. Soft-delete removed references AND remove them from the zettel's
		// collection
		// We use an ArrayList to avoid ConcurrentModificationException while iterating
		// and removing
		for (Reference oldRef : new ArrayList<>(sourceZettel.getReferences())) {
			if (!desiredByTarget.containsKey(oldRef.getTargetZettelId())) {
				LOG.info("Soft-deleting removed reference: {}", oldRef);
				sourceZettel.removeReference(oldRef); // Remove from the relationship
				refRepo.delete(oldRef); // Explicitly trigger the @SQLDelete
			}
		}

		// 2. Upsert desired references
		for (Reference desiredRef : references) {
			// Set the source zettel ID
			desiredRef.setSourceZettelId(sourceZettel.getZettelId());

			// Validate target exists
			try {
				Zettel targetZettel = searchService.findZettelById(desiredRef.getTargetZettelId());
				desiredRef.setTargetZettelId(targetZettel.getZettelId());
			} catch (Exception e) {
				LOG.error("Target zettel not found with ID: " + desiredRef.getTargetZettelId(), e);
				continue; // Skip this reference if target zettel doesn't exist
			}

			// Check if this reference already exists (ignoring soft-deleted ones)
			Reference existingRef = existingByTarget.get(desiredRef.getTargetZettelId());

			if (existingRef != null) {
				// Update existing reference
				existingRef.setType(desiredRef.getType());
				existingRef.setConnectionNote(desiredRef.getConnectionNote());
				// No need to re-add to sourceZettel.getReferences(), it's already there.
				refRepo.save(existingRef);
				LOG.info("Updated existing reference: " + existingRef);
			} else {
				// This is a new reference.
				// We don't check for soft-deleted ones to restore them for now,
				// as that adds more complexity. We create a new one.
				Reference savedReference = refRepo.save(desiredRef);
				sourceZettel.addReference(savedReference); // Add the new one to the relationship
				LOG.info("Saved new reference: " + savedReference);
			}
		}

		// 3. Save the updated zettel (this will synchronize the zettel_references
		// table)
		zettelService.saveZettel(sourceZettel);
		LOG.info("updateReferences completed. Zettel has " + sourceZettel.getReferences().size() + " references");
	}

	public int purgeSoftDeletedReferencesOlderThanDays(int days) {
		return refRepo.purgeSoftDeletedOlderThanDays(days);
	}

}
