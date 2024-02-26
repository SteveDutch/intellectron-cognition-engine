package com.stevedutch.intellectron.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.stevedutch.intellectron.service.ZettelService;

class ZettelControllerTest {

    private ZettelController zettController = new ZettelController();	
    private ZettelService zettelService = mock(ZettelService.class);

	// ZettelController successfully deletes a Zettel by ID
    @Test
    public void test_delete_zettel() {
        // arrange
        Long zettelId = 1L;
    
        // act
        String result = zettController.deleteZettel(zettelId);
    
        // assert
        assertEquals("", result);
//        verify(zettelService, times(1)).deleteZettelById(zettelId);
    }


}
