package boo.bloodstone.managers;

import boo.bloodstone.TerminatorPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Collectors;

public class CompassManager {
    
    private final TerminatorPlugin plugin;
    private final NamespacedKey compassKey;
    private BukkitTask updateTask;
    
    public CompassManager(TerminatorPlugin plugin) {
        this.plugin = plugin;
        this.compassKey = new NamespacedKey(plugin, "terminator_compass");
    }
    
    public ItemStack createTrackingCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) compass.getItemMeta();
        
        if (meta != null) {
            String nameConfig = plugin.getConfig().getString("compass.name", "&c&lТрекер");
            Component name = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(nameConfig)
                    .decoration(TextDecoration.ITALIC, false);
            meta.displayName(name);
            List<String> loreConfig = plugin.getConfig().getStringList("compass.lore");
            List<Component> lore = loreConfig.stream()
                    .map(line -> LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(line)
                            .decoration(TextDecoration.ITALIC, false))
                    .collect(Collectors.toList());
            meta.lore(lore);
            meta.getPersistentDataContainer().set(compassKey, PersistentDataType.BYTE, (byte) 1);
            
            compass.setItemMeta(meta);
        }
        
        return compass;
    }
    
    public boolean isTrackingCompass(ItemStack item) {
        if (item == null || item.getType() != Material.COMPASS) {
            return false;
        }
        
        CompassMeta meta = (CompassMeta) item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        return meta.getPersistentDataContainer().has(compassKey, PersistentDataType.BYTE);
    }
    
    public boolean updateCompass(Player player, ItemStack compass) {
        if (!isTrackingCompass(compass)) return false;
        
        Player target = plugin.getTerminatorManager().getNearestTerminator(player);
        CompassMeta meta = (CompassMeta) compass.getItemMeta();
        if (meta == null) return false;
        
        if (target == null) {
            meta.setLodestone(null);
            meta.setLodestoneTracked(false);
            compass.setItemMeta(meta);
            return false;
        }
        
        meta.setLodestone(target.getLocation());
        meta.setLodestoneTracked(false);
        compass.setItemMeta(meta);
        return true;
    }
    
    public void updateAllCompasses() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerCompasses(player);
        }
    }
    
    public void updatePlayerCompasses(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isTrackingCompass(item)) {
                updateCompass(player, item);
            }
        }
    }
    
    public void startUpdateTask() {
        int interval = plugin.getConfig().getInt("compass.update-interval", 40);
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateAllCompasses, 0L, interval);
    }
    
    public void stopUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }
    
    public void giveCompass(Player player) {
        ItemStack compass = createTrackingCompass();
        player.getInventory().addItem(compass);
        player.sendMessage(Component.text("§aВы получили компас-трекер"));
    }
}
