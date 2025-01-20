package com.jcamtech.rpextensions;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemNameCheck extends BukkitRunnable{

	MainClass plugin;
	public ItemNameCheck(MainClass plugin)
	{
		this.plugin = plugin;
	}
	//@SuppressWarnings("deprecation")
	@Override
	public void run() {
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			plugin.updateStatMonitor(player);
			Inventory i = player.getInventory();
			ItemStack[] inv = i.getContents();
			for(ItemStack inven : inv)
			{
				if(inven != null)
				{
					if(inven.getType() == Material.getMaterial(plugin.getConfig().getString("MoneyID")))
					{
						ItemMeta meta = inven.getItemMeta();
						meta.setDisplayName(plugin.basicParse(plugin.getConfig().getString("MoneyName")));
						inven.setItemMeta(meta);
					}
				}
			}
		}
		
	}

}
