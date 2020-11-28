/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author khuon
 */
public class ServerLog extends Thread {
    int port;
    public ServerLog(int port) {
        this.port = port;
    }
    

    @Override
    public void run() {
        for (;;) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
    }
}
