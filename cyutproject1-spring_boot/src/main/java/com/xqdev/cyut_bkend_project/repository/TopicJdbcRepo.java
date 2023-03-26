package com.xqdev.cyut_bkend_project.repository;

import com.xqdev.cyut_bkend_project.controller.TopicController;
import com.xqdev.cyut_bkend_project.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TopicJdbcRepo {
//    private JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//    private ItemRepository itemRepository;

    @Autowired
    public TopicJdbcRepo(
                         NamedParameterJdbcTemplate namedParameterJdbcTemplate
                         ) {
//        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
//        this.itemRepository = itemRepository;
    }

    /**
     *
     * @param topic_id
     * @param excludeItemList
     * @return
     */
    public List<Item> findItemByTopic(Long topic_id, List<Long> excludeItemList){
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("topicId", topic_id);
        parameters.addValue("excludeItemList", excludeItemList);
        String queryStr = "select * from item join item_topic it on item.id = it.item_id\n" +
                "where topic_id = :topicId and item_id not in (:excludeItemList)";
        List<Item> items = namedParameterJdbcTemplate.query(queryStr,
                parameters,
                this::mapRowToItem);
        return items;
    }

    /**
     *
     * @param topic_id
     * @param excludeItemList
     * @return
     */
    public List<Item> findItemsByCourseAndTopic(Long courseId, Long topic_id, List<Long> excludeItemList){
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("topicId", topic_id);
        parameters.addValue("excludeItemList", excludeItemList);
        String queryStr = "select * from item join item_topic it on item.id = it.item_id\n" +
                "where course_id =:courseId and topic_id = :topicId and item_id not in (:excludeItemList)";
        List<Item> items = namedParameterJdbcTemplate.query(queryStr,
                parameters,
                this::mapRowToItem);
        return items;
    }

    /**
     * 查詢結果中 item 的 topics 要另外再找。
     * @param rs
     * @param rowNum
     * @return
     * @throws SQLException
     */
    private Item mapRowToItem(ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setA(rs.getDouble("a"));
        item.setB(rs.getDouble("b"));
        item.setC(rs.getDouble("c"));
        item.setContent(rs.getString("content"));
        item.setMaxInfoTheta(rs.getDouble("max_info_theta"));
        item.setQuestion(rs.getString("question"));
        item.setQuestionNumber(rs.getString("question_number"));
        item.setTitle(rs.getString("title"));

        item.setTopics(null);
        item.setCourseId(rs.getLong("course_id"));
        return item;
    }

    /**
     * TODO: Refator. 查詢時只要取得 item_id. 之後到 Topic Service 再用 item_id 取得完整的 item.
     * @param topic_id
     * @param excludeItemList 排外清單
     * @param pageable
     * @return
     * @deprecated since 2023/03/15. 建議改用
     * {@link com.xqdev.cyut_bkend_project.service.TopicService#findItemByTopic(Long, TopicController.ExcludeItemList, Pageable)}
     */
    public Page<Item> findItemsByTopic(Long topic_id, List<Long> excludeItemList, Pageable pageable){
        long total = 0;
        List<Item> items = new ArrayList<>();

        // find the total count
        MapSqlParameterSource queryItemParam = new MapSqlParameterSource();
        queryItemParam.addValue("topicId", topic_id);
        String queryTotalCount;
        boolean isWithExcludeItemList = false;
        if (excludeItemList == null || excludeItemList.isEmpty() ) {
             queryTotalCount = "select count(1) as totalRows from item_topic " +
                    "where topic_id = :topicId";
        } else {
            queryTotalCount = "select count(1) as totalRows from item_topic " +
                    "where topic_id = :topicId and item_id not in (:excludeItemList) ";
            queryItemParam.addValue("excludeItemList", excludeItemList);
            isWithExcludeItemList = true;
        }
        total = namedParameterJdbcTemplate.query(queryTotalCount, queryItemParam, (rs) -> {
            rs.next();
            Long totalRows = rs.getLong("totalRows");
            return totalRows;
        });

        // find items with limit and offset clauses
        String queryItemStr;
        queryItemParam.addValue("limit", pageable.getPageSize());
        queryItemParam.addValue("offset", pageable.getOffset());
        if (isWithExcludeItemList){
             queryItemStr = "select * from item join item_topic it on item.id = it.item_id " +
                    "where topic_id = :topicId and item_id not in (:excludeItemList) " +
                    "order by item_id limit :limit offset :offset";
        } else {
             queryItemStr = "select * from item join item_topic it on item.id = it.item_id " +
                    "where topic_id = :topicId " +
                    "order by item_id limit :limit offset :offset";
        }

        items = namedParameterJdbcTemplate.query(queryItemStr, queryItemParam, this::mapRowToItem);

        return new PageImpl<>(items, pageable, total);

    }


    /**
     * 由課程及主題找試題
     * @param topic_id
     * @param excludeItemList 排外清單
     * @param pageable
     * @return
     * @deprecated since 2023/1/13. 此方法回傳 Item 中的 Topic 為 Null .請改用方法: {@link ItemRepository#findByTopic(Long, Long, Pageable)} 或者
     *  {@link ItemRepository#findByTopic(Long, Long, List, Pageable)}
     */
    public Page<Item> findItemsByCourseAndTopic(Long courseId, Long topic_id, List<Long> excludeItemList, Pageable pageable){
        long total = 0;
        List<Item> items = new ArrayList<>();

        // find the total count
        MapSqlParameterSource queryItemParam = new MapSqlParameterSource();
        queryItemParam.addValue("courseId", courseId);
        queryItemParam.addValue("topicId", topic_id);
        String queryTotalCount;
        boolean isWithExcludeItemList = false;
        queryTotalCount = "select count(1) as totalRows from item_topic it join item i on i.id = it.item_id " +
                "where it.topic_id = :topicId and i.course_id = :courseId";
        if ( !(excludeItemList == null || excludeItemList.isEmpty()) ) {
            queryTotalCount = queryTotalCount + " and it.item_id not in (:excludeItemList)";
            queryItemParam.addValue("excludeItemList", excludeItemList);
            isWithExcludeItemList = true;
        }

        total = namedParameterJdbcTemplate.query(queryTotalCount, queryItemParam, (rs) -> {
            rs.next();
            Long totalRows = rs.getLong("totalRows");
            return totalRows;
        });

        // find items with limit and offset clauses
        String queryItemStr;
        queryItemParam.addValue("limit", pageable.getPageSize());
        queryItemParam.addValue("offset", pageable.getOffset());
        if (isWithExcludeItemList){
            queryItemStr = "select * from item i join item_topic it on i.id = it.item_id " +
                    "where it.topic_id = :topicId and i.course_id = :courseId and it.item_id not in (:excludeItemList) " +
                    "order by item_id limit :limit offset :offset";
        } else {
            queryItemStr = "select * from item i join item_topic it on i.id = it.item_id " +
                    "where it.topic_id = :topicId and i.course_id = :courseId " +
                    "order by item_id limit :limit offset :offset";
        }

        items = namedParameterJdbcTemplate.query(queryItemStr, queryItemParam, this::mapRowToItem);

        return new PageImpl<>(items, pageable, total);

    }
}
