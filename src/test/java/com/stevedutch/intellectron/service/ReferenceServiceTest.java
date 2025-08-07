package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.ReferenceType;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.ReferenceRepository;

@ExtendWith(MockitoExtension.class)
class ReferenceServiceTest {

    @Mock
    private ReferenceRepository refRepo;

    @Mock
    private ZettelService zettelService;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private ReferenceService referenceService;

    private Reference testReference;
    private Zettel sourceZettel;
    private Zettel targetZettel;

    @BeforeEach
    void setUp() {
        // Arrange common test data
        sourceZettel = new Zettel();
        sourceZettel.setZettelId(1L);
        sourceZettel.setReferences(new HashSet<>());

        targetZettel = new Zettel();
        targetZettel.setZettelId(2L);

        testReference = new Reference();
        testReference.setId(1L);
        testReference.setSourceZettelId(1L);
        testReference.setTargetZettelId(2L);
        testReference.setType(ReferenceType.RELATES_TO);
        testReference.setConnectionNote("Test connection");
    }

    @Test
    void saveReference_ShouldReturnSavedReference() {
        // Arrange
        Reference referenceToSave = new Reference();
        referenceToSave.setSourceZettelId(1L);
        referenceToSave.setTargetZettelId(2L);
        referenceToSave.setType(ReferenceType.SUPPORTS);

        Reference savedReference = new Reference();
        savedReference.setId(1L);
        savedReference.setSourceZettelId(1L);
        savedReference.setTargetZettelId(2L);
        savedReference.setType(ReferenceType.SUPPORTS);

        when(refRepo.save(referenceToSave)).thenReturn(savedReference);

        // Act
        Reference result = referenceService.saveReference(referenceToSave);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getSourceZettelId());
        assertEquals(2L, result.getTargetZettelId());
        assertEquals(ReferenceType.SUPPORTS, result.getType());
        verify(refRepo, times(1)).save(referenceToSave);
    }

    @Test
    void updateReferences_WithNewReferences_ShouldSaveAllReferences() {
        // Arrange
        Long zettelId = 1L;
        ArrayList<Reference> references = new ArrayList<>();
        
        Reference newReference1 = new Reference();
        newReference1.setTargetZettelId(2L);
        newReference1.setType(ReferenceType.FOLLOWS_FROM);
        newReference1.setConnectionNote("Connection 1");
        
        Reference newReference2 = new Reference();
        newReference2.setTargetZettelId(3L);
        newReference2.setType(ReferenceType.CONTRADICTS);
        newReference2.setConnectionNote("Connection 2");
        
        references.add(newReference1);
        references.add(newReference2);

        Zettel targetZettel2 = new Zettel();
        targetZettel2.setZettelId(2L);
        
        Zettel targetZettel3 = new Zettel();
        targetZettel3.setZettelId(3L);

        when(searchService.findZettelById(zettelId)).thenReturn(sourceZettel);
        when(searchService.findZettelById(2L)).thenReturn(targetZettel2);
        when(searchService.findZettelById(3L)).thenReturn(targetZettel3);
        when(refRepo.findBySourceZettelIdAndTargetZettelId(1L, 2L)).thenReturn(null);
        when(refRepo.findBySourceZettelIdAndTargetZettelId(1L, 3L)).thenReturn(null);
        when(refRepo.save(any(Reference.class))).thenAnswer(invocation -> {
            Reference ref = invocation.getArgument(0);
            ref.setId(System.currentTimeMillis()); // Simulate ID generation
            return ref;
        });

        // Act
        referenceService.updateReferences(zettelId, references);

        // Assert
        verify(searchService, times(1)).findZettelById(zettelId);
        verify(searchService, times(1)).findZettelById(2L);
        verify(searchService, times(1)).findZettelById(3L);
        verify(refRepo, times(2)).save(any(Reference.class));
        verify(zettelService, times(1)).saveZettel(sourceZettel);
        
        assertEquals(1L, newReference1.getSourceZettelId());
        assertEquals(1L, newReference2.getSourceZettelId());
        assertEquals(2, sourceZettel.getReferences().size());
    }

    @Test
    void updateReferences_WithExistingReference_ShouldUpdateExistingReference() {
        // Arrange
        Long zettelId = 1L;
        ArrayList<Reference> references = new ArrayList<>();
        
        Reference updatedReference = new Reference();
        updatedReference.setTargetZettelId(2L);
        updatedReference.setType(ReferenceType.SUPPORTS);
        updatedReference.setConnectionNote("Updated connection");
        
        references.add(updatedReference);

        Reference existingReference = new Reference();
        existingReference.setId(10L);
        existingReference.setSourceZettelId(1L);
        existingReference.setTargetZettelId(2L);
        existingReference.setType(ReferenceType.RELATES_TO);
        existingReference.setConnectionNote("Old connection");

        when(searchService.findZettelById(zettelId)).thenReturn(sourceZettel);
        when(searchService.findZettelById(2L)).thenReturn(targetZettel);
        when(refRepo.findBySourceZettelIdAndTargetZettelId(1L, 2L)).thenReturn(existingReference);
        when(refRepo.save(existingReference)).thenReturn(existingReference);

        // Act
        referenceService.updateReferences(zettelId, references);

        // Assert
        verify(refRepo, times(1)).save(existingReference);
        verify(zettelService, times(1)).saveZettel(sourceZettel);
        
        assertEquals(ReferenceType.SUPPORTS, existingReference.getType());
        assertEquals("Updated connection", existingReference.getConnectionNote());
        assertEquals(1L, updatedReference.getSourceZettelId());
        assertTrue(sourceZettel.getReferences().contains(existingReference));
    }

    @Test
    void updateReferences_WithInvalidTargetZettel_ShouldSkipReference() {
        // Arrange
        Long zettelId = 1L;
        ArrayList<Reference> references = new ArrayList<>();
        
        Reference invalidReference = new Reference();
        invalidReference.setTargetZettelId(999L); // Non-existent zettel
        invalidReference.setType(ReferenceType.FOLLOWS_FROM);
        
        references.add(invalidReference);

        when(searchService.findZettelById(zettelId)).thenReturn(sourceZettel);
        when(searchService.findZettelById(999L)).thenThrow(new RuntimeException("Zettel not found"));

        // Act
        referenceService.updateReferences(zettelId, references);

        // Assert
        verify(searchService, times(1)).findZettelById(zettelId);
        verify(searchService, times(1)).findZettelById(999L);
        verify(refRepo, never()).save(any(Reference.class));
        verify(zettelService, times(1)).saveZettel(sourceZettel);
        
        assertTrue(sourceZettel.getReferences().isEmpty());
    }

    @Test
    void updateReferences_WithNullTargetZettelId_ShouldSkipReference() {
        // Arrange
        Long zettelId = 1L;
        ArrayList<Reference> references = new ArrayList<>();
        
        Reference referenceWithNullTarget = new Reference();
        referenceWithNullTarget.setTargetZettelId(null);
        referenceWithNullTarget.setType(ReferenceType.BRANCHES_FROM);
        
        references.add(referenceWithNullTarget);

        when(searchService.findZettelById(zettelId)).thenReturn(sourceZettel);

        // Act
        referenceService.updateReferences(zettelId, references);

        // Assert
        verify(searchService, times(1)).findZettelById(zettelId);
        verify(searchService, never()).findZettelById(isNull());
        verify(refRepo, never()).save(any(Reference.class));
        verify(zettelService, times(1)).saveZettel(sourceZettel);
        
        assertTrue(sourceZettel.getReferences().isEmpty());
    }

    @Test
    void updateReferences_WithEmptyReferenceList_ShouldClearExistingReferences() {
        // Arrange
        Long zettelId = 1L;
        ArrayList<Reference> emptyReferences = new ArrayList<>();
        
        // Add some existing references to the source zettel
        sourceZettel.getReferences().add(testReference);

        when(searchService.findZettelById(zettelId)).thenReturn(sourceZettel);

        // Act
        referenceService.updateReferences(zettelId, emptyReferences);

        // Assert
        verify(searchService, times(1)).findZettelById(zettelId);
        verify(refRepo, never()).save(any(Reference.class));
        verify(zettelService, times(1)).saveZettel(sourceZettel);
        
        assertTrue(sourceZettel.getReferences().isEmpty());
    }

    @Test
    void updateReferences_WithMixedNewAndExistingReferences_ShouldHandleBoth() {
        // Arrange
        Long zettelId = 1L;
        ArrayList<Reference> references = new ArrayList<>();
        
        // New reference
        Reference newReference = new Reference();
        newReference.setTargetZettelId(2L);
        newReference.setType(ReferenceType.SUPPORTS);
        newReference.setConnectionNote("New connection");
        
        // Reference that will update existing
        Reference updateReference = new Reference();
        updateReference.setTargetZettelId(3L);
        updateReference.setType(ReferenceType.CONTRADICTS);
        updateReference.setConnectionNote("Updated connection");
        
        references.add(newReference);
        references.add(updateReference);

        Zettel targetZettel3 = new Zettel();
        targetZettel3.setZettelId(3L);

        Reference existingReference = new Reference();
        existingReference.setId(20L);
        existingReference.setSourceZettelId(1L);
        existingReference.setTargetZettelId(3L);
        existingReference.setType(ReferenceType.RELATES_TO);

        when(searchService.findZettelById(zettelId)).thenReturn(sourceZettel);
        when(searchService.findZettelById(2L)).thenReturn(targetZettel);
        when(searchService.findZettelById(3L)).thenReturn(targetZettel3);
        when(refRepo.findBySourceZettelIdAndTargetZettelId(1L, 2L)).thenReturn(null);
        when(refRepo.findBySourceZettelIdAndTargetZettelId(1L, 3L)).thenReturn(existingReference);
        when(refRepo.save(any(Reference.class))).thenAnswer(invocation -> {
            Reference ref = invocation.getArgument(0);
            if (ref.getId() == null) {
                ref.setId(System.currentTimeMillis());
            }
            return ref;
        });

        // Act
        referenceService.updateReferences(zettelId, references);

        // Assert
        verify(refRepo, times(2)).save(any(Reference.class)); // One new, one updated
        verify(zettelService, times(1)).saveZettel(sourceZettel);
        
        assertEquals(2, sourceZettel.getReferences().size());
        assertEquals(ReferenceType.CONTRADICTS, existingReference.getType());
        assertEquals("Updated connection", existingReference.getConnectionNote());
    }

    @Test
    void updateReferences_ShouldSetCorrectSourceZettelId() {
        // Arrange
        Long zettelId = 5L;
        Zettel customSourceZettel = new Zettel();
        customSourceZettel.setZettelId(5L);
        customSourceZettel.setReferences(new HashSet<>());
        
        ArrayList<Reference> references = new ArrayList<>();
        Reference reference = new Reference();
        reference.setTargetZettelId(2L);
        reference.setType(ReferenceType.FOLLOWS_FROM);
        references.add(reference);

        when(searchService.findZettelById(zettelId)).thenReturn(customSourceZettel);
        when(searchService.findZettelById(2L)).thenReturn(targetZettel);
        when(refRepo.findBySourceZettelIdAndTargetZettelId(5L, 2L)).thenReturn(null);
        when(refRepo.save(any(Reference.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        referenceService.updateReferences(zettelId, references);

        // Assert
        assertEquals(5L, reference.getSourceZettelId());
        verify(zettelService, times(1)).saveZettel(customSourceZettel);
    }
}

