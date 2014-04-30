package com.jcamtech.rpextensions;

import org.bukkit.GameMode;
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
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		for(final Player player : plugin.getServer().getOnlinePlayers())
		{
			if(player.getGameMode() == GameMode.SURVIVAL)
			{
				int sleepiness = plugin.PlayerData.getInt("data."+player.getUniqueId()+".tiredness");
				if(sleepiness <= 5)
				{
					if(player.hasPotionEffect(PotionEffectType.SLOW))
					{
						player.removePotionEffect(PotionEffectType.SLOW);
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 2));
					}
					else
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 2));
					}
				}
				if(sleepiness <= 3)
				{
					if(player.hasPotionEffect(PotionEffectType.CONFUSION))
					{
						player.removePotionEffect(PotionEffectType.CONFUSION);
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 0));
					}
					else
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 0));
					}
				}
				if(sleepiness <= 1)
				{
					if(player.hasPotionEffect(PotionEffectType.BLINDNESS))
					{
						player.removePotionEffect(PotionEffectType.BLINDNESS);
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 50));
					}
					else
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 50));
					}
				}
				if(sleepiness==0)
				{
					player.sendMessage("You passed out and were eaten by monsters.");
					player.setHealth(0);
				}
			}
		}
	}
}
