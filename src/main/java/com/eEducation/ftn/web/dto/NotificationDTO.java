package com.eEducation.ftn.web.dto;

import java.util.Date;

public class NotificationDTO {
	
	private Integer id;
    private String message;
    private Date nDate;
    private CourseDTO courseId;
    private CourseFileDTO documentId;
	
	public NotificationDTO() {}

	public NotificationDTO(Notification notification) {
		this.id = notification.getId();
		this.message = notification.getMessage();
		this.nDate = notification.getNDate();
		this.courseId = new CourseDTO(notification.getCourseId());
		this.documentId = new CourseFile(notification.getDocumentId());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getnDate() {
		return nDate;
	}

	public void setnDate(Date nDate) {
		this.nDate = nDate;
	}

	public CourseDTO getCourseId() {
		return courseId;
	}

	public void setCourseId(CourseDTO courseId) {
		this.courseId = courseId;
	}

	public CourseFileDTO getDocumentId() {
		return documentId;
	}

	public void setDocumentId(CourseFileDTO documentId) {
		this.documentId = documentId;
	}
	
}
