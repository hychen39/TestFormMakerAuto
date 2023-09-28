package com.xqdev.cyut_bkend_project.repository;

import com.xqdev.cyut_bkend_project.entity.LinkedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkedItemRepo extends JpaRepository<LinkedItem, Long> {
    List<LinkedItem> findByItemIdAndLinkedItemId(Long itemId, Long linkItemId);

    /**
     * Find all linked items for a given item id
     */
    List<LinkedItem> findByItemId(Long itemId);
}
