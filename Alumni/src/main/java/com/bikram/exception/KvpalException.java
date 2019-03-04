package com.bikram.exception;

import org.springframework.http.HttpStatus;

public class KvpalException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpStatus httpStatus;
	private String message;
	private String internalCode;
	private String status;
	private Object data;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	public String getMessage() {
		return message;
	}
	public String getInternalCode() {
		return internalCode;
	}
	public String getStatus() {
		return status;
	}
	public Object getData() {
		return data;
	}
	public KvpalException(ApplicationEnum applicationEnum,Object data) {
		this.httpStatus = applicationEnum.httpStatus;
		this.message = applicationEnum.message;
		this.internalCode = applicationEnum.internalCode;
		this.status = applicationEnum.status;
		this.data = data;
	}
	public KvpalException(ApplicationEnum applicationEnum) {
		this.httpStatus = applicationEnum.httpStatus;
		this.message = applicationEnum.message;
		this.internalCode = applicationEnum.internalCode;
		this.status = applicationEnum.status;
	}
	
	
}
