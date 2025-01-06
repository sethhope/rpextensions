package com.jcamtech.rpextensions;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
@SuppressWarnings("unused")
public class ThirstDeath extends BukkitRunnable{
	
	private MainClass plugin;
	private Player player;
	private FileConfiguration PlayerData;
	private File PlayerDataFile;
	public ThirstDeath(MainClass plugin)
	{
		this.plugin = plugin;
		PlayerDataFile = plugin.getPlayerFile();
		PlayerData = plugin.getPlayerData();
	}
	public ThirstDeath(MainClass plugin, Player player)
	{
		this.player = player;
		this.plugin = plugin;
		PlayerDataFile = plugin.getPlayerFile();
		PlayerData = plugin.getPlayerData();
	}
	public void run()
	{
		
		if(PlayerData.getInt("data."+player.getUniqueId()+".thirst") != 0)
		{
			cancel();
		}else
		{
			player.damage(2);
		}
	}
}