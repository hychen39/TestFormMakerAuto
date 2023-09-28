package com.xqdev.cyut_bkend_project.controller;

import com.xqdev.cyut_bkend_project.entity.Item;
import com.xqdev.cyut_bkend_project.entity.ItemEmptyQuesCont_I;
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

    /**
     * 將沒有 content 及 topic 的 item 轉成有 topic 的 item.
     * @param itemEmptyQuesCont
     * @return
     */
    public Item convert(ItemEmptyQuesCont_I itemEmptyQuesCont){
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
