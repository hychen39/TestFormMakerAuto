package com.xqdev.cyut_bkend_project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont;
import com.xqdev.cyut_bkend_project.entity.LinkType;
import com.xqdev.cyut_bkend_project.entity.LinkedItem;
import com.xqdev.cyut_bkend_project.entity.Topic;
import com.xqdev.cyut_bkend_project.repository.ItemRepository;
import com.xqdev.cyut_bkend_project.repository.LinkTypeRepo;
import com.xqdev.cyut_bkend_project.repository.LinkedItemRepo;
import lombok.Data;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 連結 2 個 item
 */
@RestController
@RequestMapping("/api")
public class LinkItemController {
    private final LinkedItemRepo linkedItemRepo;
    private final LinkTypeRepo linkTypeRepo;
    private final ItemRepository itemRepo;

    /**
     * 題目關聯 [GET] /api/items/{item_id}/links response 所需要的 Data
     */
    @Data
     public class LinkedItemDTO {
        /**
         * Association id
         */
        private Long id;
        private String title;
        private String questionNumber;
        private Long linkedItemId;
        private LinkType linkType;
        List<Topic> topics;
    };

    @Autowired
    public LinkItemController(LinkedItemRepo linkedItemRepo, LinkTypeRepo linkTypeRepo, ItemRepository itemRepo) {
        this.linkedItemRepo = linkedItemRepo;
        this.linkTypeRepo = linkTypeRepo;
        this.itemRepo = itemRepo;
    }

    @PutMapping("/items/{item_id}/linked")
    public ResponseEntity<?> addLink(@PathVariable(name = "item_id") Long itemId,
                                    @RequestBody String jsonObjectData){
        JSONObject parsedJsonObject = new JSONObject(jsonObjectData);

        LinkedItem linkedItem = new LinkedItem();
        linkedItem.setItemId(itemId);
        try {
            linkedItem.setLinkedItemId(parsedJsonObject.getLong("linked_item_id"));
            Optional<LinkType> linkType = this.linkTypeRepo.findById(parsedJsonObject.getLong("link_type_id"));
            linkedItem.setLinkType(linkType.get());
        } catch (JSONException jsonException){
            JSONObject resp = new JSONObject();
            resp.put("success", false);
            resp.put("err_msg", jsonException.toString());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp.toString());
        }

       LinkedItem savedLinkedItem = this.linkedItemRepo.saveAndFlush(linkedItem);

        JSONObject resp = new JSONObject();

       if (savedLinkedItem.getItemId() == null){
           // save failed
           resp.put("success", false);
       } else
       {
           resp.put("success", true);
           resp.put("linked_item_pk", savedLinkedItem.getId());
       }

       return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp.toString());
    }

    @DeleteMapping("/items/{item_id}/linked/{linked_item_id}")
    public ResponseEntity<?> deleteLink(@PathVariable("item_id") Long item_id,
                                        @PathVariable("linked_item_id") Long linkedItemId){

        List<LinkedItem> linkedItemList = this.linkedItemRepo.findByItemIdAndLinkedItemId(item_id, linkedItemId);
        this.linkedItemRepo.deleteAll(linkedItemList);

        JSONObject resp = new JSONObject();
        resp.put("success", true);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp.toString());
    }

    /**
     * Find all related linked items for an item.
     * @return
     */
    @GetMapping("/items/{item_id}/links")
    public ResponseEntity<?> findLinks(@PathVariable("item_id") Long itemId){
        try {
            // Fetch the IDs of the linked items.
            List<LinkedItem> linkedItemList = this.linkedItemRepo.findByItemId(itemId);
            // Convert List<LinkedItem> to List<LinkedItemDTO>
            List<LinkedItemDTO> linkedItemDTOList = linkedItemList.stream().map((itemRelation) -> {
                LinkedItemDTO linkedItemDTO = new LinkedItemDTO();
                // Find linked item's title and questionNumber
                Optional<ItemEmptyQuesCont> itemEmptyQuesCont = this.itemRepo.findItemEmptyQuesContById(itemRelation.getLinkedItemId());
                linkedItemDTO.setId(itemRelation.getId());
                linkedItemDTO.setLinkedItemId(itemRelation.getLinkedItemId());
                linkedItemDTO.setQuestionNumber(itemEmptyQuesCont.get().getQuestionNumber());
                linkedItemDTO.setTitle(itemEmptyQuesCont.get().getTitle());
                // set the link type
                linkedItemDTO.setLinkType(itemRelation.getLinkType());
                // Find the topics for the item.
                List<Topic> topicList = this.itemRepo.findTopicsByItemId(itemRelation.getLinkedItemId());
                linkedItemDTO.setTopics(topicList);
                return linkedItemDTO;
            }).toList();
            // Create the response entity
//            String data = new ObjectMapper().writeValueAsString(linkedItemDTOList);

            List<JSONObject> linkedItemDTOJson = linkedItemDTOList.stream().map((item) -> {
                try {
                    // 為了使用 Topic entity 中的 @JsonProperty 產生出正確的 json key 值，所以使用 ObjectMapper
                    // 先轉換成 JSONObject
                    JSONObject itemJson = new JSONObject(new ObjectMapper().writeValueAsString(item));
                    return itemJson;
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).toList();

            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("data", linkedItemDTOJson);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response.toString());
        } catch (Exception exception) {
            JSONObject response = new JSONObject();
            response.put("success", false);
            response.put("err_msg", exception.toString());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response.toString());

        }
    }
}
