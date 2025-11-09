package boo.bloodstone.commands;

import boo.bloodstone.TerminatorPlugin;
import boo.bloodstone.managers.TerminatorManager;
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
            sender.sendMessage(Component.text("§cУ вас нет прав для использования этой команды"));
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
            sender.sendMessage(Component.text("§cИспользование: /terminator add <игрок>"));
            return;
        }

        Player t = Bukkit.getPlayer(args[1]);
        if (t == null) {
            sender.sendMessage(Component.text("§cИгрок не найден"));
            return;
        }

        if (terminatorManager.addTerminator(t.getUniqueId())) {
            sender.sendMessage(Component.text("§aИгрок §e" + t.getName() + " §aдобавлен в терминаторы"));
            t.sendMessage(Component.text("§c§lВы были назначены терминатором"));
        } else {
            sender.sendMessage(Component.text("§eИгрок уже является терминатором"));
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("§cИспользование: /terminator remove <игрок>"));
            return;
        }

        Player t = Bukkit.getPlayer(args[1]);
        if (t == null) {
            sender.sendMessage(Component.text("§cИгрок не найден"));
            return;
        }

        if (terminatorManager.removeTerminator(t.getUniqueId())) {
            sender.sendMessage(Component.text("§aИгрок §e" + t.getName() + " §aудалён из терминаторов"));
            t.sendMessage(Component.text("§aВы больше не терминатор"));
        } else {
            sender.sendMessage(Component.text("§cИгрок не является терминатором"));
        }
    }

    private void handleList(CommandSender sender) {
        if (terminatorManager.hasTerminators()) {
            sender.sendMessage(Component.text("§6Список терминаторов: §e" + terminatorManager.getTerminatorNames()));
        } else {
            sender.sendMessage(Component.text("§cНет активных терминаторов"));
        }
    }

    private void handleClear(CommandSender sender) {
        terminatorManager.clearTerminators();
        sender.sendMessage(Component.text("§aВсе терминаторы были очищены"));
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("§6=== Команды терминаторов ==="));
        sender.sendMessage(Component.text("§e/terminator add <игрок> §7- Добавить терминатора"));
        sender.sendMessage(Component.text("§e/terminator remove <игрок> §7- Удалить терминатора"));
        sender.sendMessage(Component.text("§e/terminator list §7- Список терминаторов"));
        sender.sendMessage(Component.text("§e/terminator clear §7- Очистить всех терминаторов"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("terminator.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list", "clear").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
