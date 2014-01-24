package com.jcamtech.rpextensions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class stats implements CommandExecutor{

	FileConfiguration config;
	FileConfiguration PlayerData;
	
	private MainClass plugin;
	
	public stats(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("stats"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("This command can only be run in game");
			} else {
				Player player = (Player) sender;
				PlayerData = plugin.getPlayerData();
				String name = PlayerData.getString("data." + player.getName() + ".name");
				int nugs = (int) PlayerData.get("data."+player.getName()+".nuggets");
				int thirst = (int) PlayerData.get("data."+player.getName()+".thirst");
				int sleepiness = PlayerData.getInt("data."+player.getName()+".tiredness");
				String unit = plugin.getConfig().getString("MoneyUnit");
				player.sendMessage("§4----------STATS----------");
				player.sendMessage("§fName: §9" + name + "§f|Thirst: §9"+thirst);
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
