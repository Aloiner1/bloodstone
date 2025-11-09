package boo.bloodstone.placeholders;

import boo.bloodstone.TerminatorPlugin;
import boo.bloodstone.managers.TerminatorManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TerminatorPlaceholder extends PlaceholderExpansion {
    private final TerminatorManager terminatorManager;

    public TerminatorPlaceholder(TerminatorPlugin plugin) {
        this.terminatorManager = plugin.getTerminatorManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "terminator";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Aloiner";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        switch (params.toLowerCase()) {
            case "count": return String.valueOf(terminatorManager.getTerminatorCount());
            case "list": return terminatorManager.hasTerminators() ? terminatorManager.getTerminatorNames() : "Нет терминаторов";
            case "istarget": return player == null ? "N/A" : terminatorManager.isTerminator(player.getUniqueId()) ? "Да" : "Нет";
            case "nearest":
                if (player == null) return "N/A";
                Player nearest = terminatorManager.getNearestTerminator(player);
                return nearest != null ? nearest.getName() : "Нет";
            case "nearest_distance":
                if (player == null) return "N/A";
                Player nearestTerminator = terminatorManager.getNearestTerminator(player);
                return nearestTerminator == null ? "∞" : String.valueOf((int) player.getLocation().distance(nearestTerminator.getLocation()));
            case "online_count": return String.valueOf(terminatorManager.getOnlineTerminators().size());
            default: return null;
        }
    }
}
