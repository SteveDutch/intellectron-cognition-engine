package com.stevedutch.intellectron.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class TagTest {

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag("TestTag");
    }

    @Test
    void testConstructorAndGetTagText() {
        assertEquals("TestTag", tag.getTagText(), "Constructor should set tagText correctly");
    }

    @Test
    void testSetAndGetId() {
        tag.setId(1L);
        assertEquals(1L, tag.getId(), "setId and getId should work correctly");
    }

    @Test
    void testSetAndGetTagText() {
        String newTagText = "UpdatedTestTag";
        tag.setTagText(newTagText);
        assertEquals(newTagText, tag.getTagText(), "setTagText and getTagText should work correctly");
    }

    @Test
    void testSetAndGetZettels() {
        List<Zettel> zettels = Arrays.asList(new Zettel(), new Zettel());
        tag.setZettels(zettels);
        assertEquals(zettels, tag.getZettels(), "setZettels and getZettels should work correctly");
    }

    @Test
    void testToString() {
        tag.setId(1L);
        tag.getZettels().add(new Zettel("testing rules"));
        String expectedString = " \n"
        		+ " Tag [id=1, tagText=TestTag, \n"
        		+ " zettels=[\n"
        		+ " Zettel \n"
        		+ " [zettelId =null, topic=testing rules Note : No note available, it's NULL added=null, changed=null,  Anzahl der tags=0, \n"
        		+ " tekst=null , \n"
        		+ "  References: []]\n"
        		+ " \n"
        		+ " ]]"; // Adjusted based on actual Zettel implementation
        assertEquals(expectedString, tag.toString(), "toString should return the correct string representation");
    }

    @Test
    void testEqualsAndHashCode() {
        Tag tag1 = new Tag("TestTag");
        tag1.setId(1L);
        Tag tag2 = new Tag("TestTag");
        tag2.setId(1L);

        assertEquals(tag1, tag2, "Equal tags should be equal");
        assertEquals(tag1.hashCode(), tag2.hashCode(), "Equal tags should have the same hash code");

        tag2.setId(2L);
        assertNotEquals(tag1, tag2, "Tags with different IDs should not be equal");
    }
}
