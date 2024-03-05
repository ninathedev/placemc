package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.github.ninathedev.place.Globals.*;

public final class Place extends JavaPlugin implements Listener {
    private Map<UUID, Long> playerBreakTimers = new HashMap<>();
    private Map<UUID, Long> playerPlaceTimers = new HashMap<>();
    private Map<UUID, Long> playerInteractTimers = new HashMap<>();
    private Map<UUID, BossBar> playerPlaceBossBars = new HashMap<>();
    private Map<UUID, BossBar> playerBreakBossBars = new HashMap<>();
    private Map<UUID, BossBar> playerInteractBossBars = new HashMap<>();

    public void addPlayerPlaceBossBar(Player player, long delayInSeconds) {
        addPlayerBossBar(player, delayInSeconds, BarColor.BLUE, playerPlaceBossBars, "Placing");
    }

    public void addPlayerBreakBossBar(Player player, long delayInSeconds) {
        addPlayerBossBar(player, delayInSeconds, BarColor.RED, playerBreakBossBars, "Breaking");
    }

    public void addPlayerInteractBossBar(Player player, long delayInSeconds) {
        addPlayerBossBar(player, delayInSeconds, BarColor.GREEN, playerInteractBossBars, "Breaking");
    }

    private void addPlayerBossBar(Player player, long delayInSeconds, BarColor color, Map<UUID, BossBar> bossBars, String theTitle) {
        if (!getConfig().getBoolean("display.show-bossbar")) return;
        // Store the total delay for later calculations
        final long totalDelayInSeconds = delayInSeconds;
        final long[] delay = {delayInSeconds}; // it errors unless i do this idk why
        final String title = theTitle;

        // Create a new boss bar
        BossBar bossBar = Bukkit.createBossBar(theTitle, color, BarStyle.SOLID);

        // Add the player to the boss bar
        bossBar.addPlayer(player);

        // Store the boss bar
        bossBars.put(player.getUniqueId(), bossBar);

        // Create a new timer
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                // Update the boss bar
                delay[0]--;
                bossBar.setTitle(title);
                bossBar.setProgress((double) delay[0] / totalDelayInSeconds);

                // Remove the boss bar when the time is up
                if (bossBar.getProgress() <= 0) {
                    bossBar.removeAll();
                    bossBars.remove(player.getUniqueId());
                    this.cancel();
                }
            }
        };

        // Start the timer
        runnable.runTaskTimer(this, 0L, 20L); // 20 ticks = 1 second
    }
    public void addPlayerBreakTimer(Player player, long delayInSeconds) {
        // Calculate the end time
        long endTime = System.currentTimeMillis() + delayInSeconds * 1000;

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
        playerBreakTimers.put(player.getUniqueId(), endTime);

        // Use BossBar
        addPlayerBreakBossBar(player, delayInSeconds);
    }

    public void addPlayerInteractTimer(Player player, long delayInSeconds) {
        // Calculate the end time
        long endTime = System.currentTimeMillis() + delayInSeconds * 1000;

        // Create a new timer
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                playerInteractTimers.remove(player.getUniqueId());
            }
        };

        // Start the timer
        runnable.runTaskLater(this, delayInSeconds * 20); // 20 ticks = 1 second

        // Store the timer
        playerInteractTimers.put(player.getUniqueId(), endTime);

        // Use BossBar
        addPlayerInteractBossBar(player, delayInSeconds);
    }

    public void addPlayerPlaceTimer(Player player, long delayInSeconds) {
        // Calculate the end time
        long endTime = System.currentTimeMillis() + delayInSeconds * 1000;

        // Create a new timer
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                playerPlaceTimers.remove(player.getUniqueId());
            }
        };

        // Start the timer
        runnable.runTaskLater(this, delayInSeconds * 20); // 20 ticks = 1 second

        // Store the end time
        playerPlaceTimers.put(player.getUniqueId(), endTime);

        // Use BossBar
        addPlayerPlaceBossBar(player, delayInSeconds);
    }

    public long getPlaceTimeLeft(Player player) {
        if (playerPlaceTimers.containsKey(player.getUniqueId())) {
            // Calculate the time left in seconds
            long timeLeft = (playerPlaceTimers.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
            return timeLeft > 0 ? timeLeft : 0;
        }
        return 0; // Return 0 if there's no timer for the player
    }
    public long getBreakTimeLeft(Player player) {
        if (playerBreakTimers.containsKey(player.getUniqueId())) {
            // Calculate the time left in seconds
            long timeLeft = (playerBreakTimers.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
            return timeLeft > 0 ? timeLeft : 0;
        }
        return 0; // Return 0 if there's no timer for the player
    }

    public long getInteractTimeLeft(Player player) {
        if (playerInteractTimers.containsKey(player.getUniqueId())) {
            // Calculate the time left in seconds
            long timeLeft = (playerInteractTimers.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
            return timeLeft > 0 ? timeLeft : 0;
        }
        return 0; // Return 0 if there's no timer for the player
    }



    @Override
    public void onEnable() {
        setPlaceMode(true);
        setBreakOnly(true);
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("reloadconfig").setExecutor(new ReloadConfig());
        getCommand("breakonlymode").setExecutor(new BreakOnlyMode());
        getCommand("resumemode").setExecutor(new ResumeMode());
        getCommand("pausemode").setExecutor(new PauseMode());
        getLogger().info("[WELCOME] Welcome to placemc!");
        getLogger().info("[WELCOME] This plugin expects that all players are in creative mode and");
        getLogger().info("[WELCOME] that the rest of the entire server is not accessible by people.");
        getLogger().info("[WELCOME] Please make sure that nothing is letting the players roam out of the 3D canvas!");
        getLogger().info("[REMINDER] We advise that you use command blocks to disable end crystals instead of this plugin.");
        getLogger().info("[REMINDER] (People can't remove the end crystal)");
        getLogger().info("[REMINDER] Repeating always active command block: \"kill @e[type=end_crystal]\"");

        // Checks if server is running paper or spigot
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
        } catch (ClassNotFoundException e) {
            getLogger().warning("[WARNING] You are running a Spigot server!");
            getLogger().warning("[WARNING] Although this plugin will most likely work on Spigot,");
            getLogger().warning("[WARNING] not only this plugin uses Paper API,");
            getLogger().warning("[WARNING] but Paper has better support for many players.");
            getLogger().warning("[WARNING] WE WILL NOT PROVIDE SUPPORT FOR USING A SPIGOT SERVER!");
        }
    }


    @Override
    public void onDisable() {
        setPlaceMode(true);
        setBreakOnly(true);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (!getConfig().getBoolean("display.show-other-players")) return;
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                if (player1 != player2) player1.hidePlayer(this, player2);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().hasPermission("place.bypassBlockLimiter")) return;

        ItemStack droppedItem = e.getItemDrop().getItemStack();
        Player player = e.getPlayer();

        for (ItemStack inventoryItem : player.getInventory().getContents()) {
            if (inventoryItem != null && inventoryItem.getType().equals(droppedItem.getType())) {
                player.sendMessage("[place] "+getConfig().getString("messages.dropping-items"));
                e.setCancelled(true);
                break; // Stop checking once a matching item is found
            }
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

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (getConfig().getBoolean("disallow-end-crystals")) {
            Entity entity = e.getEntity();

            if (entity.getType().equals(EntityType.ENDER_CRYSTAL)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (getConfig().getBoolean("disallow-end-crystals")) {
            Entity entity = e.getEntity();

            if (entity.getType().equals(EntityType.ENDER_CRYSTAL)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (getConfig().getBoolean("disallow-end-crystals")) {
            Entity entity = e.getEntity();

            if (entity.getType().equals(EntityType.ENDER_CRYSTAL)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByBlock(EntityDamageByBlockEvent e) {
        if (getConfig().getBoolean("disallow-end-crystals")) {
            Entity entity = e.getEntity();

            if (entity.getType().equals(EntityType.ENDER_CRYSTAL)) {
                e.setCancelled(true);
            }
        }
    }



    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!getPlaceMode()) {
            if (e.getPlayer().hasPermission("place.bypassBlockLimiter")) return;
            e.getPlayer().sendMessage(getConfig().getString("messages.placing-in-bom"));
            e.setCancelled(true);
            return;
        }

        Material block = e.getBlockPlaced().getType();
        if (block == Material.PISTON
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
                || block == Material.BEDROCK
                || block == Material.ANVIL
                || block == Material.CHIPPED_ANVIL
                || block == Material.DAMAGED_ANVIL
                || block == Material.COMMAND_BLOCK
                || block == Material.COMMAND_BLOCK_MINECART
                || block == Material.CHAIN_COMMAND_BLOCK
                || block == Material.REPEATING_COMMAND_BLOCK
                || block == Material.RESPAWN_ANCHOR){
            if (e.getPlayer().hasPermission("place.bypassBlockLimiter")) return;
            e.getPlayer().sendMessage("[place] "+getConfig().getString("messages.placing-restricted-blocks"));
            e.setCancelled(true);
            return;
        }

        if (playerPlaceTimers.containsKey(e.getPlayer().getUniqueId())) {
            if (e.getPlayer().hasPermission("place.bypassTimer")) return;
            String placeText = getConfig().getString("messages.timers.place").replace("%placeTimer%", String.valueOf(getPlaceTimeLeft(e.getPlayer())));
            if (getConfig().getBoolean("display.send-timer-in-chat.timer-disapproved")) e.getPlayer().sendMessage(placeText);
            e.setCancelled(true);
            return;
        }

        if (e.getPlayer().hasPermission("place.bypassTimer")) return;
        addPlayerPlaceTimer(e.getPlayer(), getConfig().getInt("timers.place"));
        String placeText = getConfig().getString("messages.timers.place").replace("%placeTimer%", String.valueOf(getPlaceTimeLeft(e.getPlayer())));
        if (getConfig().getBoolean("display.send-timer-in-chat.timer-approved")) e.getPlayer().sendMessage(placeText);
    }
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (e.getPlayer().hasPermission("place.bypassBlockLimiter")) return;
        Material bucket = e.getBucket();
        if (bucket == Material.LAVA_BUCKET || bucket == Material.WATER_BUCKET) {
            e.getPlayer().sendMessage("[place] "+getConfig().getString("messages.placing-restricted-blocks"));
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            if (!getBreakOnly()) {
                if (e.getPlayer().hasPermission("place.bypassBlockLimiter")) return;
                e.setCancelled(true);
            }
            Material block = Objects.requireNonNull(e.getClickedBlock()).getType();
            if (block == Material.BARRIER || block == Material.BEDROCK) {
                if (e.getPlayer().hasPermission("place.bypassBlockLimiter")) return;
                if (getConfig().getBoolean("display.send-timer-in-chat.timer-disapproved")) e.getPlayer().sendMessage("[place] "+getConfig().getString("messages.breaking-restricted-blocks"));
                e.setCancelled(true);
                return;
            }
            if (playerBreakTimers.containsKey(e.getPlayer().getUniqueId())) {
                if (e.getPlayer().hasPermission("place.bypassTimer")) return;
                String breakTest = getConfig().getString("messages.timers.break").replace("%breakTimer%", String.valueOf(getBreakTimeLeft(e.getPlayer())));
                if (getConfig().getBoolean("display.send-timer-in-chat.timer-disapproved")) e.getPlayer().sendMessage(breakTest);
                e.setCancelled(true);
                return;
            }

            if (e.getPlayer().hasPermission("place.bypassTimer")) return;
            addPlayerBreakTimer(e.getPlayer(), getConfig().getInt("timers.break"));
            String breakTest = getConfig().getString("messages.timers.break").replace("%breakTimer%", String.valueOf(getBreakTimeLeft(e.getPlayer())));
            if (getConfig().getBoolean("display.send-timer-in-chat.timer-approved")) e.getPlayer().sendMessage(breakTest);
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            if (playerInteractTimers.containsKey(e.getPlayer().getUniqueId())) {
                if (e.getPlayer().hasPermission("place.bypassTimer")) return;
                String interactTest = getConfig().getString("messages.timers.interact").replace("%interactTimer%", String.valueOf(getBreakTimeLeft(e.getPlayer())));
                if (getConfig().getBoolean("display.send-timer-in-chat.timer-disapproved")) e.getPlayer().sendMessage(interactTest);
                e.setCancelled(true);
                return;
            }
            if (e.getPlayer().hasPermission("place.bypassTimer")) return;
            addPlayerBreakTimer(e.getPlayer(), getConfig().getInt("timers.interact"));
            String interactTest = getConfig().getString("messages.timers.interact").replace("%interactTimer%", String.valueOf(getBreakTimeLeft(e.getPlayer())));
            if (getConfig().getBoolean("display.send-timer-in-chat.timer-approved")) e.getPlayer().sendMessage(interactTest);
        }
    }
}