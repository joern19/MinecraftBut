package gui;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ClickableItem extends ClickEvents {

    private static Integer lastID = 0; //the unique id for this item...
    private static final NamespacedKey ID_KEY = new NamespacedKey(minecraftbut.MinecraftBut.getInstance(), "guiID");
    
    private final ItemStack item;
    private Integer slot = null;
    
    
    public ClickableItem(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        im.getPersistentDataContainer().set(ID_KEY, PersistentDataType.INTEGER, (lastID += 1));
        item.setItemMeta(im);
        
        this.item = item;
    }

    public ClickableItem(Material m, String name, String lore) {
        ItemStack is = new ItemStack(m);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        ArrayList<String> list = new ArrayList<>();
        list.add(lore);
        im.setLore(list);
        im.getPersistentDataContainer().set(ID_KEY, PersistentDataType.INTEGER, (lastID += 1));
        is.setItemMeta(im);

        item = is;
    }

    public ClickableItem(Material m, String name) {
        ItemStack is = new ItemStack(m);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        im.getPersistentDataContainer().set(ID_KEY, PersistentDataType.INTEGER, (lastID += 1));
        is.setItemMeta(im);

        item = is;
    }

    public ClickableItem(Material m, String name, String... lore) {
        ItemStack is = new ItemStack(m);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        im.getPersistentDataContainer().set(ID_KEY, PersistentDataType.INTEGER, (lastID += 1));
        is.setItemMeta(im);

        item = is;
    }

    public ClickableItem(Material m, ItemMeta im) {
        ItemStack is = new ItemStack(m);
        im.getPersistentDataContainer().set(ID_KEY, PersistentDataType.INTEGER, (lastID += 1));
        is.setItemMeta(im);

        item = is;
    }

    public final void setSlot(Integer slot) {
        this.slot = slot;
    }

    public final Integer getSlot() {
        return this.slot;
    }

    public final ItemStack getItem() {
        return item;
    }
    
    public static final NamespacedKey getIdKey() {
        return ID_KEY;
    }
    
    @Deprecated
    public final ItemStack getTmpEditableItem(Player p) {
        return p.getOpenInventory().getItem(this.getSlot());
    }
    
}
