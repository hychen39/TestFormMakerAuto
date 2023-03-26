package com.xqdev.cyut_bkend_project.controller;

import com.xqdev.cyut_bkend_project.entity.TestForm;
import com.xqdev.cyut_bkend_project.service.testform.TestFormService;
import com.xqdev.cyut_bkend_project.service.testform.TestFormWithItems;
import com.xqdev.cyut_bkend_project.service.testform.TestFormWithTopics;
import lombok.Data;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(value = "/api")
@CrossOrigin
public class TestFormController {

    private TestFormService testFormService;

    @Autowired
    public TestFormController(TestFormService testFormService) {
        this.testFormService = testFormService;
    }

    private Logger logger = LoggerFactory.getLogger(TestFormController.class);


    @Data
    public static class TestFormRequestBody{
        private String title;
        private String summary;
        private Date date;
        private List<Long> items;
    }
    /**
     * 回傳 TestForm 清單，具分頁功能
     * API: /api/papers?itemsPerPage=15&page=1&search=
     *
     * Note: Search 參數並沒有使用到。
     * @return
     */
    @GetMapping("/papers")
    public CustomResponse findAllTestForms(
            @RequestParam int itemsPerPage,
            @RequestParam int page,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.by("id"));

        Page<TestFormWithTopics> queryPage = (search == null || search.isEmpty())?
                this.testFormService.findAllTestFormWithTopics(pageable):
                this.testFormService.findAllTestFormWithTopics(search, pageable);
        CustomResponse<List<TestFormWithTopics>> customResponse = new CustomResponse();
        customResponse.success = true;
        customResponse.data = queryPage.getContent();
        customResponse.pagination = new Pagination(
                queryPage.getTotalElements(),
                queryPage.getTotalPages(),
                queryPage.getSize(),
                queryPage.getNumber() + 1
        );
        return customResponse;
    }

    @GetMapping("/courses/{course_id}/papers")
    public ResponseEntity<?> findAllTestFormsByCourse(
            @PathVariable("course_id") Long courseId,
            @RequestParam int itemsPerPage,
            @RequestParam int page,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.by("id"));

        Page<TestFormWithTopics> queryPage = (search == null || search.isEmpty())?
                this.testFormService.findWithTopicsByCourseId(courseId, pageable):
                this.testFormService.findWithTopicsByCourseIdAndSearching(courseId, search, pageable);
        CustomResponse<List<TestFormWithTopics>> customResponse = CustomResponse.of(queryPage);

        return ResponseEntity.ok(customResponse);
    }

    /**
     * 讀取單一試卷. 回傳的試卷內有多個題目。
     * TestForm 內只存放其 item ids. Item 的內容要另外 fetch.
     * @param paper_id
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/papers/{paper_id}")
    public CustomResponse<TestFormWithItems> findTestForm(@PathVariable Long paper_id){
        CustomResponse<TestFormWithItems> customResponse = new CustomResponse<>();
        Optional<TestForm> testFormOptional = testFormService.getTestFormRepo().findById(paper_id);
        TestFormWithItems testFormWithItems = new TestFormWithItems(testFormOptional.get());
        // Fetch items by the given item id list.
        testFormWithItems.setItems(testFormService.findItems(testFormOptional.get().getItems()));
        customResponse.setData(testFormWithItems);
        customResponse.setPagination(null);
        return customResponse;
    }

    /**
     * 讀取單一試卷.
     * @return
     */
    @GetMapping("/courses/{course_id}/papers/{paper_id}")
    public ResponseEntity<?> findOneTestFormByCourseId(
            @PathVariable("course_id") Long courseId,
            @PathVariable("paper_id") Long paperId){
        Optional<TestForm> testFormOptional = testFormService.getTestFormRepo().findByCourseIdAndId(courseId, paperId);
        return testFormOptional.map( tf -> ResponseEntity.ok(tf)).orElse(
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/papers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CustomResponse<TestForm> create(@RequestBody TestForm testForm){
        testForm = testFormService.getTestFormRepo().save(testForm);
        logger.info(MessageFormat.format("The saved paper: {0}", testForm.toString()));
        CustomResponse<TestForm> customResponse = new CustomResponse<>();
        customResponse.setData(testForm);
        return customResponse;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/papers/{paper_id}")
    public ResponseEntity<String> delete(@PathVariable Long paper_id){
        Optional<TestForm> deleteTarget = testFormService.getTestFormRepo().findById(paper_id);
        boolean deleteStatus = false;
        if (deleteTarget.isPresent()) {
            testFormService.getTestFormRepo().deleteById(deleteTarget.get().getId());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            return ResponseEntity.ok(jsonObject.toString());
        } else {
            JSONObject jsonObject = new JSONObject();
            String msg = MessageFormat.format( "Can not find the given paper id {0, number}", paper_id);
            jsonObject.put("message", msg);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(jsonObject.toString());
        }


    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/papers/{paper_id}")
    public ResponseEntity<String> update(@PathVariable Long paper_id,
                                         @RequestBody TestForm testFormNew){
        JSONObject jsonObject = new JSONObject();
        TestForm testForm = testFormService.getTestFormRepo().getById(paper_id);
        if (testForm == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        //update non PK fields
        try {
            testForm.update(testFormNew);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
//        testForm.setCourseId(testFormNew.getCourseId());
//        testForm.setDescription(testFormNew.getDescription());
//        testForm.setCreatedDate(testFormNew.getCreatedDate());
//        testForm.setTitle(testFormNew.getTitle());
//        testForm.setItems(testFormNew.getItems());


        // Update the repository
        testFormService.getTestFormRepo().save(testForm);
        logger.info(MessageFormat.format("Updated paper: {0}", testForm));
        jsonObject.put("success", true);
        return ResponseEntity.ok(jsonObject.toString());
    }
}
