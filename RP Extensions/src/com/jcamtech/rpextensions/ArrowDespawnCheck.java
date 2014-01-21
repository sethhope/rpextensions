package com.jcamtech.rpextensions;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class ArrowDespawnCheck extends BukkitRunnable{
	MainClass plugin;
	Entity arrow;
	public ArrowDespawnCheck(MainClass plugin)
	{
		this.plugin = plugin;
	}
	public ArrowDespawnCheck(MainClass plugin, Entity arrow)
	{
		this.arrow = arrow;
		this.plugin = plugin;
	}
	@Override
	public void run() {
		if(plugin.debugMode)
			plugin.getLogger().info("PreventedDespawn");
		arrow.setTicksLived(1);
		if(!plugin.playerMap.containsValue(arrow))
		{
			cancel();
		}
	}
	

}
