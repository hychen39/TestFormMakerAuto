package com.xqdev.cyut_bkend_project.service.testform;

import com.xqdev.cyut_bkend_project.controller.ItemHelper;
import com.xqdev.cyut_bkend_project.entity.Item;
import com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont;
import com.xqdev.cyut_bkend_project.entity.TestForm;
import com.xqdev.cyut_bkend_project.entity.Topic;
import com.xqdev.cyut_bkend_project.repository.ItemRepository;
import com.xqdev.cyut_bkend_project.repository.TestFormRepo;
import com.xqdev.cyut_bkend_project.repository.TopicRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 提供 TestForm 物件的 CRUD 方法
 */
@Service
public class TestFormService {

    @Getter
    private final TopicRepository topicRepo;
    private final ItemRepository itemRepo;
    private final ItemHelper itemHelper;
    @Getter
    private TestFormRepo testFormRepo;
    @Getter
    private JdbcTemplate jdbc;

    @Autowired
    public TestFormService(TestFormRepo testFormRepo, JdbcTemplate jdbc,
                           TopicRepository topicRepo,
                           ItemRepository itemRepo,
                           ItemHelper itemHelper) {
        this.testFormRepo = testFormRepo;
        this.jdbc = jdbc;
        this.topicRepo = topicRepo;
        this.itemRepo = itemRepo;
        this.itemHelper = itemHelper;
    }

    public List<TestFormWithTopics> findAllTestFormWithTopics (){
        List<TestFormWithTopics> testFormWithTopicsList = new ArrayList<>();
        List<TestForm> testForms = this.testFormRepo.findAll();
        // convert List<TestForm> to List<TestFormWithTopics>
        testForms.forEach( (testForm -> {
            TestFormWithTopics testFormWithTopics = convertToTestFormWithTopics(testForm);
            testFormWithTopicsList.add(testFormWithTopics);
        }));
        return testFormWithTopicsList;
    }

    public Page<TestFormWithTopics> findAllTestFormWithTopics (Pageable pageable){
        Page<TestForm> testFormsPage = this.testFormRepo.findAll(pageable);
        Page<TestFormWithTopics> testFormWithTopicsPage =
                testFormsPage.map((testForm) -> {
                    return convertToTestFormWithTopics(testForm);
                });
        return testFormWithTopicsPage;
    }

    public Page<TestFormWithTopics> findWithTopicsByCourseId(Long courseId, Pageable pageable) {
        Page<TestForm> testFormsPage = this.testFormRepo.findByCourseId(courseId, pageable);
        Page<TestFormWithTopics> testFormWithTopicsPage =
                testFormsPage.map((testForm) -> {
                    return convertToTestFormWithTopics(testForm);
                });
        return testFormWithTopicsPage;
    }



    public Page<TestFormWithTopics> findAllTestFormWithTopics(String searchTerm, Pageable pageable){
        Page<TestForm> testFormsPage = this.testFormRepo.findTestFormContain(searchTerm, pageable);
        Page<TestFormWithTopics> testFormWithTopicsPage =
                testFormsPage.map((testForm) -> {
                    return convertToTestFormWithTopics(testForm);
                });
        return testFormWithTopicsPage;
    }

    public Page<TestFormWithTopics> findWithTopicsByCourseIdAndSearching(Long courseId, String search, Pageable pageable) {
        Page<TestForm> testFormsPage = this.testFormRepo.findByCourseIdAndSearch(courseId, search, pageable);
        Page<TestFormWithTopics> testFormWithTopicsPage =
                testFormsPage.map((testForm) -> {
                    return convertToTestFormWithTopics(testForm);
                });
        return testFormWithTopicsPage;
    }

    /**
     * 將 TestForm 物件，找出其 topics 物件，並回傳  TestFormWithTopics object.
     * @param testForm
     * @return
     */
    public  TestFormWithTopics convertToTestFormWithTopics(TestForm testForm){
        TestFormWithTopics testFormWithTopics = new TestFormWithTopics(testForm);
        testFormWithTopics.setItem_count(findItemCount(testForm.getId()));
        testFormWithTopics.setTopics(findTopics(testForm.getId()));
        return testFormWithTopics;
    }

    private List<TestFormWithTopics> convertToTestFormWithTopics(List<TestForm> testForms){
        List<TestFormWithTopics> testFormWithTopicsList = new ArrayList<>();
        testForms.forEach( (testForm) -> {
            TestFormWithTopics testFormWithTopic = new TestFormWithTopics(testForm);
            testFormWithTopic.setItem_count(findItemCount(testForm.getId()));
            testFormWithTopic.setTopics(findTopics(testForm.getId()));
            testFormWithTopicsList.add(testFormWithTopic);
        });
        return testFormWithTopicsList;
    }

    public int findItemCount(Long testFormId){
        String queryStr = "select count(item_id) as count from testform_items where testform_id = ?";
        Integer count = jdbc.queryForObject(queryStr, (ResultSet rs, int rowNum)->{
            Integer itemCount  = 0;
            itemCount = Integer.valueOf(rs.getInt("count"));
            return itemCount;
        } , testFormId);
        return count;
    }

    public List<Topic> findTopics(Long testFormId){
        List<Topic> topicList = new ArrayList<>();
        String queryStr = "select topic_id from testform_item_topic_vu where testform_id = ?";
        List<Long> topicIds = jdbc.queryForList(queryStr, Long.class, testFormId);
        topicIds.forEach((itemId) -> {
            topicList.add(topicRepo.findById(itemId).get());
        });
        return topicList;
    }

    /**
     * Find the items for a given list of item ids
     * @param item_ids
     * @return
     */
    public List<Item> findItems(List<Long> item_ids){
        List<Item> items = new ArrayList<>();
        // 使用 ItemHelper class 轉換 ItemEmptyQuesCont 至 Item
        item_ids.forEach(item_id -> {
            Optional<ItemEmptyQuesCont> itemEmptyQuesCont = itemRepo.findItemEmptyQuesContById(item_id);
            if(itemEmptyQuesCont.isPresent())
                items.add(itemHelper.convert(itemEmptyQuesCont.get()));
        });
        return items;
    }



}
