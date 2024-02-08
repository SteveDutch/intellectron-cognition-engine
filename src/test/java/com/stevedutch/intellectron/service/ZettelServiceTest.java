package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.repository.TextRepository;
import com.stevedutch.intellectron.repository.ZettelRepository;

class ZettelServiceTest {

	@InjectMocks
    private ZettelService zettelService;
    private NoteService noteServiceMock;
    private AuthorService authorServiceMock;
    private TagService tagServiceMock;
    private TextService tekstServiceMock;
    private TextRepository tekstRepositoryMock;
    @Mock
    private ReferenceService referenceServiceMock;
    
    private ZettelRepository zettelRepoMock;

    @BeforeEach
    public void setUp() {
    	MockitoAnnotations.openMocks(this);
        noteServiceMock = mock(NoteService.class);
        authorServiceMock = mock(AuthorService.class);
        tagServiceMock = mock(TagService.class);
        zettelRepoMock = mock(ZettelRepository.class);
        tekstServiceMock = mock(TextService.class);
        tekstRepositoryMock = mock(TextRepository.class);
        referenceServiceMock = mock(ReferenceService.class);
        zettelService = new ZettelService(noteServiceMock, zettelRepoMock, noteServiceMock, authorServiceMock, tagServiceMock, tekstServiceMock, tekstRepositoryMock);
    }

    @Test
    void testCreateZettel() {
        // Arrange
        Zettel testZettel = new Zettel();
        Note note = new Note("Dies ist eine wundervolle Testnotiz");
        Tekst testTekst = new Tekst("Dies ist ein Testtext");
        Author author = new Author("Karl", "Marx");
        Tag tag = new Tag("Wonderful Tag");
        ArrayList<Tag> tags = new ArrayList<Tag>();
        tags.add(tag);
        Reference testRef = new Reference();
        testRef.setOriginZettel(123456789012L);
        ArrayList<Reference> testRefs = new ArrayList<Reference>();
        testRefs.add(testRef);

        // Mock any dependencies if required
        when(noteServiceMock.saveNotewithZettel(Mockito.any(Note.class), Mockito.any(Zettel.class))).thenReturn(note);
        when(noteServiceMock.saveNote(Mockito.any(Note.class))).thenReturn(note);
        when(referenceServiceMock.saveReferenceWithZettel(Mockito.any(Reference.class), Mockito.any(Zettel.class))).thenReturn(testRef);

        ZettelDtoRecord zettelDto = new ZettelDtoRecord(testZettel, testTekst, note, author, tags, testRefs);

        // Act
        ZettelDtoRecord result = zettelService.createZettel(zettelDto);

        // Assert
        assertNotNull(result);
        // Add more assertions as needed to verify the behavior of the createZettel
        // method
    }

    // ... Weitere Testmethoden ...
}
