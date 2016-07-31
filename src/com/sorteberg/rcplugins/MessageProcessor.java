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
		try{
			this.logger = logger;
			this.messageArray = messageArray;
			this.userStatusList = userStatusList;
		}
		catch(Exception e){
			logger.info("MessageProcessor construction: FALSE. " + e.getMessage());
		}
	}
	
	public boolean SendMessages(){
		try{
		    for (int i = 0 ; i < userStatusList.size(); i++) {
		    	UserStatus userStatus = userStatusList.get(i);
		    	if(userStatus != null)
		    	{
			    	if(userStatus.modus == 0){
			    		if(userStatus.joined){
				    		userStatus.nextMessage++;
				    		logger.info("Sending message " + userStatus.nextMessage + " to " + userStatus.player.getName()); 
			    		}
			    		else{
			    			logger.info(userStatus.player.getName() + " is offline");
			    		}
			    	}
			    	else{
			    		
			    	}
					//logger.info("MessageSend: " + (String)obj.get("message") );
			        //JSONObject obj = (JSONObject)messageArray.get(0);
		    	}
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


//.test=event.getPlayer().getName();
//event.setJoinMessage("Hei, " + event.getPlayer().getName() + ". Du er en ku!");
//Bukkit.broadcastMessage("Velkommen til ku-serveren!");
//Player p = event.getPlayer();
//p.sendMessage("Mooo!");


