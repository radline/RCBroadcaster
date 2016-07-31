package com.sorteberg.rcplugins;

import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MessageProcessor {

	Logger logger;
	JSONArray messageArray;
	UserStatusList userStatusList;

	public MessageProcessor(
			Logger logger, 
			JSONArray messageArray, 
			UserStatusList userStatusList) {
		this.logger = logger;
		this.messageArray = messageArray;
		this.userStatusList = userStatusList;
	}
	
	
	public boolean SendMessages(JSONArray messageArray){
		
		try{
			
			
			
		    for (int i = 0 ; i < userStatusList.; i++) {
		    	
		    	
		        JSONObject obj = (JSONObject)messageArray.get(i);
				logger.info("MessageSend: " + (String)obj.get("message") );
			}			
			
			logger.info("MessageSend: " );
			return true;
		}
		catch(Exception e){
			logger.info("MessageSend: FALSE. " + e.getMessage());
			return false;
		}
		
	}

}
