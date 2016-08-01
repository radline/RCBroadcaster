package com.sorteberg.rcplugins;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class BCDataFile {
	private static String dataFileName = "RC-Broadcaster.json";
	private static String pluginName = "rc-broadcaster";
	private static int versionNumber = 1;
	private Logger logger;

	private File dataFolder;
	
	public BCDataFile(Logger logger, File dataFolder) {
	this.logger = logger;
	this.dataFolder = dataFolder;
	}

	public boolean dataFolderExist(){
		File file = new File(dataFolder.getAbsolutePath());
		return file.exists();
	}

	public boolean dataFileExist(){
		File file = new File(dataFolder, dataFileName);
		return file.exists();
	}
	
	public boolean createDataFolder(){
		File file = new File(dataFolder.getAbsolutePath());
		if(file.mkdir())
			return true;
		else
			return false;
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean createDataFile(){
		
		//We need a main object to store all three children
		JSONObject mainObj = new JSONObject();

		//We have to create and add a new header node
		JSONObject headerObj = new JSONObject();
		headerObj.put("type", pluginName);
		headerObj.put("ver", versionNumber);	
		mainObj.put("header", headerObj);

		//We have to create and add a new settings node
		JSONObject settingsObj = new JSONObject();
		settingsObj.put("interval", 10);
		settingsObj.put("slowdown-count", 10);
		settingsObj.put("slowdown-factor", 5);
		mainObj.put("settings", settingsObj);
		
		JSONObject messageObj;
		JSONArray messageObjects = new JSONArray();

		messageObj = new JSONObject();
		messageObj.put("type", "startup");
		messageObj.put("enabled", "1");
		messageObj.put("message", "Get ready to receive RC-Broadcaster Messages!");
		messageObjects.add(messageObj);
		
		messageObj = new JSONObject();
		messageObj.put("type", "recurring");
		messageObj.put("enabled", "1");
		messageObj.put("message", "I'm still here!");
		messageObjects.add(messageObj);
		
		mainObj.put("messages", messageObjects);
		
		try {
			try (FileWriter file = new FileWriter(dataFolder + "/" + dataFileName)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				file.write(gson.toJson(mainObj));
			}
			
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public JSONObject parseDataFile()
	{
        JSONParser parser = new JSONParser();
        
        try {
			Object obj = parser.parse(new FileReader(dataFolder + "/" + dataFileName));

			JSONObject mainObj =  (JSONObject) obj;
			JSONObject headerObj = (JSONObject) mainObj.get("header");
			
			String jPluginName = (String)headerObj.get("type");
			if( !jPluginName.equals(pluginName)){
				logger.info("Data file: " + jPluginName +  " is an invalid plugin name.");
				return mainObj;
			}
			else{
				logger.info("Data file: OK");
			}
			
			logger.info("Data file parsing: OK");
	        return mainObj;
			
			
		} catch (FileNotFoundException e) {
			logger.info(e.toString());
			return null;
		} catch (IOException e) {
			logger.info(e.toString());
			return null;
		} catch (ParseException e) {
			logger.info(e.toString());
			return null;		
		}
	}
}
