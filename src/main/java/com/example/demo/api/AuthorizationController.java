package com.example.demo.api;

import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.example.helper.*;
import com.example.service.AuthorizationService;

@Controller
public class AuthorizationController {

//	@Autowired
	AuthorizationService authService = new AuthorizationService();
	AppConstants AppConstants = new AppConstants();
	
	 @RequestMapping(value = "/token", method = RequestMethod.GET)
	 public ResponseEntity<String> GenerateToken()
	 {
		 String token;
		 try {
			 token = authService.generateToken();
			 JSONObject obj = new JSONObject();
			 obj.put("token", token);
			 return  ResponseEntity.status(HttpStatus.CREATED).body("{ token : '" + token + "' }");
		 }
		 catch(Exception ex)
		 {
			 return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ message : '" + ex.getMessage() + "' }");
		 }
		 
	 }
	 
	 @RequestMapping(value = "/validate", method = RequestMethod.POST)
	 public ResponseEntity<String> validateToken(@RequestHeader(value="authorization") String token)
	 {
		 String tokenStatus = "";
		 try {
			  tokenStatus = authService.authorize(token);
			 if(tokenStatus.equals(AppConstants.VALID_TOKEN))
			 {
				 return ResponseEntity.status(HttpStatus.CREATED).body("{ message : '" + AppConstants.TOKEN_SUCCESS + "' }");

			 }
			 else
			 {
				 return ResponseEntity.status(HttpStatus.OK).body("{ message : '" + token + "' }");
			 }
		 }
		 catch(Exception ex)
		 {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ message : '" + ex.getMessage() + "',\n status: '"+tokenStatus+"' }");

		 }
	 }
}
