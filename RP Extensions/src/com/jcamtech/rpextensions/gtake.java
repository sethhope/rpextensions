package com.jcamtech.rpextensions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

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
					int quarried=Integer.parseInt(args[0]);
					if(quarried < 0)
					{
						player.sendMessage("§cInvalid amount!");
						return false;
					}
					int amount=0;
					if(plugin.getConfig().getBoolean("useVault") == true)
						amount = (int) MainClass.econ.getBalance(player);
					else
						amount = PlayerData.getInt("data."+player.getUniqueId()+".nuggets");
					if(quarried <= amount)
					{
						ItemStack inven = new ItemStack(Material.getMaterial(plugin.getConfig().getString("MoneyID")), quarried);
						if(plugin.getConfig().getBoolean("RenameMoney"));
						{
							ItemMeta meta = inven.getItemMeta();
							meta.setDisplayName(plugin.getConfig().getString("MoneyName"));
							inven.setItemMeta(meta);
						}
						final Inventory inventory = player.getInventory();
						HashMap<Integer, ItemStack> hash = inventory.addItem(inven);
						ItemStack itemsLeft;
						itemsLeft = hash.get(0);
						if(itemsLeft != null)
						{
							quarried -= itemsLeft.getAmount();
							player.sendMessage("§4Not enough room in inventory. Only stored "+itemsLeft.getAmount()+config.getString("MoneyUnit"));
						}
						if(plugin.getConfig().getBoolean("useVault")==true)
						{
							MainClass.econ.withdrawPlayer(player, quarried);
							if(plugin.nodeExists(PlayerData, "data."+player.getUniqueId()+".nuggets"))
							{
								PlayerData.set("data."+player.getUniqueId()+".nuggets", MainClass.econ.getBalance(player));
								try {
									PlayerData.save(PlayerDataFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}else
						{
							PlayerData.set("data."+player.getUniqueId()+".nuggets", amount-quarried);
							try {
								PlayerData.save(PlayerDataFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						player.sendMessage("§2Withdrew "+quarried+plugin.getConfig().getString("MoneyUnit"));
					} else 
					{
						player.sendMessage("§cNot enough "+plugin.getConfig().getString("MoneyUnit")+" in the bank to withdraw!");
					}
					
				}else
				{
					player.sendMessage("§cYou are not at an ATM");
				}
			}
			
			return true;
		}
		return false;
	}
	
}
