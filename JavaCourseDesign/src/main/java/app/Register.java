package app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Register extends JFrame {
    public Register() {
        setTitle("注册界面");
        BufferedImage img;
        try {
            img = ImageIO.read(Server.class.getResource("/logo/hayden.png"));
            this.setIconImage(img);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        setLayout(null);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);
        setResizable(false);

        JLabel username_label = new JLabel("用户名");
        username_label.setBounds(60, 50, 100, 50);
        add(username_label);

        JLabel password_label = new JLabel("密码");
        password_label.setBounds(60, 150, 100, 50);
        add(password_label);

        JLabel password_label2 = new JLabel("请再次输入密码");
        password_label2.setBounds(20, 250, 100, 50);
        add(password_label2);

        JTextField username_field = new JTextField();
        username_field.setBounds(110, 50, 300, 50);
        add(username_field);

        JPasswordField password_field = new JPasswordField();
        password_field.setBounds(110, 150, 300, 50);
        add(password_field);

        JPasswordField password_field2 = new JPasswordField();
        password_field2.setBounds(110, 250, 300, 50);
        add(password_field2);


        JButton register_success = new JButton("注册");
        register_success.setBounds(130, 350, 100, 50);
        add(register_success);

        JButton back = new JButton("返回");
        back.setBounds(280, 350, 100, 50);
        add(back);

        setVisible(true);


        register_success.addActionListener(e -> {
            String username = username_field.getText();
            String password = String.valueOf(password_field.getPassword());
            String password2 = String.valueOf(password_field2.getPassword());
            System.out.println(password);
            System.out.println(password2);
            if (username.length() == 0 || password.length() == 0) {
                JOptionPane.showMessageDialog(null, "注册失败，账号或密码不能为空", "提示",
                        JOptionPane.WARNING_MESSAGE);
            } else if (!password.equals(password2)) {
                JOptionPane.showMessageDialog(null, "注册失败，前后密码不匹配", "提示",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                System.out.println();
                System.out.println(password);
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                UserDao dao = new UserDao();
                int flag = dao.register(user);
                if (flag != 0) {
                    JOptionPane.showMessageDialog(null, "注册成功，欢迎您登录", "提示",
                            JOptionPane.WARNING_MESSAGE);

                } else {
                    //建表语句中设置了user为主键，重复则建表失败
                    JOptionPane.showMessageDialog(null, "注册失败，账号已经存在", "提示",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        back.addActionListener(e -> {
            setVisible(false);
            new Client();
        });


    }

}

