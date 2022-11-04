package com.example.demo.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.helper.AppConstants;


public class API {
	AppConstants AppConstants = new AppConstants();
	
	public ResponseEntity<String> created(String ETag)
	{
		return ResponseEntity.status(HttpStatus.CREATED).eTag(ETag).body("{ message : '" + AppConstants.SUCCESS_MESSAGE + "'}");
	}
	
	public ResponseEntity<String> notModified(String jsonBody, String ETag)
	{
		 return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(ETag).body(jsonBody);
	}
	
	public ResponseEntity<String> notFound(String message)
	{
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ message : '" + message  + "'}");
	}
	
	public ResponseEntity<String> OK(String jsonBody, String eTag)
	{
		return ResponseEntity.status(HttpStatus.OK).eTag(eTag).body(jsonBody);
	}
	
	public ResponseEntity<String> noContent(String message)
	{
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{ message : '" + message  + "'}");

	}
	
	public ResponseEntity<String> forbidden(String message)
	{
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{ message : '" + message  + "'}");

	}
	
	public ResponseEntity<String> internalServerError(String message)
	{
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ message : '" + message  + "'}");

	}
}
