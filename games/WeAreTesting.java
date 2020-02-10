package games;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.block.BlockBreakEvent;
import utilities.EventManager;
import utilities.Game;

/**
 *
 * @author joern
 */
public class WeAreTesting extends Game {

    private String getDate() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy_HH.mm.ss").format(LocalDateTime.now());
    }

    @Override
    public void start() {
        if (!eventsRegisterd) {
            EventManager.getInstance().addEvent(BlockBreakEvent.class, (ll, event) -> {
                if (!stopped) {
                    BlockBreakEvent e = (BlockBreakEvent) event;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(minecraftbut.MinecraftBut.getInstance(), () -> {
                        WorldCreator wc = new WorldCreator("Map" + getDate());
                        wc.environment(World.Environment.NORMAL);
                        wc.type(WorldType.AMPLIFIED);
                        World w = wc.createWorld();
                        e.getPlayer().teleport(w.getSpawnLocation());
                    }, 1);
                }
            });
            eventsRegisterd = true;
        }
        Bukkit.getOnlinePlayers().forEach((p) -> {
            p.setAllowFlight(true);
            p.setFlying(true);
        });
        Bukkit.getWorlds().get(0).getEntities().forEach((e) -> {
            e.setGlowing(true);
        });
        stopped = false;
    }

    @Override
    public void stop() {
        Bukkit.getOnlinePlayers().forEach((p) -> {
            if (p.getGameMode() == GameMode.SURVIVAL) {
                p.setFlying(false);
                p.setAllowFlight(false);
            }
        });
        stopped = true;
    }
}
