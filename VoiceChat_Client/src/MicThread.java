
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

public class MicThread extends Thread {

    public static double amplification = 1.0;
    private ObjectOutputStream toServer;
    private TargetDataLine mic;
    private GUI gui;
    public MicThread(ObjectOutputStream toServer, GUI gui) throws LineUnavailableException {
        this.toServer = toServer;
        this.gui = gui;
        AudioFormat af = SoundPacket.defaultFormat;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, null);
        mic = (TargetDataLine) (AudioSystem.getLine(info));
        mic.open(af);
        mic.start();
    }

    @Override
    public void run() {
        for (;;) {
            if (mic.available() >= SoundPacket.defaultDataLength) {
                byte[] buff = new byte[SoundPacket.defaultDataLength];
                while (mic.available() >= SoundPacket.defaultDataLength) { 
                    mic.read(buff, 0, buff.length); 
                }
                try {
                    long tot = 0;
                    for (int i = 0; i < buff.length; i++) {
                        buff[i] *= amplification;
                        tot += Math.abs(buff[i]);
                    }
                    tot *= 2.5;
                    tot /= buff.length;
                    Message m = null;
                    if (tot == 0) {
                        m = new Message(-1, -1, new SoundPacket(null));
                    } else { 
                  
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        GZIPOutputStream go = new GZIPOutputStream(baos);
                        go.write(buff);
                        go.flush();
                        go.close();
                        baos.flush();
                        baos.close();
                        m = new Message(-1, -1, new SoundPacket(baos.toByteArray()));  
                    }
                    toServer.writeObject(m); 
                } catch (IOException ex) { 
                    GUI.enableUI();
                    JOptionPane.showMessageDialog(gui, "Phòng đã đầy");
                    stop();
                }
            } else {
                try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
        }
            }
        }
    }
}
