package boo.bloodstone;

import boo.bloodstone.commands.TCompassCommand;
import boo.bloodstone.commands.TerminatorCommand;
import boo.bloodstone.listeners.PlayerDeathListener;
import boo.bloodstone.managers.CompassManager;
import boo.bloodstone.managers.TerminatorManager;
import boo.bloodstone.placeholders.TerminatorPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TerminatorPlugin extends JavaPlugin {
    
    private static TerminatorPlugin instance;
    private TerminatorManager terminatorManager;
    private CompassManager compassManager;
    
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        terminatorManager = new TerminatorManager(this);
        compassManager = new CompassManager(this);
        registerCommands();
        registerListeners();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TerminatorPlaceholder(this).register();
        }
        compassManager.startUpdateTask();
    }
    
    @Override
    public void onDisable() {
        if (compassManager != null) compassManager.stopUpdateTask();
    }
    
    private void registerCommands() {
        TerminatorCommand tCmd = new TerminatorCommand(this);
        getCommand("terminator").setExecutor(tCmd);
        getCommand("terminator").setTabCompleter(tCmd);
        
        TCompassCommand cCmd = new TCompassCommand(this);
        getCommand("tcompass").setExecutor(cCmd);
        getCommand("tcompass").setTabCompleter(cCmd);
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
    }
    
    public static TerminatorPlugin getInstance() {
        return instance;
    }
    
    public TerminatorManager getTerminatorManager() {
        return terminatorManager;
    }
    
    public CompassManager getCompassManager() {
        return compassManager;
    }
}
