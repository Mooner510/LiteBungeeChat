package org.mooner.litebungeechat.db;

import org.bukkit.entity.Player;
import org.mooner.litebungeechat.LiteBungeeChat;
import org.mooner.moonerbungeeapi.api.BungeeAPI;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class ChatDB {
    public static ChatDB init;
    private final HashMap<UUID, Long> key;
    private static final String dbPath = "../db/";
    private static final String CONNECTION = "jdbc:sqlite:" + dbPath + "chat.db";

    public ChatDB() {
        key = new HashMap<>();

        new File(dbPath).mkdirs();
        File db = new File(dbPath, "chat.db");
        if(!db.exists()) {
            try {
                db.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Player (" +
                                "id INTEGER NOT NULL UNIQUE," +
                                "uuid TEXT NOT NULL UNIQUE," +
                                "PRIMARY KEY(id AUTOINCREMENT))")
        ) {
            s.execute();
            LiteBungeeChat.plugin.getLogger().info("성공적으로 PlayerDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Chat (" +
                                "id INTEGER NOT NULL UNIQUE," +
                                "server TEXT NOT NULL," +
                                "player TEXT NOT NULL," +
                                "message TEXT NOT NULL," +
                                "timestamp INTEGER NOT NULL," +
                                "PRIMARY KEY(id))")
        ) {
            s.execute();
            LiteBungeeChat.plugin.getLogger().info("성공적으로 ChatDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getKey(UUID uuid) {
        Long i = key.get(uuid);
        if(i != null) return i;
        return getKeyFromDB(uuid);
    }

    private long getKeyFromDB(UUID uuid) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "SELECT id FROM Player WHERE uuid=?")
        ) {
            s.setString(1, uuid.toString());
            try (
                    ResultSet r = s.executeQuery()
            ) {
                if(r.next()) {
                    long i = r.getLong(1);
                    key.put(uuid, i);
                    return i;
                } else {
                    try (
                            Connection c2 = DriverManager.getConnection(CONNECTION);
                            PreparedStatement s2 = c2.prepareStatement("INSERT INTO Player (uuid) VALUES(?)")
                    ) {
                        s2.setString(1, uuid.toString());
                        s2.executeUpdate();
                        return getKeyFromDB(uuid);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(new NullPointerException("Can't create key from DB."));
    }

    public void chat(Player p, String message) {
        final long id = getKey(p.getUniqueId());
        final long time = System.currentTimeMillis();
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("INSERT INTO Chat VALUES(?, ?, ?, ?, ?)")
        ) {
            s.setLong(1, id);
            s.setString(2, BungeeAPI.getServerType(LiteBungeeChat.port).getTag());
            s.setString(3, p.getUniqueId().toString());
            s.setString(4, message);
            s.setLong(5, time);
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
