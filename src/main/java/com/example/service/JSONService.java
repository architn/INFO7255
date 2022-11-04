package com.example.service;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.dao.PlanDAO;
import com.example.demo.api.RestController;
import com.example.helper.MD5Helper;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;


public class JSONService {
	
	 PlanDAO planDAO = new PlanDAO();
	
	 /**
	  * 
	  * @param json
	  * @return
	  */
	
	public JSONObject ValidateWhetherSchemaIsValid(String json) 
	{
		InputStream schemaStream = RestController.class.getResourceAsStream("/schema.json");
		
		JSONObject jsonSchema = new JSONObject(new JSONTokener(schemaStream));
		JSONObject jsonCurrentObject = new JSONObject(new JSONTokener(json));
		
		Schema schema = SchemaLoader.load(jsonSchema);
		schema.validate(jsonCurrentObject);
		return jsonCurrentObject;
	}
	
	/**
	 * 
	 * @param objectType
	 * @param objectID
	 * @return
	 */
	
	public String GenerateKeyForJSONObject(String objectType, String objectID)
	{
		String keyForJSON = objectType + "-" + objectID;
		return keyForJSON;
	}
	
	/**
	 * 
	 * @param keyOfJSON
	 * @return
	 */
	
	public boolean DoesPlanExistInSystem(String keyOfJSONPlan) 
	{
		String jsonInString = planDAO.getPlanRecordFromRedis(keyOfJSONPlan);
		if(jsonInString != null)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param objectType
	 * @param objectID
	 * @return
	 */
	
	public String GenerateETagKeyForJSONObject(String objectType, String objectID)
	{
		return objectType + "|" + objectID;
	}
	
	/**
	 * 
	 * @param eTagKey
	 * @return
	 */
	
	public String GetETagByETagKey(String eTagKey)
	{
		String eTag = planDAO.getETagByKey(eTagKey);
		return eTag;
	}
	
	public String GetETagOfSavedPlan(String objectType, String objectID)
	{
		String jsonKey = GenerateETagKeyForJSONObject(objectType, objectID);
		return planDAO.getETagByPlanKey(jsonKey);
	}
	
	/**
	 * 
	 * @param jsonKey
	 * @return
	 */
	
	public JSONObject GetPlanByKey(String jsonKey)
	{
		String jsonInString = planDAO.getPlanRecordFromRedis(jsonKey);
		return new JSONObject(new JSONTokener(jsonInString));
	}
	
	
	public String saveJSON(String requestBody, String objectType, String objectId) throws NoSuchAlgorithmException
	{
		 String keyOfJSONBody = GenerateKeyForJSONObject(objectType, objectId);
		 String ETag = MD5Helper.hashString(requestBody);
		 String ETagKey = GenerateETagKeyForJSONObject(objectType, objectId);
		 planDAO.savePlanToRedis(ETagKey, ETag, keyOfJSONBody, requestBody);
		 return ETag;

	}
	
	public String getPlanRecord(String objectType, String objectID)
	{
		String keyOfJSONBody = GenerateKeyForJSONObject(objectType, objectID);
		String jsonInString = planDAO.getPlanRecordFromRedis(keyOfJSONBody);
		return jsonInString;
	}
	
	public void deletePlanRecord(String objectType, String objectID)
	{
		 String keyOfJSONBody = GenerateKeyForJSONObject(objectType, objectID);
		 String keyForETag = GenerateETagKeyForJSONObject(objectType, objectID);
		 planDAO.deletePlanFromRedis(keyOfJSONBody, keyForETag);

	}
	
}
