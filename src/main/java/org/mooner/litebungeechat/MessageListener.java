package org.mooner.litebungeechat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class MessageListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        LiteBungeeChat.plugin.getLogger().info(channel + ", " + player.getName() + ", " + new String(message));
    }
}
