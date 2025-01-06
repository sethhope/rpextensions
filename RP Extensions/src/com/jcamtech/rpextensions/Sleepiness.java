package com.jcamtech.rpextensions;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class Sleepiness extends BukkitRunnable{

	MainClass plugin;
	Player player;
	public Sleepiness(MainClass plugin)
	{
		this.plugin = plugin;
	}
	public Sleepiness(MainClass plugin, Player player)
	{
		this.plugin = plugin;
		this.player = player;
	}
	@Override
	public void run() {
		int sleepiness = plugin.PlayerData.getInt("data."+player.getUniqueId()+".tiredness");
		plugin.updateStatMonitor(player);
		if(sleepiness > 0 && player.getGameMode() != GameMode.CREATIVE && !player.isDead())
		{
			sleepiness--;
			if(plugin.debugMode)
				plugin.getLogger().info("Setting sleepiness for: " + player.getDisplayName() +" to: " + sleepiness);
		}
		
		if(sleepiness == 5)
		{
			player.sendMessage("You are feeling tired");
		}
		if(sleepiness == 3)
		{
			player.sendMessage("You are getting sleepy...");
		}
		if(sleepiness == 1)
		{
			player.sendMessage("You are starting to doze off...");
		}
		if(sleepiness == 0)
		{
			player.sendMessage("zzz......");
		}
		plugin.PlayerData.set("data."+player.getUniqueId()+".tiredness", sleepiness);
		plugin.saveYamls(plugin.PlayerDataFile, plugin.PlayerData);
	}

}
