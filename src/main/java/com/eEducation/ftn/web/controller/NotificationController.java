package com.eEducation.ftn.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.eEducation.ftn.model.Course;
import com.eEducation.ftn.model.CourseFile;
import com.eEducation.ftn.model.Notification;
import com.eEducation.ftn.model.Student;
import com.eEducation.ftn.model.StudentAttendsCourse;
import com.eEducation.ftn.repository.NotificationRepository;
import com.eEducation.ftn.repository.StudentAttendsCourseRepository;
import com.eEducation.ftn.service.CourseFileService;
import com.eEducation.ftn.service.CourseService;
import com.eEducation.ftn.service.NotificationService;
import com.eEducation.ftn.service.StudentAttendsCourseService;
import com.eEducation.ftn.service.StudentService;
import com.eEducation.ftn.web.dto.NotificationDTO;

@RestController
@RequestMapping(value="api/notifications")
public class NotificationController {
	@Autowired
	NotificationService notificationService;
	
	@Autowired
	NotificationRepository notificationRepository;
	
	@Autowired
	CourseFileService courseFileService;
	
	@Autowired
	CourseService courseService;
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	StudentAttendsCourseService sacService;
	
	@Autowired
	StudentAttendsCourseRepository sacRepository;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<NotificationDTO>> getAll(){
		List<Notification> notifications = notificationService.findAll();
		List<NotificationDTO> notificationDTOs = new ArrayList<>();
		
		for(Notification n : notifications) {
			notificationDTOs.add(new NotificationDTO(n));
		}
		
		return new ResponseEntity<>(notificationDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public ResponseEntity<NotificationDTO> getById(@PathVariable Long id){
		Notification found = notificationService.findOne(id);
		if(found == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(new NotificationDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/{id}/read")
	public ResponseEntity<NotificationDTO> readNotification(@PathVariable Long id){
		Notification found = notificationService.findOne(id);
		if(found == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		// set notification to seen
		found.setSeen(true);
		notificationService.save(found);
		
		return new ResponseEntity<>(new NotificationDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes="application/json")
	public ResponseEntity<NotificationDTO> add(@RequestBody NotificationDTO notification){
		Notification newNotification = new Notification();
		newNotification.setMessage(notification.getMessage());
		newNotification.setNDate(notification.getnDate());
		
		if(notification.getCourse() == null || notification.getDocument() == null || notification.getStudent() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Course course = courseService.findOne(notification.getCourse().getId());
		CourseFile courseFile = courseFileService.findOne(notification.getDocument().getId());
		Student student = studentService.findOne(notification.getStudent().getId());
		
		if(course == null || courseFile == null || student == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		// seen is false on creation - student hasn't seen it yet
		newNotification.setSeen(false);
		
		newNotification.setCourse(course);
		newNotification.setDocument(courseFile);
		newNotification.setStudent(student);
		
		notificationService.save(newNotification);
		return new ResponseEntity<>(new NotificationDTO(newNotification), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes="application/json", value="/batchAdd")
	public ResponseEntity<Void> batchAdd(@RequestBody NotificationDTO notification){
		if(notification.getCourse() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Course course = courseService.findOne(notification.getCourse().getId());
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<StudentAttendsCourse> sacS = sacRepository.findByCourse(course);
		
		for(StudentAttendsCourse sac : sacS) {
			Notification newNotification = new Notification();
			
			newNotification.setMessage(notification.getMessage());
			newNotification.setNDate(notification.getnDate());
			
			CourseFile courseFile = courseFileService.findOne(notification.getDocument().getId());
			
			// seen is false on update - student hasn't seen it yet
			newNotification.setSeen(false);
			
			newNotification.setCourse(course);
			newNotification.setDocument(courseFile);
			newNotification.setStudent(sac.getStudent());
			
			notificationService.save(newNotification);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.PUT, consumes="application/json", value="/batchUpdate")
	public ResponseEntity<NotificationDTO> batchUpdate(@RequestBody NotificationDTO notification){
		if(notification.getCourse() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		String[] newAndOldMessageParts = notification.getMessage().split("|");
		String newMessage = newAndOldMessageParts[0];
		String oldMessage = newAndOldMessageParts[1];
		
		notification.setMessage(newMessage);
		
		Course course = courseService.findOne(notification.getCourse().getId());
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<Notification> notifications = notificationRepository.findByCourseAndMessage(course, oldMessage);
		
		for(Notification nForUpdate : notifications) {
			nForUpdate.setMessage(notification.getMessage());
			nForUpdate.setNDate(notification.getnDate());
			
			CourseFile courseFile = courseFileService.findOne(notification.getDocument().getId());
			
			// seen is false on update - student hasn't seen it yet
			nForUpdate.setSeen(false);
			nForUpdate.setDocument(courseFile);
			
			// not allowed to change course and student
			
			notificationService.save(nForUpdate);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.PUT, consumes="application/json", value="/{id}")
	public ResponseEntity<NotificationDTO> update(@RequestBody NotificationDTO notification){
		Notification found = notificationService.findOne(notification.getId());
		found.setMessage(notification.getMessage());
		found.setNDate(notification.getnDate());
		
		// not allowed to change course or student
		
		if(notification.getDocument() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		CourseFile courseFile = courseFileService.findOne(notification.getDocument().getId());
		
		if(courseFile == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		found.setSeen(false);
		found.setDocument(courseFile);
		
		notificationService.save(found);
		return new ResponseEntity<>(new NotificationDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Long id){
		Notification found = notificationService.findOne(id);
		if(found != null) {
			notificationService.remove(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/student/{studentId}")
	public ResponseEntity<List<NotificationDTO>> getByStudent(@PathVariable Long studentId){
		Student student = studentService.findOne(studentId);
		if(student == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		List<Notification> notifications = notificationRepository.findByStudent(student);
		List<NotificationDTO> notificationDTOs = new ArrayList<>();
		
		for(Notification n : notifications) {
			notificationDTOs.add(new NotificationDTO(n));
		}
		
		return new ResponseEntity<>(notificationDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/course/{courseId}/student/{studentId}")
	public ResponseEntity<List<NotificationDTO>> getByCourseAndStudent(@PathVariable Long studentId, @PathVariable Long courseId){
		Student student = studentService.findOne(studentId);
		if(student == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		Course course = courseService.findOne(courseId);
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		List<Notification> notifications = notificationRepository.findByCourseAndStudent(course, student);
		List<NotificationDTO> notificationDTOs = new ArrayList<>();
		
		for(Notification n : notifications) {
			notificationDTOs.add(new NotificationDTO(n));
		}
		
		return new ResponseEntity<>(notificationDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/course/{courseId}/distinctMessages")
	public ResponseEntity<List<NotificationDTO>> getByCourseDistinct(@PathVariable Long courseId){
		Course course = courseService.findOne(courseId);
		if(course == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		List<Notification> notifications = notificationRepository.findDistinctMessageByCourse(course);
		List<NotificationDTO> notificationDTOs = new ArrayList<>();
		
		for(Notification n : notifications) {
			notificationDTOs.add(new NotificationDTO(n));
		}
		
		return new ResponseEntity<>(notificationDTOs, HttpStatus.OK);
	}
}
