package com.jcamtech.rpextensions;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class MoneyListener implements Listener
{
	public MainClass plugin;
	
	public MoneyListener(MainClass plugin)
	{
		this.plugin = plugin;
		if(plugin.config.getBoolean("RenameMoney") == true)
		{
			ItemNameCheck itemCheck = new ItemNameCheck(plugin);
			itemCheck.runTaskTimer(plugin, 10, 20);
		}
		if(plugin.config.getBoolean("UseInterest"))
		{
			BukkitRunnable payday = new PayDay(plugin);
			payday.runTaskTimer(plugin, plugin.config.getInt("InterestTime"), plugin.config.getInt("InterestTime"));
		}
		if(plugin.config.getBoolean("useVault"))
		{
			if(!plugin.setupEconomy())
			{
				plugin.getLogger().severe("Failed to load! Could not initialize vault! (try disabling useVault in config.yml");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}
	        BukkitRunnable roundMoney = new MoneyRoundCheck(plugin);
	        roundMoney.runTaskTimer(plugin, 10, 20);
		}
		this.plugin.getLogger().info("Registering Money Listener");
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onSignCreate(SignChangeEvent sign)
	{
		FileConfiguration PlayerData = plugin.getPlayerData();
		File PlayerDataFile = plugin.getPlayerFile();
		if(sign.getPlayer().hasPermission("rpext.createatm"))
		{
			if (sign.getLine(0).equals("[rpAtm]")) 
			{
		          sign.setLine(0, "§5{ATM}");
		          sign.setLine(1, "Use /gstore");
		          sign.setLine(2, "or /gtake");
		          sign.setLine(3, "to use ATM");
		          sign.getBlock().setMetadata("isAtm", new FixedMetadataValue(plugin, "true"));
		          int atms = PlayerData.getInt("data.atms");
		          PlayerData.set("atms."+atms+".x", sign.getBlock().getX());
		          PlayerData.set("atms."+atms+".y", sign.getBlock().getY());
		          PlayerData.set("atms."+atms+".z", sign.getBlock().getZ());
		          PlayerData.set("atms."+atms+".active", true);
		          atms = atms+1;
		          PlayerData.set("data.atms", atms);
		          plugin.saveYamls(PlayerDataFile, PlayerData);
			}
		}
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if(event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			if(event.getClickedBlock().hasMetadata("isAtm"))
			{
				if(!player.hasPermission("rpext.createatm"))
				{
					player.sendMessage("You do not have permission to break this ATM!");
					event.setCancelled(true);
					return;
				}
			}
		}
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(event.getClickedBlock().hasMetadata("isAtm"))
			{
				player.sendMessage("You may not edit an ATM");
				event.setCancelled(true);
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent evnt)
	{
		Player player = evnt.getPlayer();
		Block block = evnt.getBlock();
		if(block.hasMetadata("isAtm"))
		{
			if(player.hasPermission("rpext.createatm") == false)
			{
				evnt.setCancelled(true);
				player.sendMessage("You do not have permission to break an ATM!");
				return;
			}
			block.removeMetadata("isAtm", plugin);
			FileConfiguration PlayerData = plugin.getPlayerData();
			File PlayerDataFile = plugin.getPlayerFile();
			ConfigurationSection atms = PlayerData.getConfigurationSection("atms");
			for(String sAtms : atms.getKeys(false))
			{
				if(block.getX() == atms.getInt(sAtms+".x") && block.getY() == atms.getInt(sAtms+".y") && block.getZ() == atms.getInt(sAtms+".z"))
				{
					player.sendMessage("ATM Removed!");
					PlayerData.set("atms."+sAtms+".x", null);
					PlayerData.set("atms."+sAtms+".y", null);
					PlayerData.set("atms."+sAtms+".z", null);
					PlayerData.set("atms."+sAtms+".active", null);
					PlayerData.set("atms."+sAtms, null);
				}
			}
			plugin.saveYamls(PlayerDataFile, PlayerData);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event)
	{
		if(event.getEntityType() != EntityType.PLAYER)
		{
			return;
		}
		FileConfiguration config = plugin.getConfig();
		if(config.getBoolean("RenameMoney"))
		{
			if(event.getItem().getItemStack().getType() == Material.getMaterial(config.getString("MoneyID")))
			{
				ItemMeta meta = event.getItem().getItemStack().getItemMeta();
				meta.setDisplayName(plugin.basicParse(config.getString("MoneyName")));
				event.getItem().getItemStack().setItemMeta(meta);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		FileConfiguration config = plugin.getConfig();
		if(config.getBoolean("RenameMoney"))
		{
			if(event.getItemDrop().getItemStack().getType() == Material.getMaterial(config.getString("MoneyID")))
			{
				ItemMeta meta = plugin.getServer().getItemFactory().getItemMeta(Material.getMaterial(config.getString("MoneyID")));
				event.getItemDrop().getItemStack().setItemMeta(meta);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInventoryClick(InventoryClickEvent event)
	{
		FileConfiguration config = plugin.getConfig();
		if(!config.getBoolean("RenameMoney"))
		{
			return;
		}
		if(event.getCurrentItem() == null)
		{
			return;
		}
		if(event.getCurrentItem().getType() == Material.getMaterial(config.getString("MoneyID")))
		{
			ItemMeta meta = event.getCurrentItem().getItemMeta();
			meta.setDisplayName(plugin.basicParse(config.getString("MoneyName")));
			event.getCursor().setItemMeta(meta);
			event.getCurrentItem().setItemMeta(meta);
		}
	}
	
	@EventHandler
	public void onPlayerCraft(PrepareItemCraftEvent event)
	{
		FileConfiguration config = plugin.getConfig();
		if(!config.getBoolean("RenameMoney"))
		{
			return;
		}
		if(event.getRecipe() == null)
		{
			return;
		}
		if(event.getRecipe().getResult().getType() == Material.getMaterial(config.getString("MoneyID")))
		{
			ItemMeta meta = event.getRecipe().getResult().getItemMeta();
			meta.setDisplayName(plugin.basicParse(config.getString("MoneyName")));
			event.getInventory().getResult().setItemMeta(meta);
		}
	}
}
