package com.jcamtech.rpextensions;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionType;



public final class ListenerFunction implements Listener
{
	private MainClass plugin;
	private HashMap<Player, Location> playerLoc = new HashMap<Player, Location>();
	//private Material[] bedTypes = {Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED, Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED, Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED, Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED, Material.RED_BED, Material.WHITE_BED, Material.YELLOW_BED};
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
			plugin.getLogger().info("Player: "+player.getDisplayName()+" Has joined. (UUID: " + player.getUniqueId() + ")");
		plugin.addVariable(PlayerDataFile, PlayerData, "data." + player.getUniqueId() + ".name", player.getDisplayName());
		plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".nuggets", 0);
		plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".thirst", 20);
		plugin.addVariable(PlayerDataFile, PlayerData, "data."+player.getUniqueId()+".tiredness", 20);
		
		if(plugin.getConfig().getBoolean("UseThirst"))
		{	
			if(plugin.getConfig().getBoolean("useEssentials"))
			{
				BukkitRunnable thirstloop = new ThirstLoopEss(plugin, player);
				thirstloop.runTaskTimer(plugin,  plugin.getConfig().getInt("ThirstTime"), plugin.getConfig().getInt("ThirstTime"));
			}
			else
			{
				BukkitRunnable thirstloop = new ThirstLoop(plugin, player);
				thirstloop.runTaskTimer(plugin,  plugin.getConfig().getInt("ThirstTime"), plugin.getConfig().getInt("ThirstTime"));
			}
		}
		if(plugin.getConfig().getBoolean("UseSleep"))
		{
			if(plugin.getConfig().getBoolean("useEssentials"))
			{
				BukkitRunnable tiredLoop = new SleepinessEss(plugin, player);
				tiredLoop.runTaskTimer(plugin, plugin.getConfig().getInt("SleepTime"), plugin.getConfig().getInt("SleepTime"));
			}
			else
			{
				BukkitRunnable tiredLoop = new Sleepiness(plugin, player);
				tiredLoop.runTaskTimer(plugin, plugin.getConfig().getInt("SleepTime"), plugin.getConfig().getInt("SleepTime"));
			}
			plugin.getCommand("sleep").setExecutor(new Sleep(plugin));
			BukkitRunnable effectLoop = new SleepEffectCheck(plugin);
			effectLoop.runTaskTimer(plugin, 60, 60);
		}
	}
	@EventHandler
	public void onPlayerDie(PlayerDeathEvent event) {
			
			FileConfiguration PlayerData = plugin.getPlayerData();
			File PlayerDataFile = plugin.getPlayerFile();
			Player player = event.getEntity();
			
			if(PlayerData.getInt("data."+player.getUniqueId()+".tiredness") == 0)
			{
				event.setDeathMessage(player.getDisplayName()+" has fallen asleep and been eaten by monsters");
			}
			if(PlayerData.getInt("data."+player.getUniqueId()+".thirst") == 0)
			{
				event.setDeathMessage(player.getDisplayName()+" has dehydrated and died.");
			}
			PlayerData.set("data."+player.getUniqueId()+".tiredness", 20);
			PlayerData.set("data."+player.getUniqueId()+".thirst", 20);
			plugin.saveYamls(PlayerDataFile, PlayerData);
	}
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		FileConfiguration PlayerData = plugin.getPlayerData();
		File PlayerDataFile = plugin.getPlayerFile();
		Player player = event.getPlayer();
		
		PlayerData.set("data."+player.getUniqueId()+".tiredness", 20);
		PlayerData.set("data."+player.getUniqueId()+".thirst", 20);
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
			player.sendMessage("Breaking ATM");
			FileConfiguration PlayerData = plugin.getPlayerData();
			File PlayerDataFile = plugin.getPlayerFile();
			ConfigurationSection atms = PlayerData.getConfigurationSection("atms");
			for(String sAtms : atms.getKeys(false))
			{
				if(block.getX() == atms.getInt(sAtms+".x") && block.getY() == atms.getInt(sAtms+".y") && block.getZ() == atms.getInt(sAtms+".z"))
				{
					player.sendMessage("Deleting "+sAtms);
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
		if(config.getBoolean("RenameMoney") == true)
		{
			if(event.getItem().getItemStack().getType() == Material.getMaterial(config.getString("MoneyID")))
			{
				ItemMeta meta = event.getItem().getItemStack().getItemMeta();
				meta.setDisplayName(config.getString("MoneyName"));
				event.getItem().getItemStack().setItemMeta(meta);
			}
		}
	}
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		FileConfiguration config = plugin.getConfig();
		if(config.getBoolean("RenameMoney") == true)
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
		if(event.getCurrentItem().getType() == Material.getMaterial(config.getString("MoneyID")))
		{
			ItemMeta meta = event.getCurrentItem().getItemMeta();
			meta.setDisplayName(config.getString("MoneyName"));
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
			meta.setDisplayName(config.getString("MoneyName"));
			event.getInventory().getResult().setItemMeta(meta);
			//event.getRecipe().getResult().setItemMeta(meta);
		}
	}
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event)
	{
		Player player = event.getPlayer();
		FileConfiguration PlayerData = plugin.getPlayerData();
		File PlayerDataFile = plugin.getPlayerFile();
		FileConfiguration config = plugin.getConfig();
		if(config.getBoolean("UseSleep") == true)
		{
			plugin.PlayerData.set("data."+player.getUniqueId()+".tiredness", 20);
			plugin.saveYamls(PlayerDataFile, PlayerData);
		}
	}
	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent event)
	{
		Player player = event.getPlayer();
		FileConfiguration PlayerData = plugin.getPlayerData();
		File PlayerDataFile = plugin.getPlayerFile();
		FileConfiguration config = plugin.getConfig();
		if(config.getBoolean("UseThirst") == true)
		{
			if(event.getItem().getItemMeta() instanceof PotionMeta)
			{
				final PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
				final PotionType data = meta.getBasePotionType();
				if(data == PotionType.WATER)
				{
					int count = PlayerData.getInt("data."+player.getUniqueId()+".thirst");
					if(count < 20)
					{
						count += 15;
						if(count > 20)
						{
							count = 20;
						}
						player.sendMessage("Thirst Quenched.");
						PlayerData.set("data."+player.getUniqueId()+".thirst", count);
						plugin.saveYamls(PlayerDataFile, PlayerData);
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event)
	{
		//Get standard vars
		Player player = event.getPlayer();
		FileConfiguration PlayerData = plugin.getPlayerData();
		File PlayerDataFile = plugin.getPlayerFile();
		FileConfiguration config = plugin.getConfig();
		List<Block> los = event.getPlayer().getLineOfSight((Set<Material>)null, 5);
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) //check for right clicking a block
		{
			for(Block b : los)
			{
				
				if(b.getType() == Material.getMaterial(config.getString("ChairID")) && player.isInsideVehicle() == false && plugin.config.getBoolean("UseChairs") == true)
				{
					Entity chair = b.getWorld().spawnEntity(b.getLocation().add(0.5,0.0,0.5), EntityType.ARROW);
					chair.addPassenger(player);
					plugin.isSitting.put(player, true);
					plugin.playerMap.put(player, chair);
					playerLoc.put(player, player.getLocation());
					ArrowDespawnCheck adc = new ArrowDespawnCheck(plugin, chair, player);
					adc.runTaskTimer(plugin, 10, 30);
					event.setCancelled(true);
				}
			}
		}
		if(config.getBoolean("UseThirst") == true)
		{
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				List<Block> lineOfSight = event.getPlayer().getLineOfSight((Set<Material>)null, 5);
				for(Block b : lineOfSight)
				{
					if(b.getType()==Material.WATER)
					{
						int count = PlayerData.getInt("data."+player.getUniqueId()+".thirst");
						if(count < 20)
						{
							count += 15;
							if(count > 20)
								count = 20;
							player.sendMessage("Thirst Quenched.");
							PlayerData.set("data."+player.getUniqueId()+".thirst", count);
							plugin.saveYamls(PlayerDataFile, PlayerData);
						}
					}
				}
			}
		}
	}
}