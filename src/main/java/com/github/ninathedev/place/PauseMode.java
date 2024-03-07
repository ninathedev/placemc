package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PauseMode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("place.modes")) return false;
        if (!Globals.getBreakOnly() && !Globals.getPlaceMode()) {
            commandSender.sendMessage("rawr :3");
            return false;
        }
        Globals.setBreakOnly(false);
        Globals.setPlaceMode(false);
        Bukkit.broadcastMessage("[place] "+Place.getInstance().getConfig().getString("messages.commands.pause-mode"));
        return true;
    }
}
