package app;

import game.Play;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GameRoom extends JFrame {
    int select=0;//默认未选择，火人为1冰人为2
    int oth_select=0;
    int score;
    String username;
    ArrayList<String> username_list = new ArrayList<>();


    boolean is_stop = false;
    boolean is_begin = false;
    //提示信息栏
    JLabel player_msg = new JLabel("");
    //!!
    JLabel title_label = new JLabel("");
    //其他玩家
    JLabel oth_player = new JLabel("其他玩家");
    JLabel oth_playerdata = new JLabel("  无");
    //选择按钮
    JButton fire_btn = new JButton("火人");
    JButton ice_btn = new JButton("冰人");

    //socket的数据输出流
    DataOutputStream outputStream = null;
    //socket的数据输入流
    DataInputStream inputStream = null;
    Socket socket;

    //窗口长宽
    int width = 800;
    int height = 610;


    //窗口的启动方法
    public void launch() {
        //标题
        setTitle("森林冰火人-准备房间");
        //窗口初始大小
        setSize(width,height);
        //添加头图片
        BufferedImage img;
        try {
            img = ImageIO.read(Server.class.getResource("/logo/hayden.png"));
            setIconImage(img);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        //布局
        setLayout(null);
        setResizable(false);
        //使屏幕居中
        setLocationRelativeTo(null);
        //添加关闭事件
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //用户不能调整大小
        setResizable(false);
        //使窗口可见
        setVisible(true);

        //玩家状态栏
        JLabel player_data = new JLabel("欢迎你，"+username+" 您的累计分数为："+score);
        player_data.setFont(new Font("宋体", Font.BOLD, 20));
        player_data.setBounds(10, 0, 500, 60);
        add(player_data);
        //提示信息
        player_msg.setFont(new Font("宋体", Font.BOLD, 20));
        player_msg.setBounds(10, 30, 300, 60);
        add(player_msg);
        //其他玩家显示
        oth_player.setFont(new Font("宋体", Font.BOLD, 20));
        oth_player.setBounds(650, 50, 100, 60);
        add(oth_player);
        oth_playerdata.setFont(new Font("宋体", Font.BOLD, 20));
        oth_playerdata.setBounds(650, 80, 100, 60);
        add(oth_playerdata);
        //默认为 请选择你的角色
        title_label.setFont(new Font("微软雅黑", Font.BOLD, 40));
        title_label.setBounds(250, 70, 500, 60);
        add(title_label);

        //冰火人界面
        JLabel logo = new JLabel();
        ImageIcon image = new ImageIcon("images/pPle/双人.jpg");//实例化ImageIcon 对象
        //实现缩放图片
        image.setImage(image.getImage().getScaledInstance(400, 300,Image.SCALE_DEFAULT ));
        logo.setIcon(image);
        logo.setBounds(200, 150, 400, 300);
        add(logo);

        //选择按钮

        fire_btn.setBounds(220, 470, 100, 50);
        add(fire_btn);
        ice_btn.setBounds(460, 470, 100, 50);
        add(ice_btn);
        //防止按钮被隐藏
        fire_btn.requestFocus();
        ice_btn.requestFocus();

        //火人按钮事件
        fire_btn.addActionListener(e -> {
            if (is_stop) {
                JOptionPane.showMessageDialog(null,"你已被踢出，进程已经关闭","提示",
                        JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
            //进行按钮选择
            if(select==2){  //之前选了冰人
                ice_btn.setText("冰人");
                ice_btn.setEnabled(true);
            }
            fire_btn.setText("已选择");
            fire_btn.setEnabled(false);
            player_msg.setText("您已选择：火人");
            select=1;
            //传递选择信息给服务端
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            String msg = username+"已选择火人";
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("msg", msg);
            data.put("time", time);
            data.put("select",select);
            try {
                outputStream.writeUTF(data.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        //冰人按钮事件
        ice_btn.addActionListener(e -> {
            if (is_stop) {
                JOptionPane.showMessageDialog(null,"你已被踢出，进程已经关闭","提示",
                        JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
            if(select==1){  //之前选了火人
                fire_btn.setText("火人");
                fire_btn.setEnabled(true);
            }
            ice_btn.setText("已选择");
            ice_btn.setEnabled(false);
            player_msg.setText("您已选择：冰人");
            select=2;
            //传递选择信息给服务端
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            String msg = username+"已选择冰人";
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("msg", msg);
            data.put("time", time);
            data.put("select",select);
            try {
                outputStream.writeUTF(data.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }


    public GameRoom(final String username, int score) {
        this.username = username;
        this.score = score;
        this.launch();//启动窗口

        JSONObject data = new JSONObject();
        data.put("username", username);
        data.put("msg", null);
        data.put("score",score);
        //登录进游戏
        try {
            socket = new Socket("127.0.0.1", 11111);//连接上服务器所在的ip地址
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(data.toString());
            new Thread(new GameRoom.Read()).start();
        } catch (IOException e) {
            player_msg.setText("服务器未响应");
            JOptionPane.showMessageDialog(null,"服务器无响应","提示",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public class Read implements Runnable {
        @Override
        public void run() {
            try {
                //持续接收服务端发来的信息
                while (true) {
                    String json = inputStream.readUTF();
                    JSONObject data = JSONObject.fromObject(json.trim());
                    //获取信息
                    String msg = data.getString("msg");
                    //被踢出房间或游戏人数大于2
                    if (msg.contains("踢出") && msg.contains(username)) {
                        is_stop = true;
                        player_msg.setText("您已被踢出房间");
                        JOptionPane.showMessageDialog(null,"你已经被踢出房间","提示",
                                JOptionPane.WARNING_MESSAGE);
                        System.exit(0);
                    }
                    //接受别的玩家的信息
                    //更新玩家列表
                    username_list.clear();
                    JSONArray jsonArray = data.getJSONArray("user_list");
                    for (Object o : jsonArray) {
                        username_list.add(o.toString());
                    }

                    if(username_list.size()==1){       //只有一名玩家进入房间时
                        title_label.setText("请等待另一名玩家");
                        fire_btn.setText("...");
                        ice_btn.setText("...");
                        fire_btn.setEnabled(false);
                        ice_btn.setEnabled(false);
                        oth_playerdata.setText("等待中...");
                    }else if (username_list.size()==2&&!is_begin){     //人已到齐
                        title_label.setText("请选择你的角色");
                        fire_btn.setText("火人");
                        ice_btn.setText("冰人");
                        fire_btn.setEnabled(true);
                        ice_btn.setEnabled(true);
                        for (String s : username_list) {
                            if(!s.equals(username)){
                                oth_playerdata.setText(s);//添加另一名玩家
                            }
                        }
                        is_begin=true;
                    }


                    //更新对方玩家选择
                    if(!data.getString("sender").equals(username)){
                        String oth_username = data.getString("sender");
                        if(data.getInt("select")==1) {  //选择了火人
                            if(oth_select==2){  //对面之前选了冰人
                                ice_btn.setText("冰人");
                                ice_btn.setEnabled(true);
                            }
                            fire_btn.setText(oth_username+"已选");
                            fire_btn.setEnabled(false);
                            oth_select=1;
                        }else if(data.getInt("select")==2){ //选择了冰人
                            if(oth_select==1){  //对面之前选了火人
                                fire_btn.setText("火人");
                                fire_btn.setEnabled(true);
                            }
                            ice_btn.setText(oth_username+"已选");
                            ice_btn.setEnabled(false);
                            oth_select=2;
                        }
                    }

                    //两个按钮都被选择时，游戏即将开始
                    if(!fire_btn.isEnabled()&&!ice_btn.isEnabled()&&is_begin){
                        System.out.println("游戏即将开始！！！！");
                        player_msg.setText("游戏即将开始！");
                        time();
                        break;//结束gameroom的接收
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //倒计时开始游戏
        public void time() {
            int time = 5;
            int second = 0;
            while(time>= 0) {
                second = time;
                title_label.setText("         "+second + "秒");
                time--;
                if (time < 0) {
                    System.out.println("游戏开始啦");
                    //打开游戏窗口
                    new Play(username,select,score,socket);
                    dispose();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

