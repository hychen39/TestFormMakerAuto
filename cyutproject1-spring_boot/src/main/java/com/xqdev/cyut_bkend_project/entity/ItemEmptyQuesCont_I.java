package com.xqdev.cyut_bkend_project.entity;
/**
回傳 Item，但不包含 content 欄位。
用於 Native query 的 Interface projection.
Ref:  https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections.interfaces
 */
public interface ItemEmptyQuesCont_I {
    Long getId();

    String getQuestionNumber();

    String getTitle();

    double getA();

    double getB();

    double getC();

    double getMaxInfoTheta();

    Long getCourseId();

    boolean equals(Object o);

    int hashCode();

    String toString();
}
