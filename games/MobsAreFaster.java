package games;

import minecraftbut.MinecraftBut;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import utilities.EventManager;
import utilities.Game;

public class MobsAreFaster extends Game {

    public static Integer SPEED_LEVEL = 5; //max = 25

    @Override
    public void start() {
        stopped = false;
        if (!eventsRegisterd) {
            EventManager.getInstance().addEvent(EntitySpawnEvent.class, (Listener ll, Event event) -> {
                if (!stopped) {
                    onEntitySpawn((EntitySpawnEvent) event);
                }
            });
            eventsRegisterd = true;
        }
        World w = Bukkit.getWorlds().get(0);
        w.getEntities().forEach((e) -> {
            addSpeed(e);
        });
        w.setTime(0);
        w.setStorm(false);
        w.setThundering(false);
    }

    @Override
    public void stop() {
        Bukkit.getWorlds().get(0).getEntities().forEach((e) -> {
            if (e instanceof LivingEntity) {
                ((LivingEntity) e).removePotionEffect(PotionEffectType.SPEED);
            }
        });
        stopped = true;
    }

    private void onEntitySpawn(EntitySpawnEvent e) {
        addSpeed(e.getEntity());
    }

    private static void addSpeed(Entity e) {
        if (e.getType() != EntityType.ARROW) {
            e.setVelocity(new Vector(0, 0.5, 0));
        }
        if (e.getType() == EntityType.PLAYER || !(e instanceof LivingEntity)) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(MinecraftBut.getInstance(), () -> {
            ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, SPEED_LEVEL));
        }, 20);
    }
}
