package com.sorteberg.rcplugins;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class RCBroadcaster extends JavaPlugin{

	private UserStatusList userStatusList = new UserStatusList(getLogger());
	private MessageProcessor messageProcessor;
	
	@Override
    public void onEnable() {
    	try{
    		boolean loadedOK = true;
    		
    		//
    		// To run the broadcaster successfully, we need to load a valid data file:
    		//
    		
    		//If the plugin's data folder don't exist, we have to create it.
    		BCDataFile BCDataFile = new BCDataFile(getLogger(), getDataFolder());
    		if(!BCDataFile.dataFolderExist()){
    			BCDataFile.createDataFolder();
    		}
    		
    		//If the data file don't exist, we have to create a new one with default content.
    		if(!BCDataFile.dataFileExist()){
    			if(BCDataFile.createDataFile() == true){
        	    	getLogger().info("Data file not found. New, default file created.");    				
    			}
        	    else{
        	    	getLogger().info("Data file not found. Error creating new file in " + getDataFolder().getAbsolutePath());
        	    	loadedOK=false;
        	    }
    		}
    		else{
        	    getLogger().info("Data file found.");
    		}
    		
    		JSONObject dataFile = BCDataFile.parseDataFile();
    		if(dataFile == null){
        	    getLogger().info("The data file is not received by the Plugin class.");
    			loadedOK=false;
    		}

    		//
    		// The scheduler needs a pointer to the Message processor.
    		// This must be created before the creation of the scheduler.
    		//
    		
			messageProcessor = new MessageProcessor(
					getLogger(),
					(JSONArray) dataFile.get("messages"),
					userStatusList
					);
    		
    		
    		
    		//
    		// If and when the data file is loaded successfully, 
    		// we can create a BCScheduler to send messages regular to players.
    		//
    		
    		BCScheduler BCScheduler = null;
    		if(loadedOK == true)		
    			BCScheduler = new BCScheduler(
    					getLogger(),
    					dataFile,
    					messageProcessor);
    	    
    		//
    		// If all preparations went well, register necessary events 
    		// and start the BCScheduler.
    	    //
    		if(loadedOK == true){
        	    getServer().getPluginManager().registerEvents(
        	    		new BCListener(
        	    				this,
        	    				userStatusList), 
        	    		this);
        	    
        	    
        	    
        	    BCScheduler.Schedule();
    	    	getLogger().info("Broadcaster enabled successfully.");
    	    }
    	    else{
    	    	getLogger().info("RC-Broadcaster is loaded, but will not run due to errors.");
    	    }
    		
    	}
    	catch (Exception e){
    	    getLogger().info("Error loading Plugin: " + e.getMessage());
    	}
    }
 

    @Override
    public void onDisable() {
    
    }

}
