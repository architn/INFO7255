package com.example.demo.api;

import java.io.InputStream;
import java.util.Map;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

@Controller
public class RestController {
	
	 private Jedis redisMemory = new Jedis();
	 public final String BAD_REQUEST_MESSAGE = "Error: Bad Request";
	 public final String SUCCESS_MESSAGE = "Success!";
	 public final String INTERNAL_SERVER_ERROR = "Error: Internal Server Error";
	 public final String OBJECT_NOT_FOUND = "Error: Plan not found";
	 public final String OBJECT_DELETED = "Plan deleted!";
	 public final String OBJECT_ALREADY_EXISTS = "Plan already exists!";
	 
	 /**
	  * This method is used to save JSON data to Redis
	  * @param body9
	  * @param headers
	  * @return
	  */
	 
	 @RequestMapping(value = "/add", method = RequestMethod.POST)
	 public ResponseEntity<String> Save(@RequestBody String body, @RequestHeader Map<String, String> headers)
	 {
		 
		 try {
			 JSONObject jsonObject = ValidateWhetherSchemaIsValid(body);
			 String objType = jsonObject.getString("objectType");
			 String objID = jsonObject.getString("objectId");
			 String keyOfJSONBody = GenerateKeyForJSONObject(objType, objID);
			 if(!DoesObjectExistInSystem(keyOfJSONBody))
			 {
				 redisMemory.set(keyOfJSONBody, body);
			 }
			 else 
			 {
				 return ResponseEntity.status(HttpStatus.CONFLICT).body("{ message : '" + OBJECT_ALREADY_EXISTS + "'}");
			 }
			 
		 }
		 catch(ValidationException v) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ message : '" + v.getMessage() + "' }");
			}
		 catch(Exception ex) {
			 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ message : '" + INTERNAL_SERVER_ERROR + "'}");

		 }
		 return ResponseEntity.status(HttpStatus.CREATED).body("{ message : '" + SUCCESS_MESSAGE + "'}");
	 }
	 
	 
	 
	 @SuppressWarnings("unused")
	 @RequestMapping(value = "/get/{objectType}/{ID}", method = RequestMethod.GET)
	 @ResponseBody
	 private ResponseEntity<String> GetJSON(@PathVariable("objectType") String objectType, @PathVariable("ID") String objectID) 
	 {
		 	String jsonInString = "";
			try {
				 String keyOfJSONBody = objectType + "-"+ objectID;
				 jsonInString = redisMemory.get(keyOfJSONBody);
				 if(jsonInString == null) {
					 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ message : '" + OBJECT_NOT_FOUND + "' }");
				 }
				 JSONObject jsonObject = new JSONObject(new JSONTokener(jsonInString));
				 
				 
			}
			
			catch(Exception ex) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ message : '" + ex.getMessage() + "' }");
			}
			return ResponseEntity.ok(jsonInString);
	 }
	 
	 
	 @RequestMapping(value = "/get/{objectType}/{ID}", method = RequestMethod.GET, headers = "If-Match")
	 @ResponseBody
	 private ResponseEntity<String> GetJSONWithETag(@PathVariable("objectType") String objectType,
	            @PathVariable("ID") String objectId,
	            @RequestHeader(name = HttpHeaders.IF_MATCH) String ifMatch)
	 {
		 String jsonInString = "";
		 return ResponseEntity.ok(jsonInString);
	 }
	 
	 @RequestMapping(value = "/delete/{objectType}/{ID}", method = RequestMethod.DELETE)
	 @ResponseBody
	 private ResponseEntity<String> DeleteJSON(@PathVariable("objectType") String objectType, @PathVariable("ID") String objectID)
	 {
		 try {
			 String keyOfJSONBody = objectType + "-"+ objectID;
			 redisMemory.del(keyOfJSONBody);
			 
		 }
		 catch(Exception ex) {
			 
		 }
		 return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{ message : '" + OBJECT_DELETED + "' }");
	 }
	
	private JSONObject ValidateWhetherSchemaIsValid(String json) {
		InputStream schemaStream = RestController.class.getResourceAsStream("/schema.json");
		
		JSONObject jsonSchema = new JSONObject(new JSONTokener(schemaStream));
		JSONObject jsonCurrentObject = new JSONObject(new JSONTokener(json));
		
		Schema schema = SchemaLoader.load(jsonSchema);
		schema.validate(jsonCurrentObject);
		return jsonCurrentObject;
	}
	
	public static String GenerateKeyForJSONObject(String objectType, String objectID)
	{
		String keyForJSON = objectType + "-" + objectID;
		return keyForJSON;
	}
	
	public boolean DoesObjectExistInSystem(String keyOfJSON) {
		String jsonInString = redisMemory.get(keyOfJSON);
		if(jsonInString != null)
		{
			return true;
		}
		return false;
	}
	
	
	
}
