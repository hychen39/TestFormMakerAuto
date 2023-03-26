package com.xqdev.cyut_bkend_project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 試卷 entity
 */
@Data
@Entity
@Table(name = "testform")
public class TestForm implements Updatable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 試卷標題 */
    private String title;

    /** 說明 */
    @JsonProperty("summary")
    private String description;
    /** 建立日期 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    @JsonProperty("date")
    private Date createdDate;

    // owner of the uni-directional relationship
    @ElementCollection
    @CollectionTable(
            name="testform_items",
            joinColumns = @JoinColumn(name = "testform_id")
    )
    @Column(name = "item_id")
    private List<Long> items = new ArrayList<>();

    @PrePersist
    void createdDate(){
        this.createdDate = new Date();
    }

    /**
     課程編號 FK, 參考到 {@link Course#getId()}
     */
    @Column(name = "course_id")
    @JsonProperty("course_id")
    private Long courseId;

//    public TestForm update(TestForm newTestForm) throws IllegalAccessException {
//        Class testFormClass = this.getClass();
//        Field[] fields = testFormClass.getDeclaredFields();
//        for (Field f: fields){
//            if ("id".equals(f.getName())) continue;
//            f.set(this, f.get(newTestForm));
//        }
//        return this;
//    }
}
