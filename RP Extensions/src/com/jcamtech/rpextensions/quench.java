package com.jcamtech.rpextensions;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


public class quench implements CommandExecutor{

	FileConfiguration config;
	FileConfiguration PlayerData;
	
	private MainClass plugin;
	
	public quench(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("quench"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("This command can only be run in game");
			} else
			{
				sender.sendMessage("Quenched Thirst");
				plugin.getPlayerData().set("data."+sender.getName()+".thirst", 20);
				plugin.saveYamls(plugin.getPlayerFile(), plugin.getPlayerData());
			}
			
			return true;
		}
		return false;
	}
	
}
