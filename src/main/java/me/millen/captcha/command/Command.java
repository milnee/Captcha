package me.millen.captcha.command;
/*
 *  created by turben on 29/05/2020
 */

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.millen.captcha.plugin.Captcha;

public class Command implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args){
		if(sender.hasPermission("captcha.admin") || sender.isOp()){
			if(args.length != 1){
				sender.sendMessage(ChatColor.GRAY +"Usage: /captcha (toggle/reload)");
				return false;
			}else{
				if(args[0].equalsIgnoreCase("toggle")){
					Captcha.get().setActive(!Captcha.get().isActive());
					sender.sendMessage(Captcha.get().getCache().CAPTCHA_TOGGLE().replace("{state}", Captcha.get().isActive() ? ChatColor.GREEN +"enabled" : ChatColor.RED +"disabled"));
				}else if(args[0].equalsIgnoreCase("reload")){
					sender.sendMessage(Captcha.get().getCache().CONFIG_RELOADED());
					Captcha.get().getCache().reload();
				}
			}
		}else{
			sender.sendMessage(Captcha.get().getCache().PERMISSION_DENIED());
		}
		return false;
	}
}
