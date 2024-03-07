package com.stevedutch.intellectron.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.stevedutch.intellectron.service.ZettelService;

class ZettelControllerTest {
	@Mock
	private ZettelService zettelService;
	@InjectMocks
    private ZettelController sut = new ZettelController();	
	// (Mocks initialisieren U Injektion durchführen)
    public ZettelControllerTest() {
        MockitoAnnotations.openMocks(this);
    }
	
    // ZettelController successfully deletes a Zettel by ID
    @Test
    public void test_delete_zettel() {
        // arrange
        Long zettelId = 1L;
    
        // act
        String result = sut.deleteOneZettel(zettelId);
    
        // assert
        assertEquals("redirect:/welcome", result);
        verify(zettelService, times(1)).deleteOneZettelbyId(zettelId);
    }


}
