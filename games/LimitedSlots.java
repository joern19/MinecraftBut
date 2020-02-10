package games;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import utilities.EventManager;
import utilities.Game;

/**
 *
 * @author joern
 */
public class LimitedSlots extends Game {

    public static int slotsInHotbar = 9;
    public static int slotsInInventory = 0;
    public static boolean offHandAllowed = false;

    public static boolean helmetAllowed = false;
    public static boolean chestplateAllowed = true;
    public static boolean leggingsAllowed = false;
    public static boolean bootsAllowed = false;

    private static NamespacedKey nk = null;

    private ItemStack generateBlockSlotItem() {
        ItemStack is = new ItemStack(Material.BARRIER);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName("§r§kraklsjklafg");
        im.addEnchant(Enchantment.DURABILITY, 0, true);
        im.setLore(new ArrayList<>());
        im.getPersistentDataContainer().set(nk, PersistentDataType.INTEGER, 1);
        is.setItemMeta(im);
        return is;
    }

    private boolean isBlockItem(ItemStack is) {
        return is != null && is.hasItemMeta() && is.getItemMeta().getPersistentDataContainer().has(nk, PersistentDataType.INTEGER) && is.getItemMeta().getPersistentDataContainer().get(nk, PersistentDataType.INTEGER) == 1;
    }

    public void setup(int slotsInHotbar, int slotsInInventory, boolean offHandAllowed, boolean helmetAllowed, boolean chestplateAllowed, boolean leggingsAllowed, boolean bootsAllowed) {
        LimitedSlots.slotsInHotbar = slotsInHotbar;
        LimitedSlots.slotsInInventory = slotsInInventory;
        LimitedSlots.offHandAllowed = offHandAllowed;
        LimitedSlots.helmetAllowed = helmetAllowed;
        LimitedSlots.chestplateAllowed = chestplateAllowed;
        LimitedSlots.leggingsAllowed = leggingsAllowed;
        LimitedSlots.bootsAllowed = bootsAllowed;
    }

    @Override
    public void start() {
        if (nk == null) {
            nk = new NamespacedKey(minecraftbut.MinecraftBut.getInstance(), "unMovable");
        }
        if (!eventsRegisterd) {
            EventManager.getInstance().addEvent(PlayerDropItemEvent.class, (Listener ll, Event event) -> {
                if (!stopped) {
                    onPlayerDropItem((PlayerDropItemEvent) event);
                }
            });
            EventManager.getInstance().addEvent(BlockPlaceEvent.class, (Listener ll, Event event) -> {
                if (!stopped) {
                    onBlockPlace((BlockPlaceEvent) event);
                }
            });
            EventManager.getInstance().addEvent(InventoryClickEvent.class, (Listener ll, Event event) -> {
                if (!stopped) {
                    onInventoryClick((InventoryClickEvent) event);
                }
            });
            EventManager.getInstance().addEvent(PlayerSwapHandItemsEvent.class, (Listener ll, Event event) -> {
                if (!offHandAllowed) {
                    ((PlayerSwapHandItemsEvent) event).setCancelled(!stopped);
                }
            });
            eventsRegisterd = true;
        }
        Bukkit.getOnlinePlayers().forEach((p) -> {
            for (int i = 8; i >= slotsInHotbar; i--) {
                p.getInventory().setItem(i, generateBlockSlotItem());
            }
            for (int i = 9; i <= 35 - slotsInInventory; i++) {
                p.getInventory().setItem(i, generateBlockSlotItem());
            }
            if (!helmetAllowed) {
                p.getInventory().setItem(39, generateBlockSlotItem());
            }
            if (!chestplateAllowed) {
                p.getInventory().setItem(38, generateBlockSlotItem());
            }
            if (!leggingsAllowed) {
                p.getInventory().setItem(37, generateBlockSlotItem());
            }
            if (!bootsAllowed) {
                p.getInventory().setItem(36, generateBlockSlotItem());
            }
        });
        stopped = false;
    }

    @Override
    public void stop() {
        System.out.println(Bukkit.getOnlinePlayers().size());
        Bukkit.getOnlinePlayers().forEach((p) -> {
            for (ItemStack is : p.getInventory()) {
                if (isBlockItem(is)) {
                    p.getInventory().remove(is);
                }
            }
            if (isBlockItem(p.getInventory().getHelmet())) {
                p.getInventory().setHelmet(null);
            }
            if (isBlockItem(p.getInventory().getChestplate())) {
                p.getInventory().setChestplate(null);
            }
            if (isBlockItem(p.getInventory().getLeggings())) {
                p.getInventory().setLeggings(null);
            }
            if (isBlockItem(p.getInventory().getBoots())) {
                p.getInventory().setBoots(null);
            }
        });
        stopped = true;
    }

    private void onPlayerDropItem(PlayerDropItemEvent e) {
        ItemStack is = e.getItemDrop().getItemStack();
        if (isBlockItem(is)) {
            e.setCancelled(true);
        }
    }

    private void onInventoryClick(InventoryClickEvent e) {
        if (isBlockItem(e.getCurrentItem())) {
            e.setCancelled(true);
        }
        if (!offHandAllowed && e.getSlot() == 40) {
            e.setCancelled(true);
        }
    }

    private void onBlockPlace(BlockPlaceEvent e) {
        if (isBlockItem(e.getItemInHand())) {
            e.setCancelled(true);
        }
    }

}
