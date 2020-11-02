
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnection extends Thread {

    private Server server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private long channelID;
    private ArrayList<Message> toSend = new ArrayList<Message>();

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public int getPort() {
        return socket.getPort();
    }

    public long getchannelID() {
        return channelID;
    }

    public ClientConnection(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        byte[] addr = socket.getInetAddress().getAddress();
        channelID = (addr[0] << 48 | addr[1] << 32 | addr[2] << 24 | addr[3] << 16) + socket.getPort();
    }

    public void addToQueue(Message m) {
        try {
            toSend.add(m);
        } catch (Throwable t) {
        }
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            try {
                socket.close();
                Log.add("ERROR " + getInetAddress() + ":" + getPort() + " " + ex);
            } catch (IOException ex1) {
            }
            stop();
        }
        while (true) {
            try {
                if (socket.getInputStream().available() > 0) {
                    Message toBroadcast = (Message) in.readObject();
                    if (toBroadcast.getchannelID() == -1) {
                        toBroadcast.setchannelID(channelID);
                        toBroadcast.setTimestamp(System.nanoTime() / 1000000L);
                        server.addToBroadcastQueue(toBroadcast);
                    } else {
                        continue;
                    }
                }
                try {
                    if (!toSend.isEmpty()) {
                        Message toClient = toSend.get(0);
                        if (!(toClient.getData() instanceof SoundPacket) || toClient.getTimestamp() + toClient.getTtl() < System.nanoTime() / 1000000L) { //is the message too old or of an unknown type?
                            Log.add("Dropping packet from " + toClient.getchannelID() + " to " + channelID);
                            continue;
                        }
                        out.writeObject(toClient);
                        toSend.remove(toClient);
                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                        }
                    }
                } catch (Throwable t) {
                    if (t instanceof IOException) {
                        throw (Exception) t;
                    } else {
                        continue;
                    }
                }
            } catch (Exception ex) {
                try {
                    socket.close();
                } catch (IOException ex1) {
                }
                stop();
            }
        }

    }
}
