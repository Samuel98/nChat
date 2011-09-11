package com.niccholaspage.nChat;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class nChatPlayerListener extends PlayerListener {
	public nChat plugin;

	public nChatPlayerListener(nChat plugin) {
		this.plugin = plugin;
	}
	public void onPlayerJoin(PlayerJoinEvent event){
		if (plugin.Permissions == null) return;
		
		event.setJoinMessage(plugin.formatMessage(event.getPlayer(), plugin.joinMessage, null));
	}
	public void onPlayerQuit(PlayerQuitEvent event){
		if (plugin.Permissions == null) return;
		
		event.setQuitMessage(plugin.formatMessage(event.getPlayer(), plugin.leaveMessage, null));
	}
	public void onPlayerKick(PlayerKickEvent event){
		if (plugin.Permissions == null) return;
		
		event.setLeaveMessage(plugin.formatMessage(event.getPlayer(), plugin.leaveMessage, null));
	}
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.getMessage().startsWith("/") || plugin.Permissions == null) return;
		
		event.setFormat(plugin.formatMessage(event.getPlayer(), plugin.messageFormat, event.getMessage()));
	}
}
