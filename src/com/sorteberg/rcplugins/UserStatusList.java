package com.sorteberg.rcplugins;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public class UserStatusList {

	Logger logger;
	
	// UserStatus objects for users logged in since 
	// last server restart are kept in an array list.
	private List <UserStatus> userStatuses = new ArrayList<UserStatus>();
	
	// The constructor sets a pointer to logger for use internally in this class.
	public UserStatusList(Logger logger){
		this.logger = logger;
	}
	
	// Returns number of UserStatuses in the userStatuses collection
	public int size(){
		return userStatuses.size();
	}
	
	// Returns index of user in array if it exists. 
	// Returns -1 if not found.
	public int FindInList(String userName){
	    for (int i = 0 ; i < userStatuses.size(); i++) {
	    	UserStatus userStatus = (UserStatus)userStatuses.get(i);
	    	if(userName.equals(userStatus.player.getName()))
	    		return i;
	    }
	    return -1;
	}

	// Returns a UserStatus object with the name received as parameter.
	// Returns NULL if no UserStatus is found.
	public UserStatus GetPlayer(String userName){
	    for (int i = 0 ; i < userStatuses.size(); i++) {
	    	UserStatus userStatus = (UserStatus)userStatuses.get(i);
	    	if(userName.equals(userStatus.player.getName()))
	    		return userStatus;
	    }
	    return null;
	}

	public UserStatus get(int index){
		if(index >=0 && index < userStatuses.size() ){
			return (UserStatus)userStatuses.get(index);
		}
		else{
			return null;
		}
	}

	// Adds a new UserStatus object to the collection, unless it's already added.
	public boolean Join(Player player){
		try{
			if(FindInList(player.getName())<0){
				UserStatus userStatus = new UserStatus();
				userStatus.player=player;
				userStatuses.add(userStatus);
				logger.info("User added: "+ player.getName());
			}
			else{
				GetPlayer(player.getName()).joined = true;
				logger.info("User added: "+ player.getName() + " (existing)");
			}
			return true;
		}
		catch(Exception e){
			logger.info("User added: FALSE. " + e.getMessage());
			return false;
		}
	}

	public boolean Quit(Player player){
		try{
			if(FindInList(player.getName())>=0){
				GetPlayer(player.getName()).joined = false;
			}
			return true;
		}
		catch(Exception e){
			logger.info("User added: FALSE. " + e.getMessage());
			return false;
		}
	}

}
