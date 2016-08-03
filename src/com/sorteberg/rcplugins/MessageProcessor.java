package com.sorteberg.rcplugins;

import java.util.List;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MessageProcessor {

	// A pointer to the logger created 
	// in main class is used for logging purpose.
	private BCLogger logger;

	// The userStatusList contains a list containing 
	// information about all users logged on since last server restart.
	private UserStatusList userStatusList;

	private int slowdownLimit;
	private int slowdownFactor;
	private int slowdownShowCounter=0;
	// Messages from the dataFile are stored in arrays, one for 
	// startup messages and one for recurring messages.
	private List<String> startupMessages = new ArrayList<String>();
	private List<String> recurringMessages = new ArrayList<String>();


	// Pointers to the logger, the MessageNode from the JSON data object
	// and the empty UserStatusList are received via the constructor.
	public MessageProcessor(
			BCLogger logger, 
			JSONArray messageArray, 
			UserStatusList userStatusList,
			int slowdownLimit,
			int slowdownFactor
			) {
		try{
			this.logger = logger;
			this.userStatusList = userStatusList;
			this.slowdownLimit = slowdownLimit;
			this.slowdownFactor = slowdownFactor;
			
			// check all messages in the JSON and split them
			// into the two message objects.
			// Messages marked as not enabled are ignored.
			for(int i = 0; i < messageArray.size(); i++)
			{
			    JSONObject msg = (JSONObject)messageArray.get(i);
			    String msgType = (String)msg.get("type");
			    if(msgType.equals("startup") && (long)msg.get("enabled") == 1L){
			    	startupMessages.add((String)msg.get("message"));
			    	logger.log(3,"Startup message loaded: " + (String)msg.get("message"));
			    }
			    else if(msgType.equals("recurring") && (long)msg.get("enabled") == 1L){
			    	recurringMessages.add((String)msg.get("message"));
			    	logger.log(3,"Recurring message loaded: " + (String)msg.get("message"));
			    }
			}

		}
		catch(Exception e){
			logger.log(0,"MessageProcessor construction: FALSE. " + e.getMessage());
		}
	}

	public boolean SendMessages()
	{
		try{
			slowdownShowCounter++;
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
					    		logger.log(2,
					    				"Sendt startup message " 
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
			    
	    						if(userStatus.slowdownCounter < slowdownLimit
	    								|| slowdownShowCounter == slowdownFactor){
				    				userStatus.player.sendMessage(
				    						recurringMessages.get(
				    								userStatus.nextMessage));
	
						    		logger.log(2,
						    				"Sendt recurring message " 
						    				+ userStatus.nextMessage 
						    				+ " (" + recurringMessages.get(
				    								userStatus.nextMessage) + ")"
						    				+ " to " 
						    				+ userStatus.player.getName()); 
	
						    		userStatus.nextMessage++;
	    						}
			    			}
			    			else{
			    				userStatus.slowdownCounter++;
			    				userStatus.nextMessage = 0;
			    			}
		    			}
		    		} //if(userStatus.joined)
		    		else{
		    			logger.log(2,userStatus.player.getName() + " is offline");
		    		} //else (userStatus.joined)
		    	} //if(userStatus != null)
		    }
			if(slowdownShowCounter == slowdownFactor)
				slowdownShowCounter=0;
			return true;
		}
		catch(Exception e){
			logger.log(0,"MessageProcessor construction: FALSE. " + e.getMessage());
			return false;
		}
		
		
	}
	

}


