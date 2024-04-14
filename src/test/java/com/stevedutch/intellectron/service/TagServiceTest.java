package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TagRepository;

class TagServiceTest {
	
    @InjectMocks
    private TagService tagService;
    @Mock
    private TagRepository tagRepoMock;
    @Mock
    private ZettelService zettelServiceMock;
    
    @BeforeEach
    public void setUp() {
    	MockitoAnnotations.openMocks(this);
        
    }
	
	@Test
	void testUpdateTags() {
		 // Arrange
		Tag tag1 = new Tag("tag1");
		Tag tag2 = new Tag("tag2");
		Tag tag3 = new Tag("tag3");
        Long zettelId = 1L;
        ArrayList<Tag> tags = new ArrayList<>();
        tag1.setId(1L);
        tag2.setId(2L);
        tags.add(tag1);
//        tags.add(tag2);

        Zettel testZettel = new Zettel();
        testZettel.setZettelId(zettelId);
        ArrayList<Tag> zettelTags = new ArrayList<>();
        zettelTags.add(tag3);
        testZettel.setTags(new ArrayList<>());

        when(zettelServiceMock.findZettelById(testZettel.getZettelId())).thenReturn(testZettel);
        when(tagRepoMock.findByTagText(nullable(String.class))).thenReturn(Optional.of(tag1));
//        when(tagRepoMock.findByTagText(nullable(String.class))).thenReturn(Optional.of(tag2));

        // Act
        tagService.updateTags(1L, tags);

        // Assert
        verify(zettelServiceMock, times(1)).findZettelById(zettelId);
        verify(tagRepoMock, times(1)).findByTagText(anyString());
        assertEquals(1, testZettel.getTags().size());
	}
	

}
