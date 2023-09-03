package com.xqdev.cyut_bkend_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.List;

@Entity
@Data
@Table(name = "item")
public class Item {

    /**
     * 自動產的 PK ID, 在所有 courses 中皆唯一。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 人為給定的 question number id. 在某個 Course 下需要唯一
     *
     * questionNumber 欄位資料型態改成 String. 允許使用者自建題號。例如："questionNumber": "Q1", 2022/12/20。
     */
    @Column(name = "question_number")
    private String questionNumber;

    @Column(name = "title", length = 2000)
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    /**
     * 和 Content 欄位高度重覆。
     * 不再回傳給 client side.
     *
     * @deprecated 預計之後移除此欄位。
     * 參考： {@line https://github.com/hychen39/TestFormMaker/issues/43#issuecomment-1313626105}
     */
    @Lob
    @Column(name = "question")
    @JsonIgnore
    @Deprecated
    private String question;

    @Column(name = "a")
    private double a;

    @Column(name = "b")
    private double b;

    @Column(name = "c")
    private double c;

    @Column(name = "max_info_theta")
    private double maxInfoTheta;

    @ManyToMany
    @JoinTable(name = "item_topic", joinColumns = @JoinColumn(name="item_id"), inverseJoinColumns = @JoinColumn(name="topic_id"))
    @JsonIgnoreProperties("items")
    private List<Topic> topics;

    /**
     課程編號 FK, 參考到 {@link Course} 的 PK。
     */
    @Column(name = "course_id")
    @JsonProperty("course_id")
    private Long courseId;

    /**
     * Update the fields of the item except the id field(PK)
     * Only not null fields in the newItem will be used to update the existing item.
     * Use the reflection technique to update the fields.
     * @param newItem
     * @return
     */
    public Item update(Item newItem) throws IllegalAccessException {
        Class itemClass = this.getClass();
        Field [] fields = itemClass.getDeclaredFields();
        for (Field f: fields){
            if ("id".equals(f.getName())) continue;
            if (f.get(newItem) != null){
                f.set(this, f.get(newItem));
            }
        }
        return this;
//        this.questionNumber = newItem.getQuestionNumber();
//        this.title = newItem.getTitle();
//        this.content = newItem.getContent();
//        this.a = newItem.getA();
//        this.b = newItem.getB();
//        this.c = newItem.getC();
//        this.maxInfoTheta = newItem.getMaxInfoTheta();
//        this.topics = newItem.getTopics();
//        this.courseId = newItem.getCourseId();
    }

    public Item(Long id,
                String questionNumber,
                String title,
                double a,
                double b,
                double c,
                double maxInfoTheta, Long courseId) {
        this.id = id;
        this.questionNumber = questionNumber;
        this.title = title;
        this.a = a;
        this.b = b;
        this.c = c;
        this.maxInfoTheta = maxInfoTheta;
        this.topics = null;
        this.courseId = courseId;
        this.content = null;
    }

    public Item() {
    }
}
