package minecraftbut;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import utilities.EventManager;

/**
 *
 * @author joern
 */
public class MyTimer {

    private static boolean isRunning = false;
    private static Integer SECOUNDSLEFT = null;
    private static Integer TASKPID = null;

    private static void setText(String str) {
        Bukkit.getOnlinePlayers().forEach((p) -> {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(str));
        });
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static void startTimer() {
        if (SECOUNDSLEFT == null || SECOUNDSLEFT == 0) {
            SECOUNDSLEFT = 0;
            createTimer();
        } else {
            resumeTimer();
        }
    }

    private static void createTimer() {
        EventManager.getInstance().addEvent(PlayerDeathEvent.class, (Listener ll, Event event) -> {
            if (event instanceof PlayerDeathEvent) {
                CommandManager.getInstance().commandReset();
                String msg = "§c" + ((PlayerDeathEvent) event).getEntity().getName() + " ist Gestorben...";
                Bukkit.getOnlinePlayers().forEach((p) -> {
                    p.sendMessage(msg);
                    setText(msg);
                    p.setGameMode(GameMode.SPECTATOR);
                });
            }
        });
        isRunning = true;
        TASKPID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MinecraftBut.getInstance(), () -> {
            if (isRunning == true) {
                SECOUNDSLEFT++;
            }
            int minutes = (int) SECOUNDSLEFT / 60;
            int secounds = SECOUNDSLEFT % 60;
            if (secounds < 10) {
                setText("§a" + minutes + ":0" + secounds);
            } else {
                setText("§a" + minutes + ":" + secounds);
            }
        }, 0, 20);
    }

    private static void resumeTimer() {
        isRunning = true;
    }

    public static void removeTimer() {
        isRunning = false;
        SECOUNDSLEFT = null;
        if (TASKPID != null) {
            Bukkit.getScheduler().cancelTask(TASKPID);
        }
        TASKPID = null;
        setText("");
    }

    public static void pauseTimer() {
        isRunning = false;
    }
}
