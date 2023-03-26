package com.xqdev.cyut_bkend_project.repository;

import com.xqdev.cyut_bkend_project.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Course Repository
 */

@Repository
public interface CourseRepo extends JpaRepository<Course, Long> {

    Page<Course> findByNameContainingIgnoreCase(String search, Pageable pageable);
    void deleteById(Long id);
}
