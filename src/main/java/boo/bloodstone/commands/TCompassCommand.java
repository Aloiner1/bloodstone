package boo.bloodstone.commands;

import boo.bloodstone.TerminatorPlugin;
import boo.bloodstone.managers.CompassManager;
import net.kyori.adventure.text.Component;
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
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command c,
                            @NotNull String l, @NotNull String[] args) {
        if (!s.hasPermission("terminator.compass")) {
            s.sendMessage(Component.text("§cУ вас нет прав для использования этой команд"));
            return true;
        }
        
        Player t = args.length == 0 ? (s instanceof Player ? (Player) s : null) : Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Component.text("§cИгрок не найден"));
            return true;
        }
        
        compassManager.giveCompass(t);
        if (!s.equals(t)) s.sendMessage(Component.text("§aКомпас выдан игроку §e" + t.getName()));
        return true;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command c,
                                                 @NotNull String a, @NotNull String[] args) {
        if (!s.hasPermission("terminator.compass") || args.length != 1) return new ArrayList<>();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }
}
