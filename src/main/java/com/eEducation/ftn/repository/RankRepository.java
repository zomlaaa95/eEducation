package com.eEducation.ftn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eEducation.ftn.model.Rank;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long>{

}
