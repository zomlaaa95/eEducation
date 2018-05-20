package com.eEducation.ftn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eEducation.ftn.model.CourseLesson;
import com.eEducation.ftn.repository.CourseLessonRepository;

@Service
public class CourseLessonService {
	@Autowired
	CourseLessonRepository courseLessonRepository;
	
	public CourseLesson findOne(Integer id) {
		return courseLessonRepository.findOne(id);
	}

	public List<CourseLesson> findAll() {
		return courseLessonRepository.findAll();
	}
	
	public Page<CourseLesson> findAll(Pageable page) {
		return courseLessonRepository.findAll(page);
	}

	public CourseLesson save(CourseLesson courseLesson) {
		return courseLessonRepository.save(courseLesson);
	}

	public void remove(Integer id) {
		courseLessonRepository.delete(id);
	}
}
