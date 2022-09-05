package app;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.sf.json.JSONObject;


//继承JFrame实现可视化
public class Server extends JFrame {

    //用户列表，用于存放连接上的用户信息
    ArrayList<User> user_list = new ArrayList<>();
    //用户名列表，用于显示已连接上的用户
    ArrayList<String> username_list = new ArrayList<>();

    //消息显示区域
    JTextArea show_area = new JTextArea();
    //用户名显示区域
    JTextArea show_user = new JTextArea(10, 10);

    //socket的数据输出流
    DataOutputStream outputStream = null;
    //socket的数据输入流
    DataInputStream inputStream = null;

    //构造函数
    public Server() {

        //设置流式布局
        setLayout(new BorderLayout());
        //VERTICAL_SCROLLBAR_AS_NEEDED设置垂直滚动条需要时出现
        //HORIZONTAL_SCROLLBAR_NEVER设置水平滚动条不出现
        //创建信息显示区的画布并添加到show_area
        JScrollPane panel = new JScrollPane(show_area, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //设置信息显示区标题
        panel.setBorder(new TitledBorder("信息显示区"));
        //布局到中央
        add(panel, BorderLayout.CENTER);
        //设置信息显示区为不可编辑
        show_area.setEditable(false);


        //创建用于显示用户的画布
        final JPanel panel_east = new JPanel();
        //添加流式布局
        panel_east.setLayout(new BorderLayout());
        //设置标题
        panel_east.setBorder(new TitledBorder("在线用户"));
        //在用户显示区添加show_uesr
        panel_east.add(new JScrollPane(show_user), BorderLayout.CENTER);
        //设置用户显示区域为不可编辑
        show_user.setEditable(false);
        //将显示用户的画布添加到整体布局的右侧
        add(panel_east, BorderLayout.EAST);

        //创建关于踢下线用户的画布
        final JPanel panel_south = new JPanel();
        //创建标签
        JLabel label = new JLabel("输入要踢下线用户的ID");
        //创建输入框
        JTextField out_area = new JTextField(40);
        //创建踢下线按钮
        JButton out_btn = new JButton("踢下线");
        //依次添加进画布
        panel_south.add(label);
        panel_south.add(out_area);
        panel_south.add(out_btn);
        //将踢下线用户的画布添加到整体布局的下侧
        add(panel_south, BorderLayout.SOUTH);

        //设置踢下线按钮的监听
        out_btn.addActionListener(e -> {
            //用于存储踢下线用户的名字
            String out_username;
            //从输入框中获取踢下线用户名
            out_username = out_area.getText().trim();
            //踢出该用户,踢出类型1
            dismiss(out_username,1);
            //重置输入框
            out_area.setText("");
        });

        //设置该窗口名
        setTitle("服务器 ");
        //引入图片
        BufferedImage img;
        try {
            //根据图片名引入图片
            img = ImageIO.read(Server.class.getResource("/logo/hayden.png"));
            //设置其为该窗体logo
            setIconImage(img);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        //设置窗体大小
        setSize(700, 700);
        //设置窗体位置可移动
        setLocationRelativeTo(null);
        //设置窗体关闭方式
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗体可见
        setVisible(true);

        //socket连接相关代码
        try {
            //开启socket服务器，绑定端口11111
            ServerSocket serverSocket = new ServerSocket(11111);
            //信息显示区打印服务器启动时间
            show_area.append("服务器启动时间 " + new Date() + "\n");
            //持续接收连接
            while (true) {
                //接收连接
                Socket socket = serverSocket.accept();
                //创建用户对象
                User user = new User();
                //判断是否连接上
                if (socket != null) {
                    //获取输入流
                    inputStream = new DataInputStream(socket.getInputStream());
                    //读取输入流
                    String json = inputStream.readUTF();
                    //创建信息对象
                    JSONObject data = JSONObject.fromObject(json);
                    //信息显示区打印用户上线
                    show_area.append("用户 " + data.getString("username") + " 在" + new Date() + "登陆系统" + "\n"+
                            data.getString("username")+"的累计分数为"+data.getString("score")+"\n");
                    //创建新用户
                    user = new User();
                    //存储socket对象
                    user.setSocket(socket);
                    //获取输入流用户名
                    user.setUsername(data.getString("username"));
                    //添加进用户列表
                    user_list.add(user);
                    //添加进用户名列表
                    username_list.add(data.getString("username"));

                    //刷新在线人数
                    show_user.setText("人数有 " + username_list.size() + " 人\n");
                    //刷新在线用户
                    for (String s : username_list) {
                        show_user.append(s + "\n");
                    }
                    //玩家人数大于2时自动踢出，踢出类型2
                    if(username_list.size()>2){
                        dismiss(data.getString("username"),2);
                    }
                }

                //封装信息对象
                JSONObject online = new JSONObject();
                //设置接收信息对象
                online.put("user_list", username_list);
                //设置信息内容
                online.put("msg", user.getUsername() + "上线了");
                //默认未选择角色
                online.put("select",0);
                //发送者为服务端
                online.put("sender","server");
                //依次遍历，将信息广播给所有在线用户
                for (User value : user_list) {
                    //获取输出流
                    outputStream = new DataOutputStream(value.getSocket().getOutputStream());
                    //给所有用户输出上线信息
                    outputStream.writeUTF(online.toString());
                }

                //开启新线程，持续接收该socket信息
                new Thread(new ServerThread(socket)).start();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //用于踢出用户的方法
    private void dismiss(String out_username,int type) {
        try {
            //用于判断盖用户是否被踢下线
            boolean is_out = false;
            //遍历用户列表依次判断
            for (int i = 0; i < user_list.size(); i++) {
                //比较用户名，相同则踢下线
                if (user_list.get(i).getUsername().equals(out_username)) {
                    //获取被踢下线用户对象
                    User out_user = user_list.get(i);
                    //使用json封装将要传递的数据
                    JSONObject data = new JSONObject();
                    //封装全体用户名，广播至所有用户
                    data.put("user_list", username_list);
                    //广播的信息内容
                    data.put("msg", out_user.getUsername() + "被管理员踢出\n");
                    if(type==1){//服务端消息显示区显示相应信息
                        show_area.append(out_user.getUsername() + "被你踢出\n");
                    }else if(type==2){
                        show_area.append("房间已满，自动踢出"+out_user.getUsername()+"\n");
                    }
                    //依次遍历用户列表
                    for (User value : user_list) {
                        try {
                            //获取每个用户列表的socket连接
                            outputStream = new DataOutputStream(value.getSocket().getOutputStream());
                            //传递信息
                            outputStream.writeUTF(data.toString());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    //将被踢用户移出用户列表
                    user_list.remove(i);
                    //将被踢用户移出用户名列表
                    username_list.remove(out_user.getUsername());
                    //刷新在线人数
                    show_user.setText("人数有 " + username_list.size() + " 人\n");
                    //刷新在线用户
                    for (String s : username_list) {
                        show_user.append(s + "\n");
                    }
                    //判断踢出成功
                    is_out = true;
                    break;
                }

            }
            //根据是否踢出成功弹出相应提示
            if (is_out) {
                if(type==1){
                    JOptionPane.showMessageDialog(null, "踢下线成功", "提示",
                            JOptionPane.WARNING_MESSAGE);
                }else if(type==2){
                    JOptionPane.showMessageDialog(null, "房间已满，自动踢出", "提示",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            if (!is_out&&type==1) {
                JOptionPane.showMessageDialog(null, "不存在用户", "提示",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //从主函数里面开启服务端
    public static void main(String[] args) {
        new Server();
    }

    //线程代码
    class ServerThread implements Runnable {
        //存放全局变量socket
        private final Socket socket;

        //构造函数，初始化socket
        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                //获取输入流
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                //持续接收信息
                while (true) {
                    //获取传递进来的信息
                    String json = inputStream.readUTF();
                    //封装成json格式
                    JSONObject data = JSONObject.fromObject(json.trim());
                    //通过json里面的private判断是否私发
                    boolean is_private = false;

                    //私发处理，发送游戏指令
                    if(data.getString("msg").contains("control")){
                        int select = data.getInt("select");
                        Boolean up=data.getBoolean("up");
                        Boolean left=data.getBoolean("left");
                        Boolean right=data.getBoolean("right");
                        //发送指令给两位玩家
                        send_command(select,up,left,right);
                        is_private=true;
                    }
                    //非私发的情况
                    if (!is_private) {
                        //构建信息内容
                        String msg = data.getString("username") + " " + data.getString("time") + ":\n"
                                + data.getString("msg");
                        //添加到服务器显示
                        show_area.append(msg + "\n");
                        int select = data.getInt("select");
                        //依次发给所有在线用户
                        for (int i = 0; i < user_list.size(); ) {
                            send_msg(data.getString("username"),i, msg,select);//发送信息
                            i++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //发送信息给指定用户的方法
        //username向序号为i的用户发送msg和select
        public void send_msg(String username,int i, String msg,int select) {
            //构建对象
            JSONObject data = new JSONObject();
            //封装信息
            data.put("user_list", username_list);
            data.put("sender",username);
            data.put("msg", msg);
            data.put("select",select);
            //获取目标对象
            User user = user_list.get(i);
            try {
                //获取输出流
                outputStream = new DataOutputStream(user.getSocket().getOutputStream());
                //写信息
                outputStream.writeUTF(data.toString());
            } catch (IOException e) {
                //如果没有找到，则说明该用户已经下线
                User out_user = user_list.get(i);
                //重复删除操作
                user_list.remove(i);
                username_list.remove(out_user.getUsername());
                //重新构建信息
                JSONObject out = new JSONObject();
                out.put("user_list", username_list);
                out.put("msg", out_user.getUsername() + "下线了\n");
                //将其下线通知广播给所有用户
                for (User value : user_list) {
                    try {
                        outputStream = new DataOutputStream(value.getSocket().getOutputStream());
                        outputStream.writeUTF(out.toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }

        private void send_command(int select, Boolean up, Boolean left, Boolean right) {
            //重新构建信息
            JSONObject data = new JSONObject();
            //这里有bug！！
            String msg = "control2";
            data.put("msg",msg);
            data.put("select",select);//表示操控角色
            data.put("up",up);
            data.put("left",left);
            data.put("right",right);
            //广播给所有用户
            for (User value : user_list) {
                try {
//                    System.out.println("2次发送的json");
//                    System.out.println(data.toString());
                    outputStream = new DataOutputStream(value.getSocket().getOutputStream());
                    outputStream.writeUTF(data.toString().trim());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

}
