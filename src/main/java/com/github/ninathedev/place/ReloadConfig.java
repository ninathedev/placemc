package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class ReloadConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("place.reloadconf")) return false;
        Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("place")).reloadConfig();
        Bukkit.broadcastMessage("The config file of the place is reloaded. If there exists any bugs, please do not hesitate to report to r/placemc");
        return false;
    }
}