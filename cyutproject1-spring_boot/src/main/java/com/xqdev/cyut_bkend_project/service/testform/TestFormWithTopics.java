package com.xqdev.cyut_bkend_project.service.testform;

import com.xqdev.cyut_bkend_project.entity.TestForm;
import com.xqdev.cyut_bkend_project.entity.Topic;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

/**
 * An extended class for TestForm object.
 * Add the topics field to the class.
 */
public class TestFormWithTopics {
    @Getter
    private Long id;
    @Getter
    private String title;
    @Getter
    private String summary;
    @Getter
    private Date date;
    @Getter @Setter
    private int item_count;
    @Getter @Setter
    private List<Topic> topics;

    public TestFormWithTopics(@NotNull TestForm testForm) {
//            this.testForm = testForm;
        this.id = testForm.getId();
        this.title = testForm.getTitle();
        this.summary = testForm.getDescription();
        this.date = testForm.getCreatedDate();
        this.item_count = 0;
        this.topics = null;
    }
}
