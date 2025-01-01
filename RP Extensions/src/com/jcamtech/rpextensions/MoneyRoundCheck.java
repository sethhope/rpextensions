package com.jcamtech.rpextensions;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MoneyRoundCheck extends BukkitRunnable{

	MainClass plugin;
	public MoneyRoundCheck(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public void run() {
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			int money = 0;
			if(plugin.getConfig().getBoolean("useVault") == true)
			{
				double totalMoney = MainClass.econ.getBalance(player);
				money = (int) MainClass.econ.getBalance(player);
				MainClass.econ.withdrawPlayer(player, totalMoney);
				MainClass.econ.depositPlayer(player, money);
			}
		}
	}

}