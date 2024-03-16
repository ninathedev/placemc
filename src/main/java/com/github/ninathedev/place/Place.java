package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
    private final Map<UUID, Long> playerBreakTimers = new HashMap<>();
    private final Map<UUID, Long> playerPlaceTimers = new HashMap<>();
    private final Map<UUID, Long> playerInteractTimers = new HashMap<>();
    private final Map<UUID, BossBar> playerPlaceBossBars = new HashMap<>();
    private final Map<UUID, BossBar> playerBreakBossBars = new HashMap<>();
    private final Map<UUID, BossBar> playerInteractBossBars = new HashMap<>();
    private final Map<UUID, Boolean> block = new HashMap<>();
    private Metrics metrics;
    public void addPlayerPlaceBossBar(Player player, long delayInSeconds) {
        addPlayerBossBar(player, delayInSeconds, BarColor.BLUE, playerPlaceBossBars, "Placing");
    }
    public void addPlayerBreakBossBar(Player player, long delayInSeconds) {
        addPlayerBossBar(player, delayInSeconds, BarColor.RED, playerBreakBossBars, "Breaking");
    }
    public void addPlayerInteractBossBar(Player player, long delayInSeconds) {
        addPlayerBossBar(player, delayInSeconds, BarColor.GREEN, playerInteractBossBars, "Interacting");
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
                delay[0]--;
                bossBar.setTitle(title);
                bossBar.setProgress((double) delay[0] / totalDelayInSeconds);

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
        long endTime = System.currentTimeMillis() + delayInSeconds * 1000;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                playerBreakTimers.remove(player.getUniqueId());
            }
        };
        runnable.runTaskLater(this, delayInSeconds * 20);
        playerBreakTimers.put(player.getUniqueId(), endTime);
        addPlayerBreakBossBar(player, delayInSeconds);
    }
    public void addPlayerPlaceTimer(Player player, long delayInSeconds) {
        long endTime = System.currentTimeMillis() + delayInSeconds * 1000;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                playerPlaceTimers.remove(player.getUniqueId());
            }
        };
        runnable.runTaskLater(this, delayInSeconds * 20);
        playerPlaceTimers.put(player.getUniqueId(), endTime);
        addPlayerPlaceBossBar(player, delayInSeconds);
    }
    private void addPlayerInteractTimer(Player player, int delayInSeconds) {
        long endTime = System.currentTimeMillis() + delayInSeconds * 1000L;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                playerInteractTimers.remove(player.getUniqueId());
            }
        };
        runnable.runTaskLater(this, delayInSeconds * 20L);
        playerInteractTimers.put(player.getUniqueId(), endTime);
        addPlayerInteractBossBar(player, delayInSeconds);

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
    public static Place instance;
    public static Place getInstance() {
        return instance;
    }
    // If true, block is placed.
    // If false, block is not placed.

    @Override
    public void onEnable() {
        metrics = new Metrics(this, 21338);
        metrics.addCustomChart(new Metrics.SimplePie("servers_showing_other_players", () -> {
            if (getConfig().getBoolean("display.show-other-players")) {
                return "true";
            } else {
                return "false";
            }
        }));
        metrics.addCustomChart(new Metrics.SimplePie("timers_less_than_5_seconds", () -> {
            if (((getConfig().getInt("timers.place") < 5) && (getConfig().getInt("timers.place") > 0)) || ((getConfig().getInt("timers.break") < 5) && (getConfig().getInt("timers.break") > 0)) || ((getConfig().getInt("timers.interact") < 5) && (getConfig().getInt("timers.interact") > 0))) {
                return "0 < Timers < 5";
            } else if ((getConfig().getInt("timers.place") == 0) || (getConfig().getInt("timers.break") == 0) || (getConfig().getInt("timers.interact") == 0)) {
                return "Timers = 0";
            } else {
                return "Timers ≥ 5";
            }
        }));

        instance = this;
        setPlaceMode(true);
        setBreakOnly(true);
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("reloadconfig")).setExecutor(new ReloadConfig());
        Objects.requireNonNull(getCommand("breakonlymode")).setExecutor(new BreakOnlyMode());
        Objects.requireNonNull(getCommand("resumemode")).setExecutor(new ResumeMode());
        Objects.requireNonNull(getCommand("pausemode")).setExecutor(new PauseMode());
        Objects.requireNonNull(getCommand("pc")).setExecutor(new PC());
        Objects.requireNonNull(getCommand("ps")).setExecutor(new PS());
        getLogger().info("[WELCOME] Welcome to placemc!");
        getLogger().info("[WELCOME] This plugin expects that all players are in creative mode.");
        getLogger().info("[REMINDER] We advise that you use command blocks to disable end crystals instead of this plugin. (People can't remove the end crystal)");
        getLogger().info("[REMINDER] To disable end crystals entirely, use a always active repeating command block with the following:");
        getLogger().info("[REMINDER] \"kill @e[type=end_crystal]\"");

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
        metrics.shutdown();
        block.clear();
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
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent e) {
        block.put(e.getPlayer().getUniqueId(), true);
        if (!getPlaceMode()) {
            if (e.getPlayer().hasPermission("place.bypassBlockLimiter")) return;
            e.getPlayer().sendMessage(Objects.requireNonNull(getConfig().getString("messages.placing-in-bom")));
            e.setCancelled(true);
            return;
        }

        Material block = e.getBlockPlaced().getType();
        // Check if the block is restricted
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
            String placeText = Objects.requireNonNull(getConfig().getString("messages.timers.place")).replace("%placeTimer%", String.valueOf(getPlaceTimeLeft(e.getPlayer())));
            if (getConfig().getBoolean("display.send-timer-in-chat.timer-disapproved")) e.getPlayer().sendMessage(placeText);
            e.setCancelled(true);
            return;
        }

        if (e.getPlayer().hasPermission("place.bypassTimer")) return;
        addPlayerPlaceTimer(e.getPlayer(), getConfig().getInt("timers.place"));
        String placeText = Objects.requireNonNull(getConfig().getString("messages.timers.place")).replace("%placeTimer%", String.valueOf(getPlaceTimeLeft(e.getPlayer())));
        if (getConfig().getBoolean("display.send-timer-in-chat.timer-approved")) e.getPlayer().sendMessage(placeText);
        e.getPlayer().giveExpLevels(1);
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInteractBlock(PlayerInteractEvent e) {
        if (block.get(e.getPlayer().getUniqueId()) != null && !block.get(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            block.put(e.getPlayer().getUniqueId(), false);
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (playerInteractTimers.containsKey(e.getPlayer().getUniqueId())) {
                if (e.getPlayer().hasPermission("place.bypassTimer")) return;
                String interactText = Objects.requireNonNull(getConfig().getString("messages.timers.interact")).replace("%interactTimer%", String.valueOf(getInteractTimeLeft(e.getPlayer())));
                if (getConfig().getBoolean("display.send-timer-in-chat.timer-disapproved")) e.getPlayer().sendMessage(interactText);
                e.setCancelled(true);
                return;
            }

            if (e.getPlayer().hasPermission("place.bypassTimer")) return;
            addPlayerInteractTimer(e.getPlayer(), getConfig().getInt("timers.interact"));
            String interactText = Objects.requireNonNull(getConfig().getString("messages.timers.interact")).replace("%interactTimer%", String.valueOf(getInteractTimeLeft(e.getPlayer())));
            if (getConfig().getBoolean("display.send-timer-in-chat.timer-approved")) e.getPlayer().sendMessage(interactText);
        }
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
    @EventHandler(priority = EventPriority.HIGH)
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
                String breakTest = Objects.requireNonNull(getConfig().getString("messages.timers.break")).replace("%breakTimer%", String.valueOf(getBreakTimeLeft(e.getPlayer())));
                if (getConfig().getBoolean("display.send-timer-in-chat.timer-disapproved")) e.getPlayer().sendMessage(breakTest);
                e.setCancelled(true);
                return;
            }

            if (e.getPlayer().hasPermission("place.bypassTimer")) return;
            addPlayerBreakTimer(e.getPlayer(), getConfig().getInt("timers.break"));
            String breakTest = Objects.requireNonNull(getConfig().getString("messages.timers.break")).replace("%breakTimer%", String.valueOf(getBreakTimeLeft(e.getPlayer())));
            if (getConfig().getBoolean("display.send-timer-in-chat.timer-approved")) e.getPlayer().sendMessage(breakTest);
            e.getPlayer().giveExpLevels(-1);
        }
    }
}