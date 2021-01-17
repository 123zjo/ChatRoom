package cn.edu.hcnu.client;

import cn.edu.hcnu.dto.User;
import cn.edu.hcnu.util.JDBConnection;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
    String name;
    JComboBox cb;
    JFrame f;
    JTextArea ta;
    private JTextField tf;
    static int total;// 在线人数统计
    DatagramSocket ds;


    public ChatThreadWindow(String name, DatagramSocket ds) {
        this.name =name;
        this.ds = ds;
        /*
         * 设置聊天室窗口界面
         */
        f = new JFrame();
        f.addWindowListener(
                new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {

                    }

                    @Override
                    public void windowClosing(WindowEvent e) {

                        try {
                            Connection con= JDBConnection.getConnection();
                            String sql = "UPDATE users set status='offline' WHERE username =?";
                            PreparedStatement ps = con.prepareStatement(sql);
                            ps.setString(1,name);
                            ps.executeUpdate();
                            System.out.println("我下线了");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {

                    }

                    @Override
                    public void windowIconified(WindowEvent e) {

                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {

                    }

                    @Override
                    public void windowActivated(WindowEvent e) {

                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {

                    }
                }
        );
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
        f.setTitle("聊天室" + " - " + name + "     当前在线人数:" + ++total);
        f.setLocation(300, 200);
        ta = new JTextArea();
        JScrollPane sp = new JScrollPane(ta);
        ta.setEditable(false);
        tf = new JTextField();
        tf.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {

                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode()==KeyEvent.VK_ENTER){
                            String action = (String) cb.getSelectedItem();
                            String message = tf.getText();
                            tf.setText("");
                            System.out.println("我发送了：" + message);
                            //判断是否值是否为All：true：群发，flase：私聊
                            if (action.equals("All")){
                                sendMessageOfAll(message);
                            }else {
                                sendMessageOfOne(message,action);
                            }
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {

                    }
                }
        );
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
            String sql ="SELECT * FROM users WHERE status = 'online'";
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
                    ta.append(username+"正在聊天室\n");
                    total++;
                }
                this.cb.addItem(username);
            }
            f.setTitle("聊天室" + " - " + name + "     当前在线人数:" + total);
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
    public void sendMessageOfAll(String meg){
        String message ="";

        try {
            Connection con = JDBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE status = 'online'";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            Set<User> users = new HashSet<User>();
            User user = null;

            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String ip = rs.getString("IP");
                int port = rs.getInt("PORT");

                String ips[] = ip.split("\\.");
                byte ipB[] = new byte[4];
                //转换IP地址的格式
                for (int i = 0; i < ips.length; i++) {
                    ipB[i] = (byte) Integer.parseInt(ips[i]);
                }
                if (!username.equals(name)) {
                    System.out.println("我进入聊天室了");
                    message = name + ":"+meg;
                    System.out.println(username + "....");
                    byte m[] = message.getBytes();
                    DatagramPacket dp = new DatagramPacket(m, m.length);
                    dp.setAddress(InetAddress.getByAddress(ipB));
                    dp.setPort(port);
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);
                }
            }
        }catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ta.append(message+"\n");

    }
    public void sendMessageOfOne(String message,String action){
        try {
            Connection con = JDBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE status = 'online' AND username=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,action);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String ip = rs.getString("IP");
                int port = rs.getInt("PORT");

                String ips[] = ip.split("\\.");
                byte ipB[] = new byte[4];
                //转换IP地址的格式
                for (int i = 0; i < ips.length; i++) {
                    ipB[i] = (byte) Integer.parseInt(ips[i]);
                }
                if (!username.equals(name)) {
                    System.out.println("我进入聊天室了");
                    message = name + "悄悄对"+username+"说:"+message;
                    byte m[] = message.getBytes();
                    DatagramPacket dp = new DatagramPacket(m, m.length);
                    dp.setAddress(InetAddress.getByAddress(ipB));
                    dp.setPort(port);
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);
                    ta.append(message+"\n");

                }
            }
        }catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}