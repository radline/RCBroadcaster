package com.sorteberg.rcplugins;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BCScheduler {

	private Timer timer;
	private Logger logger;
	private JSONObject settingsObj;
	private JSONArray messageArray;
	
	//The userStatusList is created in RCBroadcaster and 
	//represented locally by a private pointer.
	private MessageProcessor messageProcessor;
	
	//The constructor receives pointers to the logger, 
	//an object representing the data file and the list of user statuses.
	//For convenience the data object is split into separate objects
	//for settings and messages.
	public BCScheduler(
			Logger logger, 
			JSONObject mainObject, 
			MessageProcessor messageProcessor) {
		this.logger = logger;
		settingsObj= (JSONObject)mainObject.get("settings");
		messageArray = (JSONArray)mainObject.get("messages");
		this.messageProcessor = messageProcessor;
		timer = new Timer();
		logger.info(
				String.format(
						"Message frequence: %1d seconds", 
						getFrequence())
				);
	}

	//Returns the scheduler frequency from the settings object in seconds. 
	private long getFrequence(){
		return (long)settingsObj.get("frequence");
	}
	
	//Schedules a new schedule using the settings stored in the data file.
	public void Schedule(){
		timer.schedule(new RemindTask(), getFrequence() * 1000);
	}
	
	//The timer task executed each time our local timer fires.
	class RemindTask extends TimerTask {
		
		public void run() {
			try{
				messageProcessor.SendMessages(messageArray);
			}
			catch(Exception e){
				logger.info("userStatusList.test: NULL");
				
			}
			Schedule();
			
		}
	}
}
