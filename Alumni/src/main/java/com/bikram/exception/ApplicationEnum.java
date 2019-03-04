package com.bikram.exception;

import org.springframework.http.HttpStatus;

public enum ApplicationEnum {

	//SUCCESS HANDLERS
	
	REG_SUCCESS(HttpStatus.OK,"You have succesfully registered","SUCCESS","KVPAL1000"),
	LOGIN_SUCCESS(HttpStatus.OK,"You Logged in succesfully","SUCCESS","KVPAL1001"),
	USER_FETCHED(HttpStatus.OK,"You are registered and verified with us","SUCCESS","KVPAL1002"),
	PENDING_USER(HttpStatus.OK,"Your Profile is under verification. Please wait at most 48 hours to get verified. You will recieve an email from us once your profile verification is done. ","SUCCESS","KVPAL1003"),
	LOGOUT_SUCCESS(HttpStatus.OK,"You Logout Successfully","SUCCESS","KVPAL1004"),
	USER_STATUS_SET_SUCCESSFULLY(HttpStatus.OK,"User status set successfully","SUCCESS","KVPAL1005"),
	
	//ERROR HANDLERS
	
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED,"You provided an invalid credentials","FAILED","KVPAL5000"),
	USER_ALREADY_EXISTS(HttpStatus.CONFLICT,"You are already registered","FAILED","KVPAL5001"),
	USER_NOT_EXISTS(HttpStatus.NOT_FOUND,"Your Email Not Registered With Us","FAILED","KVPAL5002"),
	ERROR_UPLOADING_IMAGE(HttpStatus.BAD_REQUEST,"File Upload Failed due to unknown reason","FAILED","KVPAL5003"),
	TOKEN_EXPIRED(HttpStatus.BAD_REQUEST,"Your Token Is Expired, Please Login Again","FAILED","KVPAL5004"),
	UNABLE_LOGOUT(HttpStatus.UNAUTHORIZED,"Unable to logout. Provided credentials are invalid","FAILED","KVPAL5005"),
	NOT_ADMIN(HttpStatus.UNAUTHORIZED,"You are not an admin user","FAILED","KVPAL5006"),

	;
	

	HttpStatus httpStatus;
	String message;
	String status;
	String internalCode;
	private ApplicationEnum(HttpStatus httpStatus, String message, String status, String internalCode) {
		this.httpStatus = httpStatus;
		this.message = message;
		this.status = status;
		this.internalCode = internalCode;
	}
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	public String getMessage() {
		return message;
	}
	public String getStatus() {
		return status;
	}
	public String getInternalCode() {
		return internalCode;
	}
	
	
	
	
	
	
}
