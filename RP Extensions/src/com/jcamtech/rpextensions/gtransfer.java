package com.jcamtech.rpextensions;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class gtransfer implements CommandExecutor{

	FileConfiguration config;
	FileConfiguration PlayerData;
	
	private MainClass plugin;
	
	public gtransfer(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("gtransfer"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("This command can only be run in game");
			} else if(args.length != 2)
			{
				sender.sendMessage("Usage: /gtransfer [player] [amount]");
			} else {
				Player player = (Player) sender;
				Player target = (Bukkit.getServer().getPlayer(args[0]));
				int quarried = 0;
				try{
				quarried=Integer.parseInt(args[1]);
				}catch(NumberFormatException e)
				{
					player.sendMessage("Invalid format! Use /gtransfer [player] [amount]");
					return false;
				}
				if(target == null)
				{
					player.sendMessage("§cPlayer is not online");
					return false;
				}
				PlayerData = plugin.getPlayerData();
				List<Block> lineOfSight = player.getLineOfSight(null, 5);
				boolean allow = false;
				for(Block b : lineOfSight)
				{
					
					if(b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)
					{
						if(b.hasMetadata("isAtm"))
						{
							if(b.getMetadata("isAtm").get(0).asString() == "true")
							{
								allow = true;
							}
						}else
						{
							ConfigurationSection atms = PlayerData.getConfigurationSection("atms");
							for(String sAtms : atms.getKeys(false))
							{
								if(b.getX() == atms.getInt(sAtms+".x") && b.getY() == atms.getInt(sAtms+".y") && b.getZ() == atms.getInt(sAtms+".z"))
								{
									if(PlayerData.getBoolean("atms."+sAtms+".active")==true)
									{
										allow = true;
										b.setMetadata("isAtm", new FixedMetadataValue(plugin, "true"));
									}
								}
							}
						}
					}
				}
				if(allow==true)
				{
					if(quarried < 0)
					{
						player.sendMessage("§cInvalid amount of money");
						return false;
					}
					int amount=0;
					int  targetAmount = 0;
					targetAmount = PlayerData.getInt("data."+target.getUniqueId()+".nuggets");
					amount = PlayerData.getInt("data."+player.getUniqueId()+".nuggets");
					if(amount >= quarried)
					{
						amount = amount - quarried;
						targetAmount = targetAmount + quarried;
						PlayerData.set("data."+player.getUniqueId()+".nuggets", amount);
						PlayerData.set("data."+target.getUniqueId()+".nuggets", targetAmount);
						player.sendMessage("§2Transferred "+quarried+plugin.getConfig().getString("MoneyUnit")+" to "+target.getDisplayName());
						target.sendMessage(player.getDisplayName()+" §2has sent you "+quarried+plugin.getConfig().getString("MoneyUnit"));
						
					}else
					{
						player.sendMessage("§cNot enough money in your account!");
						return false;
					}
					
				}else
				{
					player.sendMessage("§cYou are not at an ATM");
					return false;
				}
			}
			
			return true;
		}
		return false;
	}
	
}
