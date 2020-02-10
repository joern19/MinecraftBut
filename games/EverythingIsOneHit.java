package games;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import utilities.EventManager;
import utilities.Game;

/**
 *
 * @author joern
 */
public class EverythingIsOneHit extends Game {

    @Override
    public void start() {
        if (!eventsRegisterd) {
            EventManager.getInstance().addEvent(BlockDamageEvent.class, (Listener ll, Event event) -> {
                if (!stopped) {
                    onBlockDamage((BlockDamageEvent) event);
                }
            });
            EventManager.getInstance().addEvent(EntityDamageEvent.class, (Listener ll, Event event) -> {
                if (!stopped) {
                    onEntityDamage((EntityDamageEvent) event);
                }
            });
            eventsRegisterd = true;
        }
        Bukkit.getOnlinePlayers().forEach((p) -> {
            p.setHealth(1); //because this makes some damage...
            p.setHealthScale(1);
        });
        stopped = false; //has to be called AFTER I nearly kill him...
    }

    @Override
    public void stop() {
        stopped = true;
        Bukkit.getOnlinePlayers().forEach((p) -> {
            p.setHealthScale(20);
        });
    }

    private void onBlockDamage(BlockDamageEvent e) {
        e.setInstaBreak(true);
    }

    private void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Damageable) {
            ((Damageable) e.getEntity()).setHealth(0);
        }
    }
}
