package com.xqdev.cyut_bkend_project.repository;

import com.xqdev.cyut_bkend_project.entity.Item;
import com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont;
import com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont_I;
import com.xqdev.cyut_bkend_project.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findByQuestionNumber(int questionNumber);

    @Query("select item from Item item where item.title like %?1% " +
            "or item.content like %?1% order by item.questionNumber")
    Page<Item> findItemsContain(@Param("searchTerm") String searchTerm, Pageable pageable);

    long countByCourseId(Long courseId);

    @Query("select new com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont(" +
            "i.id, i.questionNumber, i.title, i.a, i.b, i.c, i.maxInfoTheta, i.courseId) " +
            "from Item i where i.courseId = :course_id")
    Page<ItemEmptyQuesCont> findItemsByCourseId(@Param("course_id") Long courseId, Pageable pageable);

    @Query("select new com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont(" +
            "i.id, i.questionNumber, i.title, i.a, i.b, i.c, i.maxInfoTheta, i.courseId) " +
            "from Item i where i.courseId = :course_id and i.topics is empty ")
    Page<ItemEmptyQuesCont> findItemsByCourseIdEmptyTopic(@Param("course_id") Long courseId, Pageable pageable);

    /**
     * 使用 Native query + interface projection
     * @param courseId
     * @param searchTerm
     * @param pageable
     * @return
     */
    @Query(value = "select " +
            "id, question_number as questionNumber, title, a, b, c, max_info_theta as maxInfoTheta, course_id as courseId " +
            "from (select id, question_number, title, regexp_replace(content, '\\\\!\\\\[\\\\]\\\\(.+\\\\)|<img .+/>', 'img_placeholder') as content," +
            " a, b, c, max_info_theta, course_id from item) i " +
            "where i.course_id = :course_id and " +
            "(i.title like :search_term or " +
            " i.content like :search_term )",
            countQuery = "select count(*) from (select id, question_number, title, regexp_replace(content, '\\\\!\\\\[\\\\]\\\\(.+\\\\)|<img .+/>', 'img_placeholder') as content," +
            " a, b, c, max_info_theta, course_id from item) i " +
                    "where i.course_id = :course_id and " +
                    " (i.title like :search_term or " +
                    "   i.content like :search_term" +
                    " )",
            nativeQuery = true)
    Page<ItemEmptyQuesCont_I> findItemsByCourseIdAndContentContaining(@Param("course_id") Long courseId,
                                                                      @Param("search_term") String searchTerm,
                                                                      Pageable pageable);
    @Query(value = "select " +
            "id, question_number as questionNumber, title, a, b, c, max_info_theta as maxInfoTheta, course_id as courseId " +
            "from (select id, question_number, title, regexp_replace(content, '\\\\!\\\\[\\\\]\\\\(.+\\\\)|<img .+/>', 'img_placeholder') as content," +
            " a, b, c, max_info_theta, course_id from item " +
            " where not exists( select topic_id from item_topic it where it.topic_id = item.id) ) i " +
            " where i.course_id = :course_id and " +
            "(i.title like :search_term or " +
            " i.content like :search_term )",
            countQuery = "select count(*) " +
                    " from (select id, question_number, title, regexp_replace(content, '\\\\!\\\\[\\\\]\\\\(.+\\\\)|<img .+/>', 'img_placeholder') as content," +
                    " a, b, c, max_info_theta, course_id from item " +
                    " where not exists( select topic_id from item_topic it where it.topic_id = item.id) ) i " +
                    " where i.course_id = :course_id and " +
                    " (i.title like :search_term or " +
                    "   i.content like :search_term" +
                    " )",
            nativeQuery = true)
    Page<ItemEmptyQuesCont_I> findItemsByCourseIdAndContentContainingEmptyTopic(@Param("course_id") Long courseId,
                                                                      @Param("search_term") String searchTerm,
                                                                      Pageable pageable);

    List<Item> findByCourseIdAndQuestionNumber(Long courseId, String quesNum);

    /**
     * 找出某個 topic 下的 items.
     * @param topicId
     * @param courseId
     * @param  excludeItemList 排外試題清單
     * @return
     */
    @Query("select i from Item i, in (i.topics) t where i.courseId = :course_id and t.id = :topic_id and i.id not in(:exclude_list)")
    Page<Item> findByTopic(
            @Param("course_id") Long courseId,
            @Param("topic_id") Long topicId,
            @Param("exclude_list") @NotNull List<Long> excludeItemList,
            Pageable pageable
    );

    /**
     * 找出某個 topic 下的 items.
     * @param topicId
     * @param courseId
     * @return
     */
    @Query("select i from Item i, in (i.topics) t where i.courseId = :course_id and t.id = :topic_id")
    Page<Item> findByTopic(
            @Param("course_id") Long courseId,
            @Param("topic_id") Long topicId,
            Pageable pageable
    );
    @Query("select i.topics from Item i where i.id = :item_id")
    List<Topic> findTopicsByItemId(@Param("item_id") Long itemId);

    /**
     * 回傳的 Item 內 Content 的欄位沒有值。
     * 此外， item 的 Topics 欄位要另外 fetch. 可使用 {@link com.xqdev.cyut_bkend_project.controller.ItemHelper} 做轉換。
     * @param itemId
     * @return
     */
    @Query("select new com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont(" +
            "i.id, i.questionNumber, i.title, i.a, i.b, i.c, i.maxInfoTheta, i.courseId) " +
            "from Item i " +
            "where i.id = :item_id")
    Optional<ItemEmptyQuesCont> findItemEmptyQuesContById(@Param("item_id") Long itemId);

    /**
     * 依 courseID 及 TopicID 找 Item. TopicID 可能是 Child topic 或者 Parent topic.
     * @param topicID
     * @return
     */
    @Query(value = "select distinct " +
            "new com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont(i.id, i.questionNumber, " +
            "i.title, i.a, i.b, i.c, i.maxInfoTheta, i.courseId)" +
            "from Item i join i.topics t where (t.id = :topic_id or t.parentId = :topic_id) ",
            countQuery = "select count(distinct  i.id) " +
                    "from Item i join i.topics t where (t.id = :topic_id or t.parentId = :topic_id)")
    Page<ItemEmptyQuesCont_I> findItemEmptyQCByTopicID(@Param("topic_id") Long topicID, Pageable pageable);

    /**
     * TODO: 待測試
     * 依 courseID 及 TopicID 及 Search term 找 Item. TopicID 可能是 Child topic 或者 Parent topic.
     *
     * Create the view before use the function:
     * create or replace view item_no_content as (
     * select id, question_number, title, regexp_replace(content, '!\\[\\]\\(.+\\)|<img .+/>', 'img_placeholder') as content,
     *        a, b, c, max_info_theta, course_id from item
     *                                           );
     * @param topicID
     * @param search
     * @param pageable
     * @return
     */
    @Query(value = "select id, question_number as questionNumber, title, a, b, c, " +
            "           max_info_theta as maxInfoTheta, course_id as courseId from item_no_content " +
            " where id in (select it.item_id from item_topic it join topic t on t.id = it.topic_id " +
            "             where (t.id = :topic_id or t.parent_id = :topic_id) ) " +
            "    and (title like :search or content like :search)",
            countQuery = "select count(*) from ( " +
                    "  select id, question_number, title, a, b, c, max_info_theta, course_id from item_no_content " +
                    "       where id in (select it.item_id from item_topic it join topic t on t.id = it.topic_id " +
                    "                          where (t.id = :topic_id or t.parent_id = :topic_id)) " +
                    "                          and (title like :search or content like :search) " +
                    "                     )",
            nativeQuery = true)
    Page<ItemEmptyQuesCont_I> findItemEmptyQCByTopicID(@Param("topic_id") Long topicID,
                                                       @Param("search") String search,
                                                       Pageable pageable);

    /**
     * TODO: 待測試
     * Find items by given a topic id, excluded item list, search term.
     *
     * @param topicID
     * @param excludeItemList 提供要排除的 item 的 ID 清單。
     * @param search
     * @param pageable
     * @return
     */
    @Query(value= "select id, question_number as questionNumber, title, a, b, c, " +
            "           max_info_theta as maxInfoTheta, course_id as courseId from item_no_content " +
            "            where id in (select it.item_id from item_topic it join topic t on t.id = it.topic_id " +
            "                         where (t.id = :topic_id or t.parent_id = :topic_id) " +
            "                            and it.item_id not in (:exclude_item_list) ) " +
            "                and (title like :search or content like :search) ",
            countQuery = "select count(*) from ( " +
                    "                         select id, question_number, title, a, b, c, max_info_theta, course_id from item_no_content " +
                    "                         where id in (select it.item_id from item_topic it join topic t on t.id = it.topic_id " +
                    "                                      where (t.id = :topic_id or t.parent_id = :topic_id) " +
                    "                                        and it.item_id not in (:exclude_item_list) ) " +
                    "                           and (title like :search or content like :search) " +
                    "                     ) ds",
            nativeQuery = true)
    Page<ItemEmptyQuesCont_I> findItemEmptyQCByTopicID(@Param("topic_id") Long topicID,
                                                       @Param("exclude_item_list") List<Long> excludeItemList,
                                                       @Param("search") String search,
                                                       Pageable pageable);

    /**
     * 依 courseID 及 TopicID 找 Item. TopicID 可能是 Child topic 或者 Parent topic.
     * 提供要排除的 item 的 ID 清單。
     * QC: Question Content
     * @param topicID
     * @param excludeItemList
     * @param pageable
     * @return
     */
    @Query(value = "select distinct " +
            "new com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont(i.id, i.questionNumber, " +
            "i.title, i.a, i.b, i.c, i.maxInfoTheta, i.courseId)" +
            "from Item i join i.topics t " +
            "where i.id not in (:exclude_item_list) and (t.id = :topic_id or t.parentId = :topic_id) ",
            countQuery = "select count(distinct  i.id) " +
                    "from Item i join i.topics t " +
                    "where i.id not in (:exclude_item_list) and (t.id = :topic_id or t.parentId = :topic_id)")
    Page<ItemEmptyQuesCont_I> findItemEmptyQCByTopicID(@Param("topic_id") Long topicID,
                                                     @Param("exclude_item_list") List<Long> excludeItemList,
                                                     Pageable pageable);


}
