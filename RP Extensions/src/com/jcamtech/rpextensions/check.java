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
		config = plugin.getConfig();
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("checkstats"))
		{
			if(args.length != 1)
			{
				sender.sendMessage("Usage: /checkstats [player]");
			} else if(!(sender instanceof Player))
			{
				Player target = (Bukkit.getServer().getPlayer(args[0]));
				if(target == null)
				{
					sender.sendMessage("Player is not online!");
					return true;
				}
				PlayerData = plugin.getPlayerData();
				Object[] statMessage = config.getList("CheckStatMessage").toArray();
				for(Object line : statMessage)
				{
					String msg = plugin.parseMessage(target, line.toString());
					sender.sendMessage(msg);
				}
			} else 
			{
				Player target = (Bukkit.getServer().getPlayer(args[0]));
				if(target == null)
				{
					sender.sendMessage("Player is not online!");
					return true;
				}
				Object[] statMessage = config.getList("CheckStatMessage").toArray();
				for(Object line : statMessage)
				{
					String msg = plugin.parseMessage(target, line.toString());
					sender.sendMessage(msg);
				}
			}
			
			return true;
		}
		return false;
	}
	
}
