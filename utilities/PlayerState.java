package utilities;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author joern
 */
public class PlayerState {

    private final Location loc;
    private final ItemStack[] items;
    private final ItemStack[] armor;
    private final ItemStack offHand;
    private final GameMode gm;
    private final double health;
    private final int food;

    public PlayerState(Player p) {
        this.loc = p.getLocation().clone();
        this.gm = p.getGameMode();
        this.health = p.getHealth();
        this.food = p.getFoodLevel();

        items = p.getInventory().getContents();
        armor = p.getInventory().getArmorContents();

        p.getInventory().clear();

        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.getInventory().setItemInOffHand(null);

        offHand = p.getInventory().getItemInOffHand();
        p.getInventory().setItemInOffHand(null);
    }

    public void load(Player p) {
        if (p == null) {
            return;
        }
        p.teleport(loc);
        if (items != null) {
            p.getInventory().setContents(items);
        } else {//if the player has no inventory contents, clear their inventory
            p.getInventory().clear();
        }
        if (armor != null) {
            p.getInventory().setArmorContents(armor);
        } else {//if the player has no armor, set the armor to null
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);
        }
        if (offHand != null) {
            p.getInventory().setItemInOffHand(offHand);
        } else {
            p.getInventory().setItemInOffHand(null);
        }
        p.setGameMode(gm);
        p.setHealth(health);
        p.setFoodLevel(food);
    }
}
