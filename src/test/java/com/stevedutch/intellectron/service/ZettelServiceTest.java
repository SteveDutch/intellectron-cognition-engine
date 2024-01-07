package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.repository.TextRepository;
import com.stevedutch.intellectron.repository.ZettelRepository;

class ZettelServiceTest {

    private ZettelService zettelService;
    private NoteService noteServiceMock;
    private AuthorService authorServiceMock;
    private TagService tagServiceMock;
    private TextService tekstServiceMock;
    private TextRepository tekstRepositoryMock;
    
    private ZettelRepository zettelRepoMock;

    @BeforeEach
    public void setUp() {
        noteServiceMock = mock(NoteService.class);
        authorServiceMock = mock(AuthorService.class);
        tagServiceMock = mock(TagService.class);
        zettelRepoMock = mock(ZettelRepository.class);
        tekstServiceMock = mock(TextService.class);
        tekstRepositoryMock = mock(TextRepository.class);
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

        // Mock any dependencies if required
        when(noteServiceMock.save(Mockito.any(Note.class))).thenReturn(note);

        ZettelDtoRecord zettelDto = new ZettelDtoRecord(testZettel, testTekst, note, author, tag);

        // Act
        ZettelDtoRecord result = zettelService.createZettel(zettelDto);

        // Assert
        assertNotNull(result);
        // Add more assertions as needed to verify the behavior of the createZettel
        // method
    }

    // ... Weitere Testmethoden ...
}
