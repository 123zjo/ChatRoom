package cn.edu.hcnu.client;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class GetMessageThread extends Thread {
    DatagramSocket ds;
    JTextArea ta;
    JComboBox cb;
    public GetMessageThread(ChatThreadWindow ctw){
        ds = ctw.ds;
        ta = ctw.ta;
        cb = ctw.cb;

    }
    public void run(){
        byte [] buff = new byte[1024];
        while (true){
            DatagramPacket dp = new DatagramPacket(buff,200);
            try {
                ds.receive(dp);
                String maesage = new String(buff,0,dp.getLength());
                System.out.println("UDP接收到：" + maesage);
                ta.append(maesage+"\n");
                if(maesage.contains("进入聊天室")){
                    maesage = maesage.replace("进入聊天室","");
                }
                cb.addItem(maesage);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
