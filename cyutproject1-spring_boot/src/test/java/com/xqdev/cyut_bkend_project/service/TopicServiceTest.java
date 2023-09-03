package com.xqdev.cyut_bkend_project.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class TopicServiceTest {
    private TopicService topicService;

    @Autowired
    public TopicServiceTest(TopicService topicService) {
        this.topicService = topicService;
    }

    @Test
    void validateImportedUserTopicId() {
        // the top 3 ids are not invalidated.
        String [] ids = {"1", "1.1", "1.2", "10", "10.1"};
        Long courseId = Long.valueOf(1);
        List<String> userTopicIds = Arrays.stream(ids).toList();
        List<String> invalidTopics = this.topicService.validateImportedUserTopicId(courseId, userTopicIds, true);
        Assertions.assertTrue( invalidTopics.size() > 0 );
        invalidTopics.forEach((t) -> System.out.println(t));
    }
}
