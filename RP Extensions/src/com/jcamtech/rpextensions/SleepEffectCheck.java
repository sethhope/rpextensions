package com.jcamtech.rpextensions;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SleepEffectCheck extends BukkitRunnable{

	MainClass plugin;
	public SleepEffectCheck(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public void run() {
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			int sleepiness = plugin.PlayerData.getInt("data."+player.getName()+".tiredness");
			if(sleepiness <= 5)
			{
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2));
			}
			if(sleepiness <= 3)
			{
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 4));
			}
			if(sleepiness <= 1)
			{
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 50));
			}
		}
	}

	
}
