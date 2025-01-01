package com.jcamtech.rpextensions;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
	public HashMap<Player, Boolean> isSitting;
	FileConfiguration PlayerData;
	FileConfiguration config;
	
	public static Economy econ = null;
	public static Permission perms = null;
	public static Chat chat = null;
	
	public void onEnable()//on enable
	{	
		getServer().getPluginManager().registerEvents(new ListenerFunction(this), this);
		getCommand("quench").setExecutor(new quench(this));
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
		getCommand("checkstats").setExecutor(new check(this));
		if(config.getBoolean("UseGoldNuggetBank") == true)
		{
			if(config.getBoolean("RenameMoney") == true)
			{
				ItemNameCheck itemCheck = new ItemNameCheck(this);
				itemCheck.runTaskTimerAsynchronously(this, 10, 20);
			}
			
			getCommand("gstore").setExecutor(new gstore(this));
			getCommand("gtake").setExecutor(new gtake(this));
			getCommand("gtransfer").setExecutor(new gtransfer(this));
			//getCommand("ggive").setExecutor(new givemoney(this));
			getCommand("gset").setExecutor(new gset(this));
		}
		playerMap = new HashMap<Player, Entity>();
		isSitting = new HashMap<Player, Boolean>();
		for(Player player : this.getServer().getOnlinePlayers())
		{
			if(debugMode)
				getLogger().info("Player: "+player.getDisplayName() + " UUID:"+player.getUniqueId());
			addVariable(PlayerDataFile, PlayerData, "data." +player.getUniqueId()+ ".name", player.getDisplayName());
			addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".nuggets", 0);
			addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".thirst", 20);
			addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".tiredness", 20);
			if(config.getBoolean("UseThirst"))
			{	
				if(config.getBoolean("useEssentials"))
				{
					BukkitRunnable thirstloop = new ThirstLoopEss(this, player);
					thirstloop.runTaskTimer(this,  config.getInt("ThirstTime"), config.getInt("ThirstTime"));
				}
				else
				{
					BukkitRunnable thirstloop = new ThirstLoop(this, player);
					thirstloop.runTaskTimer(this,  config.getInt("ThirstTime"), config.getInt("ThirstTime"));
				}
			}
			if(config.getBoolean("UseSleep"))
			{
				if(config.getBoolean("useEssentials"))
				{
					BukkitRunnable tiredLoop = new SleepinessEss(this, player);
					tiredLoop.runTaskTimer(this, config.getInt("SleepTime"), config.getInt("SleepTime"));
				}
				else
				{
					BukkitRunnable tiredLoop = new Sleepiness(this, player);
					tiredLoop.runTaskTimer(this, config.getInt("SleepTime"), config.getInt("SleepTime"));
				}
				getCommand("sleep").setExecutor(new Sleep(this));
				BukkitRunnable effectLoop = new SleepEffectCheck(this);
				effectLoop.runTaskTimer(this, 60, 60);
			}
		}
		if(config.getBoolean("UseGoldNuggetBank"))
		{
			if(config.getBoolean("UseInterest"))
			{
				BukkitRunnable payday = new PayDay(this);
				payday.runTaskTimer(this, config.getInt("InterestTime"), config.getInt("InterestTime"));
			}
			if(config.getBoolean("useVault"))
			{
				if(!setupEconomy())
				{
					this.getLogger().severe("Failed to load! Could not initialize vault! (try disabling useVault in config.yml");
					this.getServer().getPluginManager().disablePlugin(this);
					return;
				}
				setupPermissions();
		        setupChat();
		        BukkitRunnable roundMoney = new MoneyRoundCheck(this);
		        roundMoney.runTaskTimer(this, 10, 20);
		        getCommand("convertEcon").setExecutor(new convertEcon(this));
			}
		}
		BukkitRunnable check = new ChairCheck(this);
		check.runTaskTimer(this, 20, 50);
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
		getLogger().info("Contega Stats Disabled");
	}
	private boolean setupEconomy()
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
}

