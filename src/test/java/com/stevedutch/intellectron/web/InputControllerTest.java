package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ModelMap;

import com.stevedutch.intellectron.service.ZettelService;

class InputControllerTest {
	
	@Mock
	private ModelMap model;
	@Mock
	private ZettelService zettelServiceMock;
	@InjectMocks
	private InputController sut = new InputController();
	
	@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


	@Test
	void testShowInputMask() {
		model.addAttribute("test");
		String result = sut.showInputMask(model);
		
		assertThat(result).isEqualTo("/input");
		Mockito.verify(zettelServiceMock).findLast10Zettel();
	}
	
    // Test showInputMask when ZettelService returns null or empty lists
    @Test
    void test_show_input_mask_with_null_or_empty_lists() {
        Mockito.when(zettelServiceMock.findLast10Zettel()).thenReturn(null);
        Mockito.when(zettelServiceMock.find10RandomZettel()).thenReturn(null);
        String result = sut.showInputMask(model);
        assertThat(result).isEqualTo("/input");
        Mockito.verify(model).put("zettels", new ArrayList<>());
        Mockito.verify(model).put("randomZettels", new ArrayList<>());
    }
    
    

}
