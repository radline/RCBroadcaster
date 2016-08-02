package com.sorteberg.rcplugins;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class BCDataFile {
	
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
		this.logger = logger;
		this.dataFolder = dataFolder;
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
			
		} catch (IOException e) {
			return false;
		}
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
			logger.setMaxLevel((int)settingsObj.get("trace-level")); 
			
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
}
