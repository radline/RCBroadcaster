package com.sorteberg.rcplugins;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class RCBroadcaster extends JavaPlugin{

	private BCLogger logger = new BCLogger(1,getLogger());
	private UserStatusList userStatusList = new UserStatusList(getLogger());
	private MessageProcessor messageProcessor;
	private BCScheduler bcScheduler;
	
	@Override
    public void onEnable() {
    	try{
    		boolean loadedOK = true;
    		
    		//
    		// To run the broadcaster successfully, we need to load a valid data file:
    		//
    		
    		//If the plugin's data folder don't exist, we have to create it.
    		BCDataFile BCDataFile = new BCDataFile(
    				logger, 
    				getDataFolder());
    		if(!BCDataFile.dataFolderExist()){
    			BCDataFile.createDataFolder();
    		}
    		
    		//If the data file don't exist, we have to create a new one with default content.
    		if(!BCDataFile.dataFileExist()){
    			if(BCDataFile.createDataFile() == true){
        	    	logger.log(1,"Data file not found. New, default file created.");    				
    			}
        	    else{
        	    	logger.log(0,"Data file not found. Error creating new file in " + getDataFolder().getAbsolutePath());
        	    	loadedOK=false;
        	    }
    		}
    		else{
        	    getLogger().info("Data file found.");
    		}
    		
    		JSONObject dataFile = BCDataFile.parseDataFile();
    		if(dataFile == null){
    			logger.log(0,"The data file is not received by the Plugin class.");
    			loadedOK=false;
    		}

    		//
    		// The scheduler needs a pointer to the Message processor.
    		// This must be created before the creation of the scheduler.
    		//

			messageProcessor = new MessageProcessor(
					logger,
					(JSONArray) dataFile.get("messages"),
					userStatusList
					);
			if(messageProcessor == null){
				logger.log(0,"Error creating the Message Processor.");
    			loadedOK=false;			
			}
    		
    		
    		//
    		// If and when the data file is loaded successfully, 
    		// we can create a BCScheduler to send messages regular to players.
    		//
    		if(loadedOK == true){
    			
    			bcScheduler = new BCScheduler(
    					logger,
    					dataFile,
    					messageProcessor);
    			if(bcScheduler == null){
    				logger.log(1,"Error creating Scheduler.");
        			loadedOK=false;			
    			}
    			
    		}

    		//
    		// If all preparations went well, register necessary events 
    		// and start the BCScheduler.
    	    //
    		if(loadedOK == true){
        	    getServer().getPluginManager().registerEvents(
        	    		new BCListener(userStatusList), 
        	    		this);
        	    
        	    
        	    bcScheduler.Schedule();
        	    logger.log(1,"Broadcaster enabled successfully.");
    	    }
    	    else{
    	    	logger.log(0,"RC-Broadcaster is loaded, but will not run due to errors.");
    	    }
    		
    	}
    	catch (Exception e){
    		logger.log(0,"Error loading Plugin: " + e.getMessage());
    	}
    }
 

    @Override
    public void onDisable() {
    
    }

}
