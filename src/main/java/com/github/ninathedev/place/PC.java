package com.github.ninathedev.place;

import org.bukkit.GameMode;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PC implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getGameMode() == GameMode.CREATIVE) {
                player.sendMessage("[place] "+Place.getInstance().getConfig().getString("messages.commands.pc.not-yet"));
                return true;
            }
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage("[place] "+Place.getInstance().getConfig().getString("messages.commands.pc.already"));
            return true;
        } else if (sender instanceof CommandBlock) {
            sender.sendMessage("[place] Beep boop. Boop beep?");
            return true;
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("[place] You must be in game to run this command!");
            return true;
        }
        return false;
    }
}
