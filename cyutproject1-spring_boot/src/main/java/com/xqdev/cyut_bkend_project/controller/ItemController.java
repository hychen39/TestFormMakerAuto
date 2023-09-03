package com.xqdev.cyut_bkend_project.controller;

import com.xqdev.cyut_bkend_project.entity.Item;
import com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont_I;
import com.xqdev.cyut_bkend_project.repository.ItemRepository;
import com.xqdev.cyut_bkend_project.service.ItemService;
import com.xqdev.cyut_bkend_project.service.TopicService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
//@CrossOrigin(allowCredentials = "true", origins = "http://192.168.0.100:8080/", allowedHeaders = "*")
@CrossOrigin
public class ItemController {

    private final ItemService itemService;
    private final TopicService topicService;
    private final ItemRepository itemRepository;
    private final ItemHelper itemHelper;

    @Autowired
    public ItemController(ItemService itemService,
                          ItemRepository itemRepository,
                            TopicService topicService,
                          ItemHelper itemHelper) {
        this.itemService = itemService;
        this.topicService = topicService;
        this.itemRepository = itemRepository;
        this.itemHelper = itemHelper;
    }

    @GetMapping("/items")
//    public Collection<Item> items() {
//        return itemService.findAll();
//    }
    public CustomResponse<List<Item>> items(@RequestParam int itemsPerPage,
                                            @RequestParam int page,
                                            @RequestParam(required = false) String search){
        // 使用 questionNumber 進行排序
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.by("questionNumber"));
        Page<Item> itemsPage = ( search == null || search.isEmpty() ) ?
                itemService.findAll(pageable): itemService.findAll(search, pageable);
        CustomResponse<List<Item>> customResponse = new CustomResponse<>();
        customResponse.success = true;
        customResponse.data = itemsPage.getContent();
        customResponse.pagination = new Pagination(
                itemsPage.getTotalElements(),
                itemsPage.getTotalPages(),
                itemsPage.getSize(),
                itemsPage.getNumber() + 1
        );
        return customResponse;
    }

    @GetMapping("/courses/{course_id}/items")
    public ResponseEntity<?> getItemsByCourse(@PathVariable("course_id") Long courseId,
                                      @RequestParam int itemsPerPage,
                                      @RequestParam int page,
                                      @RequestParam(required = false) String search,
                                              @RequestParam(required = false) Integer noTopic){
        // the default value for the noTopic is 0. The compiler sets the default value.
        Pageable pageable = null;
        Sort sort = Sort.by("questionNumber");
        if (itemsPerPage > 0)
            pageable = PageRequest.of(page - 1, itemsPerPage, sort);
        else pageable = Pageable.unpaged();
        Page<? extends ItemEmptyQuesCont_I> itemEmptyQuesContPage = null;
        if ( search == null || search.isEmpty() ) {
            if (noTopic == null ||  noTopic != 1) {
                // return items with or without topics
                itemEmptyQuesContPage = itemService.getItemRepository().findItemsByCourseId(courseId, pageable);
            }
            else {
                // return items without topics only
                itemEmptyQuesContPage = itemService.getItemRepository().findItemsByCourseIdEmptyTopic(courseId, pageable);
            }
        } else {
            if (noTopic == null || noTopic != 1) {
                itemEmptyQuesContPage = itemService.getItemRepository().
                        findItemsByCourseIdAndContentContaining(courseId, search, pageable);
            } else {
                // return items without topics only
                itemEmptyQuesContPage = itemService.getItemRepository().
                        findItemsByCourseIdAndContentContainingEmptyTopic(courseId, search, pageable);
            }
        }
//        Page<? extends ItemEmptyQuesCont_I> itemEmptyQuesContPage = ( search == null || search.isEmpty() ) ?
//                itemService.getItemRepository().findItemsByCourseId(courseId, pageable):
//                itemService.getItemRepository().findItemsByCourseIdAndContentContaining(courseId, search, pageable);
        // type conversion
        Page<Item> itemPage = itemEmptyQuesContPage.map(this.itemHelper::convert);
        CustomResponse<List<Item>> customResponse = CustomResponse.of(itemPage);
        return ResponseEntity.ok(customResponse);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItem(@PathVariable Long id) {
        Optional<Item> item = itemService.findById(id);
        return item.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * @deprecated 需要 course_id + questionNumber 才能取得試題
     * @param questionNumber
     * @return
     */
    @GetMapping("/item/qn/{questionNumber}")
    @Deprecated
    public Item getItemByQuestionNumber(@PathVariable int questionNumber) {
        Item item = itemService.findByQuestionNumber(questionNumber);
        return item;
    }

    /**
     * Get an item by a course id and a question number.
     * @return
     */
    @GetMapping("/courses/{course_id}/items/qn/{question_number}")
    public ResponseEntity<?> getItemByQuesNumAndCourseNum(
            @PathVariable("course_id") Long courseId,
            @PathVariable("question_number") String quesNum
    ){
        List<Item> items = this.itemService.getItemRepository().findByCourseIdAndQuestionNumber(
                courseId, quesNum
        );
        if (items.size() == 1) {
            CustomResponse<Item> customResponse = CustomResponse.of(items.get(0));
            return ResponseEntity.ok(customResponse);
        }
        else {
            // not exist or duplicate question_number in the course
            JSONObject resp = new JSONObject();
            resp.put("success", true);
            resp.put("data", JSONObject.NULL);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp.toString());
        }

    }



    @GetMapping("/item/qn/toHtml/{questionNumber}")
    public String getItemByQnToHtml(@PathVariable int questionNumber) {
        String itemContent = itemService.findByQnToHtml(questionNumber);
        return itemContent;
    }
    @GetMapping("/item/qn/toHtml/q/{questionNumber}")
    public String getItemByQnToHtmlOnlyQuestion(@PathVariable int questionNumber) {
        String itemContent = itemService.findByQnToHtmlOnlyQuestion(questionNumber);
        return itemContent;
    }



    /**
     * 回傳的 Body 中要有 status 及 id 欄位。 id 欄位為 DB 自動產生的 PK.
     * @param item
     * @return
     */
    @PostMapping("/items")
    public ResponseEntity<?> createItem(@RequestBody Item item) {
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
       Item savedItem =  itemService.saveItem(item);
        JSONObject resp = new JSONObject();
        resp.put("success", true);
        resp.put("id", savedItem.getId() );

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp.toString());
    }


    /**
     * 更新 item by item id
     * @param id
     * @param item
     * @return
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody Item item) {
        boolean resultFlag = itemService.changeItem(id, item);
        JSONObject resp = new JSONObject();
        resp.put("success", resultFlag?true:false);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp.toString());
    }

    /**
     * @deprecated 只有 get 時才有可能使用 course_id + questionNumber 取得試題。
     * See <a href= "https://github.com/hychen39/TestFormMaker/issues/60">https://github.com/hychen39/TestFormMaker/issues/60</a>
     * @param questionNumber
     * @param item
     * @return
     */
    @PutMapping("/item/qn/{questionNumber}")
    @Deprecated public ResponseEntity<?> updateItemByQuestionNumber(@PathVariable int questionNumber, @RequestBody Item item) {
        Item newitem = itemService.changeItemByQuestionNumber(questionNumber, item);
        return ResponseEntity.ok(newitem);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        boolean deleteFlag = itemService.deleteItem(id);
        JSONObject resp = new JSONObject();
        resp.put("success", deleteFlag?true:false);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp.toString());
//        return item.map(response -> ResponseEntity.ok().body(response))
//                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * @deprecated 只有 get 時才有可能使用 course_id + questionNumber 取得試題。
     * See <a href="https://github.com/hychen39/TestFormMaker/issues/60">https://github.com/hychen39/TestFormMaker/issues/60</a>
     * @param questionNumber
     * @return
     */
    @DeleteMapping("/item/qn/{questionNumber}")
    @Deprecated public ResponseEntity<?> deleteItemByQn(@PathVariable int questionNumber) {
        Item item = itemService.deleteItemByQn(questionNumber);
        return ResponseEntity.ok(item);
    }

}
