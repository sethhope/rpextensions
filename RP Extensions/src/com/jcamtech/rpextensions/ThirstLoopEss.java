package com.jcamtech.rpextensions;

import java.io.File;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.UserData;

public class ThirstLoopEss extends BukkitRunnable
{
	private MainClass plugin;
	public Player player;
	private FileConfiguration PlayerData;
	private File PlayerDataFile;
	public ThirstLoopEss(MainClass plugin)
	{
		this.plugin = plugin;
		PlayerDataFile = plugin.getPlayerFile();
		PlayerData = plugin.getPlayerData();
	}
	public ThirstLoopEss(MainClass plugin, Player player)
	{
		this.player = player;
		this.plugin = plugin;
		if(plugin.debugMode==true)
			plugin.getLogger().info("enabling thirst for: " + player.getDisplayName());
		PlayerDataFile = plugin.getPlayerFile();
		PlayerData = plugin.getPlayerData();
	}
	public void run()
	{
		
		if(!player.isOnline())
		{
			if(plugin.debugMode==true)
				plugin.getLogger().info("Disabling thirst for: " + player.getUniqueId());
			cancel();
			return;
		}
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
		int count = PlayerData.getInt("data."+player.getUniqueId()+".thirst");
		if(player.getGameMode() != GameMode.CREATIVE && isAFK == false && !player.isDead())
		{
			
			if(count >= 1)
			{
				count--;
				if(count >= 2 && player.isSprinting())
				{
					count--;
				}
			}
			if(count == 7)
			{
				player.sendMessage("Your mouth is a little dry.");
			}
			if(count == 5)
			{
				player.sendMessage("You are getting thirsty");
			}
			if(count == 2)
			{
				player.sendMessage("You are extremely thirsty! Find water quickly!");
			}
			if(count == 0)
			{
				BukkitRunnable thirstDie = new ThirstDeath(plugin, player);
				thirstDie.runTaskTimer(plugin, 10, 20);
				player.sendMessage("§cYou are dehydrated!");
			}
			if(plugin.debugMode)
				plugin.getLogger().info("Thirst for "+player.getDisplayName()+": "+count);
			PlayerData.set("data."+player.getUniqueId()+".thirst", count);
			plugin.saveYamls(PlayerDataFile, PlayerData);
		}
	}
}