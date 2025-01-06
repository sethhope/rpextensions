package com.jcamtech.rpextensions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class StatMonitorListener implements Listener
{
	public MainClass plugin;
	
	public StatMonitorListener(MainClass plugin)
	{
		this.plugin = plugin;
		this.plugin.getLogger().info("Registering Stat Monitor Listener");
	}
	
	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event)
	{
		if(event.getEntityType() != EntityType.PLAYER)
		{
			return;
		}
		FileConfiguration config = plugin.getConfig();
		if(config.getBoolean("UseStatMonitor"))
		{
			if(event.getItem().getItemStack().getType() == Material.getMaterial(config.getString("StatMonitorID")))
			{
				int thirst = 0;
				int sleep = 0;
				if(event.getEntity() instanceof Player)
				{
					if(event.getItem().getItemStack().getType() == Material.getMaterial(config.getString("StatMonitorID")))
					{
						if(event.getItem().getItemStack().getItemMeta().getDisplayName().contains("Sleepiness:"))
						{
							if(plugin.nodeExists(plugin.getPlayerData(), "data."+((Player)event.getEntity()).getUniqueId()+".thirst") && plugin.nodeExists(plugin.getPlayerData(), "data."+((Player)event.getEntity()).getUniqueId()+".tiredness"))
							{
								thirst = plugin.getPlayerData().getInt("data."+((Player)event.getEntity()).getUniqueId()+".thirst");
								sleep = plugin.getPlayerData().getInt("data." + ((Player)event.getEntity()).getUniqueId()+".tiredness");
								ItemMeta meta = event.getItem().getItemStack().getItemMeta();
								meta.setDisplayName("Sleepiness: "+sleep+" | Thirst: "+thirst);
								event.getItem().getItemStack().setItemMeta(meta);
							}
						}
					}
				}
			}
		}
	}
}
