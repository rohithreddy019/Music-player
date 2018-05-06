import java.io.*;
import javazoom.spi.mpeg.sampled.file.*;
import javazoom.jl.player.advanced.*; 
import javazoom.jl.player.*;
import javazoom.jl.decoder.*;
import javazoom.jl.converter.*;
import javax.sound.sampled.*;
import java.util.*;
import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;
class AudioTim
{
	public static void main(String [] args)throws UnsupportedAudioFileException,FileNotFoundException, IOException ,NullPointerException ,ClassCastException
	{
		File file = new File("filename.mp3");
	AudioFileFormat baseFileFormat = (new MpegAudioFileReader()).getAudioFileFormat(file);
	Map properties = baseFileFormat.properties();
	Long duration = (Long) properties.get("duration");
		
		
	}
    
    }

