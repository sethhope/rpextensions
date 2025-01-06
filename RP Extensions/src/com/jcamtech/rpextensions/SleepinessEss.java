package com.jcamtech.rpextensions;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.UserData;

public class SleepinessEss extends BukkitRunnable{

	MainClass plugin;
	Player player;
	public SleepinessEss(MainClass plugin)
	{
		this.plugin = plugin;
	}
	public SleepinessEss(MainClass plugin, Player player)
	{
		this.plugin = plugin;
		this.player = player;
		
	}
	@Override
	public void run() {
		boolean isAFK = false;
		if(plugin.getConfigFile().getBoolean("useEssentials") == true)
		{
			Essentials e = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			UserData u = e.getUser(player);
			if(u.isAfk() == true)
			{
				if(plugin.debugMode)
					plugin.getLogger().info(player.getName() +" is AFK");
				isAFK = true;
			}else
			{
				isAFK = false;
			}
		}
		int sleepiness = plugin.PlayerData.getInt("data."+player.getUniqueId()+".tiredness");
		plugin.updateStatMonitor(player);
		if(sleepiness > 0 && player.getGameMode() != GameMode.CREATIVE && isAFK == false && !player.isDead())
		{
			sleepiness--;
			if(plugin.debugMode)
				plugin.getLogger().info("Setting sleepiness for: " + player.getDisplayName() +" to: " + sleepiness);
		}
		if(sleepiness == 8)
		{
			player.sendMessage("You are feeling tired");
		}
		if(sleepiness == 5)
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
