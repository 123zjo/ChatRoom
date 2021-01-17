package cn.edu.hcnu.client;

import cn.edu.hcnu.util.JDBConnection;
import cn.edu.hcnu.util.MD5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginThread extends  Thread {
    private JFrame loginf;
    private JTextField t;
    public void run(){
        /*
         * 设置登录界面
         */
        loginf = new JFrame();
        loginf.setResizable(false);
        loginf.setLocation(300, 200);
        loginf.setSize(400, 150);
        loginf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginf.setTitle("聊天室" + " - 登录");

        t = new JTextField("Version " + "1.1.0" + "        By liwei");
        t.setHorizontalAlignment(JTextField.CENTER);
        t.setEditable(false);
        loginf.getContentPane().add(t, BorderLayout.SOUTH);

        JPanel loginp = new JPanel(new GridLayout(3, 2));
        loginf.getContentPane().add(loginp);

        JTextField t1 = new JTextField("登录名:");
        t1.setHorizontalAlignment(JTextField.CENTER);
        t1.setEditable(false);
        loginp.add(t1);

        final JTextField loginname = new JTextField("");
        loginname.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginname);

        JTextField t2 = new JTextField("密码:");
        t2.setHorizontalAlignment(JTextField.CENTER);
        t2.setEditable(false);
        loginp.add(t2);

        final JTextField loginPassword = new JTextField("");
        loginPassword.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginPassword);
        /*
         * 监听退出按钮(匿名内部类)
         */
        JButton b1 = new JButton("退  出");
        loginp.add(b1);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        final JButton b2 = new JButton("登  录");
        loginp.add(b2);

        loginf.setVisible(true);

        /**
         * 监听器,监听"登录"Button的点击和TextField的回车
         */
        class ButtonListener implements ActionListener {
            private Socket s;

            public void actionPerformed(ActionEvent e) {
                String username = loginname.getText();
                String password = loginPassword.getText();
                String sql = "";
                try {

                    Connection conn = JDBConnection.getConnection();
                    sql = "SELECT password FROM users WHERE username=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1,username);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {

                        String encodePassword = rs.getString("PASSWORD");
                        System.out.println("数据库读取到" + encodePassword);
                        if (MD5.checkpassword(password, encodePassword)) {
                            System.out.println("登录成功");
                             /*
                            * 获取本机ip
                            *
                            * */
                            int port = 1688;
                            DatagramSocket ds =null ;
                            while (true){
                                try {
                                    ds = new DatagramSocket(port);
                                    break;
                                } catch (IOException ex) {
                                    port +=1;

                                }
                            }

                            try {
                                InetAddress addr = InetAddress.getLocalHost();
                                System.out.println("获取ip：" + addr.getHostAddress());
                                sql = "UPDATE users set ip = ?,port =?,status='online' WHERE username =?";
                                ps = conn.prepareStatement(sql);
                                ps.setString(1,addr.getHostAddress());
                                ps.setInt(2,port);
                                ps.setString(3,username);
                                ps.executeUpdate();
                            } catch (UnknownHostException ex) {
                                ex.printStackTrace();
                            }
                            loginf.setVisible(false);
                            ChatThreadWindow ctw = new ChatThreadWindow(username,ds);
                        } else {
                            System.out.println("登录失败");
                        }
                    }
                } catch (SQLException ee) {
                    ee.printStackTrace();
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
				/*
				1、根据用户去数据库把加密后的密码拿到
				SELECT password FROM users WHERE username='liwei';
				2、把登录界面输入的密码和数据库里加密后的进行比对（调用MD5类的checkpassword方法）
				 */
            }
        }
        ButtonListener bl = new ButtonListener();
        b2.addActionListener(bl);
        loginname.addActionListener(bl);
        loginPassword.addActionListener(bl);
    }

}
