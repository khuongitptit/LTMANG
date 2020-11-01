
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

public class Server {
    
    private ArrayList<Message> broadCastQueue = new ArrayList<Message>();    
    private ArrayList<ClientConnection> clients = new ArrayList<ClientConnection>();
    private int port;
    
    private UpnpService u; 
    
    public void addToBroadcastQueue(Message m) { 
        try {
            broadCastQueue.add(m);
        } catch (Throwable t) {
            Utils.sleep(1);
            addToBroadcastQueue(m);
        }
    }
    private ServerSocket s;
    
    public Server(int port, boolean upnp) throws Exception{
        this.port = port;
        if(upnp){
            Log.add("Setting up NAT Port Forwarding...");
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException ex) {
                Log.add("Network error");
                throw new Exception("Network error");
            }
            String ipAddress = null;
            Enumeration<NetworkInterface> net = null;
            try {
                net = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                Log.add("Not connected to any network");
                throw new Exception("Network error");
            }

            while (net.hasMoreElements()) {
                NetworkInterface element = net.nextElement();
                Enumeration<InetAddress> addresses = element.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address) {
                        if (ip.isSiteLocalAddress()) {
                            ipAddress = ip.getHostAddress();
                            break;
                        }
                    }
                }
                if (ipAddress != null) {
                    break;
                }
            }
            if (ipAddress == null) {
                Log.add("Not connected to any IPv4 network");
                throw new Exception("Network error");
            }
            u = new UpnpServiceImpl(new PortMappingListener(new PortMapping(port, ipAddress, PortMapping.Protocol.TCP)));
            u.getControlPoint().search();
        }
        try {
            s = new ServerSocket(port); 
            Log.add("Port " + port + ": server started");
        } catch (IOException ex) {
            Log.add("Server error " + ex + "(port " + port + ")");
            throw new Exception("Error "+ex);
        }
        new BroadcastThread().start(); 
        while(true){
            try {
                Socket c = s.accept();
                ClientConnection cc = new ClientConnection(this, c);
                cc.start();
                addToClients(cc);
                Log.add("new client " + c.getInetAddress() + ":" + c.getPort() + " on port " + port);
            } catch (IOException ex) {
            }
        }
    }

    private void addToClients(ClientConnection cc) {
        try {
            clients.add(cc); 
        } catch (Throwable t) {
            Utils.sleep(1);
            addToClients(cc);
        }
    }

    private class BroadcastThread extends Thread {
        
        public BroadcastThread() {
        }
        
        @Override
        public void run() {
            while(true){
                try {
                    ArrayList<ClientConnection> toRemove = new ArrayList<ClientConnection>();
                    for (ClientConnection cc : clients) {
                        if (!cc.isAlive()) { 
                            Log.add("Dead connection closed: " + cc.getInetAddress() + ":" + cc.getPort() + " on port " + port);
                            toRemove.add(cc);
                        }
                    }
                    clients.removeAll(toRemove); 
                    if (broadCastQueue.isEmpty()) { 
                        Utils.sleep(10); 
                        continue;
                    } else { 
                        Message m = broadCastQueue.get(0);
                        for (ClientConnection cc : clients) { 
                            if (cc.getchannelID()!= m.getchannelID()) {
                                cc.addToQueue(m);
                            }
                        }
                        broadCastQueue.remove(m);
                    }
                } catch (Throwable t) {
                }
            }
        }
    }
}
