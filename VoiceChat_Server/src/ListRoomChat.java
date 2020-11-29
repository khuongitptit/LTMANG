
import java.util.ArrayList;

public class ListRoomChat {
        private static ArrayList<RoomChat> listRoomChat = new ArrayList();

    public static ArrayList<RoomChat> getListRoomChat() {
        return listRoomChat;
    }
    public static void addRoomChat(RoomChat rc){
        listRoomChat.add(rc);
    }
    public static boolean addParticipantToRoom(int port, Participant p){
        for(RoomChat rc : listRoomChat){
            if(rc.getPort() == port){
                if(rc.getCurrentParticipant() < rc.getMaxParticipant()){
                    rc.setCurrentParticipant(rc.getCurrentParticipant()+1);
                    ArrayList<Participant> listP = rc.getParticipants();
                    listP.add(p);
                    rc.setParticipants(listP);
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean removeParticipantFromRoom(int port){
        for(RoomChat rc : listRoomChat){
            if(rc.getPort() == port){
                if(rc.getCurrentParticipant() >0){
                    rc.setCurrentParticipant(rc.getCurrentParticipant()-1);
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isRoomAvailable(int port){
        for(RoomChat rc : listRoomChat){
            if(rc.getPort() == port){
                return rc.isRoomAvailable(port);
            }
        }
        return false;
    }
}
