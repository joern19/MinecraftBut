package games;

import java.util.Random;
import minecraftbut.MinecraftBut;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import utilities.Game;

/**
 *
 * @author joern
 */
public class RandomEffect extends Game {

    public static int applyEverySecoundsMin = 60;
    public static int applyEverySecoundsMax = 90;
    public static int effectLevel = 1;
    public static int effectLength = 30;
    private int taskPid = 0;
    public static boolean everyPlayerDiffrentEffect = false;
    public static PotionEffectType[] allowedEffects = PotionEffectType.values();

    private int generateWaitTime() {
        if (applyEverySecoundsMax == applyEverySecoundsMin) {
            return applyEverySecoundsMax * 20;
        }
        Random r = new Random();
        int res = r.nextInt(applyEverySecoundsMax - applyEverySecoundsMin) + applyEverySecoundsMin;
        res *= 20;
        return res;
    }

    private PotionEffect generateEffect() {
        if (allowedEffects.length == 1) {
            return new PotionEffect(allowedEffects[0], effectLength * 20, effectLevel - 1);
        }
        Random r = new Random();
        int index = r.nextInt(allowedEffects.length);
        return new PotionEffect(allowedEffects[index], effectLength * 20, effectLevel - 1);
    }

    private void generateTheDelayedTask() {
        taskPid = Bukkit.getScheduler().scheduleSyncDelayedTask(MinecraftBut.getInstance(), () -> {
            if (allowedEffects.length == 0) {
                return;
            }
            PotionEffect pe = generateEffect();

            Bukkit.getOnlinePlayers().forEach((p) -> {
                if (!everyPlayerDiffrentEffect) {
                    p.addPotionEffect(pe);
                } else {
                    p.addPotionEffect(generateEffect());
                }
            });
            generateTheDelayedTask();
        }, generateWaitTime());
    }

    @Override
    public void start() {
        generateTheDelayedTask();
        stopped = false;
    }

    @Override
    public void stop() {
        if (taskPid != 0) {
            Bukkit.getScheduler().cancelTask(taskPid);
            taskPid = 0;
        }
        Bukkit.getOnlinePlayers().forEach((p) -> {
            p.getActivePotionEffects().forEach((pe) -> {
                p.removePotionEffect(pe.getType());
            });
        });
        stopped = true;
    }
}
