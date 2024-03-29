package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TagRepository;

class TagServiceTest {
	
    @InjectMocks
    private TagService tagServiceMock;
    @Mock
    private TagRepository tagRepoMock;
    
    @BeforeEach
    public void setUp() {
    	MockitoAnnotations.openMocks(this);
    	tagRepoMock = mock(TagRepository.class);
		tagServiceMock = new TagService(tagRepoMock);
        
    }

	@Test
	void testSaveTagwithZettel() {
		
		// Arrange
		Tag sut = new Tag("testomat");
		//sut.setTagText("Dies ist ein Tagtext");
		List <Tag> tags = new ArrayList<Tag>();
		tags.add(sut);
		Tag tag = new Tag("finally?");
		System.out.println(sut.getTagText() + Optional.of(sut).isPresent());
		System.out.println("tags ="  + Optional.of(tags).isPresent());
		Zettel testZettel = new Zettel(1234L, "Das ist eine supertolle Testüberschrift", null, LocalDateTime.now(), 
				LocalDateTime.now(), 1L, tags , null);
		
		// Mock any dependencies if required
        when(tagRepoMock.save(Mockito.any(Tag.class))).thenReturn(sut);

		// Act
		Tag result = tagServiceMock.saveTagwithZettel(sut, testZettel);
		System.out.println("sut.TagText im Test = " + sut.getTagText());
		System.out.println("Optional of sut im TeßagServiceTest = " + Optional.of(sut).isPresent());
		System.out.println("Optional of result im TeßagServiceTest = " + Optional.of(result.getTagText()).isEmpty());
		// Assert
		assertNotNull(sut);
		assertNotNull(result);
	}

}
