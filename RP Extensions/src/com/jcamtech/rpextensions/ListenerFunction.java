package com.jcamtech.rpextensions;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;


public final class ListenerFunction implements Listener
{
	private MainClass plugin;
	private HashMap<Player, Location> playerLoc = new HashMap<Player, Location>();
	public ListenerFunction(MainClass plugin)
	{
		this.plugin = plugin;
	}
	@EventHandler
	public void onLogin(final PlayerLoginEvent evt)
	{
		final FileConfiguration PlayerData = plugin.getPlayerData();
		final File PlayerDataFile = plugin.getPlayerFile();
		Player player = evt.getPlayer();
		if(plugin.debugMode)
			plugin.getLogger().info("Player: "+player.getName()+" Has joined");
		plugin.addVariable(PlayerDataFile, PlayerData, "data." + player.getName() + ".name", player.getDisplayName());
		plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getName()+".nuggets", 0);
		plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getName()+".thirst", 20);
		plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getName()+".tiredness", 20);
		
		if(plugin.getConfig().getBoolean("UseThirst")==true)
		{
				if(plugin.debugMode)
					plugin.getLogger().info("Enabling thirst for: "+player.getName());
				BukkitRunnable thirstloop = new ThirstLoop(plugin, player);
				thirstloop.runTaskTimer(plugin,  1200, 1200);
				
		}
	}
	@EventHandler
	public void onPlayerDie(PlayerDeathEvent event) {
			
			FileConfiguration PlayerData = plugin.getPlayerData();
			File PlayerDataFile = plugin.getPlayerFile();
			Player player = event.getEntity();
			
			if(PlayerData.getInt("data."+player.getName()+".tiredness") == 0)
			{
				event.setDeathMessage(player.getName()+" has fallen asleep and been eaten by monsters");
			}
			if(PlayerData.getInt("data."+player.getName()+".thirst") == 0)
			{
				event.setDeathMessage(player.getName()+" has dehydrated and died.");
			}
			PlayerData.set("data."+player.getName()+".tiredness", 20);
			PlayerData.set("data."+player.getName()+".thirst", 20);
			plugin.saveYamls(PlayerDataFile, PlayerData);
	}
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event){
        if(event.getLeaveMessage().equalsIgnoreCase("Nope!)")||event.getReason().equalsIgnoreCase("Nope!")){
            event.setReason("Chair bug! Use Right Click next time!");
            event.setCancelled(true);
            event.getPlayer().teleport(playerLoc.get(event.getPlayer()));
            }
        else
        {
        Player player = event.getPlayer();
        if(plugin.playerMap.containsKey(player))
        {
        	plugin.playerMap.get(player).remove();
        	plugin.playerMap.remove(player);
        }
        if(plugin.isSitting.containsKey(player))
        	plugin.isSitting.remove(player);
        }
    }
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
        if(plugin.playerMap.containsKey(player))
        {
        	plugin.playerMap.get(player).remove();
        	plugin.playerMap.remove(player);
        }
        if(plugin.isSitting.containsKey(player))
        	plugin.isSitting.remove(player);
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onSignCreate(SignChangeEvent sign)
	{
		if(sign.getPlayer().hasPermission("rpext.createatm"))
		{
			if (sign.getLine(0).equals("[rpAtm]")) 
			{
		          sign.setLine(0, "§5{ATM}");
		          sign.setLine(1, "Use /gstore");
		          sign.setLine(2, "or /gtake");
		          sign.setLine(3, "to use ATM");
		          sign.getBlock().setMetadata("isAtm", new FixedMetadataValue(plugin, "true"));
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		FileConfiguration PlayerData = plugin.getPlayerData();
		File PlayerDataFile = plugin.getPlayerFile();
		FileConfiguration config = plugin.getConfig();
		List<Block> los = event.getPlayer().getLineOfSight(null, 5);
		for(Block b : los)
		{
			if(b.getType().getId() == config.getInt("ChairID") && player.isInsideVehicle() == false && plugin.config.getBoolean("UseChairs"))
			{
				Entity chair = b.getWorld().spawnEntity(b.getLocation().add(0.5,0.5,0.5), EntityType.ARROW);
				chair.setPassenger(player);
				plugin.isSitting.put(player, true);
				plugin.playerMap.put(player, chair);
				playerLoc.put(player, player.getLocation());
				ArrowDespawnCheck adc = new ArrowDespawnCheck(plugin, chair);
				adc.runTaskTimer(plugin, 10, 80);
			}
			if(b.getType()==Material.BED || b.getType() == Material.BED_BLOCK && player.getWorld().getTime() > 12541 && player.getWorld().getTime() < 23458)
			{
				plugin.PlayerData.set("data."+player.getName()+".tiredness", 20);
				plugin.saveYamls(PlayerDataFile, PlayerData);
			}
			
		}
		if(plugin.getConfig().getBoolean("UseThirst") == true)
		{
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				List<Block> lineOfSight = event.getPlayer().getLineOfSight(null, 5);
				
				for(Block b : lineOfSight)
				{
					if(b.getType()==Material.STATIONARY_WATER || b.getType()==Material.WATER)
					{
						int count = PlayerData.getInt("data."+player.getName()+".thirst");
						count += 15;
						if(count > 20)
							count = 20;
						player.sendMessage("Quenched thirst!");
						PlayerData.set("data."+player.getName()+".thirst", count);
						plugin.saveYamls(PlayerDataFile, PlayerData);
					}
				}
				if(player.getItemInHand().getType() == Material.POTION)
				{
					if(player.getItemInHand().getDurability() == 0)
					{
						int count = PlayerData.getInt("data."+player.getName()+".thirst");
						count += 15;
						if(count > 20)
							count = 20;
						player.sendMessage("Quenched thirst!");
						PlayerData.set("data."+player.getName()+".thirst", count);
						player.getItemInHand().setType(Material.GLASS_BOTTLE);
						plugin.saveYamls(PlayerDataFile, PlayerData);
					}
				}
			}
		}
	}
}