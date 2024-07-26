package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stevedutch.intellectron.domain.Reference;
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
    private Zettel testZettel;

    @BeforeEach
    void setUp() {
        testReference = new Reference();
        testReference.setZettels(new HashSet<>());
        testZettel = new Zettel();
        testZettel.setZettelId(1L);
        testZettel.setSignature(23456L);
    }

    @Test
    void testSaveReferenceWithZettel() {
        when(refRepo.save(any(Reference.class))).thenReturn(testReference);

        Reference result = referenceService.saveReferenceWithZettel(testReference, testZettel);

        assertNotNull(result);
        assertTrue(result.getZettels().contains(testZettel));
        verify(refRepo).save(testReference);
    }

    @Test
    void testUpdateReferences_NewReference() {
        ArrayList<Reference> references = new ArrayList<>();
        references.add(testReference);

        when(searchService.findZettelById(anyLong())).thenReturn(testZettel);
        when(refRepo.findByOriginZettelAndTargetZettel(anyLong(), anyLong())).thenReturn(null);

        referenceService.updateReferences(1L, references);

        verify(refRepo).save(testReference);
        verify(zettelService).saveZettel(testZettel);
    }

    @Test
    void testUpdateReferences_ExistingReference() {
        ArrayList<Reference> references = new ArrayList<>();
        references.add(testReference);
        testReference.setReferenceId(1L);

        when(searchService.findZettelById(anyLong())).thenReturn(testZettel);
        when(refRepo.findByOriginZettelAndTargetZettel(anyLong(), anyLong())).thenReturn(testReference);

        referenceService.updateReferences(1L, references);

        verify(refRepo, never()).save(testReference);
        verify(zettelService).saveZettel(testZettel);
    }
}

