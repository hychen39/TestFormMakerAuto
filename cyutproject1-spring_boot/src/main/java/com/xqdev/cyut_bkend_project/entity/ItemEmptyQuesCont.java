package com.xqdev.cyut_bkend_project.entity;

import lombok.Value;

/**
 * Item with no question content and topics.
 * item.topics is the List type. The type cannot be used in DTO object in
 * the JPQL.
 * Use as a DTO (Data-To-Object)
 * Ref: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections (class-based projection)
 */
@Value
public class ItemEmptyQuesCont implements ItemEmptyQuesCont_I {
    Long id;
    String questionNumber;
    String title;
    double a;
    double b;
    double c;
    double maxInfoTheta;
    Long courseId;
}
