package miniGames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import minecraftbut.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import utilities.EventManager;
import utilities.MiniGame;
import utilities.PlayerState;

public class Replica extends MiniGame {

    public static int countdownInSecs = 90; //this is editable

    private static final Map<UUID, PlayerState> PLAYER_STATES = new HashMap<>();
    private static final Map<UUID, Integer> ROOM_NUMBER = new HashMap<>();
    private static boolean running = false;
    private static int currentBuildId = 0;
    private static final int heightOfFirstCompareLayer = 37;
    private static final ArrayList<UUID> PLAYER_DONE = new ArrayList<>();

    private static int myCounter = 1;
    private static int taskId;
    private static BossBar countDownBar;

    private void startCountdown() {
        countDownBar = Bukkit.createBossBar("Zeit überig", BarColor.BLUE, BarStyle.SOLID);
        countDownBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach((p) -> {
            countDownBar.addPlayer(p);
        });
        myCounter = 0;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(minecraftbut.MinecraftBut.getInstance(), () -> {
            if (myCounter == countdownInSecs + 1) {
                returnPlayer();
                countDownBar.removeAll();
                ArrayList<String> loosers = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach((p) -> {
                    if (!PLAYER_DONE.contains(p.getUniqueId())) {
                        loosers.add(p.getDisplayName());
                    }
                });
                String str = "§cDiese Spieler haben es nicht geschafft: ";
                Bukkit.getOnlinePlayers().forEach((p) -> {
                    p.sendMessage(str);
                    loosers.forEach((s) -> {
                        p.sendMessage(s);
                    });
                    p.setGameMode(GameMode.SPECTATOR);
                });

                CommandManager.getInstance().commandReset();

                Bukkit.getScheduler().cancelTask(taskId);
                return;
            }
            double bossBarHealth = myCounter;
            countDownBar.setProgress(bossBarHealth / countdownInSecs);
            myCounter++;
        }, 0, 20);
    }

    private void startReturnCountdown() {
        BossBar bb = Bukkit.createBossBar("Kurz warten...", BarColor.GREEN, BarStyle.SEGMENTED_10);
        bb.setProgress(0);
        bb.setVisible(true);
        Bukkit.getOnlinePlayers().forEach((p) -> {
            bb.addPlayer(p);
        });
        myCounter = 0;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(minecraftbut.MinecraftBut.getInstance(), () -> {
            if (myCounter == 31) {
                returnPlayer();
                bb.removeAll();
                Bukkit.getScheduler().cancelTask(taskId);
                return;
            }
            double bossBarHealth = myCounter;
            bb.setProgress(bossBarHealth / 30);
            myCounter++;
        }, 0, 2);
    }

    private static Material[] copyBuild(int Zeroto37) {
        currentBuildId = Zeroto37;
        ArrayList<Material> differentMaterials = new ArrayList<>();

        Location pos1 = new Location(getWorld(), 9, 5, 16);
        Location pos2 = new Location(getWorld(), 9, 12, 23);

        for (int i = 1; i < 11; i++) {
            for (int y = pos1.getBlockY(); y <= pos2.getBlockY(); y++) { //y
                for (int z = pos1.getBlockZ(); z <= pos2.getBlockZ(); z++) { //z
                    Material m = new Location(getWorld(), 9 + Zeroto37, y, z).getBlock().getType();
                    if (!differentMaterials.contains(m)) {
                        differentMaterials.add(m);
                    }
                    Location dst = new Location(getWorld(), 9, y, z + i * 16);
                    dst.getBlock().setType(m);
                }
            }
        }
        return differentMaterials.toArray(new Material[differentMaterials.size()]);
    }

