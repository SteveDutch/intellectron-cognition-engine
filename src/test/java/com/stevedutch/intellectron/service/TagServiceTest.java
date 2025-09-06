package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepo;

    @Mock
    private SearchService searchService;
    
    @Mock
    private ZettelService zettelService;

    @InjectMocks
    private TagService tagService;

    private Tag tag;
    private Zettel zettel;

    @BeforeEach
    public void setUp() {
        tag = new Tag();
        tag.setTagText("testTag");
        zettel = new Zettel();
    }

    @Test
    public void testSaveTagNew() {
        when(tagRepo.findByTagText(anyString())).thenReturn(Optional.empty());
        when(tagRepo.save(any(Tag.class))).thenReturn(tag);

        Tag savedTag = tagService.saveTag(tag);

        assertEquals(tag, savedTag);
        verify(tagRepo, times(1)).save(tag);
    }

    @Test
    public void testSaveTagExisting() {
        when(tagRepo.findByTagText(anyString())).thenReturn(Optional.of(tag));

        Tag savedTag = tagService.saveTag(tag);

        assertEquals(tag, savedTag);
        verify(tagRepo, never()).save(any(Tag.class));
    }

    @Test
    public void testConnectTagsWithZettel() {
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag);

        when(tagRepo.findByTagText(anyString())).thenReturn(Optional.of(tag));

        ArrayList<Tag> connectedTags = tagService.connectTagsWithZettel(tags, zettel);

        assertEquals(1, connectedTags.size());
        assertEquals(1, connectedTags.get(0).getZettels().size());
        verify(tagRepo, times(1)).findByTagText(anyString());
    }

    @Test
    public void testUpdateTags() {
        Long zettelId = 1L;
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag);

        when(searchService.findZettelById(zettelId)).thenReturn(zettel);
        when(tagRepo.findByTagText(anyString())).thenReturn(Optional.of(tag));

        tagService.updateTags(zettelId, tags);

        assertEquals(1, zettel.getTags().size());
        verify(searchService, times(1)).findZettelById(zettelId);
        verify(tagRepo, times(1)).findByTagText(anyString());
    }
    
    
    @Test
    void testUpdateTags2() {
        // Arrange
        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");
        Tag tag3 = new Tag("tag3");
        Long zettelId = 1L;
        ArrayList<Tag> tags = new ArrayList<>();
        tag1.setId(1L);
        tag2.setId(2L);
        tags.add(tag1);
        tags.add(tag2);

        Zettel testZettel = new Zettel();
        testZettel.setZettelId(zettelId);
        ArrayList<Tag> zettelTags = new ArrayList<>();
        zettelTags.add(tag3);
        testZettel.setTags(new ArrayList<>());
        when(searchService.findZettelById(testZettel.getZettelId())).thenReturn(testZettel);
        when(tagRepo.findByTagText("tag1")).thenReturn(Optional.of(tag1));
        when(tagRepo.findByTagText("tag2")).thenReturn(Optional.of(tag2));
        // Act
        tagService.updateTags(1L, tags);
        // Assert
        verify(searchService, times(1)).findZettelById(zettelId);
        verify(tagRepo, times(2)).findByTagText(anyString());
        assertEquals(2, testZettel.getTags().size());
        assertTrue(testZettel.getTags().contains(tag1));
        assertTrue(testZettel.getTags().contains(tag2));
    }
	

}

