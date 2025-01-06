package com.jcamtech.rpextensions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetStats implements CommandExecutor{
	
	private MainClass plugin;
	
	public SetStats(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("setstats"))
		{
			if(args.length < 3)
			{
				sender.sendMessage("Useage: /setstats [player] [thirst] [sleep]");
				return true;
			}
			Player target = Bukkit.getServer().getPlayer(args[0]);
			int thirst = 20;
			int sleep = 20;
			try
			{
				thirst = Integer.parseInt(args[1]);
				sleep = Integer.parseInt(args[2]);
			}
			catch(Exception e)
			{
				sender.sendMessage("Useage: /setstats [player] [thirst] [sleep]");
				return true;
			}
			thirst = Math.clamp(thirst, 0, 20);
			sleep = Math.clamp(sleep, 0, 20);
			plugin.getPlayerData().set("data."+target.getUniqueId()+".thirst", thirst);
			plugin.getPlayerData().set("data."+target.getUniqueId()+".tiredness", sleep);
			plugin.saveYamls(plugin.getPlayerFile(), plugin.getPlayerData());
			sender.sendMessage("Playerdata set!");
			return true;
		}
		return false;
	}
}
