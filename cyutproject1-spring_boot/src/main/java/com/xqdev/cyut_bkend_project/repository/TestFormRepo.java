package com.xqdev.cyut_bkend_project.repository;

import com.xqdev.cyut_bkend_project.entity.TestForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TestFormRepo extends JpaRepository<TestForm, Long> {

    /**
     * Search the title and description fields of the TestForm object
     * according to the given search term.
     * @param searchTerm search term
     * @return
     */
    @Query("select tf from TestForm tf where tf.title like %?1% " +
            "or tf.description like %?1% order by tf.id ")
    Page<TestForm> findTestFormContain(@Param("searchTerm") String searchTerm, Pageable pageable);

    long countByCourseId(Long courseId);

    @Query("select tf from TestForm tf where tf.courseId = ?1 and (tf.title like %?2% " +
            "or tf.description like %?2%) order by tf.id ")
    Page<TestForm> findByCourseIdAndSearch(Long courseId, String searchTerm, Pageable pageable);

    Page<TestForm> findByCourseId(Long courseId, Pageable pageable);

    /**
     *
     * @param courseId
     * @param id test form id
     * @return
     */
    Optional<TestForm> findByCourseIdAndId(Long courseId, Long id);
}
