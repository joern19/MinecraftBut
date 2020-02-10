package games;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import utilities.EventManager;
import utilities.Game;

/**
 *
 * @author joern
 */
public class Veggi extends Game {

    Material[] notAllowFood = new Material[]{
        Material.COOKED_MUTTON, Material.COOKED_PORKCHOP, Material.COOKED_SALMON, Material.SPIDER_EYE, Material.COOKED_BEEF,
        Material.COOKED_CHICKEN, Material.COOKED_COD, Material.COOKED_RABBIT, Material.RABBIT_STEW,
        Material.BEEF, Material.CHICKEN, Material.MUTTON, Material.PORKCHOP, Material.RABBIT,
        Material.PUFFERFISH, Material.COD, Material.SALMON, Material.ROTTEN_FLESH, Material.TROPICAL_FISH
    };

    public static int instandDamage = 4; // 2 heart
    public static int secoundsPotiond = 10;

    @Override
    public void start() {
        if (!eventsRegisterd) {
            EventManager.getInstance().addEvent(PlayerItemConsumeEvent.class, (Listener ll, Event event) -> {
                if (!stopped) {
                    onPlayerItemConsume((PlayerItemConsumeEvent) event);
                }
            });
            eventsRegisterd = true;
        }
        stopped = false;
    }

    @Override
    public void stop() {
        stopped = true;
    }

    private void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if (Arrays.stream(notAllowFood).anyMatch(e.getItem().getType()::equals)) {
            e.getPlayer().damage(instandDamage);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, secoundsPotiond * 20, 1));
        }
    }

}
