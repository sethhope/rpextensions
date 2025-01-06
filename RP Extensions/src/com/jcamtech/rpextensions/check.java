package com.jcamtech.rpextensions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class check implements CommandExecutor{

	FileConfiguration config;
	FileConfiguration PlayerData;
	
	private MainClass plugin;
	
	public check(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("checkstats"))
		{
			if(!(sender instanceof Player))
			{
				Player target = (Bukkit.getServer().getPlayer(args[0]));
				if(target == null)
				{
					sender.sendMessage("Player is not online!");
					return true;
				}
				PlayerData = plugin.getPlayerData();
				String name = PlayerData.getString("data." + target.getUniqueId() + ".name");
				int nugs = (int) PlayerData.getInt("data."+target.getUniqueId()+".nuggets");
				if(plugin.getConfig().getBoolean("useVault")==true)
				{
					nugs = (int) MainClass.econ.getBalance(target);
				}
				int thirst = (int) PlayerData.getInt("data."+target.getUniqueId()+".thirst");
				int sleepiness = PlayerData.getInt("data."+target.getUniqueId()+".tiredness");
				String unit = plugin.getConfig().getString("MoneyUnit");
				sender.sendMessage("------TARGET--STATS------");
				sender.sendMessage("UUID: "+target.getUniqueId());
				sender.sendMessage("Name: " + name + "|Thirst: "+thirst);
				if(plugin.getConfig().getBoolean("UseSleep"))
					sender.sendMessage("Sleepiness: "+sleepiness);
				if(plugin.getConfig().getBoolean("UseGoldNuggetBank")==true)
					sender.sendMessage("Money: " + nugs + unit);
				sender.sendMessage("-------------------------");
			} else if(args.length != 1)
			{
				sender.sendMessage("Usage: /checkstats [player]");
			} else {
				Player player = (Player) sender;
				Player target = (Bukkit.getServer().getPlayer(args[0]));
				if(target == null)
				{
					sender.sendMessage("Player is not online!");
					return true;
				}
				PlayerData = plugin.getPlayerData();
				String name = PlayerData.getString("data." + target.getUniqueId() + ".name");
				int nugs = (int) PlayerData.getInt("data."+target.getUniqueId()+".nuggets");
				if(plugin.getConfig().getBoolean("useVault")==true)
				{
					nugs = (int) MainClass.econ.getBalance(target);
				}
				int thirst = (int) PlayerData.getInt("data."+target.getUniqueId()+".thirst");
				int sleepiness = PlayerData.getInt("data."+target.getUniqueId()+".tiredness");
				String unit = plugin.getConfig().getString("MoneyUnit");
				player.sendMessage("§4------TARGET--STATS------");
				player.sendMessage("§fUUID: §9"+target.getUniqueId());
				player.sendMessage("§fName: §9" + name + "§f|Thirst: §9"+thirst);
				if(plugin.getConfig().getBoolean("UseSleep"))
					player.sendMessage("§fSleepiness: §9"+sleepiness);
				if(plugin.getConfig().getBoolean("UseGoldNuggetBank")==true)
					player.sendMessage("§fMoney: §9" + nugs + unit);
				player.sendMessage("§4-------------------------");
			}
			
			return true;
		}
		return false;
	}
	
}
