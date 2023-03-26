package com.xqdev.cyut_bkend_project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "topic",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "user_topic_id"}))
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 1000)
    private String name;

    @Column(name="parent_id")
    @JsonProperty("parent_id")
    private Long parentId;

    /**
     * Note for commenting out the codes below: <br/>
     * Topic 下的多個 item. 和 topic 和 item 間是 bidirectional many-to-many relations.
     * 每次取一個 topic, 便會捉取該 topic 下的 item, 影響效能。 <br/>
     * 改成單向關係。
     *
     * 之後，取得某個 topic 下的 item 要另外執行查詢。
     */
    // @JsonIgnoreProperties("topics")
    // @ManyToMany(mappedBy = "topics")
    // private List<Item> items;

//    @ManyToOne(cascade = {CascadeType.ALL})
//    @JoinColumn(name="parent_id")
//    private Topic parentTop;
    /**
     * 該主題下的子主題
     */
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
     @ToString.Exclude
     private List<Topic> children = new ArrayList<>();

    /**
     課程編號 FK, 參考到 {@link Course#getId()}
     */
    @Column(name = "course_id")
    @JsonProperty("course_id")
    private Long courseId;

    /**
     * 使用者自訂主題編號, 需要 unique constraint.
     */
    @Column(name = "user_topic_id", unique = true)
    @JsonProperty("user_topic_id")
    private String userTopicId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Topic topic = (Topic) o;
        return id != null && Objects.equals(id, topic.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Update the fields by another topic, except the id.
     * @param newTopic
     * @return
     */
    public Topic update(Topic newTopic){
        this.name = newTopic.getName();
        this.parentId = newTopic.getParentId();
        this.courseId = newTopic.getCourseId();
        this.children = newTopic.getChildren();
        return this;
    }

    /**
     * Update by using the reflection technique.
     * the id field is not updated.
     * @param newTopic
     * @return
     * @throws IllegalAccessException
     */
    public Topic updateReflec(Topic newTopic) throws IllegalAccessException {
        Class topicClass = this.getClass();
        Field [] fields = topicClass.getDeclaredFields();
        for (Field f: fields){
            if ("id".equals(f.getName())) continue;
            f.set(this, f.get(newTopic));
        }

        return this;
    }
}
