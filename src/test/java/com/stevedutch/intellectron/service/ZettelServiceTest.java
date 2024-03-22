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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger LOG = LoggerFactory.getLogger(ZettelService.class);
	

	@InjectMocks
    private ZettelService zettelService;
    @Mock
	private NoteService noteServiceMock;
    @Mock
    private AuthorService authorServiceMock;
    @Mock
    private TagService tagServiceMock;
    @Mock
    private TextService tekstServiceMock;
    @Mock
    private TextRepository tekstRepositoryMock;
    @InjectMocks
    private ReferenceService refService;
    
    private ZettelRepository zettelRepoMock;
    private Zettel zettelMock;

    @BeforeEach
    public void setUp() {
    	MockitoAnnotations.openMocks(this);
        noteServiceMock = mock(NoteService.class);
        authorServiceMock = mock(AuthorService.class);
        tagServiceMock = mock(TagService.class);
        zettelRepoMock = mock(ZettelRepository.class);
        tekstServiceMock = mock(TextService.class);
        tekstRepositoryMock = mock(TextRepository.class);
        refService = mock(ReferenceService.class);
        zettelMock = mock(Zettel.class);
        zettelService = new ZettelService();
    }

    @Test
    void testCreateZettel1() {
        // Arrange
        Zettel testZettel = new Zettel();
        testZettel.setZettelId(22L);
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
        when(refService.saveReferenceWithZettel(Mockito.any(Reference.class), Mockito.any(Zettel.class))).thenReturn(testRef);

        ZettelDtoRecord zettelDto = new ZettelDtoRecord(testZettel, testTekst, note, author, tags, testRefs);

        // Act
        System.out.println("\n  im junit test \n" + zettelDto + refService);
        LOG.info("zettelRepoMock: " + zettelRepoMock);
        ZettelDtoRecord result = zettelService.createZettel(zettelDto);

        // Assert
        assertNotNull(result);
        // Add more assertions as needed to verify the behavior of the createZettel
        // method
    }

    // ... Weitere Testmethoden ...

@Test
void testCreateZettel() {
    // Arrange
    ZettelDtoRecord zettelDto = new ZettelDtoRecord(zettelMock, null, null, null, null, null/* initialize with necessary objects */);
//    zettelDto.
    zettelMock.setZettelId(42L);
    ArrayList<Tag> tags = new ArrayList<Tag>();
    // Mock the dependencies
    when(noteServiceMock.saveNotewithZettel(Mockito.any(Note.class), Mockito.any(Zettel.class))).thenReturn(null/* return the expected Note */);
//    when(tagServiceMock.saveTagsWithZettel(Mockito.anyList(tags), Mockito.any(Zettel.class))).thenReturn(/* return the expected Tags */);
    when(zettelRepoMock.save(Mockito.any(Zettel.class))).thenReturn(null/* return the expected Zettel */);
    when(tekstServiceMock.saveTextwithZettel(Mockito.any(Tekst.class), Mockito.any(Zettel.class))).thenReturn(null/* return the expected Tekst */);
    when(authorServiceMock.saveAuthorWithText(Mockito.any(Author.class), Mockito.any(Tekst.class))).thenReturn(null/* return the expected Author */);
    when(refService.saveReferenceWithZettel(Mockito.any(Reference.class), Mockito.any(Zettel.class))).thenReturn(null/* return the expected Reference */);

    // Act
    when(zettelRepoMock.save(Mockito.any(Zettel.class))).thenReturn(zettelMock/* return the expected Zettel */);
    ZettelDtoRecord result = zettelService.createZettel(zettelDto);

    // Assert
    assertNotNull(result);
    // Add more assertions as needed to verify the behavior of the createZettel method
}

@Test
void testUpdateOneZettelbyId() {
    // Arrange
    Long zettelId = 42L;
    ZettelDtoRecord changes = new ZettelDtoRecord(null /* initialize with necessary objects */, null, null, null, null, null);

    // Mock the dependencies if required

    // Act
    zettelService.updateOneZettelbyId(zettelId, changes);

    // Assert
    // Add assertions to verify the behavior of the updateOneZettelbyId method
}}