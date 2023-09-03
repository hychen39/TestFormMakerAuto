package com.xqdev.cyut_bkend_project.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void update() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setContent("content");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setTitle("title");

        try {
            item1.update(item2);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        System.out.println(item1.getContent());
        assertNotNull(item1.getContent());
        assertEquals(1L, item1.getId());
        assertEquals("title", item1.getTitle());
    }
}
