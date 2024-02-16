package com.github.ninathedev.place;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;

public class ReloadConfig implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            Place.plugin.reloadConfig();
            return true;
        } catch (ExecutionException err) {
            err.printStackTrace();
            return false;
        }

    }
}