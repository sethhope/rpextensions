package com.jcamtech.rpextensions;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.scheduler.BukkitRunnable;

public class SleepListener implements Listener
{
	public MainClass plugin;
	public SleepListener(MainClass plugin)
	{
		this.plugin = plugin;
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			if(plugin.config.getBoolean("UseSleep"))
			{
				if(plugin.config.getBoolean("useEssentials"))
				{
					BukkitRunnable tiredLoop = new SleepinessEss(plugin, player);
					tiredLoop.runTaskTimer(plugin, plugin.config.getInt("SleepTime"), plugin.config.getInt("SleepTime"));
				}
				else
				{
					BukkitRunnable tiredLoop = new Sleepiness(plugin, player);
					tiredLoop.runTaskTimer(plugin, plugin.config.getInt("SleepTime"), plugin.config.getInt("SleepTime"));
				}
				BukkitRunnable effectLoop = new SleepEffectCheck(plugin);
				effectLoop.runTaskTimer(plugin, 60, 60);
			}
		}
		plugin.getLogger().info("Registering Sleep Listener");
	}
	
	@EventHandler
	public void onLogin(final PlayerLoginEvent evt)
	{
		Player player = evt.getPlayer();
		if(plugin.getConfig().getBoolean("UseSleep"))
		{
			if(plugin.getConfig().getBoolean("useEssentials"))
			{
				BukkitRunnable tiredLoop = new SleepinessEss(plugin, player);
				tiredLoop.runTaskTimer(plugin, plugin.getConfig().getInt("SleepTime"), plugin.getConfig().getInt("SleepTime"));
			}
			else
			{
				BukkitRunnable tiredLoop = new Sleepiness(plugin, player);
				tiredLoop.runTaskTimer(plugin, plugin.getConfig().getInt("SleepTime"), plugin.getConfig().getInt("SleepTime"));
			}
			plugin.getCommand("sleep").setExecutor(new Sleep(plugin));
			BukkitRunnable effectLoop = new SleepEffectCheck(plugin);
			effectLoop.runTaskTimer(plugin, 60, 60);
		}
	}
	
	@EventHandler
	public void onPlayerDie(PlayerDeathEvent event) 
	{
		FileConfiguration PlayerData = plugin.getPlayerData();
		File PlayerDataFile = plugin.getPlayerFile();
		Player player = event.getEntity();
		
		if(PlayerData.getInt("data."+player.getUniqueId()+".tiredness") == 0)
		{
			event.setDeathMessage(player.getDisplayName()+" has fallen asleep and been eaten by monsters");
		}
		PlayerData.set("data."+player.getUniqueId()+".tiredness", 20);
		plugin.saveYamls(PlayerDataFile, PlayerData);
	}
	
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event)
	{
		if(event.getBedEnterResult() == BedEnterResult.OK)
		{
			Player player = event.getPlayer();
			FileConfiguration PlayerData = plugin.getPlayerData();
			File PlayerDataFile = plugin.getPlayerFile();
			FileConfiguration config = plugin.getConfig();
			if(config.getBoolean("UseSleep") == true)
			{
				plugin.PlayerData.set("data."+player.getUniqueId()+".tiredness", 20);
				plugin.saveYamls(PlayerDataFile, PlayerData);
			}
		}
	}
}