    private static boolean isPlayerDone(UUID uuid) {
        int roomNumber = ROOM_NUMBER.get(uuid);
        int minX = -2;
        int maxX = 5;
        int minZ = 16;
        int maxZ = 23;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Material original = (new Location(getWorld(), x, heightOfFirstCompareLayer - currentBuildId, z)).getBlock().getType();
                Material fromUser = (new Location(getWorld(), x, 3, z + (roomNumber + 1) * 16)).getBlock().getType();
                if (original == Material.VOID_AIR || original == Material.AIR) {
                    original = Material.VOID_AIR;
                }
                if (fromUser == Material.VOID_AIR || fromUser == Material.AIR) {
                    fromUser = Material.VOID_AIR;
                }
                if (original != fromUser) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void teleportPlayer() {
        running = true;
        LinkedList<Location> ll = new LinkedList();
        Location startLoc = new Location(getWorld(), 2, 8, 36, -90, 2);
        ll.add(startLoc);
        for (int i = 0; i < 10; i++) {
            Location toAdd = startLoc.clone();
            toAdd.setZ(toAdd.getZ() + (i+1) * 16);
            ll.add(toAdd);
        }
        Collection<? extends Player> onlinePlayer = Bukkit.getOnlinePlayers();
        if (onlinePlayer.size() > 10) {
            try {
                throw new Exception("More Player online than Replica can handle...");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        int indexCounter = 0;
        for (Player p : onlinePlayer) {
            PLAYER_STATES.put(p.getUniqueId(), new PlayerState(p));
            ROOM_NUMBER.put(p.getUniqueId(), indexCounter);
            PLAYER_DONE.clear();
            p.teleport(ll.get(indexCounter));
            p.setGameMode(GameMode.CREATIVE);
            p.setFlying(true);
            indexCounter++;
        }
        countDownBar = Bukkit.createBossBar("Gleich gehts los", BarColor.RED, BarStyle.SOLID);
        countDownBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach((p) -> {
            countDownBar.addPlayer(p);
        });
        myCounter = 0;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(minecraftbut.MinecraftBut.getInstance(), () -> {
            if (myCounter == 20 + 1) {
                countDownBar.removeAll();
                Random r = new Random();
                Material[] materials = copyBuild(r.nextInt(heightOfFirstCompareLayer));
                Bukkit.getOnlinePlayers().forEach((p) -> {
                    for (Material m : materials) {
                        p.getInventory().addItem(new ItemStack(m));
                    }
                });
                Bukkit.getScheduler().scheduleSyncDelayedTask(minecraftbut.MinecraftBut.getInstance(), () -> {
                    startCountdown();
                }, 1);
                Bukkit.getScheduler().cancelTask(taskId);
                return;
            }
            double bossBarHealth = myCounter;
            countDownBar.setProgress(bossBarHealth / 20);
            myCounter++;
        }, 0, 4);
    }

    private boolean playerAllowedToBuild(Player p, Location loc) {
        if (loc.getBlockY() != 3) { //check y
            return false;
        }
        int room_number = ROOM_NUMBER.get(p.getUniqueId());
        Location a = new Location(getWorld(), -2, 3, 32 + 16 * room_number);
        Location b = new Location(getWorld(), 5, 3, 39 + 16 * room_number);
        if (!(loc.getBlockX() <= b.getBlockX() && loc.getBlockX() >= a.getBlockX())) { //check x
            return false;
        }
        return loc.getBlockZ() <= b.getBlockZ() && loc.getBlockZ() >= a.getBlockZ(); //finaly check z
    }

    private static void clearAreas() {
        Location pos1 = new Location(getWorld(), 9, 5, 16);
        Location pos2 = new Location(getWorld(), 9, 12, 23);

        for (int i = 1; i < 11; i++) {
            for (int y = pos1.getBlockY(); y <= pos2.getBlockY(); y++) { //y
                for (int z = pos1.getBlockZ(); z <= pos2.getBlockZ(); z++) { //z
                    Location dst = new Location(getWorld(), pos1.getBlockX(), y, z + i * 16);
                    dst.getBlock().setType(Material.VOID_AIR);
                }
            }
        }

        Location p1 = new Location(getWorld(), -2, 3, 32);
        Location p2 = new Location(getWorld(), 5, 3, 39);

        for (int i = 0; i < 10; i++) {
            for (int x = p1.getBlockX(); x <= p2.getBlockX(); x++) { //y
                for (int z = p1.getBlockZ(); z <= p2.getBlockZ(); z++) { //z
                    Location loc = new Location(getWorld(), x, 3, z);
                    loc.setZ(loc.getBlockZ() + i * 16);
                    loc.getBlock().setType(Material.VOID_AIR);
                }
            }
        }
    }

    @Override
    public void returnPlayer() {
        PLAYER_STATES.keySet().forEach((uuid) -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                PLAYER_STATES.get(uuid).load(p);
            }
        });
        PLAYER_STATES.clear();
        ROOM_NUMBER.clear();
        clearAreas();
        running = false;
        Bukkit.getScheduler().cancelTask(taskId);
    }

    private void onPlayerDone(Player p) {
        Bukkit.getOnlinePlayers().forEach((all) -> {
            all.sendMessage("§a" + p.getDisplayName() + " hat es Geschafft.");
        });
        p.getInventory().clear();
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
        p.setGameMode(GameMode.SPECTATOR);

        int roomNumber = ROOM_NUMBER.get(p.getUniqueId());

        Location p1 = new Location(getWorld(), -2, 3, 32);
        Location p2 = new Location(getWorld(), 5, 3, 39);
        for (int x = p1.getBlockX(); x <= p2.getBlockX(); x++) { //y
            for (int z = p1.getBlockZ(); z <= p2.getBlockZ(); z++) { //z
                Location loc = new Location(getWorld(), x + 0.5, 4, z + 0.5);
                loc.setZ(loc.getZ() + roomNumber * 16);
                getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 1);
            }
        }
        PLAYER_DONE.add(p.getUniqueId());
        if (Bukkit.getOnlinePlayers().size() == PLAYER_DONE.size()) {
            Bukkit.getScheduler().cancelTask(taskId);
            countDownBar.removeAll();
            startReturnCountdown();
        }
    }

