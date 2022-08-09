package org.mooner.litebungeechat;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public final class LiteBungeeChat extends JavaPlugin implements Listener {
    public static LiteBungeeChat plugin;

    public static String chat(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void sendBungeeMessage(String message) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF("Message");
            out.writeUTF("ALL");
            out.writeUTF(message);

            if (!this.getServer().getOnlinePlayers().isEmpty()) {
                this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
                    Player p = this.getServer().getOnlinePlayers().iterator().next();
                    p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
                });
            }
        } catch (Exception e) {
            this.getLogger().warning("Failed to send BungeeCord message");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Rank rank = Rank.getPlayerRank(e.getPlayer());
        String s = rank.getPrefix() + e.getPlayer().getName() + ": " + e.getMessage();
        sendBungeeMessage(s);
    }

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("Plugin Enabled!");
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
//        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Disabled!");
        // Plugin shutdown logic
    }
}
