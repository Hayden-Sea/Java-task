package app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Client extends JFrame {


    public Client() {
        setTitle("森林冰火人-登陆界面");
        BufferedImage img;

        try {
            img = ImageIO.read(Server.class.getResource("/logo/hayden.png"));
            setIconImage(img);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        setLayout(null);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);
        setResizable(false);

        JLabel logo = new JLabel();
        ImageIcon image = new ImageIcon("images/logo/HaydenLogo.png");//实例化ImageIcon 对象
        //实现缩放图片
        image.setImage(image.getImage().getScaledInstance(220, 80,Image.SCALE_DEFAULT ));
        logo.setIcon(image);
        logo.setBounds(140, 10, 220, 80);
        add(logo);


        JLabel username_label = new JLabel("用户名");
        username_label.setBounds(60, 100, 100, 50);
        add(username_label);

        JLabel password_label = new JLabel("密码");
        password_label.setBounds(60, 200, 100, 50);
        add(password_label);

        JTextField username_field = new JTextField();
        username_field.setBounds(110, 100, 300, 50);
        add(username_field);

        JPasswordField password_field = new JPasswordField();
        password_field.setBounds(110, 200, 300, 50);
        add(password_field);

        JButton login = new JButton("登陆");
        login.setBounds(130, 300, 100, 50);
        add(login);
        JButton register = new JButton("注册");
        register.setBounds(280, 300, 100, 50);
        add(register);

        setVisible(true);

        //登录按钮事件
        login.addActionListener(e -> {
            String username = username_field.getText();
            String password = String.valueOf(password_field.getPassword());
            if (username.length() != 0 && password.length() != 0) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                UserDao dao = new UserDao();
                User u = dao.login(user);//通过账号密码查询登录
                if (u != null) {
                    setVisible(false);
                    //登录成功，新建一个聊天窗口
                    new GameRoom(username,u.getScore());
                } else {
                    JOptionPane.showMessageDialog(null, "登录失败，账号或密码错误", "提示",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "登录失败，账号或密码不能为空", "提示",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        //注册按钮事件
        register.addActionListener(e -> {
            setVisible(false);//使该窗口消失
            new Register();
        });
    }

    public static void main(String[] args) {
        new Client();
    }

}

