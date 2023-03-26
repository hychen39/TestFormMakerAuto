package com.xqdev.cyut_bkend_project.entity;

import org.springframework.beans.factory.annotation.Value;

/**
 * Item that excludes the content field.
 * 使用 JPA Projection: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections
 */
public interface ItemNoContent {
    Long getId();
    String getQuestionNumber();
    String getTitle();
    double getA();
    double getB();
    double getC();
    double getMaxInfoTheta();
//    List<Topic> getTopics();
    Long getCourseId();
    @Value("#{'LONG TEXT'}")
    String getContent();
    @Value("#{'LONG TEXT'}")
    String getQuestion();
}
