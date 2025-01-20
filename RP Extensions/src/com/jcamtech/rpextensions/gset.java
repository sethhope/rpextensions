package com.jcamtech.rpextensions;

import java.util.List;
import java.util.Set;

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

public class gset implements CommandExecutor{

	FileConfiguration config;
	FileConfiguration PlayerData;
	
	private MainClass plugin;
	
	public gset(MainClass plugin)
	{
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}
	//@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("gset"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("This command can only be run in game");
			} else if(args.length != 2)
			{
				sender.sendMessage("Usage: /gset [player] [amount]");
			} else {
				Player player = (Player) sender;
				Player target = (Bukkit.getServer().getPlayer(args[0]));
				int quarried = 0;
				try{
				quarried=Integer.parseInt(args[1]);
				}catch(NumberFormatException e)
				{
					player.sendMessage("Invalid format! Use /gset [player] [amount]");
					return false;
				}
				if(target == null)
				{
					player.sendMessage("§cPlayer is not online");
					return false;
				}
				PlayerData = plugin.getPlayerData();
				List<Block> lineOfSight = player.getLineOfSight((Set<Material>)null, 5);
				boolean allow = false;
				for(Block b : lineOfSight)
				{
					if(b.getType() == Material.OAK_SIGN || b.getType() == Material.OAK_HANGING_SIGN || b.getType() == Material.OAK_WALL_HANGING_SIGN || b.getType() == Material.OAK_WALL_SIGN)
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
					int  targetAmount = 0;
					targetAmount = quarried;
					PlayerData.set("data."+target.getUniqueId()+".nuggets", targetAmount);
					if(plugin.getConfig().getBoolean("useVault")==true)
					{
						double curA = MainClass.econ.getBalance(target);
						MainClass.econ.withdrawPlayer(target, curA);
						MainClass.econ.depositPlayer(target, targetAmount);
					}
					if(plugin.getConfig().getBoolean("PrefixUnit"))
					{
						player.sendMessage("§2Set "+target.getDisplayName()+"§2's account to "+plugin.getConfig().getString("MoneyUnit")+quarried);
						target.sendMessage(player.getDisplayName()+" §2has set your account to "+plugin.getConfig().getString("MoneyUnit")+quarried);
					}
					else
					{
						player.sendMessage("§2Set "+target.getDisplayName()+"§2's account to "+quarried+plugin.getConfig().getString("MoneyUnit"));
						target.sendMessage(player.getDisplayName()+" §2has set your account to "+quarried+plugin.getConfig().getString("MoneyUnit"));
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
