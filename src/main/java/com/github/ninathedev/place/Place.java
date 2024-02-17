package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static com.github.ninathedev.place.Globals.*;

public final class Place extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(this.getCommand("reloadconfig")).setExecutor(new ReloadConfig());
        Objects.requireNonNull(this.getCommand("breakonlymode")).setExecutor(new BreakOnlyMode());
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
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                if (player1 != player2) player1.hidePlayer(player2);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        if(Objects.requireNonNull(e.getPlayer().getInventory().getItem(8)).getType().equals(e.getItemDrop().getItemStack().getType())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent e) {
        if (e.getEntity() instanceof FallingBlock) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e)
    {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG)
        {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (getBreakOnly()) {
            e.setCancelled(true);
        }

        Material block = e.getBlockPlaced().getType();
        if (block == Material.LAVA
                || block ==  Material.WATER
                || block == Material.PISTON
                || block == Material.STICKY_PISTON
                || block == Material.FIRE
                || block == Material.SOUL_FIRE
                || block == Material.CAMPFIRE
                || block == Material.SOUL_CAMPFIRE
                || block == Material.TNT
                || block == Material.TNT_MINECART
                || block == Material.BEE_NEST
                || block == Material.BEEHIVE ){
            e.setCancelled(true);
        }
    }
}
