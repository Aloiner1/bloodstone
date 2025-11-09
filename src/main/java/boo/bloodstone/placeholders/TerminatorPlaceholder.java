package boo.bloodstone.placeholders;

import boo.bloodstone.TerminatorPlugin;
import boo.bloodstone.managers.TerminatorManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TerminatorPlaceholder extends PlaceholderExpansion {
    private final TerminatorPlugin plugin;
    private final TerminatorManager terminatorManager;

    public TerminatorPlaceholder(TerminatorPlugin plugin) {
        this.plugin = plugin;
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
    public @Nullable String onPlaceholderRequest(Player p, @NotNull String params) {
        switch (params.toLowerCase()) {
            case "count": return String.valueOf(terminatorManager.getTerminatorCount());
            case "list": return terminatorManager.hasTerminators() ? terminatorManager.getTerminatorNames() : "Нет терминаторов";
            case "istarget": return p == null ? "N/A" : terminatorManager.isTerminator(p.getUniqueId()) ? "Да" : "Нет";
            case "nearest":
                if (p == null) return "N/A";
                Player n = terminatorManager.getNearestTerminator(p);
                return n != null ? n.getName() : "Нет";
            case "nearest_distance":
                if (p == null) return "N/A";
                Player nt = terminatorManager.getNearestTerminator(p);
                return nt == null ? "∞" : String.valueOf((int) p.getLocation().distance(nt.getLocation()));
            case "online_count": return String.valueOf(terminatorManager.getOnlineTerminators().size());
            default: return null;
        }
    }
}
