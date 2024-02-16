package com.github.ninathedev.place;

import org.bukkit.plugin.java.JavaPlugin;

public final class Place extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getLogger().info("[WELCOME] Welcome to place!");
        getLogger().info("[WELCOME] This plugin expects that all players are in creative mode and");
        getLogger().info("[WELCOME] that the rest of the entire server is not accessible by people.");
        getLogger().info("[WELCOME] Please make sure that nothing is letting the players roam out of the 3D canvas!");


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
