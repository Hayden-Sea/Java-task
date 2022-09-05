package game;

import net.sf.json.JSONObject;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

//游玩界面
public class Play extends JFrame{
    private int x = 350,y = 100,wide = 1010,hight = 800;
    String username;
    int user_score;
    int select =0;//为玩家角色，1为火，2为冰
    JPanel playPanel;
    JLabel backgroundPicture;     //背景图片
    JLabel timer=new JLabel();    //计时器
    ImageIcon Fimage,Iimage,Cimage,Pimage;        //火/冰钻石
    JLabel[] Fjewel=new JLabel[8];                //火钻石标签数组
    JLabel[] Ijewel=new JLabel[8];                //冰
    JLabel[] door=new JLabel[2];                  //门
    JLabel[] plank=new JLabel[2];                 //木板
    Rectangle[] rectangle=new Rectangle[14];      //障碍挡板矩形块
    Rectangle[] Fire_jewel =new Rectangle[8],Ice_jewel =new Rectangle[8],//火水钻石矩形块
    Rdoor=new Rectangle[2], Rplank=new Rectangle[2] ;     //门、木板

    Rectangle down_rectangle,left_rectangle,right_rectangle;      //下、左右侧障碍
    //!!!
    JLabel FirePeople,IcePeople;
    //创建两个游戏角色对象
    game.FirePeople people1;
    game.IcePeople people2;
    Mykey myKey;
    //游戏房间对象
    app.GameRoom gameRoom;
    //socket的数据输出流
    DataOutputStream outputStream = null;
    //socket的数据输入流
    DataInputStream inputStream = null;
    Socket socket;

