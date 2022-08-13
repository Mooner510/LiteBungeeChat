package org.mooner.litebungeechat;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.litebungeechat.db.ChatDB;
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonerbungeeapi.api.Rank;

import static org.mooner.moonerbungeeapi.api.BungeeAPI.sendBungeeMessage;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if(e.isCancelled()) return;
        ChatDB.init.command(e.getPlayer(), e.getMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if(e.isCancelled()) return;
        e.setCancelled(true);
        Rank rank = BungeeAPI.getPlayerRank(e.getPlayer());
        final String s = rank.getPrefix() + e.getPlayer().getName() + ": " + e.getMessage();
        sendBungeeMessage(s);
        Bukkit.getConsoleSender().sendMessage(s);
//        BungeeAPI.sendForward("chat", s);
//        BungeeAPI.sendForward("ALL", "chat", s);
        ChatDB.init.chat(e.getPlayer(), e.getMessage());
    }

//    @EventHandler
//    public void onReceive(BungeeMessageEvent e) {
//        this.getLogger().info(e.getChannel() + ": [" + e.getPlayer().getName() + "] " + e.getMessage());
//        if(e.getChannel().equals("chat")) {
//            if (KeyWordDB.init.check(e.getPlayer(), e.getMessage())) {
//                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
//            }
//        }
//    }
}
