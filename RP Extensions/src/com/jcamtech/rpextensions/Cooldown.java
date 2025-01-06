package com.jcamtech.rpextensions;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Cooldown extends BukkitRunnable{

	MainClass plugin;
	public Cooldown(MainClass plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		for(Player k : plugin.isCooldown.keySet())
		{
			if(plugin.isCooldown.get(k) != null)
			{
				if(plugin.isCooldown.get(k) == true)
				{
					plugin.isCooldown.put(k, false);
				}
			}
		}
	}

}
