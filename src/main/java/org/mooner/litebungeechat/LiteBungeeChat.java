package org.mooner.litebungeechat;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.litebungeechat.db.ChatDB;
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonerbungeeapi.api.Rank;

import static org.mooner.moonerbungeeapi.api.BungeeAPI.sendBungeeMessage;
import static org.mooner.moonerbungeeapi.api.Rank.chat;

public final class LiteBungeeChat extends JavaPlugin implements Listener {
    public static LiteBungeeChat plugin;
    public static int port;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("Plugin Enabled!");
        port = this.getServer().getPort();
        ChatDB.init = new ChatDB();
        Bukkit.getPluginManager().registerEvents(this, this);
        //        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Disabled!");
        // Plugin shutdown logic
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Rank rank = BungeeAPI.getPlayerRank(e.getPlayer());
        sendBungeeMessage(rank.getPrefix() + e.getPlayer().getName() + chat(" &e> ") + e.getMessage());
        ChatDB.init.chat(e.getPlayer(), e.getMessage());
    }
}
