package boo.bloodstone.commands;

import boo.bloodstone.TerminatorPlugin;
import boo.bloodstone.managers.TerminatorManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TerminatorCommand implements CommandExecutor, TabCompleter {
    private final TerminatorManager terminatorManager;

    public TerminatorCommand(TerminatorPlugin plugin) {
        this.terminatorManager = plugin.getTerminatorManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("terminator.admin")) {
            sender.sendMessage(Component.text("У вас нет прав для использования этой команды").color(NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                handleAdd(sender, args);
                break;
            case "remove":
                handleRemove(sender, args);
                break;
            case "list":
                handleList(sender);
                break;
            case "clear":
                handleClear(sender);
                break;
            default:
                sendUsage(sender);
        }
        return true;
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Использование: /terminator add <игрок>").color(NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Игрок не найден").color(NamedTextColor.RED));
            return;
        }

        if (terminatorManager.addTerminator(target.getUniqueId())) {
            sender.sendMessage(Component.text("Игрок ").color(NamedTextColor.GREEN).append(Component.text(target.getName()).color(NamedTextColor.YELLOW)).append(Component.text(" добавлен в терминаторы").color(NamedTextColor.GREEN)));
            target.sendMessage(Component.text("Вы были назначены терминатором").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
        } else {
            sender.sendMessage(Component.text("Игрок уже является терминатором").color(NamedTextColor.YELLOW));
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Использование: /terminator remove <игрок>").color(NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Игрок не найден").color(NamedTextColor.RED));
            return;
        }

        if (terminatorManager.removeTerminator(target.getUniqueId())) {
            sender.sendMessage(Component.text("Игрок ").color(NamedTextColor.GREEN).append(Component.text(target.getName()).color(NamedTextColor.YELLOW)).append(Component.text(" удалён из терминаторов").color(NamedTextColor.GREEN)));
            target.sendMessage(Component.text("Вы больше не терминатор").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Игрок не является терминатором").color(NamedTextColor.RED));
        }
    }

    private void handleList(CommandSender sender) {
        if (terminatorManager.hasTerminators()) {
            sender.sendMessage(Component.text("Список терминаторов: ").color(NamedTextColor.GOLD).append(Component.text(terminatorManager.getTerminatorNames()).color(NamedTextColor.YELLOW)));
        } else {
            sender.sendMessage(Component.text("Нет активных терминаторов").color(NamedTextColor.RED));
        }
    }

    private void handleClear(CommandSender sender) {
        terminatorManager.clearTerminators();
        sender.sendMessage(Component.text("Все терминаторы были очищены").color(NamedTextColor.GREEN));
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("=== Команды терминаторов ===").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/terminator add <игрок>").color(NamedTextColor.YELLOW).append(Component.text(" - Добавить терминатора").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/terminator remove <игрок>").color(NamedTextColor.YELLOW).append(Component.text(" - Удалить терминатора").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/terminator list").color(NamedTextColor.YELLOW).append(Component.text(" - Список терминаторов").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/terminator clear").color(NamedTextColor.YELLOW).append(Component.text(" - Очистить всех терминаторов").color(NamedTextColor.GRAY)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("terminator.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list", "clear").stream()
                    .filter(subcommand -> subcommand.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(playerName -> playerName.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
