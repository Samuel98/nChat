package com.niccholaspage.nChat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.niccholaspage.nChat.permissions.*;
import com.niccholaspage.nChat.api.ChatFormatEvent;
import com.niccholaspage.nChat.api.Node;
import com.niccholaspage.nChat.api.PermissionsHandlerEnabledEvent;
import com.niccholaspage.nChat.api.PlayerChatFormatEvent;
import com.niccholaspage.nChat.commands.*;

public class nChat extends JavaPlugin {
	//Permissions Handler
	private PermissionsHandler permissionsHandler;
	//Config Handler
	private ConfigHandler configHandler;
	
	public void onEnable(){
		new nChatPlayerListener(this);
		
		new nChatServerListener(this);
		
		setupPermissions();
		
		getDataFolder().mkdirs();
		
		loadConfig();
		
		//Register commands
		getCommand("nchat").setExecutor(new nChatCommand(this));
	}
	
	private void setupPermissions(){
		PermissionsHandlerEnabledEvent event = new PermissionsHandlerEnabledEvent();
		
		getServer().getPluginManager().callEvent(event);
		
		if (event.getPermissionsHandler() != null){
			permissionsHandler = event.getPermissionsHandler();
			
			return;
		}
		
		Plugin PEX = getServer().getPluginManager().getPlugin("PermissionsEx");
		
		Plugin bPermissions = getServer().getPluginManager().getPlugin("bPermissions");
		
		Plugin groupManager = getServer().getPluginManager().getPlugin("EssentialsGroupManager");
		
		if(PEX != null){
			permissionsHandler = new PermissionsExHandler();
		}else if (bPermissions != null){
			permissionsHandler = new bPermissionsHandler();
		}else if (groupManager != null){
			permissionsHandler = new GroupManagerHandler(groupManager);
		}else {
			permissionsHandler = new DinnerPermissionsHandler(this);
		}
	}
	
	public void loadConfig(){
		File configFile = new File(getDataFolder(), "config.yml");

		try {
			configFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File phrasesFile = new File(getDataFolder(), "phrases.yml");
		
		configHandler = new ConfigHandler(configFile, phrasesFile);
		
		permissionsHandler.reload();
	}
	
	public String formatMessage(Player player, String out, String message){
		if (out == null || out == ""){
			return null;
		}
		
		PlayerPermissionsHandler handler = null;

		String name = "";

		String displayName = "";

		String world = "";

		String group = "";

		String prefix = "";

		String suffix = "";

		if (player != null){
			handler = permissionsHandler.getPlayerPermissionsHandler(player);
			
			name = player.getName();

			displayName = player.getDisplayName();

			world = player.getWorld().getName();
			
			group = handler.getGroup();

			prefix = handler.getPrefix();

			suffix = handler.getSuffix();
		}

		Date now = new Date();

		SimpleDateFormat dateFormat = new SimpleDateFormat(configHandler.getTimestampFormat());

		String time = dateFormat.format(now);

		String[] old = new String[]{"+name", "+rname", "+group", "+prefix", "+suffix", "+world", "+timestamp", "&", "+message"};

		String[] replacements = new String[]{displayName, name, group, prefix, suffix, world, time, "\u00A7", message};

		out = replaceSplit(out, old, replacements);

		//API time
		ChatFormatEvent event;

		if (player != null){
			event = new PlayerChatFormatEvent(player);
		}else {
			event = new ChatFormatEvent();
		}

		getServer().getPluginManager().callEvent(event);

		for (Node node : event.getNodes()){
			if (node.getValue() == null){
				continue;
			}

			out = out.replace("+" + node.getName(), node.getValue());
		}

		if (player != null){
			if ((permissionsHandler.hasPermission(player, "nChat.colors")) || (permissionsHandler.hasPermission(player, "nChat.colours"))) {
				out = out.replace(configHandler.getColorCharacter(), "\u00A7");
			}
		}

		out = out.replaceAll("%", "%%");

		return out;
	}
	
	public ConfigHandler getConfigHandler(){
		return configHandler;
	}
	
	public PermissionsHandler getPermissionsHandler(){
		return permissionsHandler;
	}

	public String replaceSplit(String str, String[] search, String[] replace){
		if (search.length != replace.length){
			return "";
		}

		for (int i = 0; i < search.length; i++){
			String[] split = search[i].split(",");

			for (int j = 0; j < split.length; j++){
				if (replace[i] == null) continue;

				str = str.replace(split[j], replace[i]);
			}
		}

		return str;
	}
}