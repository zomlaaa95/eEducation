package com.eEducation.ftn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eEducation.ftn.model.Course;
import com.eEducation.ftn.repository.CourseRepository;

@Service
public class CourseService {
	@Autowired
	CourseRepository courseRepository;
	
	public Course findOne(Integer id) {
		return courseRepository.findOne(id);
	}

	public List<Course> findAll() {
		return courseRepository.findAll();
	}
	
	public Page<Course> findAll(Pageable page) {
		return courseRepository.findAll(page);
	}

	public Course save(Course course) {
		return courseRepository.save(course);
	}

	public void remove(Integer id) {
		courseRepository.delete(id);
	}
}
