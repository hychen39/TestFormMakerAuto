package com.xqdev.cyut_bkend_project.controller;

import lombok.Data;

/**
 * 前端要求的 pagination 格式
 */
@Data
public class Pagination {
    /** total element*/
    long total;
    /** total pages */
    int totalPages;
    /** number of elements per page */
    int perPage;
    /** current page*/
    int currentPage;

    public Pagination(long total, int totalPages, int perPage, int currentPage) {
        this.total = total;
        this.totalPages = totalPages;
        this.perPage = perPage;
        this.currentPage = currentPage;
    }
}
