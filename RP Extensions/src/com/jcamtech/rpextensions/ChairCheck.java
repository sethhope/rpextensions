package com.jcamtech.rpextensions;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChairCheck extends BukkitRunnable{

	MainClass plugin;
	public ChairCheck(MainClass plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			if(player.getVehicle() == null)
			{
				if(plugin.isSitting.containsKey(player))
				{
					boolean isSeated = plugin.isSitting.get(player);
					
					if(isSeated)
					{
						if(plugin.isSitting.containsKey(player))
						{
							plugin.isSitting.remove(player);
						}
						if(plugin.playerMap.containsKey(player))
						{
							plugin.playerMap.get(player).remove();
							plugin.playerMap.remove(player);
						}
					}
				}
			}
		}
		
	}

}
