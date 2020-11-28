
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author khuon
 */
class Participant {
    private String IPAddress;
    private int remotePort;

    public Participant() {
    }

    public Participant(String IPAddress, int remotePort) {
        this.IPAddress = IPAddress;
        this.remotePort = remotePort;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
    
}
public class RoomChat {
    private int port;
    private int maxParticipant;
    private int currentParticipant;
    private ArrayList<Participant> participants;
    public RoomChat(int port, int maxParticipant, int currentParticipant) {
        this.port = port;
        this.maxParticipant = maxParticipant;
        this.currentParticipant = currentParticipant;
        this.participants = new ArrayList<>();
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    public RoomChat() {
    }

    public int getPort() {
        return port;
    }

    public int getMaxParticipant() {
        return maxParticipant;
    }

    public int getCurrentParticipant() {
        return currentParticipant;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setMaxParticipant(int maxParticipant) {
        this.maxParticipant = maxParticipant;
    }

    public void setCurrentParticipant(int currentParticipant) {
        this.currentParticipant = currentParticipant;
    }
    
    public boolean isRoomAvailable(int port) {
        return currentParticipant < maxParticipant;
    }
}
