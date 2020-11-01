
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;


public class AudioChannel extends Thread {

    private long channelID; 
    private ArrayList<Message> queue = new ArrayList<Message>(); 
    private int lastSoundPacketLen = SoundPacket.defaultDataLength;
    private long lastPacketTime = System.nanoTime();

    public boolean canKill() { //returns true if it's been a long time since last received packet
        if (System.nanoTime() - lastPacketTime > 5000000000L) {
            return true; //5 seconds with no data
        } else {
            return false;
        }
    }

    public void closeAndKill() {
        if (speaker != null) {
            speaker.close();
        }
        stop();
    }

    public AudioChannel(long channelID) {
        this.channelID = channelID;
    }

    public long getchannelID() {
        return channelID;
    }

    public void addToQueue(Message m) { 
        queue.add(m);
    }
    private SourceDataLine speaker = null; 

    @Override
    public void run() {
        try {
            AudioFormat af = SoundPacket.defaultFormat;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(af);
            speaker.start();
            while(true){
                if (queue.isEmpty()) { 
                    Utils.sleep(10);
                    continue;
                } else {
                    lastPacketTime = System.nanoTime();
                    Message in = queue.get(0);
                    queue.remove(in);
                    if (in.getData() instanceof SoundPacket) { 
                        SoundPacket m = (SoundPacket) (in.getData());
                        if (m.getData() == null) {
                            byte[] noise = new byte[lastSoundPacketLen];
                            for (int i = 0; i < noise.length; i++) {
                                noise[i] = (byte) ((Math.random() * 3) - 1);
                            }
                            speaker.write(noise, 0, noise.length);
                        } else {
                            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(m.getData()));
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            for (;;) {
                                int b = gis.read();
                                if (b == -1) {
                                    break;
                                } else {
                                    baos.write((byte) b);
                                }
                            }
                            byte[] toPlay=baos.toByteArray();
                            speaker.write(toPlay, 0, toPlay.length);
                            lastSoundPacketLen = m.getData().length;
                        }
                    } else { 
                        continue; 
                    }
                }
            }
        } catch (Exception e) { 
            System.out.println("Receiver Thread " + channelID + " ,error: " + e.toString());
            if (speaker != null) {
                speaker.close();
            }
            stop();
        }
    }
}
