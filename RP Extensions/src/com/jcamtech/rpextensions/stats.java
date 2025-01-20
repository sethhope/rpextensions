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
		this.config = plugin.getConfig();
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
				Object[] statMessage = config.getList("StatMessage").toArray();
				for(Object line : statMessage)
				{
					String msg = plugin.parseMessage(player, line.toString());
					player.sendMessage(msg);
				}
			}
			
			return true;
		}
		return false;
	}
	
	
}
