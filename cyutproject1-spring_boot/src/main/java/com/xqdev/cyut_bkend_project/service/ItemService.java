package com.xqdev.cyut_bkend_project.service;

import com.xqdev.cyut_bkend_project.entity.Item;
import com.xqdev.cyut_bkend_project.repository.ItemRepository;
import lombok.Getter;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    @Getter
    private ItemRepository itemRepository;

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Collection<Item> findAll() {
        return itemRepository.findAll();
    }

    /**
     *
     * @param pageable
     * @return
     */
    public Page<Item> findAll(Pageable pageable){
        return itemRepository.findAll(pageable);
    }

    public Page<Item> findAll(String searchTerm, Pageable pageable){
        return itemRepository.findItemsContain(searchTerm, pageable);
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item findByQuestionNumber(int questionNumber) {
        return itemRepository.findByQuestionNumber(questionNumber);
    }

    public String findByQnToHtml(int questionNumber) {
        Item item = itemRepository.findByQuestionNumber(questionNumber);
        Parser parser = Parser.builder().build();
		Node document = parser.parse(item.getContent());
		HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        return htmlRenderer.render(document);
    }

    public String findByQnToHtmlOnlyQuestion(int questionNumber) {
        Item item = itemRepository.findByQuestionNumber(questionNumber);
        Parser parser = Parser.builder().build();
		Node document = parser.parse(item.getQuestion());
		HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        return htmlRenderer.render(document);
    }

    /**
     *
     * @param id
     * @param newItem
     * @return True if update successfully; Otherwise return false.
     */
    public boolean changeItem(Long id, Item newItem) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item != null) {
           item.update(newItem);
           this.getItemRepository().save(item);
           return true;
        }
        return false;
    }


    /**
     * @deprecated Since 2022/12/22. questionNumber 不是 PK，需要配合 course_id 才能找到 unique item.
     * See See <a href= "https://github.com/hychen39/TestFormMaker/issues/60">https://github.com/hychen39/TestFormMaker/issues/60</a>.
     *
     * @param questionNumber
     * @param item
     * @return
     */
    @Deprecated
    public Item changeItemByQuestionNumber(int questionNumber, Item item) {
        Item updateItem = itemRepository.findByQuestionNumber(questionNumber);
        if (updateItem != null) {
            updateItem.setQuestionNumber(item.getQuestionNumber());
            updateItem.setTitle(item.getTitle());
            updateItem.setContent(item.getContent());
            updateItem.setQuestion(item.getQuestion());
            updateItem.setA(item.getA());
            updateItem.setB(item.getB());
            updateItem.setC(item.getC());
            updateItem.setMaxInfoTheta(item.getMaxInfoTheta());
            updateItem.setTopics(null);
            updateItem.setTopics(item.getTopics());
        }
        return itemRepository.save(updateItem);
    }

    public boolean deleteItem(Long id) {
        if (itemRepository.findById(id).isPresent()) {
            // Optional<Item> item = itemRepository.findById(id);
            itemRepository.deleteById(id);
            return true;
        } else
            return false;
    }

    public Item deleteItemByQn(int questionNumber) {
        if (itemRepository.findByQuestionNumber(questionNumber) != null) {
            Item item = itemRepository.findByQuestionNumber(questionNumber);
            itemRepository.delete(item);
            return null;
        }
        return null;
    }

}
