package games;

import utilities.Game;

public class MiniGames extends Game {

    @Override
    public void start() {
        minecraftbut.MinecraftBut.getInstance().miniGameInstances.forEach((mg) -> {
            mg.teleportPlayer();
        });
    }

    @Override
    public void stop() {
        minecraftbut.MinecraftBut.getInstance().miniGameInstances.forEach((mg) -> {
            mg.returnPlayer();
        });
    }

}
