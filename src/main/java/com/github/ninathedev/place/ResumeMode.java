package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ResumeMode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("place.modes")) return false;
        if (Globals.getBreakOnly() && Globals.getPlaceMode()) {
            commandSender.sendMessage("rawr :3");
            return false;
        }
        Globals.setBreakOnly(true);
        Globals.setPlaceMode(true);
        Bukkit.broadcastMessage("[place] "+Place.getInstance().getConfig().getString("messages.commands.resume-mode"));
        return true;
    }
}
