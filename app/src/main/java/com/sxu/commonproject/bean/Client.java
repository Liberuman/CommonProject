package com.sxu.commonproject.bean;

import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by juhg on 16/3/7.
 */
public class Client {

    private int port;
    private String ipAddress;
    private Socket socket;

    public Client(String ipAddress, int port) {
        try {
            socket = new Socket(ipAddress, port);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void sendMsg(String content) throws Exception {
        if (socket != null) {
            OutputStream os = socket.getOutputStream();
            os.write("android客户端".getBytes("GBK"));
            os.flush();
            os.close();
            socket.close();
        }


    }
}
