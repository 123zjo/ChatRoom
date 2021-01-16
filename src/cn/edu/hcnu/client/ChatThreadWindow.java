package cn.edu.hcnu.client;

import cn.edu.hcnu.dto.User;
import cn.edu.hcnu.util.JDBConnection;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * 聊天线程
 */
public class ChatThreadWindow {
    private String name;
    JComboBox cb;
    private JFrame f;
    JTextArea ta;
    private JTextField tf;
    private static int total;// 在线人数统计
    DatagramSocket ds;


    public ChatThreadWindow(String name, DatagramSocket ds) {
        this.name =name;
        this.ds = ds;
        /*
         * 设置聊天室窗口界面
         */
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
        f.setTitle("聊天室" + " - " + name + "     当前在线人数:" + ++total);
        f.setLocation(300, 200);
        ta = new JTextArea();
        JScrollPane sp = new JScrollPane(ta);
        ta.setEditable(false);
        tf = new JTextField();
        cb = new JComboBox();
        cb.addItem("All");
        JButton jb = new JButton("私聊窗口");
        JPanel pl = new JPanel(new BorderLayout());
        pl.add(cb);
        pl.add(jb, BorderLayout.WEST);
        JPanel p = new JPanel(new BorderLayout());
        p.add(pl, BorderLayout.WEST);
        p.add(tf);
        f.getContentPane().add(p, BorderLayout.SOUTH);
        f.getContentPane().add(sp);
        f.setVisible(true);
        GetMessageThread gmt = new GetMessageThread(this);
        gmt.start();
        showXXXIntoChatRoom();

    }
    public void showXXXIntoChatRoom(){
        try {
            Connection con = JDBConnection.getConnection();
            String sql ="SELECT * FROM users WHERE ststus = 'online'";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            Set<User> users = new HashSet<User>();
            User user =null;

            while (rs.next()){
                String username =rs.getString("USERNAME");
                String ip = rs.getString("IP");
               int port = rs.getInt("PORT");

               String ips [] = ip.split("\\.");
               byte ipB [] = new byte[4];
               //转换IP地址的格式
                for (int i=0;i<ips.length;i++){
                    ipB[i] = (byte) Integer.parseInt(ips[i]);
                }
                if(!username.equals(name)){
                    System.out.println("我进入聊天室了");
                    String message = name+"进入聊天室";
                    byte m [] = message.getBytes();
                    DatagramPacket dp = new DatagramPacket(m,m.length);
                    dp.setAddress(InetAddress.getByAddress(ipB));
                    dp.setPort(port);
                    DatagramSocket ds =new DatagramSocket();
                    ds.send(dp);
                }
                this.cb.addItem(username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}