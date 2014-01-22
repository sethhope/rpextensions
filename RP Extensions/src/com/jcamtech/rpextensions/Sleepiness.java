package com.jcamtech.rpextensions;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Sleepiness extends BukkitRunnable{

	MainClass plugin;
	public Sleepiness(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public void run() {
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			int sleepiness = plugin.PlayerData.getInt("data."+player.getName()+".tiredness");
			if(sleepiness > 0)
				sleepiness--;
			
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
			plugin.PlayerData.set("data."+player.getName()+".tiredness", sleepiness);
			plugin.saveYamls(plugin.PlayerDataFile, plugin.PlayerData);
		}
	}

}
