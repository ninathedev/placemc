package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PauseMode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("place.modes")) return false;
        if (!Globals.getBreakOnly() && !Globals.getPlaceMode()) {
            Bukkit.broadcastMessage("rawr :3");
            return false;
        }
        Globals.setBreakOnly(false);
        Globals.setPlaceMode(false);
        Bukkit.broadcastMessage("Please wait; the place is paused. (Most likely for backing up the world)");
        return false;
    }
}
