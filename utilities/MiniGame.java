package utilities;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 *
 * @author joern
 */
public abstract class MiniGame {

    private static final String WORLD_NAME = "miniGames";

    protected static World getWorld() {
        for (World w : Bukkit.getWorlds()) {
            if (w.getName().equals(WORLD_NAME)) {
                return w; //world already loaded
            }
        }
        Bukkit.getServer().createWorld(new WorldCreator(WORLD_NAME));
        return Bukkit.getWorld(WORLD_NAME);
    }

    //private final HashMap<UUID, Location> oldLocs = new HashMap<>();
    /*public void tpPlayer() {
        Location[] locs = getLocations();
        if (locs.length == 0) {
            Bukkit.getLogger().log(Level.OFF, "getLocations returnd no locations.");
        }
        int counter = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (counter > locs.length - 1) {
                counter = 0;
            }
            oldLocs.put(p.getUniqueId(), p.getLocation());
            p.teleport(locs[counter]);
            counter++;
        }
    }*/
 /*public void tpPlayerBack() {
        oldLocs.keySet().forEach((uuid) -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.teleport(oldLocs.get(uuid));
            }
        });
        oldLocs.clear();
    }*/
    public abstract void registerEvents();

    public abstract void returnPlayer();

    public abstract void teleportPlayer();
}
