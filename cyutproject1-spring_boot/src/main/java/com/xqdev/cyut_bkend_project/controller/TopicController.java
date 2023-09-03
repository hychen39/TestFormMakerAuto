package com.xqdev.cyut_bkend_project.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xqdev.cyut_bkend_project.entity.*;
import com.xqdev.cyut_bkend_project.service.ItemService;
import com.xqdev.cyut_bkend_project.service.TopicService;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
//@CrossOrigin(allowCredentials = "true", origins = "http://192.168.0.100:8080/", allowedHeaders = "*")
@CrossOrigin
public class TopicController {


    final private TopicService topicService;
    final private ItemService itemService;
    final private ItemHelper itemHelper;
    private Logger logger = LoggerFactory.getLogger(TopicController.class);




    @Autowired
    public TopicController( TopicService topicService,
                            ItemService itemService,
                            ItemHelper itemHelper) {
        this.topicService = topicService;
        this.itemService = itemService;
        this.itemHelper = itemHelper;
    }

    /**
     * 例外題目清單，用於 findItemsByTopic()
     */
    @Data
    public static class ExcludeItemList {
        List<Long> except;
    }

    /**
     * 由前端送來要匯入的 topic 資料，其結構程 {@link Topic} 不同。
     */
    @Getter
    @Setter
    public static class ImportedTopic {
        String name;
        @JsonProperty("user_topic_id")
        String userTopicId;
        @JsonProperty("user_parent_id")
        String userParentId;
    }
    /**
     * Return all topics and their subtopics
     * @return Collection of {@link Topic} objects.
     */
    @GetMapping("/topics")
    public Collection<Topic> topics() {
        return topicService.findTopicsIncludingSubtopics();
//        return topicService.findAll();
    }

    @GetMapping("/courses/{course_id}/topics")
    public ResponseEntity<?> getTopicsByCourse(@PathVariable("course_id") Long courseId){
        return ResponseEntity.ok(topicService.getTopicRepository().findByCourseIdAndParentIdIsNull(courseId));

    }

