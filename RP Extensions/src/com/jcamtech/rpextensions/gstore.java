package com.jcamtech.rpextensions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class gstore implements CommandExecutor{

	FileConfiguration config;
	FileConfiguration PlayerData;
	
	private MainClass plugin;
	
	public gstore(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("gstore"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("This command can only be run in game");
			} else if(args.length != 1)
				{
					sender.sendMessage("Usage: /gstore [amount]");
				} else {
				Player player = (Player) sender;
				PlayerData = plugin.getPlayerData();
				File PlayerDataFile = plugin.getPlayerFile();
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
					Inventory i = player.getInventory();
					ItemStack[] inv = i.getContents();
					int baseAmount = Integer.parseInt(args[0]);
					if(baseAmount < 0)
					{
						player.sendMessage("§cInvalid amount!");
						return false;
					}
					if(plugin.debugMode)
						plugin.getLogger().info(player.getDisplayName() + " withdrew "+baseAmount);
					int amount = 0;
					int stored=0;
					float stacksToTake = ((float)baseAmount/64f);
					if(plugin.debugMode)
						plugin.getLogger().info("Withdrawing "+stacksToTake+" stacks to " + player.getDisplayName());
					for(ItemStack inven : inv)
					{
						if(inven != null)
						{
							if(inven.getType().equals(Material.getMaterial(plugin.getConfig().getInt("MoneyID"))))
							{
								amount += inven.getAmount();
							}
						}
					}
					if(baseAmount <= amount)
					{
						InventoryUtil.removeInventoryItems(i, Material.getMaterial(plugin.getConfig().getInt("MoneyID")), baseAmount);
						player.sendMessage("§2Stored "+baseAmount+plugin.getConfig().getString("MoneyUnit"));
						int nuggets=0;
						if(plugin.nodeExists(PlayerData, "data."+player.getUniqueId()+".nuggets"))
						{
							nuggets = (int)PlayerData.get("data."+player.getUniqueId()+".nuggets");
							PlayerData.set("data."+player.getUniqueId()+".nuggets", nuggets+baseAmount);
							try {
								PlayerData.save(PlayerDataFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else
						{
							if(baseAmount > 0)
							{
								plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".nuggets", stored);
							}
						}
					}else
					{
						player.sendMessage("§cNot enough "+plugin.getConfig().getString("MoneyUnit")+" to store");
					}
				}
				else
				{
					player.sendMessage("§cYou are not at an ATM");
				}
				
			}
			
			return true;
		}
		return false;
	}
	
}
