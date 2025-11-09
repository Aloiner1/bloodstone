package boo.bloodstone.commands;

import boo.bloodstone.TerminatorPlugin;
import boo.bloodstone.managers.TerminatorManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    private final MiniMessage miniMessage;

    public TerminatorCommand(TerminatorPlugin plugin) {
        this.terminatorManager = plugin.getTerminatorManager();
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("terminator.admin")) {
            sender.sendMessage(miniMessage.deserialize("<red>У вас нет прав для использования этой команды</red>"));
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
            sender.sendMessage(miniMessage.deserialize("<red>Использование: /terminator add <игрок></red>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(miniMessage.deserialize("<red>Игрок не найден</red>"));
            return;
        }

        if (terminatorManager.addTerminator(target.getUniqueId())) {
            sender.sendMessage(miniMessage.deserialize("<green>Игрок <yellow>" + target.getName() + "</yellow> добавлен в терминаторы</green>"));
            target.sendMessage(miniMessage.deserialize("<red><bold>Вы были назначены терминатором</bold></red>"));
        } else {
            sender.sendMessage(miniMessage.deserialize("<yellow>Игрок уже является терминатором</yellow>"));
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize("<red>Использование: /terminator remove <игрок></red>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(miniMessage.deserialize("<red>Игрок не найден</red>"));
            return;
        }

        if (terminatorManager.removeTerminator(target.getUniqueId())) {
            sender.sendMessage(miniMessage.deserialize("<green>Игрок <yellow>" + target.getName() + "</yellow> удалён из терминаторов</green>"));
            target.sendMessage(miniMessage.deserialize("<green>Вы больше не терминатор</green>"));
        } else {
            sender.sendMessage(miniMessage.deserialize("<red>Игрок не является терминатором</red>"));
        }
    }

    private void handleList(CommandSender sender) {
        if (terminatorManager.hasTerminators()) {
            sender.sendMessage(miniMessage.deserialize("<gold>Список терминаторов: <yellow>" + terminatorManager.getTerminatorNames() + "</yellow></gold>"));
        } else {
            sender.sendMessage(miniMessage.deserialize("<red>Нет активных терминаторов</red>"));
        }
    }

    private void handleClear(CommandSender sender) {
        terminatorManager.clearTerminators();
        sender.sendMessage(miniMessage.deserialize("<green>Все терминаторы были очищены</green>"));
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize("<gold>=== Команды терминаторов ===</gold>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/terminator add <игрок></yellow> <gray>- Добавить терминатора</gray>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/terminator remove <игрок></yellow> <gray>- Удалить терминатора</gray>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/terminator list</yellow> <gray>- Список терминаторов</gray>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/terminator clear</yellow> <gray>- Очистить всех терминаторов</gray>"));
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
