package com.eEducation.ftn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eEducation.ftn.model.CollegeDirection;

@Repository
public interface CollegeDirectionRepository extends JpaRepository<CollegeDirection, Long> {

}
