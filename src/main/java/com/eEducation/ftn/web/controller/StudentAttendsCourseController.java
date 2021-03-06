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

import com.eEducation.ftn.model.Course;
import com.eEducation.ftn.model.Student;
import com.eEducation.ftn.model.StudentAttendsCourse;
import com.eEducation.ftn.repository.StudentAttendsCourseRepository;
import com.eEducation.ftn.service.CourseService;
import com.eEducation.ftn.service.StudentAttendsCourseService;
import com.eEducation.ftn.service.StudentService;
import com.eEducation.ftn.web.dto.StudentAttendsCourseDTO;
import com.eEducation.ftn.web.dto.StudentDTO;

@RestController
@RequestMapping(value="api/studentAttendsCourse")
public class StudentAttendsCourseController {

	private static final Logger logger = LoggerFactory.getLogger(StudentAttendsCourseController.class);
	
	@Autowired
	StudentAttendsCourseService sacService;
	
	@Autowired
	StudentAttendsCourseRepository sacRepository;
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	CourseService courseService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<StudentAttendsCourseDTO>> getAll(){
		List<StudentAttendsCourse> sacS = sacService.findAll();
		List<StudentAttendsCourseDTO> sacDTOs = new ArrayList<>();
		
		for(StudentAttendsCourse sac : sacS) {
			sac.getStudent().setSPassword("");
			sacDTOs.add(new StudentAttendsCourseDTO(sac));
		}
		
		logger.info("sac - returned all");
		
		return new ResponseEntity<>(sacDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public ResponseEntity<StudentAttendsCourseDTO> getById(@PathVariable Long id){
		StudentAttendsCourse found = sacService.findOne(id);
		if(found == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		found.getStudent().setSPassword("");
		
		logger.info("sac - found by id " + id);
		
		return new ResponseEntity<>(new StudentAttendsCourseDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes="application/json")
	public ResponseEntity<StudentAttendsCourseDTO> add(@RequestBody StudentAttendsCourseDTO sac){
		StudentAttendsCourse newSac = new StudentAttendsCourse();
		
		if(sac.getStudent() == null || sac.getCourse() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Student student = studentService.findOne(sac.getStudent().getId());
		Course course = courseService.findOne(sac.getCourse().getId());
		
		if(student == null || course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		newSac.setStudent(student);
		newSac.setCourse(course);
		
		sacService.save(newSac);
		
		newSac.getStudent().setSPassword("");
		
		logger.info("sac - created new, course is " + course.getName() + " and student is " + student.getEmail());
		
		return new ResponseEntity<>(new StudentAttendsCourseDTO(newSac), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.PUT, consumes="application/json", value="/{id}")
	public ResponseEntity<StudentAttendsCourseDTO> update(@RequestBody StudentAttendsCourseDTO sac){
		StudentAttendsCourse found = sacService.findOne(sac.getId());
		if(found == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if(sac.getStudent() == null || sac.getCourse() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Student student = studentService.findOne(sac.getStudent().getId());
		Course course = courseService.findOne(sac.getCourse().getId());
		
		if(student == null || course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		found.setStudent(student);
		found.setCourse(course);
		
		sacService.save(found);
		
		found.getStudent().setSPassword("");
		
		logger.info("sac - updated sac with id " + sac.getId());
		
		return new ResponseEntity<>(new StudentAttendsCourseDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Long id){
		StudentAttendsCourse found = sacService.findOne(id);
		if(found != null) {
			logger.info("sac - deleted sac with id " + id);
			sacService.remove(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	// collection methods
	
	@RequestMapping(method=RequestMethod.POST, consumes="application/json", value="/{courseId}/batchAdd")
	public ResponseEntity<Void> batchAdd(@PathVariable Long courseId, @RequestBody List<StudentDTO> students){
		Course course = courseService.findOne(courseId);
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		for (StudentDTO student : students) {
			StudentAttendsCourse newSac = new StudentAttendsCourse();
			newSac.setCourse(course);
			
			Student foundStudent = studentService.findOne(student.getId());
			if(foundStudent == null) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			newSac.setStudent(foundStudent);
			
			sacService.save(newSac);
		}
		
		logger.info("sac - batch added sac for course " + course.getName());
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes="application/json", value="/{courseId}/batchRemove")
	public ResponseEntity<Void> batchRemove(@PathVariable Long courseId, @RequestBody List<StudentDTO> students){
		Course course = courseService.findOne(courseId);
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		for (StudentDTO student : students) {
			Student foundStudent = studentService.findOne(student.getId());
			if(foundStudent == null) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			StudentAttendsCourse foundSac = sacRepository.findByStudentAndCourse(foundStudent, course);
			
			sacService.remove(foundSac.getId());
		}
		
		logger.info("sac - batch removed sac for course " + course.getName());
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}