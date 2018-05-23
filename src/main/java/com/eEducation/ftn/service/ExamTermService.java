package com.eEducation.ftn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eEducation.ftn.model.ExamTerm;
import com.eEducation.ftn.repository.ExamTermRepository;

@Service
public class ExamTermService {
	@Autowired
	ExamTermRepository examTermRepository;
	
	public ExamTerm findOne(Integer id) {
		return examTermRepository.findOne(id);
	}

	public List<ExamTerm> findAll() {
		return examTermRepository.findAll();
	}
	
	public Page<ExamTerm> findAll(Pageable page) {
		return examTermRepository.findAll(page);
	}

	public ExamPeriod save(ExamTerm examTerm) {
		return examTermRepository.save(examTerm);
	}

	public void remove(Integer id) {
		examTermRepository.delete(id);
	}
}
