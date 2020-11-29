
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.JOptionPane;


public class Client extends Thread {

    private Socket socket;
    private ArrayList<AudioChannel> audioChannels = new ArrayList<AudioChannel>();
    private MicThread micThread;
    private GUI gui;
    public Client(GUI gui, String serverIp, int serverPort) throws UnknownHostException, IOException {
        this.gui = gui;
        socket = new Socket(serverIp, serverPort);
    }

    @Override
    public void run() {
        try {
            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());  
            ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
            try {
                try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
                micThread = new MicThread(toServer);  
                micThread.start(); 
            } catch (Exception e) { 
                e.printStackTrace();
            }
            while(true){
                if (socket.getInputStream().available() > 0) { 
                    Message in = (Message) (fromServer.readObject()); 
                    AudioChannel sendTo = null; 
                    for (AudioChannel audioChannel : audioChannels) {
                        if (audioChannel.getchannelID()== in.getchannelID()) {
                            sendTo = audioChannel;
                        }
                    }
                    if (sendTo != null) {
                        sendTo.addToQueue(in);
                    } else { 
                        AudioChannel ch = new AudioChannel(in.getchannelID());
                        ch.addToQueue(in);
                        ch.start();
                        audioChannels.add(ch);
                    }
                }else{ 
                    ArrayList<AudioChannel> channelToKill=new ArrayList<AudioChannel>();
                    for(AudioChannel channel:audioChannels) if(channel.canKill()) channelToKill.add(channel);
                    for(AudioChannel c:channelToKill){c.closeAndKill(); audioChannels.remove(c);}
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        } catch (Exception e) {
                        GUI.enableUI();
            JOptionPane.showMessageDialog(gui, "Phòng đã đầy");

            e.printStackTrace();
        }
    }
}
 