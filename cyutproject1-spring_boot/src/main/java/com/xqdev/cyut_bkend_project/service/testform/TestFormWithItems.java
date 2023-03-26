package com.xqdev.cyut_bkend_project.service.testform;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.xqdev.cyut_bkend_project.entity.Item;
import com.xqdev.cyut_bkend_project.entity.TestForm;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Date;
import java.util.List;

/**
 * 包含 item 內容的試卷 class, 回傳給前端用
 */
@JsonPropertyOrder({"id", "title", "date", "summary", "items"})
public class TestFormWithItems {
    @JsonIgnore
    private TestForm testForm;


    public TestFormWithItems(TestForm testForm) {
        this.testForm = testForm;
    }

    public Long getId(){
        return testForm.getId();
    }

    public String getTitle(){
        return testForm.getTitle();
    }

    public Date getDate(){
        return testForm.getCreatedDate();
    }

    public String getSummary(){
        return testForm.getDescription();
    }

    @Setter @Getter
    private List<Item> items;

}
