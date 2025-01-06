package com.jcamtech.rpextensions;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;



public final class MainListener implements Listener
{
	private MainClass plugin;
	//private Material[] bedTypes = {Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED, Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED, Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED, Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED, Material.RED_BED, Material.WHITE_BED, Material.YELLOW_BED};
	public MainListener(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@EventHandler
	public void onLogin(final PlayerLoginEvent evt)
	{
		final FileConfiguration PlayerData = plugin.getPlayerData();
		final File PlayerDataFile = plugin.getPlayerFile();
		Player player = evt.getPlayer();
		if(plugin.debugMode)
			plugin.getLogger().info("Player: "+player.getDisplayName()+" Has joined. (UUID: " + player.getUniqueId() + ")");
		plugin.addVariable(PlayerDataFile, PlayerData, "data." + player.getUniqueId() + ".name", player.getDisplayName());
		plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".nuggets", 0);
		plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".thirst", 20);
		plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".tiredness", 20);
	}
	@EventHandler
	public void onPlayerDie(PlayerDeathEvent event) {
			
			FileConfiguration PlayerData = plugin.getPlayerData();
			File PlayerDataFile = plugin.getPlayerFile();
			Player player = event.getEntity();
			
			if(PlayerData.getInt("data."+player.getUniqueId()+".tiredness") == 0)
			{
				event.setDeathMessage(player.getDisplayName()+" has fallen asleep and been eaten by monsters");
			}
			if(PlayerData.getInt("data."+player.getUniqueId()+".thirst") == 0)
			{
				event.setDeathMessage(player.getDisplayName()+" has dehydrated and died.");
			}
			PlayerData.set("data."+player.getUniqueId()+".tiredness", 20);
			PlayerData.set("data."+player.getUniqueId()+".thirst", 20);
			plugin.saveYamls(PlayerDataFile, PlayerData);
	}
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		FileConfiguration PlayerData = plugin.getPlayerData();
		File PlayerDataFile = plugin.getPlayerFile();
		Player player = event.getPlayer();
		
		PlayerData.set("data."+player.getUniqueId()+".tiredness", 20);
		PlayerData.set("data."+player.getUniqueId()+".thirst", 20);
		plugin.saveYamls(PlayerDataFile, PlayerData);
	}
}