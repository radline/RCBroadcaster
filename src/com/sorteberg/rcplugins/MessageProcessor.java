package com.sorteberg.rcplugins;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MessageProcessor {

	private Logger logger;
	private UserStatusList userStatusList;
	private List<String> startupMessages = new ArrayList<String>();
	private List<String> recurringMessages = new ArrayList<String>();


	public MessageProcessor(
			Logger logger, 
			JSONArray messageArray, 
			UserStatusList userStatusList) {
		try{
			this.logger = logger;
			this.userStatusList = userStatusList;
			for(int i = 0; i < messageArray.size(); i++)
			{
			    JSONObject msg = (JSONObject)messageArray.get(i);
			    String msgType = (String)msg.get("type");
			    if(msgType.equals("startup")){
			    	startupMessages.add((String)msg.get("message"));
			    	logger.info("Startup message loaded: " + (String)msg.get("message"));
			    }
			    else if(msgType.equals("recurring")){
			    	recurringMessages.add((String)msg.get("message"));
			    	logger.info("Recurring message loaded: " + (String)msg.get("message"));
			    }
			}

		}
		catch(Exception e){
			logger.info("MessageProcessor construction: FALSE. " + e.getMessage());
		}
	}
	public boolean SendMessages()
	{
		try{
		    for (int i = 0 ; i < userStatusList.size(); i++) {
		    	UserStatus userStatus = userStatusList.get(i);
		    	if(userStatus != null)
		    	{
		    		if(userStatus.joined){
		    			if(userStatus.modus == 0){
			    			if(userStatus.nextMessage < startupMessages.size()){
			    				userStatus.player.sendMessage(
			    						startupMessages.get(
			    								userStatus.nextMessage));

					    		userStatus.nextMessage++;
					    		logger.info("Sendt startup message " 
					    				+ userStatus.nextMessage 
					    				+ " to " 
					    				+ userStatus.player.getName()); 
			    			}
			    			else{
			    				// All startup messages are sent. 
			    				// Reset nextMessage counter and flag for recurring messages.
			    				userStatus.modus = 1;
			    				userStatus.nextMessage = 0;
			    			}
		    			}
		    			else{ //Modus == 1
			    			if(userStatus.nextMessage < recurringMessages.size()){
			    				userStatus.player.sendMessage(
			    						recurringMessages.get(
			    								userStatus.nextMessage));

					    		logger.info("Sendt recurring message " 
					    				+ userStatus.nextMessage 
					    				+ " (" + recurringMessages.get(
			    								userStatus.nextMessage) + ")"
					    				+ " to " 
					    				+ userStatus.player.getName()); 

					    		userStatus.nextMessage++;
}
			    			else{
			    				userStatus.nextMessage = 0;
			    			}
		    			}
		    		} //if(userStatus.joined)
		    		else{
		    			logger.info(userStatus.player.getName() + " is offline");
		    		} //else (userStatus.joined)
		    	} //if(userStatus != null)
		    }
			return true;
		}
		catch(Exception e){
			logger.info("MessageProcessor construction: FALSE. " + e.getMessage());
			return false;
		}
		
	}
	

}


