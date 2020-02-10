package games;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import utilities.EventManager;
import utilities.Game;

public class DrinkInsteadOfEat extends Game {

    int hungerLevel = 1;

    @Override
    public void start() {
        EventManager.getInstance().addEvent(PlayerItemConsumeEvent.class, (Listener ll, Event event) -> {
            if (!stopped) {
                onPlayerItemConsume((PlayerItemConsumeEvent) event);
            }
        });
        //PotionEffect pe = new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, saturationLevel, false, false, false);
        PotionEffect pe = new PotionEffect(PotionEffectType.HUNGER, 99999, hungerLevel, false, false, false);
        Bukkit.getOnlinePlayers().forEach((p) -> {
            p.addPotionEffect(pe);
            System.out.println(p.getName());
        });
        stopped = false;
    }

    @Override
    public void stop() {
        Bukkit.getOnlinePlayers().forEach((p) -> {
            p.removePotionEffect(PotionEffectType.HUNGER);
        });
        stopped = true;
    }

    private void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.POTION && e.getItem().hasItemMeta()) {
            PotionMeta pm = (PotionMeta) e.getItem().getItemMeta();
            if (pm.getBasePotionData().getType() == PotionType.WATER) {
                Player p = e.getPlayer();
                p.setFoodLevel(20);
            }
        } else {
            e.setCancelled(true);
        }
    }
}
