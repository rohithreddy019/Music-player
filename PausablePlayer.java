import javazoom.jl.player.advanced.*; 
import java.io.*;
import javazoom.jl.player.*;
import javazoom.jl.decoder.*;
import javazoom.jl.converter.*;
public class PausablePlayer 
{

    private final static int NOTSTARTED = 0;
    private final static int PLAYING = 1;
    private final static int PAUSED = 2;
    private final static int FINISHED = 3;
    private final Player player;
    private final Object playerLock = new Object();
    private int playerStatus = NOTSTARTED;

    public PausablePlayer(final InputStream inputStream) throws JavaLayerException 
	{
        this.player = new Player(inputStream);
    }

    public void play() throws JavaLayerException {
        synchronized (playerLock) {
            switch (playerStatus) {
                case NOTSTARTED:
                    final Thread t = new Thread() {
                        public void run() {
                            playInternal();
                        }
                    };
                    t.setPriority(Thread.MAX_PRIORITY);
                    playerStatus = PLAYING;
                    t.start();
                    break;
                case PAUSED:
                    resume();
                    break;
                default:
                    break;
            }
        }
    }
	
    public boolean pause() {
        synchronized (playerLock) {
            if (playerStatus == PLAYING) {
                playerStatus = PAUSED;
            }
            return playerStatus == PAUSED;
        }
    }

    public boolean resume() {
        synchronized (playerLock) {
            if (playerStatus == PAUSED) {
                playerStatus = PLAYING;
                playerLock.notifyAll();
            }
            return playerStatus == PLAYING;
        }
    }

    public void stop() {
        synchronized (playerLock) {
            playerStatus = FINISHED;
            playerLock.notifyAll();
        }
    }

    private void playInternal() 
	{
        while (playerStatus != FINISHED) 
		{
            try 
			{
                if (!player.play(1)) 
{
                    break;
                }
            } catch (final JavaLayerException e) 
			{
                break;
            }
            synchronized (playerLock) {
                while (playerStatus == PAUSED) 
				{
                    try 
					{
                        playerLock.wait();
                    } catch (final InterruptedException e) 
					{
                        break;
                    }
                }
            }
        }
        close();
    }

    public void close() 
	{
        synchronized (playerLock) 
		{
            playerStatus = FINISHED;
        }
        try 
		{
            player.close();
        }
		catch (final Exception e) 
		{

        }
    }


  public static void main(String[] args)
	{
        try 
		{
           FileInputStream input = new FileInputStream("kool.mp3"); 
            PausablePlayer player = new PausablePlayer(input);
            player.play();
            Thread.sleep(5000);
            player.pause();     
            Thread.sleep(5000);
            player.resume();
			Thread.sleep(30000);
			player.stop();
			
        } 
		catch (final Exception e) 
		{
            throw new RuntimeException(e);
        }
    }
}
