/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author khuon
 */
public class ServerLogger {
    private static String log="";
    public static void add(String s){
        log+=s+"\n";
    }
    public static String get(){
        return log;
    }
}
