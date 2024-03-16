package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReloadConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("place.reloadconf")) return false;
        Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("place")).reloadConfig();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("[place] "+Place.getInstance().getConfig().getString("messages.commands.reload-config"));
        }
        Place.getInstance().getLogger().info("Config reloaded by: "+sender.getName());
        return true;
    }
}