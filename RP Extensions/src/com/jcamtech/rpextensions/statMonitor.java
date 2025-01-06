package com.jcamtech.rpextensions;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class statMonitor implements CommandExecutor{

	FileConfiguration config;
	FileConfiguration PlayerData;
	
	private MainClass plugin;
	
	public statMonitor(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(plugin.getConfigFile().getBoolean("UseStatMonitor") == false)
		{
			sender.sendMessage("UseStatMonitor has been disabled in the config");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("statmonitor"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("This command can only be run in game");
			} else {
				Player player = (Player) sender;
				PlayerData = plugin.getPlayerData();
				if(plugin.isCooldown.get(player) != null)
				{
					if(plugin.isCooldown.get(player))
					{
						player.sendMessage("Please wait a little longer before repeating this command.");
						return true;
					}
				}
				ItemStack item = new ItemStack(Material.getMaterial(plugin.getConfigFile().getString("StatMonitorID")), 1);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("Sleepiness: ## | Thirst: ##");
				item.setItemMeta(meta);
				player.getInventory().addItem(item);
				player.sendMessage("Stat Monitor Spawned!");
				plugin.isCooldown.put(player, true);
			}
			
			return true;
		}
		return false;
	}
	
}
