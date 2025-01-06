package com.jcamtech.rpextensions;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

public class ThirstListener implements Listener
{
	public MainClass plugin;
	
	public ThirstListener(MainClass plugin)
	{
		this.plugin = plugin;
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			if(plugin.config.getBoolean("useEssentials"))
			{
				BukkitRunnable thirstloop = new ThirstLoopEss(plugin, player);
				thirstloop.runTaskTimer(plugin,  plugin.config.getInt("ThirstTime"), plugin.config.getInt("ThirstTime"));
			}
			else
			{
				BukkitRunnable thirstloop = new ThirstLoop(plugin, player);
				thirstloop.runTaskTimer(plugin,  plugin.config.getInt("ThirstTime"), plugin.config.getInt("ThirstTime"));
			}
		}
		this.plugin.getLogger().info("Registering Thirst Listener");
	}
	
	@EventHandler
	public void onLogin(final PlayerLoginEvent evt)
	{
		Player player = evt.getPlayer();
		if(plugin.getConfig().getBoolean("UseThirst"))
		{	
			if(plugin.getConfig().getBoolean("useEssentials"))
			{
				BukkitRunnable thirstloop = new ThirstLoopEss(plugin, player);
				thirstloop.runTaskTimer(plugin,  plugin.getConfig().getInt("ThirstTime"), plugin.getConfig().getInt("ThirstTime"));
			}
			else
			{
				BukkitRunnable thirstloop = new ThirstLoop(plugin, player);
				thirstloop.runTaskTimer(plugin,  plugin.getConfig().getInt("ThirstTime"), plugin.getConfig().getInt("ThirstTime"));
			}
		}
	}
	
	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent event)
	{
		Player player = event.getPlayer();
		FileConfiguration PlayerData = plugin.getPlayerData();
		File PlayerDataFile = plugin.getPlayerFile();
		FileConfiguration config = plugin.getConfig();
		if(config.getBoolean("UseThirst") == true)
		{
			if(event.getItem().getItemMeta() instanceof PotionMeta)
			{
				final PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
				final PotionType data = meta.getBasePotionType();
				if(data == PotionType.WATER)
				{
					int count = PlayerData.getInt("data."+player.getUniqueId()+".thirst");
					if(count < 20)
					{
						count += 15;
						if(count > 20)
						{
							count = 20;
						}
						player.sendMessage("Thirst Quenched.");
						PlayerData.set("data."+player.getUniqueId()+".thirst", count);
						plugin.saveYamls(PlayerDataFile, PlayerData);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event)
	{
		//Get standard vars
		Player player = event.getPlayer();
		FileConfiguration PlayerData = plugin.getPlayerData();
		File PlayerDataFile = plugin.getPlayerFile();
		FileConfiguration config = plugin.getConfig();
		if(config.getBoolean("UseThirst") == true)
		{
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				List<Block> lineOfSight = event.getPlayer().getLineOfSight((Set<Material>)null, 5);
				for(Block b : lineOfSight)
				{
					if(b.getType()==Material.WATER)
					{
						int count = PlayerData.getInt("data."+player.getUniqueId()+".thirst");
						if(count < 20)
						{
							count += 15;
							if(count > 20)
								count = 20;
							player.sendMessage("Thirst Quenched.");
							PlayerData.set("data."+player.getUniqueId()+".thirst", count);
							plugin.saveYamls(PlayerDataFile, PlayerData);
						}
					}
				}
			}
		}
	}
}
