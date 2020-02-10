package minecraftbut;

import debug.LoadNewPlugin;
import games.SlowChunkDeletion;
import gui.AdminGUI;
import java.util.ArrayList;
import miniGames.Replica;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import utilities.EventManager;
import utilities.Game;
import utilities.MiniGame;

/**
 *
 * @author joern
 */
public class MinecraftBut extends JavaPlugin {

    private static MinecraftBut INSTANCE = null;
    public ArrayList<Game> activeGames = new ArrayList<>();
    public ArrayList<MiniGame> miniGameInstances = new ArrayList<>();
    
    public static synchronized MinecraftBut getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return CommandManager.getInstance().onCommand(sender, command, args);
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
        new EventManager(this);
    }

    private static void loadMiniGameWorld() {
        for (World w : Bukkit.getWorlds()) {
            if (w.getName().equals("miniGames")) {
                return;
            }
        }
        Bukkit.getServer().createWorld(new WorldCreator("miniGames"));
    }
    
    @Override
    public void onEnable() {
        AdminGUI.reload();
        loadMiniGameWorld();
        miniGameInstances.add(new Replica());
        //miniGameInstances.add(new JumpAndRun());
        miniGameInstances.forEach((mg) -> {
            mg.registerEvents();
        });
    }
  
}