    @GetMapping("/topic/{id}")
    public ResponseEntity<?> getTopic(@PathVariable Long id) {
        Optional<Topic> topic = topicService.findById(id);
        return topic.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/courses/{course_id}/topics/{id}")
    public ResponseEntity<?> getOneTopicByCourse(
            @PathVariable("course_id") Long courseId,
            @PathVariable("id") Long topicId
    ){
        Optional<Topic> topic = Optional.ofNullable(
                topicService
                        .getTopicRepository()
                        .findByCourseIdAndId(
                courseId, topicId
        ));
        return topic.map((t -> ResponseEntity.ok(t) )).orElse(
                new ResponseEntity<>(HttpStatus.NOT_FOUND)
        );
    }


    /**
     * 建立分類主題
     * @param topic
     * @return
     */
    @PostMapping("/topics")
    public ResponseEntity<?> createTopic(@RequestBody Topic topic) {
        if (topic == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Topic savedTopic = topicService.getTopicRepository().save(topic);
        CustomResponse<Topic> customResponse = CustomResponse.of(savedTopic);
        return ResponseEntity.ok(customResponse);
    }

    /**
     * 修改主題 API
     * Ref： https://hackmd.io/cim_13ymRb-ImmYSsAhFCQ?view#%E4%BF%AE%E6%94%B9%E4%B8%BB%E9%A1%8C-PUT-apitopicstopic_id
     * @param id
     * @param topic
     * @return
     */
    @PutMapping("/topics/{topic_id}")
    public ResponseEntity<?> updateTopic(@PathVariable("topic_id") Long id, @RequestBody Topic topic) {
        // search the target first
        Optional<Topic> updateTopic = this.topicService.updateTopic(id, topic);
        if (updateTopic.isPresent()) {
            CustomResponse<Topic> customResponse = CustomResponse.of(updateTopic.get());
            customResponse.setSuccess(true);
            return ResponseEntity.ok(customResponse);
        } else {
            // target not found
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"success\": false}");
        }
    }


    @DeleteMapping("/topics/{topic_id}")
    public ResponseEntity<?> deleteTopic(@PathVariable("topic_id") Long id) {
        // 需先判斷是否存在，否則抛出例外。
        Optional<Topic> topic = topicService.deleteTopic(id);
        return topic.map(response -> ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"success\": true}"))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 找出某個 topic 下的所由 item。 Request Body 中提供排除的項目。
     * @param topic_id
     * @param excludeItemList 要排除的例外項目。
     * @return
     */
    @PostMapping("/topic/{topic_id}/allItems")
    public CustomResponse<List<Item>> findItemsByTopic(@PathVariable Long topic_id,
                                                       @RequestBody ExcludeItemList excludeItemList){
        // As topicService to find items given the topic_id and the excludeItemList

        List<Item> items = topicService.findItemByTopic(topic_id, excludeItemList);

        CustomResponse<List<Item>> customResponse = new CustomResponse<>();
        customResponse.setData(items);
        return customResponse;
    }

    /**
     * DONE: Modify according to issue #105
     * 找出某個 topic 下的所由 item。 Request Body 中提供排除的項目。
     * Query parameter: ?itemsPerPage=15&page=1&search=
     * @param topic_id
     * @param excludeItemList
     * @param itemsPerPage
     * @param page
     * @param search 搜尋字詞。
     * @return CustomResponse<List<ItemEmptyQuesCont_I>> 回傳的 item 中 content 欄位為空，
     *  需要另外 call 再取得 content 的內容。
     */
    @PostMapping("/topic/{topic_id}/items")
    public CustomResponse<List<ItemEmptyQuesCont_I>> findItemsByTopic(@PathVariable Long topic_id,
                                                       @RequestBody ExcludeItemList excludeItemList,
                                                       @RequestParam int itemsPerPage,
                                                       @RequestParam int page,
                                                       @RequestParam(required = false) String search){

        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        // DONE: Test
        // Page<Item> pageItems = null;
        Page<ItemEmptyQuesCont_I> pageItemsEmptyContent = null;
        if (search == null || search.isEmpty()) {
            // no search term case
            pageItemsEmptyContent = topicService.findItemByTopic(topic_id, excludeItemList, pageable);
        } else {
            // with the search term
            if (excludeItemList == null || excludeItemList.getExcept().isEmpty()) {
                // no excluded item list
                pageItemsEmptyContent = itemService
                    .getItemRepository()
                    .findItemEmptyQCByTopicID(topic_id, search, pageable);}
            else {
                // find with topic_id, search term, and the excluded list.
                // DONE: Create the method and write it sql
                pageItemsEmptyContent = itemService
                        .getItemRepository()
                        .findItemEmptyQCByTopicID(topic_id, excludeItemList.getExcept(), search, pageable);
            }
        }

        CustomResponse<List<ItemEmptyQuesCont_I>> customResponse = new CustomResponse<>();
        customResponse.setData(pageItemsEmptyContent.getContent());
        Pagination pagination = new Pagination(
                pageItemsEmptyContent.getTotalElements(),
                pageItemsEmptyContent.getTotalPages(),
                pageItemsEmptyContent.getSize(),
                pageItemsEmptyContent.getNumber() + 1
        );
        customResponse.setPagination(pagination);
        return customResponse;
    }

    /**
     * 找出某個 topic 下的所由 item。 Request Body 中提供排除的項目。
     * Query parameter: ?itemsPerPage=15&page=1&search=
     * @param topicId
     * @param excludeItemList
     * @param itemsPerPage
     * @param page
     * @param search 尚未實作此參數
     * @return
     */
    @PostMapping("/courses/{course_id}/topics/{topic_id}/items")
    public CustomResponse<List<Item>> findItemsByCourseAndTopic(
            @PathVariable("course_id") Long courseId,
            @PathVariable("topic_id") Long topicId,
            @RequestBody ExcludeItemList excludeItemList,
            @RequestParam int itemsPerPage,
            @RequestParam int page,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
//        Page<Item> pageItems = topicService.getTopicJdbcRepo().
//                findItemsByCourseAndTopic(courseId, topic_id, excludeItemList.getExcept(), pageable);
        Page<Item> pageItems = excludeItemList.getExcept().size() != 0?
                this.itemService.getItemRepository().findByTopic(
                courseId, topicId, excludeItemList.getExcept(), pageable):
                this.itemService.getItemRepository().findByTopic(
                        courseId, topicId, pageable);


        CustomResponse<List<Item>> customResponse = CustomResponse.of(pageItems);
        return customResponse;
    }

    @PostMapping("/courses/{course_id}/topics/import")
    public ResponseEntity<?> importTopics(@PathVariable("course_id") Long courseId,
                                          @RequestBody List<ImportedTopic> importedTopics){
        // check the user-topic-ids to be imported
        List<String> uncheckedTopics = importedTopics.stream().map((it) -> it.getUserTopicId()).toList();
        List<String> invalidTopics = this.topicService.validateImportedUserTopicId(courseId, uncheckedTopics, true);
        if (! invalidTopics.isEmpty()){
            JSONObject resp = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("user_topic_ids", invalidTopics);
            resp.put("success", false);
            resp.put("status", "Invalid user topic ids");
            resp.put("data", data);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resp.toString());
        }

        // save to repository
        final List<String> invalidParentTopics = new ArrayList<>();
        List<Long> savedChildTopicIds = new ArrayList<>();
        List<Long> savedParentTopicIds = new ArrayList<>();
        try {
            importedTopics.forEach((t -> {
                Topic topic = new Topic();
                topic.setCourseId(courseId);
                topic.setName(t.getName());
                topic.setUserTopicId(t.getUserTopicId());

                if (t.getUserParentId() == null)
                    topic.setParentId(null);
                else {
                    // find the topic id for the user_parent_id
                    Optional<Topic> parentTopic = this.topicService
                            .getTopicRepository()
                            .findByUserTopicIdAndCourseId(t.getUserParentId(), courseId);
                    if (parentTopic.isPresent())
                        topic.setParentId(parentTopic.get().getId());
                    else {
                        // Cannot find the user_parent_id
                        invalidParentTopics.add(t.getUserParentId());
                        String exceptMsg = MessageFormat.format("The provided user_parent_id {0} does not exist!!",
                                t.getUserParentId());
                        throw new RuntimeException(exceptMsg);
                    }
                }
                // Save to database
                Topic savedTopic = this.topicService.getTopicRepository().save(topic);
                if (savedTopic.getParentId() == null )
                    savedParentTopicIds.add(savedTopic.getId());
                else
                    savedChildTopicIds.add(savedTopic.getId());
                logger.info(String.format("Assigned topic id: %s", topic.getId().toString()));
            }));
        } catch (RuntimeException exception) {
            // Delete the saved topics to rollback
            try {
                this.topicService.getTopicRepository().deleteAllById(savedChildTopicIds);
                logger.info(MessageFormat.format("delete id: {0}", savedChildTopicIds.toString()));
                this.topicService.getTopicRepository().deleteAllById(savedParentTopicIds);
                logger.info(MessageFormat.format("delete id: {0}", savedParentTopicIds.toString()));
            } catch (RuntimeException runtimeException) {
                JSONObject respBody = new JSONObject();
                respBody.put("success", false);
                respBody.put("exception_message", runtimeException.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(respBody.toString());
            }
            //
            JSONObject respBody = new JSONObject();
            JSONObject invalidIds = new JSONObject();
            invalidIds.put("user_topic_ids", invalidParentTopics);
            respBody.put("success", false);
            respBody.put("data", invalidIds);
            respBody.put("exception_message", exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(respBody.toString());
        }

        JSONObject respBody = new JSONObject();
        respBody.put("success", true);
        JSONObject savedTopic = new JSONObject();
        savedTopic.put("user_topic_id", savedChildTopicIds);
        respBody.put("data", savedTopic);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(respBody.toString());

    }

    /**
     * TODO: Refactor. Should be move to ItemController
     * @param courseID
     * @param topicIds
     * @return
     */
    @PostMapping("/courses/{course_id}/topics/multiple/items")
    public ResponseEntity<?> findItemsByCourseAndTopicList(@PathVariable("course_id") Long courseID,
                                                           @RequestBody List<Long> topicIds){
//        List<Item> items = this.topicService.getTopicRepository().findItemsByCourseIDAndTopicList(
//                courseID, topicIds
//        );

        List<ItemEmptyQuesCont> itemEmptyQuesContList = this.topicService
                .getTopicRepository()
                .findItemEmptyQuesContByCourseIdAndTopicList(
                courseID, topicIds
        );
        // Type conversion from ItemEmptyQuesCont to Item

        List<Item> items = itemEmptyQuesContList.stream().map(
                 this.itemHelper::convert
        ).toList();


        JSONObject respBody = new JSONObject();
        respBody.put("success", true);
        respBody.put("data", items);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(respBody.toString());

    }

    /**
     * TODO: Refactor. Should be move to ItemController
     * @param courseId
     * @return
     */
    @GetMapping("/courses/{course_id}/topics/item/count")
    public ResponseEntity<?> findItemCountsOfTopics(@PathVariable("course_id") Long courseId){
        List<ItemCountsOfTopics> itemCounts =
                this.topicService.getTopicRepository().findItemCountGroupByTopics(courseId);

        List<JSONObject> itemCountsJson =
                itemCounts.stream().map((itemCountsOfTopics) -> {
                    JSONObject itemCountJson = new JSONObject();
                    itemCountJson.put("id", itemCountsOfTopics.getTopicId());
                    itemCountJson.put("item_count", itemCountsOfTopics.getItemCount());
                    return itemCountJson;
                }).toList();

        JSONObject respBody = new JSONObject();
        respBody.put("success", true);
        respBody.put("data", itemCountsJson);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(respBody.toString());
    }

}
