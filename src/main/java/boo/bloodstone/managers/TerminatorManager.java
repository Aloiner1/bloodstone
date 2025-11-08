package boo.bloodstone.managers;

import boo.bloodstone.TerminatorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TerminatorManager {
    
    private final TerminatorPlugin plugin;
    private final Set<UUID> terminators;
    private final NamespacedKey terminatorKey;
    
    public TerminatorManager(TerminatorPlugin plugin) {
        this.plugin = plugin;
        this.terminators = ConcurrentHashMap.newKeySet();
        this.terminatorKey = new NamespacedKey(plugin, "is_terminator");
    }
    
    public boolean addTerminator(UUID playerId) {
        boolean added = terminators.add(playerId);
        if (added) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.getPersistentDataContainer().set(terminatorKey, PersistentDataType.BYTE, (byte) 1);
            }
        }
        return added;
    }
    
    public boolean removeTerminator(UUID playerId) {
        boolean removed = terminators.remove(playerId);
        if (removed) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.getPersistentDataContainer().remove(terminatorKey);
            }
        }
        return removed;
    }
    
    public boolean isTerminator(UUID playerId) {
        return terminators.contains(playerId);
    }
    
    public boolean hasTerminatorTag(Player player) {
        return player.getPersistentDataContainer().has(terminatorKey, PersistentDataType.BYTE);
    }
    
    public boolean toggleTerminator(UUID playerId) {
        if (isTerminator(playerId)) {
            removeTerminator(playerId);
            return false;
        }
        addTerminator(playerId);
        return true;
    }
    
    public Set<UUID> getTerminators() {
        return Collections.unmodifiableSet(terminators);
    }
    
    public List<Player> getOnlineTerminators() {
        List<Player> onlineTerminators = new ArrayList<>();
        for (UUID terminatorId : terminators) {
            Player player = Bukkit.getPlayer(terminatorId);
            if (player != null && player.isOnline()) {
                onlineTerminators.add(player);
            }
        }
        return onlineTerminators;
    }
    
    public Player getNearestTerminator(Player player) {
        List<Player> online = getOnlineTerminators();
        online.removeIf(t -> t.getUniqueId().equals(player.getUniqueId()));
        
        if (online.isEmpty()) return null;
        
        Player nearest = null;
        double minDist = Double.MAX_VALUE;
        
        for (Player t : online) {
            if (!t.getWorld().equals(player.getWorld())) continue;
            
            double dist = t.getLocation().distance(player.getLocation());
            if (dist < minDist) {
                minDist = dist;
                nearest = t;
            }
        }
        
        return nearest;
    }
    
    public int getTerminatorCount() {
        return terminators.size();
    }
    
    public boolean hasTerminators() {
        return !terminators.isEmpty();
    }
    
    public void clearTerminators() {
        for (UUID terminatorId : new ArrayList<>(terminators)) {
            Player player = Bukkit.getPlayer(terminatorId);
            if (player != null && player.isOnline()) {
                player.getPersistentDataContainer().remove(terminatorKey);
            }
        }
        terminators.clear();
    }
    
    public String getTerminatorNames() {
        List<String> names = new ArrayList<>();
        for (UUID id : terminators) {
            Player p = Bukkit.getPlayer(id);
            names.add(p != null ? p.getName() : Bukkit.getOfflinePlayer(id).getName());
        }
        return String.join(", ", names);
    }
}
