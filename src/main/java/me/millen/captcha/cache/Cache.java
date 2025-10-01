package me.millen.captcha.cache;
/*
 *  created by turben on 29/05/2020
 */

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import me.millen.captcha.plugin.Captcha;

public class Cache{

	private LoadingCache<String, String> keys = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES)
			.build(new CacheLoader<String, String>(){
				public String load(String key){
					return Captcha.get().getConfig().getString(key);
				}
			});

	public void setup(){
		for(String key : Captcha.get().getConfig().getKeys(false))
			if(Captcha.get().getConfig().get(key) instanceof String)
				keys.put(key, Captcha.get().getConfig().getString(key));
	}

	public void reload(){
		Captcha.get().reloadConfig();
		keys.cleanUp();
		setup();
	}

	public Integer getFails(){ return Captcha.get().getConfig().getInt("failed-attempts"); }
	public Integer getTimeout(){ return Captcha.get().getConfig().getInt("timeout"); }
	public boolean isOpBypass(){ return Captcha.get().getConfig().getBoolean("op-bypass"); }

	public String PERMISSION_DENIED(){
		return getString("permission-denied");
	}

	public String TIMEOUT_KICK(){ return getString("timeout-kick"); }
	public String FAILED_ATTEMPTS_KICK(){ return getString("failed-attempts-kick"); }
	public String CAPTCHA_TOGGLE(){ return getString("captcha-toggle"); }
	public String CONFIG_RELOADED(){ return getString("config-reloaded"); }

	public String getString(String key){
		try{
			return ChatColor.translateAlternateColorCodes('&', keys.get(key));
		}catch(ExecutionException e){
			e.printStackTrace();
			return null;
		}
	}
}