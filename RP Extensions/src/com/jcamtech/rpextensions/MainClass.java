package com.jcamtech.rpextensions;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class MainClass extends JavaPlugin {
	
	public boolean debugMode;
	File configFile;
	File PlayerDataFile;
	public HashMap<Player, Entity> playerMap;
	public HashMap<Block, Entity> chairMap;
	public HashMap<Player, Boolean> isSitting;
	public HashMap<Player, Boolean> isCooldown;
	FileConfiguration PlayerData;
	FileConfiguration config;
	
	public static Economy econ = null;
	public static Permission perms = null;
	public static Chat chat = null;
	
	public void onEnable()//on enable
	{	
		getServer().getPluginManager().registerEvents(new MainListener(this), this);
		getLogger().info("loading files");//BEGIN File LOADING
		configFile = new File(getDataFolder(), "config.yml");
		PlayerDataFile = new File(getDataFolder(), "PlayerData.yml");
		try
		{
			firstRun();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		config = new YamlConfiguration();
		PlayerData = new YamlConfiguration();
		loadYamls(configFile, config);
		loadYamls(PlayerDataFile, PlayerData);
		getLogger().info("successfully loaded files");//END CONFIG LOADING
		if(!PlayerDataFile.exists())
		{
			PlayerDataFile.getParentFile().mkdirs();
			copy(getResource("PlayerData.yml"), PlayerDataFile);
		}
		debugMode = config.getBoolean("DebugMode");
		getCommand("stats").setExecutor(new stats(this));
		getCommand("setstats").setExecutor(new SetStats(this));
		if(config.getBoolean("UseStatMonitor"))
		{
			getServer().getPluginManager().registerEvents(new StatMonitorListener(this), this);
			getCommand("checkstats").setExecutor(new check(this));
		}
		getCommand("statmonitor").setExecutor(new statMonitor(this));
		if(config.getBoolean("UseGoldNuggetBank") == true)
		{
			getCommand("gstore").setExecutor(new gstore(this));
			getCommand("gtake").setExecutor(new gtake(this));
			getCommand("gtransfer").setExecutor(new gtransfer(this));
			getCommand("gset").setExecutor(new gset(this));
		}
		if(config.getBoolean("UseChairs"))
		{
			playerMap = new HashMap<Player, Entity>();
			chairMap = new HashMap<Block, Entity>();
			isSitting = new HashMap<Player, Boolean>();
			isCooldown = new HashMap<Player, Boolean>();
			getServer().getPluginManager().registerEvents(new ChairListener(this), this);
		}
		
		if(config.getBoolean("UseThirst"))
		{
			getServer().getPluginManager().registerEvents(new ThirstListener(this), this);
			getCommand("quench").setExecutor(new quench(this));
		}
		if(config.getBoolean("UseSleep"))
		{
			getServer().getPluginManager().registerEvents(new SleepListener(this), this);
			getCommand("sleep").setExecutor(new Sleep(this));
		}
		for(Player player : this.getServer().getOnlinePlayers())
		{
			if(debugMode)
				getLogger().info("Player: "+player.getDisplayName() + " UUID:"+player.getUniqueId());
			addVariable(PlayerDataFile, PlayerData, "data." +player.getUniqueId()+ ".name", player.getDisplayName());
			addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".nuggets", 0);
			addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".thirst", 20);
			addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".tiredness", 20);
		}
		if(config.getBoolean("UseGoldNuggetBank"))
		{
			getServer().getPluginManager().registerEvents(new MoneyListener(this), this);
		}
		if(config.getBoolean("UseVault"))
		{
			if(config.getBoolean("UseGoldNuggetBank"))
			{
				getCommand("convertEcon").setExecutor(new convertEcon(this));
			}
			setupPermissions();
	        setupChat();
		}
		BukkitRunnable cooldown = new Cooldown(this);
		cooldown.runTaskTimer(this, 600, 600);
		getLogger().info("Finished initialization!");
		
	}
	
	public void onDisable()
	{
		for(Player player : this.getServer().getOnlinePlayers())
		{
			Entity e = playerMap.get(player);
			if(e != null)
			{
				player.teleport(e.getLocation());
				e.remove();
			}
			if(isSitting.containsKey(player))
			{
				if(debugMode)
					getLogger().info(player.getDisplayName()+" is supposed to be sitting!");
				boolean isSeated = isSitting.get(player);
				
				if(isSeated)
				{
					isSitting.remove(player);
					if(playerMap.containsKey(player))
					{
						if(debugMode)
							getLogger().info("removing arrow for " +player.getDisplayName());
						playerMap.get(player).remove();
						playerMap.remove(player);
					}
				}
			}
		}
		isSitting.clear();
		playerMap.clear();
		getLogger().info("RP Extensions Disabled");
	}
	public boolean setupEconomy()
	{
		if(getServer().getPluginManager().getPlugin("Vault") == null)
		{
			this.getLogger().severe("Failed to find vault");
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null)
		{
			this.getLogger().severe("Failed to get rsp");
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if(rsp != null)
        	chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if(rsp != null)
        	perms = rsp.getProvider();
        return perms != null;
    }
	private void firstRun() throws Exception
	{
		if(!configFile.exists())
		{
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
		}
	}
	
	private void copy(InputStream in, File file) {
		try
		{
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while((len=in.read(buf))>0)
			{
				out.write(buf,0,len);
			}
			out.close();
			in.close();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public File getPlayerFile()
	{
		return PlayerDataFile;
	}
	public FileConfiguration getConfigFile()
	{
		return config;
	}
	public FileConfiguration getPlayerData()
	{
		return PlayerData;
	}
	public void saveYamls(File file, FileConfiguration yamlFile) {
	    try {
	        yamlFile.save(file);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	}
	public void loadYamls(File file, FileConfiguration yamlFile) {
	    try {
	        yamlFile.load(file);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public void addVariable(File file, FileConfiguration yamlFile, String loc, Object object)
	{
		final Map<String, Object> defParams = new HashMap<String, Object>();
		yamlFile.options().copyDefaults(true);
		defParams.put(loc, object);
		
		for (final Entry<String, Object> e : defParams.entrySet())
			if(!yamlFile.contains(e.getKey()))
				yamlFile.set(e.getKey(), e.getValue());
		saveYamls(file, yamlFile);
	}
	
	public boolean nodeExists(FileConfiguration config, String path)
	{
		String node = config.getString(path);
		return(node!=null);
	}
	public void updateStatMonitor(Player player)
	{
		if(config.getBoolean("UseStatMonitor") == false)
		{
			return;
		}
		PlayerInventory i = player.getInventory();
		ItemStack[] inv = i.getContents();
		for(ItemStack inven : inv)
		{
			int thirst = 0;
			int sleep = 0;
			if(inven != null)
			{
				if(inven.getType() == Material.getMaterial(config.getString("StatMonitorID")))
				{
					if(inven.getItemMeta().getDisplayName().contains("Sleepiness:"))
					{
						if(nodeExists(PlayerData, "data."+player.getUniqueId()+".thirst") && nodeExists(PlayerData, "data."+player.getUniqueId()+".tiredness"))
						{
							thirst = PlayerData.getInt("data."+player.getUniqueId()+".thirst");
							sleep = PlayerData.getInt("data." + player.getUniqueId()+".tiredness");
							ItemMeta meta = inven.getItemMeta();
							meta.setDisplayName("Sleepiness: "+sleep+" | Thirst: "+thirst);
							inven.setItemMeta(meta);
						}
					}
				}
			}
		}
	}
}

