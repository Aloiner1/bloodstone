package boo.bloodstone.listeners;

import boo.bloodstone.TerminatorPlugin;
import boo.bloodstone.managers.TerminatorManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final TerminatorPlugin plugin;
    private final TerminatorManager terminatorManager;

    public PlayerDeathListener(TerminatorPlugin plugin) {
        this.plugin = plugin;
        this.terminatorManager = plugin.getTerminatorManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (!terminatorManager.isTerminator(victim.getUniqueId())) return;

        terminatorManager.removeTerminator(victim.getUniqueId());
        Player killer = victim.getKiller();
        String killerName = killer != null ? killer.getName() : "Неизвестный";
        
        Component message = Component.text(killerName).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD).append(Component.text(" убил терминатора ").color(NamedTextColor.RED)).append(Component.text(victim.getName()).color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
        Bukkit.broadcast(message);

        playSound("sounds.terminator-killed", "entity.ender_dragon.growl");
        if (!terminatorManager.hasTerminators()) handleAllTerminatorsKilled();
    }

    private void handleAllTerminatorsKilled() {
        Component message = Component.text("Все терминаторы были убиты").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD);
        Bukkit.broadcast(message);
        playSound("sounds.all-killed", "ui.toast.challenge_complete");
    }

    private void playSound(String configPath, String defaultSound) {
        if (!plugin.getConfig().getBoolean("sounds.enabled", true)) return;
        try {
            String soundKey = plugin.getConfig().getString(configPath, defaultSound).toLowerCase();
            Sound sound = Sound.sound(Key.key(soundKey), Sound.Source.MASTER, 100f, 1f);
            Bukkit.getServer().playSound(sound);
        } catch (Exception ignored) {}
    }
}
