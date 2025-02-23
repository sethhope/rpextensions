package com.jcamtech.rpextensions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArrowDespawnCheck extends BukkitRunnable{
	MainClass plugin;
	Entity arrow;
	Player player;
	public ArrowDespawnCheck(MainClass plugin)
	{
		this.plugin = plugin;
	}
	public ArrowDespawnCheck(MainClass plugin, Entity arrow, Player player)
	{
		this.arrow = arrow;
		this.plugin = plugin;
		this.player = player;
	}
	@Override
	public void run() {
		if(arrow == null)
		{
			if(plugin.debugMode == true)
				plugin.getLogger().info("Chair is null");
			cancel();
		}
		if(player.isInsideVehicle())
		{
			arrow.setTicksLived(5);
		}
		else
		{
			if(plugin.debugMode == true)
				plugin.getLogger().info("Removing chair: Player isn't inside vehicle");
			arrow.remove();
			cancel();
		}
		if(!plugin.playerMap.containsValue(arrow))
		{
			if(plugin.debugMode == true)
				plugin.getLogger().info("Removing chair: Playermap doesn't contain arrow");
			arrow.remove();
			cancel();
		}
	}
	

}
