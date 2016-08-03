package com.sorteberg.rcplugins;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.sorteberg.rcplugins.BCScheduler.RemindTask;


public class BCDataFile {

	// A timer will check the data file for changes regulary.
	private Timer timer;
	private long datafileLoadedTS;
	
	// All data will be stored in RC-Broadcaster.json in the
	// plugin's default data folder.
	private static String dataFileName = "RC-Broadcaster.json";
	private File dataFolder;
	
	// The datafile is identified by plugin name and version number.
	private static String pluginName = "rc-broadcaster";
	private static int versionNumber = 1;
	
	// A pointer to the logger created 
	// in main class is used for logging purpose.
	private BCLogger logger;

	// Pointers to the logger and data folder are received via constructor.
	public BCDataFile(BCLogger logger, File dataFolder) {

		try{
			this.logger = logger;
			this.dataFolder = dataFolder;
			SetChangeTS();
			timer = new Timer();
			Schedule();
		}
		catch (NullPointerException e) {
			logger.log(0,"Error constructing BCDatafile" + e.toString());
		}
		catch (Exception e) {
			logger.log(0,"Error constructing BCDatafile" + e.toString());
		}
		
	}

	// Returns true if the dataFolder is created. 
	public boolean dataFolderExist(){
		File file = new File(dataFolder.getAbsolutePath());
		return file.exists();
	}

	// Returns true if the data file exists in dataFolder.
	public boolean dataFileExist(){
		File file = new File(dataFolder, dataFileName);
		return file.exists();
	}
	
	// Returns true if a new dataFolder is created.
	public boolean createDataFolder(){
		File file = new File(dataFolder.getAbsolutePath());
		if(file.mkdir())
			return true;
		else
			return false;
	}

	private void SetChangeTS()
	{
		try{
			File file = new File(dataFolder, dataFileName);
			datafileLoadedTS = file.lastModified();
		}
		catch (Exception e) {
			logger.log(0,"Error storing last file date: " + e.toString());
		}
	}
	
	// Creates a new data file with default content.
	@SuppressWarnings("unchecked")
	public boolean createDataFile(){
		
		// We need a main object to store all three children
		JSONObject mainObj = new JSONObject();

		// We have to create and add a new header node
		JSONObject headerObj = new JSONObject();
		headerObj.put("type", pluginName);
		headerObj.put("ver", versionNumber);	
		mainObj.put("header", headerObj);

		// We have to create and add a new settings node
		JSONObject settingsObj = new JSONObject();
		settingsObj.put("interval", 10);
		settingsObj.put("slowdown-count", 10);
		settingsObj.put("slowdown-factor", 5);
		settingsObj.put("debug-level", 1);
		settingsObj.put("data-source", "local");
		mainObj.put("settings", settingsObj);
		
		JSONObject messageObj;
		JSONArray messageObjects = new JSONArray();

		messageObj = new JSONObject();
		messageObj.put("type", "startup");
		messageObj.put("enabled", 1);
		messageObj.put("message", "Get ready to receive RC-Broadcaster Messages!");
		messageObjects.add(messageObj);
		
		messageObj = new JSONObject();
		messageObj.put("type", "recurring");
		messageObj.put("enabled", 1);
		messageObj.put("message", "I'm still here!");
		messageObjects.add(messageObj);
		
		mainObj.put("messages", messageObjects);

		try {
			// Write the new data file to the dataFolder
			try (FileWriter file = new FileWriter(dataFolder + "/" + dataFileName)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				file.write(gson.toJson(mainObj));
			}
			
		} 
		catch (IOException e) {
			return false;
		}
		
		SetChangeTS();		
		return true;
	}

	// Reads the dataFile from dataFolder and puts the content in a JSONObject.
	// Returns the entire JSONObject if the header is valid.
	// Updates the loggers logging level before return.
	public JSONObject parseDataFile()
	{
        JSONParser parser = new JSONParser();
        
        try {
			Object obj = parser.parse(new FileReader(dataFolder + "/" + dataFileName));

			// We will return entire JSON if valid. 
			// Header is extracted for the validation,
			// and settings is extracted to set defined logging level.
			JSONObject mainObj =  (JSONObject) obj;
			JSONObject headerObj = (JSONObject) mainObj.get("header");
			JSONObject settingsObj = (JSONObject) mainObj.get("settings");
			
			// Check if the plugin name is correct
			String jPluginName = (String)headerObj.get("type");
			if( !jPluginName.equals(pluginName)){
				logger.log(0,"Data file: " + jPluginName +  " is an invalid plugin name.");
				return mainObj;
			}

			// Set the loggers trace level
			logger.setMaxLevel((int)((long)settingsObj.get("debug-level"))); 

			// Return the entire file
			logger.log(2,"Data file parsing: OK");
	        return mainObj;
			
			
		} catch (FileNotFoundException e) {
			logger.log(0,e.toString());
			return null;
		} catch (IOException e) {
			logger.log(0,e.toString());
			return null;
		} catch (ParseException e) {
			logger.log(0,e.toString());
			return null;		
		}
	}
	
	//
	public void Schedule(){
		try{
			logger.log(2,"Checking data file date.");
			timer.schedule(new FileCheckerTask(), 10 * 1000);
		}
		catch(Exception e){
			logger.log(0,"BCDataFile.Schedule: " + e.getMessage());
		}
	}

	// The timer task executed each time our local timer fires.
	class FileCheckerTask extends TimerTask {
		
		public void run() {
			try{
				logger.log(2,"Checking data file date.");
				File file = new File(dataFolder, dataFileName);
				long timeStamp = file.lastModified();
				if(timeStamp != datafileLoadedTS){
					parseDataFile();
					logger.log(1,"Datafile changed. Reloading file.");
					SetChangeTS();
					}
			}
			catch(Exception e){
				logger.log(0,"RemindTask.run: ERROR " + e.getMessage());
			}
			Schedule();
			
		}
	}

}
