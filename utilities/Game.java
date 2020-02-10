package utilities;

/**
 *
 * @author joern
 */
public abstract class Game {
    
    protected static boolean stopped = true;
    public boolean eventsRegisterd = false;
    
    public abstract void start();
    public abstract void stop();
    
}
