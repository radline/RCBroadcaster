package com.sorteberg.rcplugins;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

import org.bukkit.entity.Player;

public class UserStatusList {

	Logger logger;
	private List <UserStatus> userStatuses = new ArrayList<UserStatus>();
	
	public UserStatusList(Logger logger){
		this.logger = logger;
	}
	
	public int size(){
		return userStatuses.size();
	}
	
	public int FindInList(String userName){
	    for (int i = 0 ; i < userStatuses.size(); i++) {
	    	UserStatus userStatus = (UserStatus)userStatuses.get(i);
	    	if(userName.equals(userStatus.userName))
	    		return i;
	    }
	    return -1;
	}

	public Player GetPlayer(String userName){
	    for (int i = 0 ; i < userStatuses.size(); i++) {
	    	UserStatus userStatus = (UserStatus)userStatuses.get(i);
	    	if(userName.equals(userStatus.userName))
	    		return userStatus.player;
	    }
	    return null;
	}

	public boolean Add(Player player){
		
		try{
			if(FindInList(player.getName())<0){
				UserStatus userStatus = new UserStatus();
				userStatus.player=player;
				userStatus.userName=player.getName();
				userStatuses.add(userStatus);
				logger.info("User added: "+ player.getName());
			}
			else{
				logger.info("User added: "+ player.getName() + " (existing)");
			}
			return true;
		}
		catch(Exception e){
			logger.info("User added: FALSE. " + e.getMessage());
			return false;
		}
	}
		
}
