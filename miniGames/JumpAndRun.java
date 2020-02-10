package miniGames;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import utilities.EventManager;
import utilities.MiniGame;
import utilities.PlayerState;

/**
 *
 * @author joern
 */
public class JumpAndRun extends MiniGame {

    private static final Location SPAWN = new Location(getWorld(), 296.5, 11.5, -6.5, -90, 10);
    private static final Map<UUID, Location> LAST_CHECKPOINT = new HashMap<>();
    private static final Map<UUID, PlayerState> PLAYER_STATES = new HashMap<>();
    
    private static int myCounter;
    private static int taskId;
    private static boolean running = false;
    
    @Override
    public void registerEvents() {
        EventManager.getInstance().addEvent(EntityDamageEvent.class, (ll, event) -> {
            if (!running) {
                return;
            }
            EntityDamageEvent e = (EntityDamageEvent) event;
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID && e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                if (LAST_CHECKPOINT.containsKey(p.getUniqueId())) {
                    p.teleport(LAST_CHECKPOINT.get(p.getUniqueId()));
                    e.setCancelled(true);
                }
            }
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL && e.getEntity() instanceof Player) {
                e.setCancelled(true);
            }
        });
        EventManager.getInstance().addEvent(BlockBreakEvent.class, (ll, event) -> {
            BlockBreakEvent e = (BlockBreakEvent) event;
            if (running) {
                e.setCancelled(true);
            }
        });
        EventManager.getInstance().addEvent(PlayerInteractEvent.class, (ll, event) -> {
            if (!running) {
                return;
            }
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                Location checkpoint = e.getClickedBlock().getLocation();
                checkpoint.setX(checkpoint.getX() + 0.5);
                checkpoint.setY(checkpoint.getY() + 0.5);
                checkpoint.setZ(checkpoint.getZ() + 0.5);
                if (LAST_CHECKPOINT.get(e.getPlayer().getUniqueId()).equals(checkpoint)) {
                    return;
                }
                LAST_CHECKPOINT.put(e.getPlayer().getUniqueId(), checkpoint);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
            }
            if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                Player p = e.getPlayer();
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
                System.out.println(e.getPlayer().getDisplayName() + " finished...");
                p.setGameMode(GameMode.SPECTATOR);
            }
        });
    }

    @Override
    public void returnPlayer() {
        running = false;
        LAST_CHECKPOINT.clear();
        placeGlass();
        PLAYER_STATES.keySet().forEach((uuid) -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                PLAYER_STATES.get(uuid).load(p);
            }
        });
        PLAYER_STATES.clear();
    }
    
    private void placeGlass() {
        (new Location(getWorld(), 296, 11, -6)).getBlock().setType(Material.GLASS);
        (new Location(getWorld(), 296, 12, -6)).getBlock().setType(Material.GLASS);
        
        (new Location(getWorld(), 296, 11, -8)).getBlock().setType(Material.GLASS);
        (new Location(getWorld(), 296, 12, -8)).getBlock().setType(Material.GLASS);
        
        (new Location(getWorld(), 297, 11, -7)).getBlock().setType(Material.GLASS);
        (new Location(getWorld(), 297, 12, -7)).getBlock().setType(Material.GLASS);
        
        (new Location(getWorld(), 295, 11, -7)).getBlock().setType(Material.GLASS);
        (new Location(getWorld(), 295, 12, -7)).getBlock().setType(Material.GLASS);
    }
    
    private void removeGlass() {
        (new Location(getWorld(), 296, 11, -6)).getBlock().setType(Material.AIR);
        (new Location(getWorld(), 296, 12, -6)).getBlock().setType(Material.AIR);
        
        (new Location(getWorld(), 296, 11, -8)).getBlock().setType(Material.AIR);
        (new Location(getWorld(), 296, 12, -8)).getBlock().setType(Material.AIR);
        
        (new Location(getWorld(), 297, 11, -7)).getBlock().setType(Material.AIR);
        (new Location(getWorld(), 297, 12, -7)).getBlock().setType(Material.AIR);
        
        (new Location(getWorld(), 295, 11, -7)).getBlock().setType(Material.AIR);
        (new Location(getWorld(), 295, 12, -7)).getBlock().setType(Material.AIR);
    }
    
    private void startGlassRemoveCountdown() {
        BossBar bb = Bukkit.createBossBar("Gleich gehts los..", BarColor.GREEN, BarStyle.SOLID);
        bb.setProgress(0);
        bb.setVisible(true);
        Bukkit.getOnlinePlayers().forEach((p) -> {
            bb.addPlayer(p);
        });
        myCounter = 0;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(minecraftbut.MinecraftBut.getInstance(), () -> {
            if (myCounter == 51) {
                removeGlass();
                bb.removeAll();
                Bukkit.getScheduler().cancelTask(taskId);
                return;
            }
            double bossBarHealth = myCounter;
            bb.setProgress(bossBarHealth / 50);
            myCounter++;
        }, 0, 2);
    }

    @Override
    public void teleportPlayer() {
        running = true;
        Bukkit.getOnlinePlayers().forEach((p) -> {
            PLAYER_STATES.put(p.getUniqueId(), new PlayerState(p));
            LAST_CHECKPOINT.put(p.getUniqueId(), SPAWN);
            p.teleport(SPAWN);
        });
        startGlassRemoveCountdown();
    }
}
