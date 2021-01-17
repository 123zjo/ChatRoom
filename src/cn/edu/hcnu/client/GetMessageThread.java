package cn.edu.hcnu.client;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class GetMessageThread extends Thread {
    DatagramSocket ds;
    JTextArea ta;
    JComboBox cb;

    JFrame f;
    String name;
    ChatThreadWindow ctw;
    public GetMessageThread(ChatThreadWindow ctw){
        ds = ctw.ds;
        ta = ctw.ta;
        cb = ctw.cb;
        f =ctw.f;
        ctw = ctw;
        name = ctw.name;
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
                    ctw.total++;
                    maesage = maesage.replace("进入聊天室","");
                    cb.addItem(maesage);
                    f.setTitle("聊天室" + " - " + name + "     当前在线人数:" + ctw.total);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
