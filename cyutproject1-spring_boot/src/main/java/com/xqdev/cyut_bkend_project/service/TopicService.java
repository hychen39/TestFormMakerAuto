package com.xqdev.cyut_bkend_project.service;

import com.xqdev.cyut_bkend_project.controller.ItemHelper;
import com.xqdev.cyut_bkend_project.controller.TopicController;
import com.xqdev.cyut_bkend_project.entity.Item;
import com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont_I;
import com.xqdev.cyut_bkend_project.entity.Topic;
import com.xqdev.cyut_bkend_project.repository.ItemRepository;
import com.xqdev.cyut_bkend_project.repository.TopicJdbcRepo;
import com.xqdev.cyut_bkend_project.repository.TopicRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TopicService {
    @Getter
    private TopicRepository topicRepository;
    @Getter
    private TopicJdbcRepo topicJdbcRepo;
    private ItemRepository itemRepository;
    final private ItemHelper itemHelper;
    private Map<String, Long> userTopicIdIndex;

    @Autowired
    public TopicService(TopicRepository topicRepository, TopicJdbcRepo topicJdbcRepo, ItemRepository itemRepository, ItemHelper itemHelper) {
        this.topicRepository = topicRepository;
        this.topicJdbcRepo = topicJdbcRepo;
        this.itemRepository = itemRepository;
        this.itemHelper = itemHelper;
        this.userTopicIdIndex = null;
    }

    public Topic saveTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    public Collection<Topic> findAll() {
        return topicRepository.findAll();
    }

    public Optional<Topic> findById(Long id) {
        return topicRepository.findById(id);
    }

    /**
     * 更新分類主題
     * @param id topic id (PK)
     * @param topic topic with updated fields
     * @return true if update success; otherwise, false.
     */
    public Optional<Topic> updateTopic(Long id, Topic topic) {
        Optional<Topic> currentTopic = this.topicRepository.findById(id);
        if (currentTopic.isPresent()) {
            try {
                currentTopic.get().updateReflec(topic);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            topicRepository.save(currentTopic.get());

        }
        return currentTopic;
    }

    public Optional<Topic> deleteTopic(Long id) {
        Optional<Topic> topic = topicRepository.findById(id);
        if (topic.isPresent()) {
            topicRepository.deleteById(id);
            return topic;
        }
        return topic;
    }

    public Collection<Topic> findTopicsIncludingSubtopics(){
        return topicRepository.findTopicsIncludingSubtopics();
    }

    /**
     * Note: 回傳 item 的 topics 沒有內容。
     * @param topic_id
     * @param excludeItemList 要排除的 item id
     * @return
     */
    public List<Item> findItemByTopic(Long topic_id, TopicController.ExcludeItemList excludeItemList) {
        List<Item> items = topicJdbcRepo.findItemByTopic(topic_id, excludeItemList.getExcept());
        return items;
    }

    /**
     * 給 TopicID, 回傳 item. TopicID 可能是 subtopic ID 或者 Parent Topic id.
     * Modify to meet issue #105.
     * @param topic_id
     * @param excludeItemList 排外的 item id 清單
     * @param pageable
     * @return
     */
    public Page<ItemEmptyQuesCont_I> findItemByTopic(Long topic_id, TopicController.ExcludeItemList excludeItemList, Pageable pageable) {
        // 回傳的 item 內 content 欄位為空值，節省 search time
        Page<ItemEmptyQuesCont_I> itemsOfEmptyCont = null;
        if (excludeItemList == null || excludeItemList.getExcept().isEmpty()){
            itemsOfEmptyCont = this.itemRepository.findItemEmptyQCByTopicID(
                    topic_id, pageable
            );
        } else {
            itemsOfEmptyCont = this.itemRepository.findItemEmptyQCByTopicID(
                    topic_id, excludeItemList.getExcept(),
                    pageable
            );
        }
        // Convert
        // Page<Item> pageItems = itemsOfEmptyCont.map(this.itemHelper::convert);
//        Page<Item> pageItems = topicJdbcRepo.findItemsByTopic(topic_id, excludeItemList.getExcept(), pageable);
//        // populate the topics in each item
//        pageItems.get().forEach((item -> {
//            Optional<Item> itemWithTopic  = itemRepository.findById(item.getId());
//            item.setTopics(itemWithTopic.get().getTopics());
//        }));
        return itemsOfEmptyCont;
        // return pageItems;
    }

    /**
     * 檢查提供的 user_topic_id list 的元素是否在資料庫中有重覆。
     * 第一次使用時，會將 topic 資料載入到 cache 中。
     *
     * @param courseId
     * @param importedIds
     * @param reloadCache 是否要更新 cache. 預設為 yes.
     * @return 不合法的 user_topic_id 清單
     */
    public List<String> validateImportedUserTopicId(Long courseId, List<String> importedIds, boolean reloadCache){
        // Load the cache and create the map for indexing
        if (this.userTopicIdIndex == null || reloadCache){
            List<Topic> topics = this.getTopicRepository().findAllByCourseId(courseId);
            // indexing the user_topic_id
            this.userTopicIdIndex = new HashMap<>();
            topics.forEach((topic ->
                    this.userTopicIdIndex.put(topic.getUserTopicId(), topic.getId())
            ));
        }
        // validate and create the invalidated user-topic-id.
        List<String> invalidUserTopicId = new ArrayList<>();
        importedIds.forEach((utID) -> {
            if (this.userTopicIdIndex.containsKey(utID))
                invalidUserTopicId.add(utID);
        });
        return invalidUserTopicId;
    }
}
