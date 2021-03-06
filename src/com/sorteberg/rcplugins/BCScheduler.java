package com.sorteberg.rcplugins;
import java.util.Timer;
import java.util.TimerTask;
import org.json.simple.JSONObject;

public class BCScheduler {

	private Timer timer;
	private BCLogger logger;
	private JSONObject settingsObj;
	
	// The userStatusList is created in RCBroadcaster and 
	// represented locally by a private pointer.
	private MessageProcessor messageProcessor;
	
	// The constructor receives pointers to the logger, 
	// an object representing the data file and the list of user statuses.
	// For convenience the data object is split into separate objects
	// for settings and messages.
	public BCScheduler(
			BCLogger logger, 
			JSONObject mainObject, 
			MessageProcessor messageProcessor) {
		try{
			this.logger = logger;
			settingsObj= (JSONObject)mainObject.get("settings");
			this.messageProcessor = messageProcessor;
			timer = new Timer();
			logger.log(
					2, 
					String.format(
							"Message interval: %1d seconds", 
							getInterval())
					);
			
		}
		catch(Exception e){
			logger.log(0,"Error constructing BCScheduler " + e.getMessage());
			
		}
	}

	// Returns the scheduler frequency from the settings object in seconds. 
	private long getInterval(){
		return (long)settingsObj.get("interval");
	}
	
	// Schedules a new schedule using the settings stored in the data file.
	public void Schedule(){
		timer.schedule(new RemindTask(), getInterval() * 1000);
	}
	
	// The timer task executed each time our local timer fires.
	class RemindTask extends TimerTask {
		
		public void run() {
			try{
				messageProcessor.SendMessages();
			}
			catch(Exception e){
				logger.log(0,"RemindTask.run: ERROR " + e.getMessage());
			}
			Schedule();
			
		}
	}
}
