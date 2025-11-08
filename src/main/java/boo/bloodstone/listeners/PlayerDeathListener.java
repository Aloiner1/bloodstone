package boo.bloodstone.listeners;

import boo.bloodstone.TerminatorPlugin;
import boo.bloodstone.managers.TerminatorManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        if (!terminatorManager.isTerminator(victim.getUniqueId())) return;
        
        terminatorManager.removeTerminator(victim.getUniqueId());
        Player killer = victim.getKiller();
        String name = killer != null ? killer.getName() : "Неизвестный";
        Bukkit.broadcast(Component.text("§6§l" + name + " §cубил терминатора §c§l" + victim.getName()));
        
        playSound("sounds.terminator-killed", "ENTITY_ENDER_DRAGON_GROWL");
        if (!terminatorManager.hasTerminators()) handleAllTerminatorsKilled();
    }
    
    private void handleAllTerminatorsKilled() {
        Bukkit.broadcast(Component.text("§a§lВсе терминаторы были убиты"));
        playSound("sounds.all-killed", "UI_TOAST_CHALLENGE_COMPLETE");
    }
    
    private void playSound(String path, String def) {
        if (!plugin.getConfig().getBoolean("sounds.enabled", true)) return;
        try {
            @SuppressWarnings("deprecation")
            Sound s = Sound.valueOf(plugin.getConfig().getString(path, def).toUpperCase());
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), s, 1.0f, 1.0f));
        } catch (Exception ignored) {}
    }
}
