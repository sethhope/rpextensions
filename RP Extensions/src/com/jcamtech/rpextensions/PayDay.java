package com.jcamtech.rpextensions;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.UserData;

public class PayDay extends BukkitRunnable{

	MainClass plugin;
	int initialAmount;
	int gain;
	public PayDay(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public void run() {
		
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			if(plugin.getConfigFile().getBoolean("useEssentials") == true)
			{
				Essentials e = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
				UserData u = e.getUser(player);
				if(u.isAfk() == true)
				{
					return;
				}
			}
			if(plugin.config.getBoolean("UseInterest"))
			{
				double rate = (double) plugin.config.get("InterestRate");
				int money = 0;
				int total = 0;
				if(plugin.getConfig().getBoolean("useVault") == true)
				{
					money = (int) MainClass.econ.getBalance(player);
					MainClass.econ.depositPlayer(player, money*rate);
					total = (int) (money+(money*rate));
				}
				else
				{
					money = plugin.PlayerData.getInt("data."+player.getUniqueId()+".nuggets");
					total = (int) (money+(money*rate));
				}
				initialAmount = money;
				gain = (int)Math.round(money*rate);
				plugin.PlayerData.set("data."+player.getUniqueId()+".nuggets", total);
				plugin.saveYamls(plugin.PlayerDataFile, plugin.PlayerData);
				Object[] statMessage = plugin.getConfig().getList("PaydayMessage").toArray();
				for(Object line : statMessage)
				{
					String msg = parseMessage(player, line.toString());
					player.sendMessage(msg);
				}
			}
		}
	}
	
	public String parseMessage(Player p, String s)
	{
		if(p.isOnline() == false)
		{
			return "";
		}
		String retString = "";
		Boolean inBracket = false;
		String tmp = "";
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(c == '&')
			{
				c = '§';
			}
			if(inBracket == false)
			{
				if(c == '{')
				{
					inBracket = true;
				}
				else
				{
					retString = retString + c;
				}
			}
			else
			{
				if(c == '}')
				{
					inBracket = false;
					String keyString = parseKey(p, tmp);
					if(keyString == "")
					{
						retString = retString + "Invalid Keyword in Config";
					}
					else
					{
						retString = retString + keyString;
					}
					tmp = "";
				}
				else
				{
					tmp = tmp + c;
				}
			}
		}
		return retString;
	}
	public String parseKey(Player p, String s)
	{
		if(p.isOnline() == false)
		{
			return "";
		}
		String retString = "";
		switch(s.toUpperCase())
		{
		case "PLAYER_NAME":
			retString = p.getDisplayName();
			break;
		case "THIRST":
			retString = plugin.getPlayerData().getString("data."+p.getUniqueId()+".thirst");
			break;
		case "SLEEPINESS":
			retString = plugin.getPlayerData().getString("data."+p.getUniqueId()+".tiredness");
			break;
		case "MONEY":
			if(plugin.getConfig().getBoolean("useVault"))
			{
				retString = retString + (int)(MainClass.econ.getBalance(p));
			}
			else
			{
				retString = retString + plugin.PlayerData.getInt("data."+p.getUniqueId()+".nuggets");
			}
			break;
		case "UNIT":
			retString = plugin.getConfig().getString("MoneyUnit");
			break;
		case "UUID":
			retString = p.getUniqueId().toString();
			break;
		case "PREVIOUS":
			retString = retString + initialAmount;
			break;
		case "ADDED":
			retString = retString + gain;
			break;
		case "RATE":
			retString = retString + plugin.config.get("InterestRate");
			break;
		}
		return retString;
	}

}
