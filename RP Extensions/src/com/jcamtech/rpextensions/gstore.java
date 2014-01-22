package com.jcamtech.rpextensions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
				@SuppressWarnings("deprecation")
				List<Block> lineOfSight = player.getLineOfSight(null, 5);
				boolean allow = false;
				for(Block b : lineOfSight)
				{
					
					if(b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)
					{
						plugin.getLogger().info("Sign");
						if(b.hasMetadata("isAtm"))
						{
							if(b.getMetadata("isAtm").get(0).asString() == "true")
							{
								allow = true;
							}
						}
					}
				}
				if(allow==true)
				{
					Inventory i = player.getInventory();
					ItemStack[] inv = i.getContents();
					int baseAmount = Integer.parseInt(args[0]);
					if(plugin.debugMode)
						plugin.getLogger().info("Withdraw "+baseAmount);
					int amount = 0;
					int stored=0;
					float stacksToTake = ((float)baseAmount/64f);
					if(plugin.debugMode)
						plugin.getLogger().info("Withdrawing "+stacksToTake+" stacks.");
					for(ItemStack inven : inv)
					{
						if(inven != null)
						{
							if(inven.getType().equals(Material.GOLD_NUGGET))
							{
								amount += inven.getAmount();
							}
						}
					}
					for(ItemStack inven : inv)
					{
						if(inven != null)
						{
							if(inven.getType().equals(Material.GOLD_NUGGET))
							{
								int curAm=inven.getAmount();
								if(plugin.debugMode)
									plugin.getLogger().info("Has " +amount+" Nuggs");
								
								if(stacksToTake >= 1 && curAm == 64)
								{
									i.remove(inven);
									stacksToTake -= 1;
									stored += 64;
									if(plugin.debugMode)
										plugin.getLogger().info("Withdrawing 1 stack.");
									
								}else
								{
									if(curAm > (stacksToTake*64))
									{
										inven.setAmount((int) (curAm - (stacksToTake*64)));
										stored+=stacksToTake*64;
										if(plugin.debugMode)
											plugin.getLogger().info("Withdrawing "+stacksToTake*64+" items.");
										stacksToTake=0;
									}else{
										i.remove(inven);
										stored+=curAm;
										if(plugin.debugMode)
											plugin.getLogger().info("Withdrawing "+curAm+" items.");
										stacksToTake=0;
									}
								}
							}
						}
					}
					player.sendMessage("§2Stored "+stored+"g");
					int nuggets=0;
					if(plugin.nodeExists(PlayerData, "data."+player.getName()+".nuggets"))
					{
						nuggets = (int)PlayerData.get("data."+player.getName()+".nuggets");
						PlayerData.set("data."+player.getName()+".nuggets", nuggets+stored);
						try {
							PlayerData.save(PlayerDataFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else
					{
						if(amount > 0)
						{
							plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getName()+".nuggets", stored);
						}
					}
				}
				else
				{
					player.sendMessage("You are not at an ATM");
				}
				
			}
			
			return true;
		}
		return false;
	}
	
}
