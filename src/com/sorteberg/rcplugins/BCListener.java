package com.sorteberg.rcplugins;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BCListener implements Listener {

	//The userStatusList is created in RCBroadcaster and 
	//represented locally by a private pointer.
	private UserStatusList userStatusList;
	
	public BCListener(UserStatusList userStatusList){
		this.userStatusList = userStatusList;
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	userStatusList.Join(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
    	userStatusList.Quit(event.getPlayer());
    }

}
