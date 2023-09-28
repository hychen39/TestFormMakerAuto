package com.xqdev.cyut_bkend_project.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Item 間的 link type 名稱
 */
@Entity
@Data
@Table(name = "link_types")
public class LinkType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 關係名稱
     */
    @Column(name = "name")
    private String name;
}
