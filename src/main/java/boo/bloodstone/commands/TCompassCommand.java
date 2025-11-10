package boo.bloodstone.commands;

import boo.bloodstone.TerminatorPlugin;
import boo.bloodstone.managers.CompassManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TCompassCommand implements CommandExecutor, TabCompleter {
    private final CompassManager compassManager;

    public TCompassCommand(TerminatorPlugin plugin) {
        this.compassManager = plugin.getCompassManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("terminator.compass")) {
            sender.sendMessage(Component.text("У вас нет прав для использования этой команды").color(NamedTextColor.RED));
            return true;
        }

        Player target = args.length == 0 ? (sender instanceof Player ? (Player) sender : null) : Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Игрок не найден").color(NamedTextColor.RED));
            return true;
        }

        compassManager.giveCompass(target);
        if (!sender.equals(target)) {
            sender.sendMessage(Component.text("Компас выдан игроку ").color(NamedTextColor.GREEN).append(Component.text(target.getName()).color(NamedTextColor.YELLOW)));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("terminator.compass") || args.length != 1) return new ArrayList<>();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(playerName -> playerName.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }
}
