package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Place extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("[WELCOME] Welcome to place!");
        getLogger().info("[WELCOME] This plugin expects that all players are in creative mode and");
        getLogger().info("[WELCOME] that the rest of the entire server is not accessible by people.");
        getLogger().info("[WELCOME] Please make sure that nothing is letting the players roam out of the 3D canvas!");


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                if (player1 != player2) player1.hidePlayer(player2);
            }
        }
    }
}
