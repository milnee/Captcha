package me.millen.captcha.plugin;
/*
 *  created by turben on 29/05/2020
 */

import me.millen.captcha.cache.Cache;
import me.millen.captcha.command.Command;
import me.millen.captcha.handler.Handler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Captcha extends JavaPlugin{

	private boolean active;
	private Cache cache;

	public static Captcha get(){
		return getPlugin(Captcha.class);
	}

	public void onEnable(){
		verifyConfiguration();
		cache = new Cache();
		cache.setup();

		getCommand("captcha").setExecutor(new Command());
		getServer().getPluginManager().registerEvents(new Handler(), this);

		this.active = getConfig().getBoolean("active");
	}

	public void onDisable(){
		getConfig().set("active", active);
	}

	public Cache getCache(){
		return cache;
	}

	public boolean isActive(){
		return active;
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public void verifyConfiguration(){
		File file = new File(getDataFolder(), "config.yml");
		if(!file.exists()){
			saveDefaultConfig();
			reloadConfig();
			return;
		}

		reloadConfig();
	}
}
