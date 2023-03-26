package com.xqdev.cyut_bkend_project.repository;

import com.xqdev.cyut_bkend_project.entity.Item;
import com.xqdev.cyut_bkend_project.entity.ItemCountsOfTopics;
import com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont;
import com.xqdev.cyut_bkend_project.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    /**
     * 回傳 第一層的 topic 及其 children topic.
     * @return
     */
    @Query("select t from Topic t where t.parentId is null")
    Collection<Topic> findTopicsIncludingSubtopics();

    /**
     * 只回傳第一層的 topic. subtopic 透過 entity 的 JPA 屬性取得。
     * @param courseId
     * @return
     */
    Collection<Topic> findByCourseIdAndParentIdIsNull(Long courseId);

    /**
     * Find a topic by given course id and topic id
     * @param courseId course id
     * @param id topic id
     * @return Topic
     */
    Topic findByCourseIdAndId(Long courseId, Long id);

    /**
     * Find a topic by a given user-topic-id (使用者自編主題編號).
     * @param userTopicId
     * @return
     */
    Optional<Topic> findByUserTopicIdAndCourseId(String userTopicId, Long courseId);

    /**
     * 找出某個 courseId 下的所有 topic
     * @param courseId
     * @return
     */
    List<Topic> findAllByCourseId(Long courseId);

    @Override
    void deleteAllById(Iterable<? extends Long> longs);

    @Query("select distinct i from Item i join i.topics t where i.courseId = :course_id and t.id in (:topic_id_list)")
    List<Item> findItemsByCourseIDAndTopicList(@Param("course_id") Long courseID,
                                                      @Param("topic_id_list") List<Long> topicIDList);
    @Query(value = "select distinct " +
            "new com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont(i.id, i.questionNumber, " +
            "i.title, i.a, i.b, i.c, i.maxInfoTheta, i.courseId)" +
            "from Item i join i.topics t where i.courseId = :course_id and t.id in (:topic_id_list)")
    List<ItemEmptyQuesCont> findItemEmptyQuesContByCourseIdAndTopicList(@Param("course_id") Long courseID,
                                                                     @Param("topic_id_list") List<Long> topicIDList);

    /**
     * 找出 topic 下的 item count
     * 使用 Spring JPA 的 interface projection 技術
     * Ref: https://medium.com/swlh/spring-data-jpa-projection-support-for-native-queries-a13cd88ec166
     * Ref: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections
     * @param courseId
     * @return
     */
    @Query(value = "with topic_item_count as ( " +
            "select t.id as topic_id, t.parent_id as parent_id, count(i.id)  as item_count " +
            "    from item i join item_topic it on i.id = it.item_id " +
            "        join topic t on it.topic_id = t.id " +
            "    where i.course_id = :course_id " +
            "    group by t.id, t.parent_id " +
            "    order by 1) " +
            "select topic_id as topicId, item_count as itemCount from topic_item_count " +
            "union " +
            "select parent_id, sum(item_count) " +
            "    from topic_item_count " +
            "    group by parent_id " +
            "order by 1; ",
    nativeQuery = true)
    List<ItemCountsOfTopics> findItemCountGroupByTopics(@Param("course_id") Long courseId);
}
