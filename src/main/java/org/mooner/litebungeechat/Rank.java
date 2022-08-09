package org.mooner.litebungeechat;

import org.bukkit.entity.Player;

import static org.mooner.litebungeechat.LiteBungeeChat.chat;

public enum Rank {
    DEFAULT("&e[ &f유저 &e] &f", null),
    CHAT("&b[ &f채팅 &b] &f", "rank.chat"),
    DEVELOP("&d[ &f개발자 &d] &f", "rank.develop"),
    ADMIN("&a[ &f관리자 &a] &f", "rank.admin");

    private final String prefix;
    private final String permission;

    Rank(String s, String permission) {
        this.prefix = chat(s);
        this.permission = permission;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPermission() {
        return permission;
    }

    public static Rank getPlayerRank(Player p) {
        if(p.isOp()) {
            if(p.getName().equals("Mooner510")) return DEVELOP;
            return ADMIN;
        }
        for (Rank value : values()) {
            if (value.permission != null && p.hasPermission(value.permission)) {
                return value;
            }
        }
        return DEFAULT;
    }
}