    Thread thred1;
    //构造方法
    public Play(String username, int select, int score, Socket socket){ //p为玩家角色，1为火，2为冰
        super("森林冰火人");      //调用父类的初始化方法
        this.username=username;
        this.select = select;
        this.user_score =score;
        this.socket=socket;

        //传递选择信息给服务端
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        JSONObject data = new JSONObject();
        data.put("username", username);
        data.put("msg", "进入了游戏");
        data.put("time", time);
        data.put("select", this.select);
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(data.toString());
            new Thread(new Play.Read()).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"服务器无响应","提示",
                    JOptionPane.WARNING_MESSAGE);
        }


        myKey = new Mykey(this,select);

        people1 = new FirePeople(this);
        people2 = new IcePeople(this);
        playPanel=new JPanel();
        new TimeThread().start();
        orthogon();              //创建矩形块
        playPanel.setOpaque(true);      //控件不透明
        playPanel.setBounds(0, 0,wide,hight);
        this.setBounds(x, y,wide,hight);
        setBack();               //添加控件
        this.setVisible(true);     //窗口可见
        this.setResizable(false);   //窗口大小不可改变
        //结束窗口所在的应用程序。在窗口被关闭的时候会退出JVM。
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void setBack(){
        //创建一张带图片的Lable
        backgroundPicture=new JLabel(new ImageIcon("images/plPicture/游戏主页面背景.jpg"));
        timer.setBounds(450, -10, 200, 50);
        timer.setFont((new java.awt.Font("Dialog", 1, 30)));
        timer.setForeground(Color.black);
        backgroundPicture.setBounds(0, 0,wide,hight);
        Fimage=new ImageIcon("images/plPicture/火钻石.jpg");
        Iimage=new ImageIcon("images/plPicture/水钻石.jpg");
        Pimage=new ImageIcon("images/plPicture/木板.jpg");

        //添加操控角色说明
        if(select ==1){
            JLabel select_msg = new JLabel(username+",你的角色是：火人");
            select_msg.setFont(new Font("宋体", Font.BOLD, 20));
            select_msg.setForeground(Color.RED);
            select_msg.setBounds(10, 30, 300, 60);
            add(select_msg);
        }else if (select ==2){
            JLabel select_msg = new JLabel(username+",你的角色是：冰人");
            select_msg.setFont(new Font("宋体", Font.BOLD, 20));
            select_msg.setForeground(Color.CYAN);
            select_msg.setBounds(10, 30, 300, 60);
            add(select_msg);
        }



        //添加钻石
        for(int i = 0;i < 8;i++) {
            Fjewel[i]=new JLabel(Fimage);
            Ijewel[i]=new JLabel(Iimage);
        }
        //设置钻石坐标
        Fjewel[0].setBounds(465, 88, 30, 25);
        Fjewel[1].setBounds(465, 345, 30, 25);
        Fjewel[2].setBounds(390, 495, 30, 25);
        Fjewel[3].setBounds(720, 495, 30, 25);
        Fjewel[4].setBounds(620, 620, 30, 25);
        Fjewel[5].setBounds(690, 620, 30, 25);
        Fjewel[6].setBounds(213, 695, 30, 25);
        Fjewel[7].setBounds(315, 695, 30, 25);
        //设置冰钻石坐标
        Ijewel[0].setBounds(515, 88, 30, 25);
        Ijewel[1].setBounds(515, 345, 30, 25);
        Ijewel[2].setBounds(263, 495, 30, 25);
        Ijewel[3].setBounds(593, 495, 30, 25);
        Ijewel[4].setBounds(213, 620, 30, 25);
        Ijewel[5].setBounds(315, 620,30, 25);
        Ijewel[6].setBounds(618, 695, 30, 25);
        Ijewel[7].setBounds(695, 695, 30, 25);
        //添加门
        door[0]=new JLabel(new ImageIcon("images/plPicture/火门.jpg"));
        door[1]=new JLabel(new ImageIcon("images/plPicture/水门.jpg"));
        door[0].setBounds(50,55,50,100);
        door[1].setBounds(125, 55, 50, 100);
        //添加木板
        plank[0]=new JLabel(Pimage);
        plank[1]=new JLabel(Pimage);
        plank[0].setBounds(280, 340, 100, 20);
        plank[1].setBounds(635, 340, 100, 20);
        //添加人物
        //添加火人
        FirePeople= people1.firePeople;
        FirePeople.setSize(people1.width, people1.hight);
        FirePeople.setLocation(people1.x, people1.y);
        backgroundPicture.add(FirePeople);
        //添加冰人
        IcePeople=people2.icePeople;
        IcePeople.setSize(people2.width,people2.hight);
        IcePeople.setLocation(people2.x, people2.y);
        backgroundPicture.add(IcePeople);


        backgroundPicture.add(timer);
        for(int i=0;i<8;i++) {
            backgroundPicture.add(Fjewel[i]);
            backgroundPicture.add(Ijewel[i]);
            if(i<2)backgroundPicture.add(door[i]);
        }
        backgroundPicture.add(plank[0]);
        backgroundPicture.add(plank[1]);
        playPanel.add(backgroundPicture);
        this.add(playPanel);
        this.setFocusable(true);              //获取焦点
        this.addKeyListener(myKey);
        try {
            people1.start();
            people2.start();//????
        }catch(NumberFormatException p) {}
    }
    //把钻石，短板，木板设置成矩形块
    private void orthogon() {
        down_rectangle=new Rectangle(0,715,1010,25);//715
        left_rectangle=new Rectangle(0,0,40,800);
        right_rectangle=new Rectangle(970,0,40,620);
        rectangle[0]=new Rectangle(945,615,40,100);
        rectangle[1]=new Rectangle(895,660,50,50);
        rectangle[2]=new Rectangle(160,640,230,25);
        rectangle[3]=new Rectangle(565,640,205,25);
        rectangle[4]=new Rectangle(110,535,780,30);
        rectangle[5]=new Rectangle(40,463,75,102);
        rectangle[6]=new Rectangle(170,413,290,30);
        rectangle[7]=new Rectangle(435,385,130,60);
        rectangle[8]=new Rectangle(575,418,997,30);
        rectangle[9]=new Rectangle(840,335,205,73);
        rectangle[10]=new Rectangle(900,255,175,75);
        rectangle[11]=new Rectangle(815,185,55,25);
        rectangle[12]=new Rectangle(770,155,55,55);
        rectangle[13]=new Rectangle(40,135,750,30);
        //火钻石
        Fire_jewel[0]=new Rectangle(465, 88, 30, 25);
        Fire_jewel[1]=new Rectangle(465, 345, 30, 25);
        Fire_jewel[2]=new Rectangle(410, 495, 30, 25);
        Fire_jewel[3]=new Rectangle(720, 495, 30, 25);
        Fire_jewel[4]=new Rectangle(620, 620, 30, 25);
        Fire_jewel[5]=new Rectangle(690, 620, 30, 25);
        Fire_jewel[6]=new Rectangle(200, 695, 30, 25);
        Fire_jewel[7]=new Rectangle(315, 695, 30, 25);
        //水钻石
        Ice_jewel[0]=new Rectangle(515, 88, 30, 25);
        Ice_jewel[1]=new Rectangle(515, 345, 30, 25);
        Ice_jewel[2]=new Rectangle(263, 495, 30, 25);
        Ice_jewel[3]=new Rectangle(593, 495, 30, 25);
        Ice_jewel[4]=new Rectangle(213, 620, 30, 25);
        Ice_jewel[5]=new Rectangle(315, 620,30, 25);
        Ice_jewel[6]=new Rectangle(618, 695, 30, 25);
        Ice_jewel[7]=new Rectangle(695, 695, 30, 25);

        //木板
        Rplank[0]=new Rectangle(280, 340, 100, 20);
        Rplank[1]=new Rectangle(635, 340, 100, 20);
    }

    //发送控制信息
    public void control(int select,Boolean up, Boolean left, Boolean right) {
        //发送指令
        JSONObject data = new JSONObject();
        String msg = "control1";
        data.put("msg",msg);
        data.put("select",select);//表示操控角色
        data.put("up",up);
        data.put("left",left);
        data.put("right",right);
        try {
//            System.out.println("发送的json");
//            System.out.println(data.toString());
            outputStream.writeUTF(data.toString().trim());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*
     * 计时器——线程
     * 设置初试时间为两分钟，到时间未能通关则执行People类的verdict()方法
     */
    class TimeThread extends Thread {
        int time = 300;
        int minute = 0;
        int second = 0;
        public void run() {
            while(time>= 0) {
                minute = time / 60;
                second = time - (minute * 60);
                timer.setText(minute + "分" + second + "秒");
                time--;
                if (time < 0) {     //时间不足结束游戏
                    if(select==1) people1.verdict(2);
                    else if(select==2) people2.verdict(2);
                }
                if(!people1.isAlive || !people2.isAlive){         //触碰坑游戏失败
                    if(select==1) people1.verdict(2);
                    else if(select==2) people2.verdict(2);
                }
                if(people1.fire_flag&&people2.ice_flag){        //游戏胜利
                    if(select==1) people1.verdict(1);
                    else if(select==2) people2.verdict(1);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

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
                    //接收到操控信息
                    if(msg.contains("control")){
                        int select = data.getInt("select");
                        Boolean up = data.getBoolean("up");
                        Boolean left = data.getBoolean("left");
                        Boolean right = data.getBoolean("right");
                        if (select==1){
                            //操控火人
                            people1.up=up;
                            people1.left=left;
                            people1.right=right;
                            people1.move(up,left,right);
                        }else if(select==2){
                            people2.up=up;
                            people2.left=left;
                            people2.right=right;
                            people2.move(up,left,right);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
