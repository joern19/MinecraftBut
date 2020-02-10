/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package games;

import java.awt.Point;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import utilities.Game;

/**
 *
 * @author joern
 */
public class SlowChunkDeletion extends Game {

    DELETION_WAY dw = DELETION_WAY.SNAKE;
    public static double pauseBetweenDeletions = 0.1; //in secounds

    private final LinkedList<MyChunk> enterdChunks = new LinkedList<>(); //save state of the Chunk
    private int taskID = 0;

    private MyChunk getMyChunk(Chunk c) {
        for (MyChunk mc : enterdChunks) {
            if (mc.getChunk().hashCode() == c.hashCode()) {
                return mc;
            }
        }
        MyChunk mc = new MyChunk(c);
        enterdChunks.add(mc);
        return mc;
    }

    private void repeatingTask() {
        LinkedList<Chunk> preventMultipleExecution = new LinkedList<>();
        Bukkit.getOnlinePlayers().forEach((player) -> {
            Chunk c = player.getLocation().getChunk();
            if (!preventMultipleExecution.contains(c)) {
                preventMultipleExecution.add(c);
                MyChunk mc = getMyChunk(c);
                Point p = chunkDeleter(c, mc.getX(), mc.getZ());
                if (p != null) {
                    mc.set(p.x, p.y);
                }
            }
        });
    }

    @Override
    public void start() {
        stopped = false;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(minecraftbut.MinecraftBut.getInstance(), () -> {
            if (!stopped) {
                repeatingTask();
            } else {
                Bukkit.getScheduler().cancelTask(taskID);
                taskID = 0;
            }
        }, 20 * 5, (long) (20 * pauseBetweenDeletions));
    }

    private static Point chunkDeleter(Chunk c, int curX, int curZ) { //returns next X and Z if finished then null
        if (curX > 15 || curZ > 15) { //finished with Chunk
            return null;
        }
        for (int y = 0; y < 256; y++) {
            Block b = c.getBlock(curX, y, curZ);
            if (b.getType() != Material.BEDROCK) {
                c.getBlock(curX, y, curZ).setType(Material.AIR);
            }
        }
        if ((curX % 2 != 0) && curZ == 0) {
            return new Point(curX + 1, 0);
        }
        if ((curX % 2 == 0) && curZ == 15) {
            return new Point(curX + 1, 15);
        }
        if (curX % 2 != 0) {
            return new Point(curX, curZ - 1); //odd: -
        } else {
            return new Point(curX, curZ + 1); //even: +
        }
    }

    public class MyChunk {

        private final Chunk chunk;

        private int curX = 0;
        private int curZ = 0;

        public MyChunk(Chunk c) {
            this.chunk = c;
        }

        public Chunk getChunk() {
            return chunk;
        }

        public int getX() {
            return curX;
        }

        public int getZ() {
            return curZ;
        }

        public void set(int x, int z) {
            curX = x;
            curZ = z;
        }
    }

    @Override
    public void stop() {
        stopped = true;
        Bukkit.getScheduler().cancelTask(taskID);
        taskID = 0;
    }

    public static enum DELETION_WAY {
        SNAKE, RANDOM_BLOCK
    }

}
