package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BreakOnlyMode implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (Globals.getBreakOnly() && !Globals.getPlaceMode()) {
            Bukkit.broadcastMessage("rawr :3");
            return false;
        }
        Globals.setBreakOnly(true);
        Globals.setPlaceMode(false);
        Bukkit.broadcastMessage("The place is ending!");
        return true;
    }
}