package com.github.ninathedev.place;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResumeMode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!commandSender.hasPermission("place.modes")) return false;
        if (Globals.getBreakOnly() && Globals.getPlaceMode()) {
            commandSender.sendMessage("[place] rawr :3");
            return false;
        }
        Globals.setBreakOnly(true);
        Globals.setPlaceMode(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("[place] "+Place.getInstance().getConfig().getString("messages.commands.resume-mode"));
        }
        Place.getInstance().getLogger().info("Resume mode enabled by: "+commandSender.getName());
        return true;
    }
}
