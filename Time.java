import javazoom.jl.player.advanced.*; 
import java.io.*;
import javazoom.jl.player.*;
import javazoom.jl.decoder.*;
import javazoom.jl.converter.*;
import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;
class Time
{
	public static void main(String[] args)
	{Header h=null;
		FileInputStream file=null;
		try
		{
		file=new FileInputStream("kool.mp3");
		}
		catch(FileNotFoundException e)
		{
		Logger.getLogger(MP3.class.getName()).log(Level.SEVERE,null,ex);
		}
		try{
			h=bitstream.readFrame();
		}
		catch(BitStreamException ex){Logger.getLogger(MP3.class.getName()).log(Level.SEVERE,null,ex);}
		int size=h.calculate_framesize();
		float ms_per_frame=h.ms_per_frame();
		int maxSize=h.max_number_of_frames(10000);
		float t=h.total_ms(size);
		long tn=0;
	}	
}
