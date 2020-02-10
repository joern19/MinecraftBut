package minecraftbut;

import gui.AdminGUI;
import gui.Page;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager {

    private static CommandManager INSTANCE = null;
    private static boolean isStarted = false;

    private CommandManager() {
    }

    public static synchronized CommandManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CommandManager();
        }
        return INSTANCE;
    }

    protected boolean onCommand(CommandSender sender, Command cmd, String[] args) {
        if (cmd.getName().equalsIgnoreCase("start")) {
            commandStart();
            if (!(args.length == 1 && args[0].equalsIgnoreCase("off"))) {
                MyTimer.startTimer();
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("pause")) {
            commandStop();
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("reset")) {
            commandReset();
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("gui")) {
            if (sender instanceof Player) {
                AdminGUI.reload();
                Page.getInstance(AdminGUI.NAME_MAIN).openPage((Player) sender);
            } else {
                sender.sendMessage("§cDas kann nur ein Spieler.");
            }
            return true;
        }
        return false;
    }

    public void commandStart() {
        if (isStarted) {
            Bukkit.getOnlinePlayers().forEach((p) -> {
                p.sendMessage("§cGame has already started.");
            });
            return;
        }
        isStarted = true;
        Bukkit.getOnlinePlayers().forEach((p) -> {
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealthScale(20);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setLevel(0);
            p.setExp(0);
            p.getInventory().clear();
        });
        MinecraftBut.getInstance().activeGames.forEach((g) -> {
            g.start();
        });
    }

    public void commandStop() {
        if (!isStarted) {
            Bukkit.getOnlinePlayers().forEach((p) -> {
                p.sendMessage("§cGame has already stopped.");
            });
            return;
        }
        isStarted = false;
        MinecraftBut.getInstance().activeGames.forEach((g) -> {
            g.stop();
        });
        MyTimer.pauseTimer();
    }

    public void commandReset() {
        isStarted = false;
        MinecraftBut.getInstance().activeGames.forEach((g) -> {
            g.stop();
        });
        MyTimer.removeTimer();
    }
}
