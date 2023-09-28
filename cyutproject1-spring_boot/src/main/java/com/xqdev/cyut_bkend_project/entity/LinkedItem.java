package com.xqdev.cyut_bkend_project.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 兩個 Item 間關係的描述
 */
@Entity
@Data
@Table(name = "linked_items", uniqueConstraints = {
        @UniqueConstraint( name = "UniqueLinkRelation",
                columnNames =  {"item_id", "linked_item_id"})
})
public class LinkedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "linked_item_id")
    private Long linkedItemId;

//    @Column(name = "link_type_id")
//    private Long linkTypeId;

    @OneToOne
    private LinkType linkType;
}
