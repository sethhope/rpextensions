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
	@Override
	public void run() {
		for(final Player player : plugin.getServer().getOnlinePlayers())
		{
			if(player.getGameMode() == GameMode.SURVIVAL)
			{
				int sleepiness = plugin.PlayerData.getInt("data."+player.getUniqueId()+".tiredness");
				if(sleepiness <= 5)
				{
					if(player.hasPotionEffect(PotionEffectType.SLOWNESS))
					{
						player.removePotionEffect(PotionEffectType.SLOWNESS);
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2));
					}
					else
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2));
					}
				}
				if(sleepiness <= 3)
				{
					if(player.hasPotionEffect(PotionEffectType.NAUSEA))
					{
						player.removePotionEffect(PotionEffectType.NAUSEA);
						player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
					}
					else
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
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
