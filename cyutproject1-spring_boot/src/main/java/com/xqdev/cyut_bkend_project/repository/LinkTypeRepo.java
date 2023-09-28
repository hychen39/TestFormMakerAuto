package com.xqdev.cyut_bkend_project.repository;

import com.xqdev.cyut_bkend_project.entity.LinkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkTypeRepo extends JpaRepository<LinkType, Long> {
}
