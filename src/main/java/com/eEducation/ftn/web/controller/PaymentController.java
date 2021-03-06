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

import com.eEducation.ftn.model.Payment;
import com.eEducation.ftn.model.Student;
import com.eEducation.ftn.repository.PaymentRepository;
import com.eEducation.ftn.repository.StudentRepository;
import com.eEducation.ftn.service.PaymentService;
import com.eEducation.ftn.service.StudentService;
import com.eEducation.ftn.web.dto.PaymentDTO;

@RestController
@RequestMapping(value="api/payments")
public class PaymentController {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	PaymentRepository paymentRepository;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<PaymentDTO>> getAll(){
		List<Payment> payments = paymentService.findAll();
		List<PaymentDTO> paymentDTOs = new ArrayList<>();
		
		for(Payment p : payments) {
			paymentDTOs.add(new PaymentDTO(p));
		}
		
		logger.info("payment - returned all");
		return new ResponseEntity<>(paymentDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public ResponseEntity<PaymentDTO> getById(@PathVariable Long id){
		Payment found = paymentService.findOne(id);
		if(found == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		logger.info("payment - found one by id " + id);
		return new ResponseEntity<>(new PaymentDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes="application/json")
	public ResponseEntity<PaymentDTO> add(@RequestBody PaymentDTO payment){
		Payment newPayment = new Payment();
		
		if(payment.getStudent() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Student student = studentService.findOne(payment.getStudent().getId());
		if(student == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		newPayment.setStudent(student);
		newPayment.setCause(payment.getCause());
		newPayment.setPaymentDate(payment.getPaymentDate());
		newPayment.setOwes(payment.getOwes());
		
		paymentService.save(newPayment);
		return new ResponseEntity<>(new PaymentDTO(newPayment), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.PUT, consumes="application/json", value="/{id}")
	public ResponseEntity<PaymentDTO> update(@RequestBody PaymentDTO payment){
		Payment found = paymentService.findOne(payment.getId());
		if(found == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if(payment.getStudent() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Student student = studentService.findOne(payment.getStudent().getId());
		if(student == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		found.setStudent(student);
		found.setCause(payment.getCause());
		found.setPaymentDate(payment.getPaymentDate());
		found.setOwes(payment.getOwes());
		
		paymentService.save(found);
		return new ResponseEntity<>(new PaymentDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Long id){
		Payment found = paymentService.findOne(id);
		if(found != null) {
			try {
				paymentService.remove(id);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/students/{studentId}")
	public ResponseEntity<List<PaymentDTO>> getByStudent(@PathVariable Long studentId){
		Student student = studentService.findOne(studentId);
		if(student == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		List<Payment> payments = paymentRepository.findByStudent(student);
		List<PaymentDTO> paymentDTOs = new ArrayList<>();
		
		for(Payment p : payments) {
			paymentDTOs.add(new PaymentDTO(p));
		}
		
		logger.info("payment - returned all payments for student");
		
		return new ResponseEntity<>(paymentDTOs, HttpStatus.OK);
	}
}
