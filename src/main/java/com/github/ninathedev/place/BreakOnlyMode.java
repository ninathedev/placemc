package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BreakOnlyMode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!commandSender.hasPermission("place.modes")) return false;
        if (Globals.getBreakOnly() && !Globals.getPlaceMode()) {
            commandSender.sendMessage("[place] rawr :3");
            return false;
        }
        Globals.setBreakOnly(true);
        Globals.setPlaceMode(false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("[place] "+Place.getInstance().getConfig().getString("messages.commands.break-only-mode"));
        }
        Place.getInstance().getLogger().info("Break only mode enabled by: "+commandSender.getName());
        return true;
    }
}
