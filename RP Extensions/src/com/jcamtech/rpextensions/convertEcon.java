package com.jcamtech.rpextensions;

import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class convertEcon implements CommandExecutor 
{
	FileConfiguration config;

	FileConfiguration PlayerData;

	private MainClass plugin;

	public convertEcon(MainClass plugin)
	{
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (cmd.getName().equalsIgnoreCase("convertEcon")) 
		{
			ConfigurationSection players = plugin.getPlayerData().getConfigurationSection("data");
			for (String sPlayer : players.getKeys(false)) 
			{
				if (plugin.getPlayerData().getString("data." + sPlayer + ".name") != null) 
				{
					OfflinePlayer offlinePlayer = null;
					Integer nugs = Integer.valueOf(this.plugin.getPlayerData().getInt("data." + sPlayer + ".nuggets"));
					plugin.getLogger().info(String.valueOf(sPlayer) + " has " + nugs.toString() + " nuggets");
					UUID pID = UUID.fromString(sPlayer);
					Player player = plugin.getServer().getPlayer(pID);
					
					if (player == null)
						offlinePlayer = this.plugin.getServer().getOfflinePlayer(pID);
					else
						offlinePlayer = player;
					double amount = MainClass.econ.getBalance(offlinePlayer);
					MainClass.econ.withdrawPlayer(offlinePlayer, amount);
					MainClass.econ.depositPlayer(offlinePlayer, nugs.intValue());
				}
			}
			return true;
		}
		return false;
	}
}