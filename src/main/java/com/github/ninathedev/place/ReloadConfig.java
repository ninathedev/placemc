package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class ReloadConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("place.reloadconf")) return false;
        Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("place")).reloadConfig();
        Bukkit.broadcastMessage("[place] "+Place.getInstance().getConfig().getString("messages.commands.reload-config"));
        return true;
    }
}