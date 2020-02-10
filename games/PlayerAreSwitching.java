package games;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import minecraftbut.MinecraftBut;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import utilities.Game;

public class PlayerAreSwitching extends Game {

    public static int switchEveryXSecoundsMin = 30;
    public static int switchEveryXSecoundsMax = 60;
    
    private int taskPid = 0;

    private int generateWaitTime() {
        return ((new Random()).nextInt(switchEveryXSecoundsMax - switchEveryXSecoundsMin) + switchEveryXSecoundsMin) * 20;
    }
    
    private void generateTheDelayedTask() {
        taskPid = Bukkit.getScheduler().scheduleSyncDelayedTask(MinecraftBut.getInstance(), () -> {
            ArrayList<Player> orderd = new ArrayList<>(Bukkit.getOnlinePlayers());
            ArrayList<Location> orderdLocations = new ArrayList();
            orderd.forEach((p) -> {
                orderdLocations.add(p.getLocation());
            });
            ArrayList<Player> shuffeld = (ArrayList<Player>) orderd.clone();
            shuffeld.add(shuffeld.remove(0)); //just put the first element at the end.
            
            for (int i = 0; i < orderd.size(); i++) {
                Player from = shuffeld.get(i);
                Location to = orderdLocations.get(i);
                from.teleport(to);
            }
            Bukkit.getLogger().log(Level.INFO, "All Player Teleported.");
            generateTheDelayedTask();
        }, generateWaitTime());
    }
    
    @Override
    public void start() {
        generateTheDelayedTask();
        stopped = false;
    }

    @Override
    public void stop() {
        if (taskPid != 0) {
            Bukkit.getScheduler().cancelTask(taskPid);
            taskPid = 0;
        }
        stopped = true;
    }
}
