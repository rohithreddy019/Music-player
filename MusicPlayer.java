import javazoom.jl.player.advanced.*; 
import java.io.*;
import javazoom.jl.player.*;
import javazoom.jl.decoder.*;
import javazoom.jl.converter.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
class PausablePlayer 
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
}
public class MusicPlayer extends Frame implements ActionListener
{
	private Panel panel;
	private Panel panel_2;
	private Button play;
	private Button pause;
	private Button stop;
	private Button resume;
	private Button previous;
	private Button next;
	private Label label;
	private JFileChooser jfc;
	final int PLAY=1;
	final int PAUSED=2;
	final int RESUME=3;
	final int STOP=4;
	final int NOTPLAYING=0;
	static File dir=new File("MusicFolder");
	static File[] fil=dir.listFiles();
	static PausablePlayer[] player=new PausablePlayer[fil.length];
	int m=0;
	static int status=0;
	public MusicPlayer()
	{
		setTitle("Music Player");
		setLayout(new FlowLayout());
		jfc = new JFileChooser(dir);
        play=new Button("Play");
		play.setActionCommand("Playing");
		pause=new Button("Pause");
		pause.setActionCommand("Pausing");
		stop=new Button("Stop");
		stop.setActionCommand("Stopping");
		resume=new Button("Resume");
		resume.setActionCommand("Resuming");
		previous=new Button("Previous");
		previous.setActionCommand("Previous");
		next=new Button("Next");
		next.setActionCommand("Next");
		label=new Label(fil[0].getName(),Label.CENTER);
		play.addActionListener(this);
		pause.addActionListener(this);
		resume.addActionListener(this);
		stop.addActionListener(this);
		next.addActionListener(this);
		previous.addActionListener(this);
		jfc.addActionListener(this);
		panel_2=new Panel();
		panel_2.add(label);
		panel=new Panel();
		Menu menu=new Menu("File");
		MenuItem mItem=new MenuItem("Open");
		mItem.setActionCommand("Open");
		mItem.addActionListener(this);
		menu.add(mItem);
		MenuBar mbar=new MenuBar();
		mbar.add(menu);
		setMenuBar(mbar);
		panel.add(previous);
		panel.add(play);
		panel.add(pause);
		panel.add(resume);
		panel.add(stop);
		panel.add(next);
		add(panel_2);
		add(panel);
		addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });    

		setSize(600,300);
		setVisible(true);
	}
	static void setFiles()throws Exception
	{
		for(int i=0;i<fil.length;i++)
		{
			player[i]=new PausablePlayer(new FileInputStream(fil[i])); 
		}
	}
	public static void main(String[] args)
	{
		try
		{	setFiles();
			new MusicPlayer();
		}
		catch(Exception e)
		{
			
		}	
	}
	 public void actionPerformed(ActionEvent e)
	{
		try{
		
		if("Playing".equals(e.getActionCommand()))
		{
			if(status==NOTPLAYING)
			{
				player[m].play();
				status=PLAY;
			}
		}
		else if("Pausing".equals(e.getActionCommand()))
		{
			if(status!=NOTPLAYING)
			{
				player[m].pause();
				status=PAUSED;
			}
		}
		else if("Resuming".equals(e.getActionCommand()))
		{
			if(status!=PLAY)
			{
				player[m].resume();
				status=PLAY;
			}
		}
		else if("Stopping".equals(e.getActionCommand()))
		{
			if(status!=NOTPLAYING)
			{
				player[m].stop();
				player[m]=null;
				player[m]=new PausablePlayer(new FileInputStream(fil[m]));
				status=NOTPLAYING;
			}
		}
		else if("Next".equals(e.getActionCommand()))
		{
			player[m].stop();
			player[m]=null;
			player[m]=new PausablePlayer(new FileInputStream(fil[m]));
			status=NOTPLAYING;
			if(m==player.length-1)
				m=0;
			else
				m++;
			label.setText(fil[m].getName());
			player[m].play();
			status=PLAY;
			
		}
		else if("Previous".equals(e.getActionCommand()))
		{
			player[m].stop();
			player[m]=null;
			player[m]=new PausablePlayer(new FileInputStream(fil[m]));
			status=NOTPLAYING;
			if(m==0)
				m=(player.length)-1;
			else
				m--;
			label.setText(fil[m].getName());
			player[m].play();
			status=PLAY;
		}
		else if("Open".equals(e.getActionCommand()))
		{
		int ret=jfc.showOpenDialog(this);
		if(ret==JFileChooser.APPROVE_OPTION)
		{
			File f = jfc.getSelectedFile();
			player[m].stop();
			player[m]=null;
			player[m]=new PausablePlayer(new FileInputStream(fil[m]));
			for(int i=0;i<fil.length;i++)
				if((fil[i].getName()).equals(f.getName()))
					m=i;
			player[m]=null;
			player[m]=new PausablePlayer(new FileInputStream(fil[m]));
			label.setText(fil[m].getName());
			player[m].play();
			status=PLAY;
		}
		}
		}
		catch(Exception ex){}
	}
}