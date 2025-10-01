package me.millen.captcha.handler;
/*
 *  created by turben on 29/05/2020
 */

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;

import me.millen.captcha.plugin.Captcha;

public class Handler implements Listener{

	public Handler(){
		new BukkitRunnable(){
			@Override
			public void run(){
				for(UUID uuid : requests.keySet()){
					if(get(uuid).getStopwatch().reached(Captcha.get().getCache().getTimeout()))
						if(Bukkit.getPlayer(uuid) != null)
							Bukkit.getPlayer(uuid).kickPlayer(Captcha.get().getCache().TIMEOUT_KICK());
				}
			}
		}.runTaskTimer(Captcha.get(), 0, 10);
	}

	private Map<UUID, Request> requests = Maps.newConcurrentMap();

	public void create(UUID uuid){
		Inventory inv = Bukkit.createInventory(null, 9, "Please click the green wool");

		for(int i = 0; i < 9; i++){
			inv.setItem(i, new ItemStack(Material.WOOL));
		}

		int slot = new Random().nextInt(9);
		inv.setItem(slot, new ItemStack(Material.WOOL, 1, (short) 5));

		requests.put(uuid, new Request(slot, inv));
	}

	public Request get(UUID uuid){
		return requests.get(uuid);
	}

	@EventHandler
	public void join(PlayerJoinEvent event){
		if(Captcha.get().isActive() && !(event.getPlayer().isOp() && Captcha.get().getCache().isOpBypass())){
			Bukkit.getScheduler().runTaskLater(Captcha.get(), () -> {
				create(event.getPlayer().getUniqueId());
				event.getPlayer().openInventory(get(event.getPlayer().getUniqueId()).getInventory());
			}, 1);
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent event){
		if(get(event.getPlayer().getUniqueId()) != null)
			requests.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void click(InventoryClickEvent event){
		if(event.getClickedInventory() == null || get(event.getWhoClicked().getUniqueId()) == null)
			return;

		if(event.getClickedInventory().equals(get(event.getWhoClicked().getUniqueId()).getInventory())){
			event.setCancelled(true);

			Request request = get(event.getWhoClicked().getUniqueId());

			if(event.getSlot() == request.getSlot()){
				if(event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.WOOL) && event.getCurrentItem().getDurability() == 5){
					requests.remove(event.getWhoClicked().getUniqueId());
					event.getWhoClicked().closeInventory();
				}
			}else{
				if(request.getClicks() < Captcha.get().getCache().getFails())
					request.setClicks(request.getClicks() + 1);
				else
					((Player) event.getWhoClicked()).kickPlayer(Captcha.get().getCache().FAILED_ATTEMPTS_KICK());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void interact(PlayerInteractEvent event){
		if(get(event.getPlayer().getUniqueId()) != null)
			event.setCancelled(true);
	}

	@EventHandler
	public void close(InventoryCloseEvent event){
		if(event.getView().getTitle().equalsIgnoreCase("Please click the green wool") && get(event.getPlayer().getUniqueId()) != null)
			Bukkit.getScheduler().runTaskLater(Captcha.get(), () -> {
				if(!(event.getPlayer() == null) && !(get(event.getPlayer().getUniqueId()) == null))
					event.getPlayer().openInventory(get(event.getPlayer().getUniqueId()).getInventory());
			}, 0);
	}

	@EventHandler
	public void move(PlayerMoveEvent event){
		if(get(event.getPlayer().getUniqueId()) != null
				&& (event.getPlayer().getLocation().getX() != event.getFrom().getX()
		|| event.getPlayer().getLocation().getX() != event.getFrom().getX()
		|| event.getPlayer().getLocation().getX() != event.getFrom().getX()))
			event.setCancelled(true);
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent event){
		if(get(event.getPlayer().getUniqueId()) != null)
			event.setCancelled(true);
	}

	@EventHandler
	public void command(PlayerCommandPreprocessEvent event){
		if(get(event.getPlayer().getUniqueId()) != null)
			event.setCancelled(true);
	}

	public class Request{

		private final Stopwatch stopwatch;
		private final int slot;
		private int clicks;
		private final Inventory inventory;

		public Request(int slot, Inventory inventory){
			this.slot = slot;
			this.inventory = inventory;
			this.stopwatch = new Stopwatch();
		}

		public int getSlot(){ return slot; }
		public int getClicks(){ return clicks; }
		public Inventory getInventory(){ return inventory; }
		public Stopwatch getStopwatch(){ return stopwatch; }

		public void setClicks(int clicks){
			this.clicks = clicks;
		}
	}
}
