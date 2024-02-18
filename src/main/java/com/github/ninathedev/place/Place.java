package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.github.ninathedev.place.Globals.*;

public final class Place extends JavaPlugin implements Listener {
    private Map<UUID, BukkitRunnable> playerBreakTimers = new HashMap<>();
    private Map<UUID, BukkitRunnable> playerPlaceTimers = new HashMap<>();

    public void addPlayerBreakTimer(Player player, long delayInSeconds) {

        // Create a new timer
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                playerBreakTimers.remove(player.getUniqueId());
            }
        };

        // Start the timer
        runnable.runTaskLater(this, delayInSeconds * 20); // 20 ticks = 1 second

        // Store the timer
        playerBreakTimers.put(player.getUniqueId(), runnable);
    }
    public void addPlayerPlaceTimer(Player player, long delayInSeconds) {

        // Create a new timer
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                playerPlaceTimers.remove(player.getUniqueId());
            }
        };

        // Start the timer
        runnable.runTaskLater(this, delayInSeconds * 20); // 20 ticks = 1 second

        // Store the timer
        playerPlaceTimers.put(player.getUniqueId(), runnable);
    }
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(this.getCommand("reloadconfig")).setExecutor(new ReloadConfig());
        Objects.requireNonNull(this.getCommand("breakonlymode")).setExecutor(new BreakOnlyMode());
        Objects.requireNonNull(this.getCommand("resumemode")).setExecutor(new BreakOnlyMode());
        Objects.requireNonNull(this.getCommand("pausemode")).setExecutor(new BreakOnlyMode());
        getLogger().info("[WELCOME] Welcome to placemc!");
        getLogger().info("[WELCOME] This plugin expects that all players are in creative mode and");
        getLogger().info("[WELCOME] that the rest of the entire server is not accessible by people.");
        getLogger().info("[WELCOME] Please make sure that nothing is letting the players roam out of the 3D canvas!");
    }

    @Override
    public void onDisable() {
        setPlaceMode(true);
        setBreakOnly(true);
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
            e.getPlayer().sendMessage("To prevent lag from dropped items, we have disabled dropped items.");
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
    public void onCreatureSpawn(@NotNull CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            e.setCancelled(true);
        }
    }

    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.ENDER_CRYSTAL) {
            e.setCancelled(true);
        }
    }



    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!getPlaceMode()) {
            e.getPlayer().sendMessage("The place is ending!");
            e.setCancelled(true);
            return;
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
                || block == Material.BEEHIVE
                || block == Material.BARRIER
                || block == Material.BEDROCK){
            e.getPlayer().sendMessage("To prevent griefing and potentially breaking the map, we disabled that block.");
            e.setCancelled(true);
            return;
        }

        if (playerPlaceTimers.containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage("You have a "+getConfig().getInt("timers.place")+" second timer! If you think it has already been surpassed, it is either that the server is lagging or the game is bugged.");
            e.setCancelled(true);
            return;
        }

        addPlayerPlaceTimer(e.getPlayer(), getConfig().getInt("timers.place"));
    }

    public void onBlockBreak(BlockBreakEvent e) {
        if (!getBreakOnly()) e.setCancelled(true);
        Material block = e.getBlock().getType();
        if (block == Material.BARRIER || block == Material.BEDROCK){
            e.getPlayer().sendMessage("You can't escape.");
            e.setCancelled(true);
            return;
        }
        if (playerBreakTimers.containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage("You have a "+getConfig().getInt("timers.break")+" second timer! If you think it has already been surpassed, it is either that the server is lagging or the game is bugged.");
            e.setCancelled(true);
            return;
        }

        addPlayerBreakTimer(e.getPlayer(), getConfig().getInt("timers.break"));
    }
}
