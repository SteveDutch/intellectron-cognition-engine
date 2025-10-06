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
	public void updateReferences(Long zettelId, ArrayList<Reference> desiredReferences) {
		LOG.info("Start of updateReferences for Zettel ID: {}", zettelId);

		Zettel sourceZettel = searchService.findZettelById(zettelId);

		// Create a map of desired references for quick lookups.
		Map<Long, Reference> desiredByTarget = getDesiredReferencesMap(desiredReferences);
		
		// Step 1: Find and soft-delete references that are no longer desired.
		softDeleteRemovedReferences(sourceZettel, desiredByTarget);

		// Step 2: Create new references or update existing ones.
		upsertDesiredReferences(sourceZettel, desiredReferences);
		
		// Step 3: Save the parent Zettel to persist relationship changes.
		zettelService.saveZettel(sourceZettel);
		LOG.info("updateReferences completed. Zettel has {} references", sourceZettel.getReferences().size());
	}

	/**
	 * Iterates through the Zettel's current references and soft-deletes any that are not in the
	 * desired set.
	 */
	private void softDeleteRemovedReferences(Zettel sourceZettel, Map<Long, Reference> desiredByTarget) {
		// We use a new ArrayList to avoid a ConcurrentModificationException while iterating and removing.
		for (Reference oldRef : new ArrayList<>(sourceZettel.getReferences())) {
			if (!desiredByTarget.containsKey(oldRef.getTargetZettelId())) {
				LOG.info("Soft-deleting removed reference: {}", oldRef);
				sourceZettel.removeReference(oldRef); // Removes from the relationship (zettel_references table)
				refRepo.delete(oldRef);               // Triggers the @SQLDelete on the Reference (pointer table)
			}
		}
	}

	/**
	 * Iterates through the desired references, updating existing ones or creating new ones.
	 */
	private void upsertDesiredReferences(Zettel sourceZettel, ArrayList<Reference> desiredReferences) {
		// Get a map of the currently existing (non-soft-deleted) references for comparison.
		Map<Long, Reference> existingByTarget = sourceZettel.getReferences().stream()
				.collect(Collectors.toMap(Reference::getTargetZettelId, ref -> ref));
		
		for (Reference desiredRef : desiredReferences) {
			// Ensure the reference is linked to the correct source Zettel.
			desiredRef.setSourceZettelId(sourceZettel.getZettelId());

			// Validate that the target Zettel exists before proceeding.
			if (!targetZettelExists(desiredRef.getTargetZettelId())) {
				continue;
			}

			Reference existingRef = existingByTarget.get(desiredRef.getTargetZettelId());

			if (existingRef != null) {
				// This reference already exists, so we update its properties.
				existingRef.setType(desiredRef.getType());
				existingRef.setConnectionNote(desiredRef.getConnectionNote());
				refRepo.save(existingRef);
				LOG.info("Updated existing reference: {}", existingRef);
			} else {
				// This is a new reference.
				// For now, we create a new one instead of trying to restore a soft-deleted one.
				Reference savedReference = refRepo.save(desiredRef);
				sourceZettel.addReference(savedReference); // Add the new reference to the relationship.
				LOG.info("Saved new reference: {}", savedReference);
			}
		}
	}

	/**
	 * Creates a Map from a list of references with the target Zettel ID as the key.
	 */
	private Map<Long, Reference> getDesiredReferencesMap(ArrayList<Reference> references) {
		return references.stream()
			.filter(ref -> ref.getTargetZettelId() != null)
			.collect(Collectors.toMap(Reference::getTargetZettelId, ref -> ref, (ref1, ref2) -> ref1)); // In case of duplicates, keep the first one
	}

	/**
	 * Checks if a Zettel with the given ID exists.
	 */
	private boolean targetZettelExists(Long targetId) {
		if (targetId == null) {
			LOG.warn("Skipping reference with null target zettel ID");
			return false;
		}
		try {
			searchService.findZettelById(targetId);
			return true;
		} catch (Exception e) {
			LOG.error("Target zettel not found with ID: {}", targetId, e);
			return false;
		}
	}

	public int purgeSoftDeletedReferencesOlderThanDays(int days) {
		return refRepo.purgeSoftDeletedOlderThanDays(days);
	}

}
