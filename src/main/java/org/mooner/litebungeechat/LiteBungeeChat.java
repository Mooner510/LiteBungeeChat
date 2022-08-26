package org.mooner.litebungeechat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonerbungeeapi.api.Rank;
import org.mooner.moonerbungeeapi.api.events.BungeeMessageEvent;
import org.mooner.moonerbungeeapi.db.ChatDB;
import org.mooner.moonerbungeeapi.db.KeyWordDB;
import org.mooner.moonerbungeeapi.db.PlayerDB;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.mooner.moonerbungeeapi.api.BungeeAPI.sendBungeeMessage;

public final class LiteBungeeChat extends JavaPlugin implements Listener {
    public static LiteBungeeChat plugin;
    public static int port;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("Plugin Enabled!");
        port = this.getServer().getPort();
        Bukkit.getPluginManager().registerEvents(this, this);
        //        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Disabled!");
        // Plugin shutdown logic
    }

    private final Set<String> allow = Set.of("튜토리얼", "xbxhfldjf", "tutorial");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if(e.isCancelled()) return;
        long time = System.currentTimeMillis();
        if(!e.getPlayer().isOp() && !PlayerDB.init.isTutorial(e.getPlayer())) {
            if(!allow.contains(e.getMessage().substring(1).split(" ")[0])) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "먼저 튜토리얼을 끝내주세요!");
                return;
            }
        }
        ChatDB.init.command(e.getPlayer(), e.getMessage());
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                new URL("https://web.lite24.net/api/post/command?player=" + e.getPlayer().getName() + "&cmd=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8) + "&time=" + time).openStream().close();
            } catch (IOException ignore) {
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if(e.isCancelled()) return;
        e.setCancelled(true);
        if(!e.getPlayer().isOp() && !PlayerDB.init.isTutorial(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "먼저 튜토리얼을 끝내주세요!");
            return;
        }
        long time = System.currentTimeMillis();
        Rank rank = BungeeAPI.getPlayerRank(e.getPlayer());
        final String message = e.getMessage();
        final String s = rank.getPrefix() + e.getPlayer().getName() + ": " + message;
        sendBungeeMessage(s);
        Bukkit.getConsoleSender().sendMessage(s);
        BungeeAPI.sendMessage(e.getPlayer(), "chat", ChatColor.stripColor(s));
//        BungeeAPI.sendForward("ALL", "chat", s);
        ChatDB.init.chat(e.getPlayer(), e.getMessage());
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                new URL("https://web.lite24.net/api/post/chat?player="+e.getPlayer().getName()+"&message="+ URLEncoder.encode(message, StandardCharsets.UTF_8)+"&time="+time).openStream().close();
            } catch (IOException ignore) {
            }
        });
    }

    @EventHandler
    public void onReceive(BungeeMessageEvent e) {
        if(!e.getChannel().equals("chat")) return;
        Bukkit.getOnlinePlayers().forEach(p -> {
            if(!e.getUUID().equals(p.getUniqueId()) && KeyWordDB.init.check(p, e.getMessage()))
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        });
    }
}