    @Override
    public void registerEvents() {
        EventManager.getInstance().addEvent(BlockPlaceEvent.class, (Listener ll, Event event) -> {
            if (!running) {
                return;
            }
            BlockPlaceEvent e = (BlockPlaceEvent) event;
            if (!playerAllowedToBuild(e.getPlayer(), e.getBlock().getLocation())) {
                e.setCancelled(true);
            } else {
                e.getPlayer().getInventory().getItemInMainHand().setType(e.getBlock().getType());
                if (isPlayerDone(e.getPlayer().getUniqueId())) {
                    onPlayerDone(e.getPlayer());
                }
            }
        });
        EventManager.getInstance().addEvent(BlockBreakEvent.class, (ll, event) -> {
            if (!running) {
                return;
            }
            BlockBreakEvent e = (BlockBreakEvent) event;
            if (!playerAllowedToBuild(e.getPlayer(), e.getBlock().getLocation())) {
                e.setCancelled(true);
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(minecraftbut.MinecraftBut.getInstance(), () -> {
                    if (isPlayerDone(e.getPlayer().getUniqueId())) {
                        onPlayerDone(e.getPlayer());
                    }
                }, 1);
            }
        });
        EventManager.getInstance().addEvent(InventoryCreativeEvent.class, (ll, event) -> {
            if (!(event instanceof InventoryCreativeEvent)) {
                return;
            }
            InventoryCreativeEvent e = (InventoryCreativeEvent) event;
            if (running) {
                e.setCancelled(true);
            }
        });
        EventManager.getInstance().addEvent(InventoryClickEvent.class, (ll, event) -> {
            InventoryClickEvent e = (InventoryClickEvent) event;
            if (running) {
                e.setCancelled(true);
            }
        });
        EventManager.getInstance().addEvent(PlayerDropItemEvent.class, (ll, event) -> {
            PlayerDropItemEvent e = (PlayerDropItemEvent) event;
            if (running) {
                e.setCancelled(true);
            }
        });
    }
}
