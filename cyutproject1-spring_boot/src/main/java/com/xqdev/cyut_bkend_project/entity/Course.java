package com.xqdev.cyut_bkend_project.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 課程 entity
 */
@Entity
@Data
@Table(name="course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Course name
    private String name;

    // 課程說明
    @Column(name = "long_desc", length = 2000)
    private String longDesc;
}
