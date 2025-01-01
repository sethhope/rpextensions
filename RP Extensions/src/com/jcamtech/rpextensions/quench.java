package com.jcamtech.rpextensions;


import org.bukkit.Bukkit;
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
			if(args.length > 0)
			{
				if(args.length != 1)
				{
					sender.sendMessage("Useage: /quench [player]");
					return true;
				}
				Player target = (Bukkit.getServer().getPlayer(args[0]));
				if(target == null)
				{
					sender.sendMessage("Player is not online!");
					return true;
				}
				if(sender instanceof Player)
				{
					if(sender.hasPermission("rpext.sleep.others") == false)
					{
						sender.sendMessage("You do not have permission to use that command!");
						return true;
					}
				}
				sender.sendMessage("Quenched Thirst for "+args[0]);
				plugin.getPlayerData().set("data."+target.getUniqueId()+".thirst", 20);
				plugin.saveYamls(plugin.getPlayerFile(), plugin.getPlayerData());
				target.sendMessage("Your thirst has been quenched!");
			}
			else
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage("This command can only be run in game");
				} else
				{
					Player player = (Player)sender;
					sender.sendMessage("Quenched Thirst");
					plugin.getPlayerData().set("data."+player.getUniqueId()+".thirst", 20);
					plugin.saveYamls(plugin.getPlayerFile(), plugin.getPlayerData());
				}
			}
			return true;
		}
		return false;
	}
	
}
