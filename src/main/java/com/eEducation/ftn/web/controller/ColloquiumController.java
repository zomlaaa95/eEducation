package com.eEducation.ftn.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.eEducation.ftn.model.Colloquium;
import com.eEducation.ftn.model.Course;
import com.eEducation.ftn.repository.ColloquiumRepository;
import com.eEducation.ftn.service.ColloquiumService;
import com.eEducation.ftn.service.CourseService;
import com.eEducation.ftn.web.dto.ColloquiumDTO;

@RestController
@RequestMapping(value="api/course/{courseId}/colloquiums")
public class ColloquiumController {
	
	private static final Logger logger = LoggerFactory.getLogger(ColloquiumController.class);
	
	@Autowired
	ColloquiumService colloquiumService;
	
	@Autowired
	ColloquiumRepository colloquiumRepository;
	
	@Autowired
	CourseService courseService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<ColloquiumDTO>> getAll(@PathVariable Long courseId){
		Course course = courseService.findOne(courseId);
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<Colloquium> colloquiums = colloquiumService.findAll();
		List<ColloquiumDTO> colloquiumDTOs = new ArrayList<>();
		
		for(Colloquium c : colloquiums){
			colloquiumDTOs.add(new ColloquiumDTO(c));
		}
		
		return new ResponseEntity<>(colloquiumDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public ResponseEntity<ColloquiumDTO> getById(@PathVariable Long courseId, @PathVariable Long id){
		Course course = courseService.findOne(courseId);
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Colloquium found = colloquiumService.findOne(id);
		if(found == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(new ColloquiumDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes="application/json")
	public ResponseEntity<ColloquiumDTO> add(@RequestBody ColloquiumDTO colloquium, @PathVariable Long courseId){
		Course course = courseService.findOne(courseId);
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Colloquium newColloquium = new Colloquium();		
		newColloquium.setCourse(course);
		newColloquium.setMaxPoints(colloquium.getMaxPoints());
		newColloquium.setExamType(colloquium.getExamType());
		newColloquium.setExamDateTime(colloquium.getExamDateTime());
		
		colloquiumService.save(newColloquium);
		return new ResponseEntity<>(new ColloquiumDTO(newColloquium), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.PUT, consumes="application/json", value="/{id}")
	public ResponseEntity<ColloquiumDTO> update(@RequestBody ColloquiumDTO colloquium, @PathVariable Long courseId){
		Course course = courseService.findOne(courseId);
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Colloquium found = colloquiumService.findOne(colloquium.getId());
		if(found == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		// not allowed to change course
		
		found.setMaxPoints(colloquium.getMaxPoints());
		found.setExamType(colloquium.getExamType());
		found.setExamDateTime(colloquium.getExamDateTime());
		
		colloquiumService.save(found);
		return new ResponseEntity<>(new ColloquiumDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Long id, @PathVariable Long courseId){
		Course course = courseService.findOne(courseId);
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Colloquium found = colloquiumService.findOne(id);
		if(found != null){
			try {
				colloquiumService.remove(id);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/byCourse")
	public ResponseEntity<List<ColloquiumDTO>> getByCourse(@PathVariable Long courseId){
		Course course = courseService.findOne(courseId);
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<Colloquium> colloquiums = colloquiumRepository.findByCourse(course);
		List<ColloquiumDTO> colloquiumDTOs = new ArrayList<>();
		
		for(Colloquium c : colloquiums){
			colloquiumDTOs.add(new ColloquiumDTO(c));
		}
		
		return new ResponseEntity<>(colloquiumDTOs, HttpStatus.OK);
	}
	
}
