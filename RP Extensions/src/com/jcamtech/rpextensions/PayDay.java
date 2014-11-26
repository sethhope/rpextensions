package com.jcamtech.rpextensions;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PayDay extends BukkitRunnable{

	MainClass plugin;
	public PayDay(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public void run() {
		
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			if(plugin.config.getBoolean("UseInterest"))
			{
				double rate = (double) plugin.config.get("InterestRate");
				int money = plugin.PlayerData.getInt("data."+player.getUniqueId()+".nuggets");
				int total = (int) (money+(money*rate));
				plugin.PlayerData.set("data."+player.getUniqueId()+".nuggets", total);
				plugin.saveYamls(plugin.PlayerDataFile, plugin.PlayerData);
				player.sendMessage("§3§lPAYDAY");
				player.sendMessage("§4==============================");
				player.sendMessage("§3Original Amount: "+money+plugin.config.get("MoneyUnit")+" Interest Rate: "+rate);
				player.sendMessage("§3Interest Gained: "+Math.round(money*rate));
				player.sendMessage("§3Final Balance: "+total+plugin.config.get("MoneyUnit"));
				player.sendMessage("§4==============================");
				
			}
		}
	}

}
