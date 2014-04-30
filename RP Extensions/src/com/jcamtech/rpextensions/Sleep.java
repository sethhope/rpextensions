package com.jcamtech.rpextensions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Sleep implements CommandExecutor{

	FileConfiguration config;
	FileConfiguration PlayerData;
	
	private MainClass plugin;
	
	public Sleep(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("sleep"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("This command can only be run in game");
			} else
			{
				Player player = (Player)sender;
				sender.sendMessage("You have slept!");
				plugin.getPlayerData().set("data."+player.getUniqueId()+".tiredness", 20);
				plugin.saveYamls(plugin.getPlayerFile(), plugin.getPlayerData());
			}
			
			return true;
		}
		return false;
	}
	
}