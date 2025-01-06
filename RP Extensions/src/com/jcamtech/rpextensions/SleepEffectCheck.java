package com.jcamtech.rpextensions;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SleepEffectCheck extends BukkitRunnable{

	MainClass plugin;
	public SleepEffectCheck(MainClass plugin)
	{
		this.plugin = plugin;
	}
	int dozeTimer = 0;
	@Override
	public void run() {
		dozeTimer = dozeTimer + 1;
		if(dozeTimer >= 20)
		{
			dozeTimer = 0;
		}
		for(final Player player : plugin.getServer().getOnlinePlayers())
		{
			if(player.getGameMode() == GameMode.SURVIVAL)
			{
				int sleepiness = plugin.PlayerData.getInt("data."+player.getUniqueId()+".tiredness");
				boolean addEffect = false;
				if(sleepiness > 5)
				{
					if(player.getMetadata("HasSleepEffects").size() > 0)
					{
						if(player.getMetadata("HasSleepEffects").get(0).asBoolean() == true)
						{
							player.removeMetadata("HasSleepEffects", plugin);
							player.removePotionEffect(PotionEffectType.SLOWNESS);
							player.removePotionEffect(PotionEffectType.NAUSEA);
							player.removePotionEffect(PotionEffectType.BLINDNESS);
						}
					}
				}
				if(sleepiness <= 5 && sleepiness > 0)
				{
					if(!player.hasPotionEffect(PotionEffectType.SLOWNESS))
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 9999999, 1));
						addEffect = true;
					}
				}
				if(sleepiness <= 3 && sleepiness > 0)
				{
					if(!player.hasPotionEffect(PotionEffectType.NAUSEA))
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 9999999, 1));
						addEffect = true;
					}
				}
				if(sleepiness <= 1 && sleepiness > 0)
				{
					if(!player.hasPotionEffect(PotionEffectType.BLINDNESS))
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 10));
						addEffect = true;
					}
				}
				if(sleepiness <= 0)
				{
					if(dozeTimer == 0)
					{
						player.sendTitle("Dozing...", null, 10, 300, 10);
					}
					if(dozeTimer < 5)
					{
						if(player.hasPotionEffect(PotionEffectType.BLINDNESS))
						{
							player.removePotionEffect(PotionEffectType.BLINDNESS);
							player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 9999999));
							addEffect = true;
						}
						else
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 9999999));
							addEffect = true;
						}
						if(player.hasPotionEffect(PotionEffectType.SLOWNESS))
						{
							player.removePotionEffect(PotionEffectType.SLOWNESS);
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 9999999, 9999999));
							addEffect = true;
						}
						else
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 9999999, 9999999));
							addEffect = true;
						}
					}
					else if(dozeTimer == 5)
					{
						if(player.hasPotionEffect(PotionEffectType.BLINDNESS))
						{
							player.removePotionEffect(PotionEffectType.BLINDNESS);
							player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 10));
							addEffect = true;
						}
						else
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 10));
							addEffect = true;
						}
						if(!player.hasPotionEffect(PotionEffectType.NAUSEA))
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 9999999, 1));
							addEffect = true;
						}
						else
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 9999999, 1));
							addEffect = true;
						}
						if(player.hasPotionEffect(PotionEffectType.SLOWNESS))
						{
							player.removePotionEffect(PotionEffectType.SLOWNESS);
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 9999999, 2));
							addEffect = true;
						}
						else
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 9999999, 2));
							addEffect = true;
						}
					}
					else
					{
						if(!player.hasPotionEffect(PotionEffectType.BLINDNESS))
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 10));
							addEffect = true;
						}
						if(!player.hasPotionEffect(PotionEffectType.NAUSEA))
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 9999999, 1));
							addEffect = true;
						}
						if(!player.hasPotionEffect(PotionEffectType.SLOWNESS))
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 9999999, 2));
							addEffect = true;
						}
					}
				}
				if(addEffect)
				{
					player.removeMetadata("HasSleepEffects", plugin);
					player.setMetadata("HasSleepEffects", new FixedMetadataValue(plugin, true));
				}
			}
		}
	}
}
