/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author khuon
 */
public class ServerThread extends Thread {

    private int port;
    private boolean unpn;
    public ServerThread(int port, boolean unpn) {
        this.port = port;
        this.unpn = unpn;
    }

    @Override
    public void run() {
        try {
            new Server(port, true);
        } catch (Exception ex) {
            return;
        }
    }

}
