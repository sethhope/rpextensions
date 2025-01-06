package com.jcamtech.rpextensions;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChairListener implements Listener
{
	private MainClass plugin;
	public ChairListener(MainClass plugin)
	{
		this.plugin = plugin;
		BukkitRunnable check = new ChairCheck(plugin);
		check.runTaskTimer(plugin, 120, 80);
		plugin.getLogger().info("Registering chair listener");
	}
	@EventHandler
	public void onPlayerDie(PlayerDeathEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player player = event.getEntity();
	        if(plugin.playerMap.containsKey(player))
	        {
	        	plugin.playerMap.get(player).remove();
	        	plugin.playerMap.remove(player);
	        }
	        if(plugin.isSitting.containsKey(player))
	        {
	        	plugin.isSitting.remove(player);
	        }
		}
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
        if(plugin.playerMap.containsKey(player))
        {
        	plugin.playerMap.get(player).remove();
        	plugin.playerMap.remove(player);
        }
        if(plugin.isSitting.containsKey(player))
        	plugin.isSitting.remove(player);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evnt)
	{
		Block block = evnt.getBlock();
		if(plugin.config.getBoolean("UseChairs"))
		{
			Object[] chairList = plugin.config.getList("ChairID").toArray();
			for(Object id : chairList)
			{
				if(block.getType() == Material.getMaterial(id.toString()))
				{
					for(Block b : plugin.chairMap.keySet())
					{
						if(block.equals(b))
						{
							plugin.chairMap.get(b).eject();
							Entity c = plugin.chairMap.get(b);
							for(Player p : plugin.playerMap.keySet())
							{
								if(plugin.playerMap.get(p) == c)
								{
									plugin.playerMap.remove(p);
									if(plugin.isSitting.containsKey(p))
									{
										plugin.isSitting.put(p, false);
									}
									break;
								}
							}
							if(plugin.debugMode == true)
								plugin.getLogger().info("Removing chair: Block was broken");
							plugin.chairMap.get(b).remove();
							plugin.chairMap.remove(b);
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event)
	{
		if(plugin.config.getBoolean("UseChairs") == false)
		{
			return;
		}
		//Get standard vars
		Player player = event.getPlayer();
		FileConfiguration config = plugin.getConfig();
		List<Block> los = event.getPlayer().getLineOfSight((Set<Material>)null, 5);
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) //check for right clicking a block
		{
			for(Block b : los)
			{
				Object[] chairList = config.getList("ChairID").toArray();
				for(Object id : chairList)
				{
					if(b.getType() == Material.getMaterial(id.toString()) && player.isInsideVehicle() == false)
					{
						Entity chair = b.getWorld().spawnEntity(b.getLocation().add(0.5,0.0,0.5), EntityType.ARROW);
						chair.addPassenger(player);
						plugin.isSitting.put(player, true);
						plugin.playerMap.put(player, chair);
						plugin.chairMap.put(b, chair);
						ArrowDespawnCheck adc = new ArrowDespawnCheck(plugin, chair, player);
						adc.runTaskTimer(plugin, 10, 30);
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
	
}
