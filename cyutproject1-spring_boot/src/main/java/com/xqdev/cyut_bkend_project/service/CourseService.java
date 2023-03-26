package com.xqdev.cyut_bkend_project.service;

import com.xqdev.cyut_bkend_project.entity.Course;
import com.xqdev.cyut_bkend_project.repository.CourseRepo;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
    @Autowired
    @Getter
    private CourseRepo  courseRepo;

    public Page<Course> findAll(Pageable pageable){
        return courseRepo.findAll(pageable);
    }


}
