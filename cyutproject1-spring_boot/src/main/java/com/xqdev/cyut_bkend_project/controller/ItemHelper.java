package com.xqdev.cyut_bkend_project.controller;

import com.xqdev.cyut_bkend_project.entity.Item;
import com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont;
import com.xqdev.cyut_bkend_project.entity.Topic;
import com.xqdev.cyut_bkend_project.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Helper class for Item class conversion.
 *
 */
@Component
public class ItemHelper {
//    private ItemService itemService;
    private ItemRepository itemRepository;
    @Autowired
    public ItemHelper(ItemRepository itemRepository) {
//        this.itemService = itemService;
        this.itemRepository = itemRepository;
    }

    public Item convert(ItemEmptyQuesCont itemEmptyQuesCont){
        Item item = new Item();
        item.setId(itemEmptyQuesCont.getId());
        item.setTitle(itemEmptyQuesCont.getTitle());
        item.setQuestionNumber(itemEmptyQuesCont.getQuestionNumber());
        item.setA(itemEmptyQuesCont.getA());
        item.setB(itemEmptyQuesCont.getB());
        item.setC(itemEmptyQuesCont.getC());
        item.setMaxInfoTheta(itemEmptyQuesCont.getMaxInfoTheta());
        item.setCourseId(itemEmptyQuesCont.getCourseId());
        item.setContent("");
        List<Topic> topics= this
                .itemRepository
                .findTopicsByItemId(item.getId());
        item.setTopics(topics);
        return item;
    }
}
