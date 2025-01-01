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

public class givemoney implements CommandExecutor{

	FileConfiguration config;
	FileConfiguration PlayerData;
	
	private MainClass plugin;
	
	public givemoney(MainClass plugin)
	{
		this.plugin = plugin;
	}
	//@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("ggive"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("This command can only be run in game");
			} else if(args.length != 2)
			{
				sender.sendMessage("Usage: /ggive [player] [amount]");
			} else {
				Player player = (Player) sender;
				Player target = (Bukkit.getServer().getPlayer(args[0]));
				int quarried = 0;
				try{
				quarried=Integer.parseInt(args[1]);
				}catch(NumberFormatException e)
				{
					player.sendMessage("Invalid format! Use /ggive [player] [amount]");
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
					//TODO: Add iterator check for sign types
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
					if(plugin.getConfig().getBoolean("useVault")==true)
						targetAmount = (int) MainClass.econ.getBalance(target);
					else
						targetAmount = PlayerData.getInt("data."+target.getUniqueId()+".nuggets");
					
					targetAmount = targetAmount + quarried;
					PlayerData.set("data."+target.getUniqueId()+".nuggets", targetAmount);
					if(plugin.getConfig().getBoolean("useVault")==true)
					{
						double cura = MainClass.econ.getBalance(target);
						MainClass.econ.withdrawPlayer(target, cura);
						MainClass.econ.depositPlayer(target, targetAmount);
					}
					player.sendMessage("§2Gave "+quarried+plugin.getConfig().getString("MoneyUnit")+" to "+target.getDisplayName());
					target.sendMessage(player.getDisplayName()+" §2has given you "+quarried+plugin.getConfig().getString("MoneyUnit"));
					
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
