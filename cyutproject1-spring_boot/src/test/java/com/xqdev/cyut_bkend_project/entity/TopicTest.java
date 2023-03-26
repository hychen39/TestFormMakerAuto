package com.xqdev.cyut_bkend_project.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    @Test
    void updateReflec() {
        Topic topic1 = new Topic();
        topic1.setId(Long.valueOf(1));
        Topic topic2 = new Topic();
        topic2.setId(Long.valueOf(2));
        topic2.setName("topic 2");
        try {
            topic1.updateReflec(topic2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(topic1.getName());
        assertNotNull(topic1.getName());

    }
}
