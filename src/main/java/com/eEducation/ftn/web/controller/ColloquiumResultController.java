package com.eEducation.ftn.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.eEducation.ftn.service.ColloquiumResultService;
import com.eEducation.ftn.web.dto.ColloquiumResultDTO;

@RestController
@RequestMapping(value="api/colloquiumResults")
public class ColloquiumResult.Controller {
	@Autowired
	ColloquiumResultService colloquiumResultService;
	
	@Autowired
	ColloquiumService colloquiumService;
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	StudentDocumentService studentDocumentService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<ColloquiumResultDTO>> getAll(){
		List<ColloquiumResult> results = ColloquiumResultService.findAll();
		List<ClassDTO> resultDTOs = new ArrayList<>();
		
		for(ColloquiumResult c : collo){
			resultDTOs.add(new ColloquiumResultDTO(c));
		}
		
		return new ResponseEntity(resultDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public ResponseEntity<ColloquiumResultDTO> getById(@PathVariable Integer id){
		ColloquiumResult found = colloquiumResultService.findOne(id);
		if(found == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(new ColloquiumResultDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes="application/json")
	public ResponseEntity<ColloquiumResultDTO> save(@RequestBody ColloquiumResultDTO colloquiumResult){
		ColloquiumResult newColloquiumResult = new ColloquiumResult();
		newColloquiumResult.setPoints(colloquiumResult.getPoints());
		
		if(colloquiumResult.getColloquium() == null || colloquiumResult.getStudent() == null ||
				colloquiumResult.getDocument() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Colloquium colloquium = colloquiumService.findOne(colloquiumResult.getColloquium().getId());
		Student student = studentService.findOne(colloquiumResult.getStudent().getId());
		StudentDocument studDoc = studentDocumentService.findOne(colloquiumResult.getDocument().getId());
		
		if(colloquium == null || student == null || studDoc == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		newColloquiumResult.setColloquium(colloquium);
		newColloquiumResult.setStudent(student);
		newColloquiumResult.setDocument(studDoc);
		
		colloquiumResultService.save(newColloquiumResult);
		return new ResponseEntity<>(new ColloquiumResultDTO(newColloquiumResult), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.PUT, consumes="application/json")
	public ResponseEntity<ColloquiumResultDTO> update(@RequestBody ColloquiumResultDTO colloquiumResult){
		ColloquiumResult found = colloquiumResultService.findOne(colloquiumResult.getId());
		if(found == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		found.setPoints(colloquiumResult.getPoints());
		
		if(colloquiumResult.getDocument() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		StudentDocument studDoc = studentDocumentService.findOne(colloquiumResult.getDocument().getId());
		
		if(studDoc == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		found.setDocument(studDoc);
		
		// not allowed to change colloquium and student
		
		colloquiumResultService.save(found);
		return new ResponseEntity<>(new ColloquiumResultDTO(found), HttpStatus.OK);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer id){
		ColloquiumResult found = colloquiumResultService.findOne(id);
		if(found != null){
			colloquiumResultService.remove(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	// collection methods
}