package com.sorteberg.rcplugins;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
//import org.bukkit.entity.Player;
//import org.bukkit.Bukkit;
//import org.json.simple.JSONObject;

public class BCListener implements Listener {

	//The userStatusList is created in RCBroadcaster and 
	//represented locally by a private pointer.
	private UserStatusList userStatusList;
	
	public BCListener(RCBroadcaster plugin, UserStatusList userStatusList){
		this.userStatusList = userStatusList;
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	userStatusList.Add(event.getPlayer());
    	//.test=event.getPlayer().getName();
    	//event.setJoinMessage("Hei, " + event.getPlayer().getName() + ". Du er en ku!");
    	//Bukkit.broadcastMessage("Velkommen til ku-serveren!");
    	//Player p = event.getPlayer();
    	//p.sendMessage("Mooo!");
    }

}
