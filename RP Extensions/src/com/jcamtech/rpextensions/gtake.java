package com.jcamtech.rpextensions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

public class gtake implements CommandExecutor{

	FileConfiguration config;
	FileConfiguration PlayerData;
	
	private MainClass plugin;
	
	public gtake(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("gtake"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("This command can only be run in game");
			} else if(args.length != 1)
			{
				sender.sendMessage("Usage: /gtake [amount]");
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
					int quarried=Integer.parseInt(args[0]);
					int amount=0;
					amount = PlayerData.getInt("data."+player.getName()+".nuggets");
					if(quarried <= amount)
					{
						ItemStack inven = new ItemStack(Material.GOLD_NUGGET, quarried, (short)1);
						final Inventory inventory = player.getInventory();
						HashMap<Integer, ItemStack> hash = inventory.addItem(inven);
						ItemStack itemsLeft;
						itemsLeft = hash.get(0);
						if(itemsLeft != null)
						{
							quarried -= itemsLeft.getAmount();
							player.sendMessage("�4Not enough room in inventory. Only stored "+itemsLeft.getAmount()+"g.");
						}
						PlayerData.set("data."+player.getName()+".nuggets", amount-quarried);
						try {
							PlayerData.save(PlayerDataFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						player.sendMessage("�2Withdrew "+quarried+"g");
					} else 
					{
						player.sendMessage("�cNot enough gold in the bank to withdraw!");
					}
					
				}else
				{
					player.sendMessage("You are not at an ATM");
				}
			}
			
			return true;
		}
		return false;
	}
	
}
