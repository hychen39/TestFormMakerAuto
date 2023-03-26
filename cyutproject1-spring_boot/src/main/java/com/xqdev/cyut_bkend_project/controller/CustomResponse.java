package com.xqdev.cyut_bkend_project.controller;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 前端要求的 Response 格式
 */
@Data
public class CustomResponse<T> {
    boolean success = true;
    T data;
    Pagination pagination;

    /**
     * Convert from {@code Page<T>} to {@code CustomResponse<List<T>>} object.
     * The {@link CustomResponse#success} field defaults to true.
     * @param page {@code org.springframework.data.domain.Page} object
     * @param <T>
     * @return
     */
    public static <T> CustomResponse<List<T>> of(Page<T> page){
       CustomResponse<List<T>> customResponse = new CustomResponse<>();
       customResponse.success = true;
       customResponse.data = page.getContent();
       customResponse.pagination = new Pagination(
         page.getTotalElements(),
         page.getTotalPages(),
         page.getSize(),
         page.getNumber() + 1
       );
       return customResponse;
    }

    public static <T> CustomResponse<T> of(T obj){
        CustomResponse<T> customResponse = new CustomResponse<>();
        customResponse.success = true;
        customResponse.data = obj;
        customResponse.pagination = null;
        return customResponse;
    }
}
