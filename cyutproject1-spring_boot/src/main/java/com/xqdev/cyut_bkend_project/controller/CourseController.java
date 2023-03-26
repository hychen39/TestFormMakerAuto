package com.xqdev.cyut_bkend_project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xqdev.cyut_bkend_project.entity.Course;
import com.xqdev.cyut_bkend_project.service.CourseService;
import com.xqdev.cyut_bkend_project.service.ItemService;
import com.xqdev.cyut_bkend_project.service.testform.TestFormService;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CourseController {

    @Data
    public static class CourseExtend {
        Long id;
        String name;
        String longDesc;
        long itemCount;
        long paperCount;
    }

    @Autowired
    public CourseController(CourseService courseService, ItemService itemService, TestFormService testFormService) {
        this.courseService = courseService;
        this.itemService = itemService;
        this.testFormService = testFormService;
    }

//    @Autowired
    private CourseService courseService;
//    @Autowired
    private ItemService itemService;
//    @Autowired
    private TestFormService testFormService;

    @GetMapping("/courses")
    public CustomResponse<List<CourseExtend>> getAllCourses(
            @RequestParam int itemsPerPage,
            @RequestParam int page,
            @RequestParam(required = false) String search
    ){
        // 使用  name column 排序
        Pageable pageable = PageRequest.of(page - 1,
                itemsPerPage,
                Sort.by("name").ascending());
        Page<Course> coursePage = courseService.getCourseRepo().findByNameContainingIgnoreCase(
                search, pageable);

        // convert Course to CourseExtend
        Page<CourseExtend> coursePageExtend = coursePage.map(this::convertToCourseExtend);

        CustomResponse<List<CourseExtend>> customResponse = CustomResponse.of(coursePageExtend);
        // Done 底下的 code 寫成 一個 function 來轉換型態
//        CustomResponse<List<Course>> customResponse = new CustomResponse<>();
//        customResponse.success = true;
//        customResponse.data = coursePage.getContent();
//        customResponse.pagination = new Pagination(
//                coursePage.getTotalElements(),
//                coursePage.getTotalPages(),
//                coursePage.getSize(),
//                coursePage.getNumber() + 1
//        );
        return customResponse;
    }

    private CourseExtend convertToCourseExtend (Course course){
        CourseExtend courseExtend = new CourseExtend();
        courseExtend.id = course.getId();
        courseExtend.name = course.getName();
        courseExtend.longDesc = course.getLongDesc();
        courseExtend.itemCount = itemService.getItemRepository().countByCourseId(course.getId());
        courseExtend.paperCount = testFormService.getTestFormRepo().countByCourseId(course.getId());
        return courseExtend;
    }

    @GetMapping("/courses/{course_id}")
    public CustomResponse<CourseExtend> getCourse(@PathVariable(name = "course_id") Long id){
        Optional<Course> course = courseService.getCourseRepo().findById(id);
        CourseExtend courseExtend = convertToCourseExtend(course.get());
        CustomResponse<CourseExtend> customResponse = CustomResponse.of(courseExtend);

        return customResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/courses/{course_id}")
    public ResponseEntity<String>  deleteCourse(@PathVariable(name="course_id") Long id){
        Optional<Course> course = courseService.getCourseRepo().findById(id);
        boolean status = false;
        String msg = "";

        if (course.isPresent()){
            courseService.getCourseRepo().deleteById(id);
            status = true;
        } else {
            msg =" Cannot find the given id: " + id;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", status);
        jsonObject.put("data", JSONObject.NULL);
        jsonObject.put("message", msg);
        return (status ? ResponseEntity.ok(jsonObject.toString()):
                ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(jsonObject.toString()));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/courses", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CustomResponse<Course> create(@RequestBody Course course){
        courseService.getCourseRepo().save(course);
        CustomResponse<Course> customResponse = CustomResponse.of(course);
        return customResponse;

    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/courses/{course_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@PathVariable(name="course_id") Long id,
                                         @RequestBody Course newCourse) throws JsonProcessingException {
        Optional<Course> course = courseService.getCourseRepo().findById(id);
//        boolean status = false;

        if (course.isEmpty())
           return ResponseEntity.badRequest().body("ID: " + id + " does not exist.");

        newCourse.setId(id);
        courseService.getCourseRepo().save(newCourse);

        CustomResponse<Course> customResponse = CustomResponse.of(newCourse);
        return ResponseEntity.ok((new ObjectMapper()).writeValueAsString(customResponse));

    }
}
