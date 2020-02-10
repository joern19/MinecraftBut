package utilities;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class EventManager implements Listener {
    
    private static JavaPlugin plugin;
    private static EventManager instance;
    
    /*
     * @param main the Class where you wrote "extends JavaPlugin"
     */
    public EventManager(JavaPlugin main) {
        if (instance != null) {
            Bukkit.getLogger().log(Level.WARNING, "You called the Constructor of EventManager at least twice. This should not happen.. Please remove the unnecessary Callers.");
            return;
        }
        plugin = main;
        instance = this;
    }
    
    /**
     * @return WARNING can return null if constructor was never called.
     */
    public static EventManager getInstance() {
        return instance;
    }
    
    public void addEvent(Class<? extends Event> eventType, EventExecutor ee) {
        Bukkit.getServer().getPluginManager().registerEvent(eventType, this, EventPriority.NORMAL, ee, plugin);
    }
}
