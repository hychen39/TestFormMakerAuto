package com.xqdev.cyut_bkend_project.controller;

import com.xqdev.cyut_bkend_project.entity.LinkType;
import com.xqdev.cyut_bkend_project.repository.LinkTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class LinkTypeController {
    private final LinkTypeRepo linkTypeRepo;

    @Autowired
    public LinkTypeController(LinkTypeRepo linkTypeRepo) {
        this.linkTypeRepo = linkTypeRepo;
    }

    @GetMapping("/linked-types")
    public ResponseEntity<?> getAllTypes(){

        List<LinkType> linkTypeList = this.linkTypeRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));

        CustomResponse<List<LinkType>> customResponse = CustomResponse.of(linkTypeList);
        return ResponseEntity.ok(customResponse);
    }


}
