package gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import utilities.BasicFunctions;
import utilities.EventManager;

/**
 *
 * @author joern
 */
public class Page {

    private static final LinkedList<Page> INSTANCES = new LinkedList<>();
    private static boolean eventRegisterd = false;

    private final String name;
    private final ClickableItem[] items;
    private LinkedList<ClickableItem> tmpItems = new LinkedList<>(); //dont i need to add UUID for each Player??
    private Map<UUID, Object[]> information = new HashMap<>(); //like the Player thePage is about..
    private final UUID uniqueID;

    public Object[] getInformationFor(UUID playerUUID) {
        return information.get(playerUUID);
    }
    
    private static boolean existsUUID(UUID uuid) {
        return (INSTANCES.stream().anyMatch((p) -> (p.uniqueID == uuid)));
    }

    private static boolean existsName(String name) {
        return (INSTANCES.stream().anyMatch((p) -> (p.name == null ? name == null : p.name.equals("§r" + name))));
    }
    
    public void destroy() {
        INSTANCES.remove(this);
    }
    
    /**
     * List info about all registerd instances.
     */
    public static void debug() {
        INSTANCES.forEach((p) -> {
            System.out.println(p.name + p.uniqueID + p.toString());
        });
    }
    
    /**
     * Can Return null if Name was not found.
     *
     * @param name
     * @return
     */
    public static Page getInstance(String name) {
        for (Page p : INSTANCES) {
            if ((p.name).equals("§r" + name)) {
                return p;
            }
        }
        return null;
    }
    
    public static Page getInstanceWithNothing() {
        for (Page p : INSTANCES) {
            if (p.name.equals("")) {
                return p;
            }
        }
        return null;
    }
    
    public static Page getInstanceWithInventoryName(String invName) {
        for (Page p : INSTANCES) {
            if (p.name.equals(invName)) {
                return p;
            }
        }
        return null;
    }

    private static synchronized void addEventListener() {
        if (eventRegisterd) {
            return;
        } else {
            eventRegisterd = true;
        }
        EventManager.getInstance().addEvent(InventoryClickEvent.class, (Listener ll, Event event) -> {
            onInventoryClick((InventoryClickEvent) event);
        });
        EventManager.getInstance().addEvent(InventoryCloseEvent.class, (Listener ll, Event event) -> {
            onInventoryClose((InventoryCloseEvent) event);
        });
    }
    
    private ClickableItem getItemById(ItemStack is) {
        for (ClickableItem i : items) {
            if (BasicFunctions.compareKey(i.getItem(), is)) {
                return i;
            }
        }
        for (ClickableItem i : tmpItems) {
            if (BasicFunctions.compareKey(i.getItem(), is)) {
                return i;
            }
        }
        return null;
    }

    //@SuppressWarnings("LeakingThisInConstructor")
    public Page(String name, ClickableItem[] items) {
        addEventListener();
        if (existsName(name)) {
            //throw new IllegalCallerException("The name is already in use. It is used to Identify the Inventory");
            Bukkit.getLogger().log(Level.OFF, "The name is already in use. It is used to Identify the Inventory. The name is: {0}", name);
            this.name = null;
            this.items = null;
            this.uniqueID = null;
            return;
        }
        this.name = "§r" + name;
        this.items = items;
        UUID tmp;
        do {
            tmp = UUID.randomUUID();
        } while (existsUUID(tmp));
        this.uniqueID = tmp;
        INSTANCES.add(this); //important that it is added AFTER the uuid is generated.*/
    }

    public boolean addClickListener(ClickableItem ci) {
        return tmpItems.add(ci);
    }
    
    public boolean removeClickListener(ClickableItem ci) {
        return tmpItems.remove(ci);
    }
    
    private static void onInventoryClick(InventoryClickEvent e) {
        if (!((e.getWhoClicked() instanceof Player) && e.getWhoClicked().isOp())) {
            return;
        }
        Page p = getInstanceWithInventoryName(e.getView().getTitle());
        if (p == null) {
            return;
        }
        ClickableItem ci = p.getItemById(e.getCurrentItem());
        if (ci == null) {
            return;
        }
        Object[] infos = p.information.get(e.getWhoClicked().getUniqueId());
        if (infos == null) {
            infos = new Object[] {};
        }
        
        e.setCancelled(true);
        ci.click((Player) e.getWhoClicked(), e.isShiftClick(), infos);
        ci.click((Player) e.getWhoClicked(), e.isShiftClick());
        if (e.isLeftClick()) {
            ci.leftClick((Player) e.getWhoClicked(), e.isShiftClick(), infos);
            ci.leftClick((Player) e.getWhoClicked(), e.isShiftClick());
        }
        if (e.isRightClick()) {
            ci.rightClick((Player) e.getWhoClicked(), e.isShiftClick(), infos);
            ci.rightClick((Player) e.getWhoClicked(), e.isShiftClick());
        }
    }
    
    public static void onInventoryClose(InventoryCloseEvent e) {
        //Page p = Page.getInstanceWithNothing();//cannot use name because its propably changed.
        //p.tmpItems
    }
    
    /**
     * The Admin check is included.
     *
     * @param p the Player who wants to see the Inventory
     */
    public void openPage(Player p) {
        if (!p.isOp()) {
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 9 * BasicFunctions.getNeededRows(items.length), name);
        p.openInventory(inv);
        
        int slotCounter = 0;
        for (ClickableItem ci : items) { //can not use functional operator because we need to set the counter.
            ci.setSlot(slotCounter);
            inv.setItem(slotCounter, ci.getItem());
            ci.onLoad(p);
            slotCounter += 1;
        }
    }
    
    /**
    * Change the Rows on Load:
    * PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(player.activeContainer.windowId, BasicFunctions.getContainerType(BasicFunctions.getNeededRows(itemCount), new ChatMessage(p.getOpenInventory().getTitle()));
    * ep.playerConnection.sendPacket(packet);
    * ep.updateInventory(ep.activeContainer);
    */
    public void openPage(Player p, Integer rows) {
        if (!p.isOp()) {
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 9 * rows, name);
        p.openInventory(inv);
        
        int slotCounter = 0;
        for (ClickableItem ci : items) { //can not use functional operator because we need to set the counter.
            ci.setSlot(slotCounter);
            inv.setItem(slotCounter, ci.getItem());
            ci.onLoad(p);
            slotCounter += 1;
        }
    }
    
    /**
     * The Admin check is included.
     *
     * @param p the Player who wants to see the Inventory
     * @param information information that was selected in the inventory before..
     */
    public void openPage(Player p, Object[] information) {
        if (!p.isOp()) {
            return;
        }
        
        this.information.put(p.getUniqueId(), information);
        Inventory inv = Bukkit.createInventory(null, 9 * BasicFunctions.getNeededRows(items.length), name);
        p.openInventory(inv);
        
        int slotCounter = 0;
        for (ClickableItem ci : items) { //can not use functional operator because we need to set the counter.
            ci.setSlot(slotCounter);
            inv.setItem(slotCounter, ci.getItem());
            ci.onLoad(p);
            ci.onLoad(p, information);
            slotCounter += 1;
        }
    }
}
