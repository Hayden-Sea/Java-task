package game;

import app.UserDao;

import javax.swing.*;
import java.awt.*;

public class IcePeople extends Thread {
    public Play play;        //声明对象
    String username;
    int user_score;            //用户累计得分
    int score = 0;             //得分
    public JLabel icePeople = new JLabel();
    public int x = 80,y = 650,width = 30,hight = 50;  //人物的坐标，大小
    int Lvelocity,Rvelocity,jump;               //控制左右、上的移动速度
    Rectangle People;                           //人物的矩形封装-
    Boolean up = false,left = false,right = false;       //控制移动的变量
    boolean isAlive = true;                          //控制线程的变量
    boolean ice_flag=false;
    boolean[] eat_flag = new boolean[8];//8个钻石是否被吃

    public IcePeople(Play play){        //对象传参
        this.play = play;
        this.username=play.username;
        this.user_score=play.user_score;
        icePeople = new JLabel(new ImageIcon("images/pPle/小冰人.gif"));
        icePeople.setSize(width, hight);
        icePeople.setLocation(x,y);
    }
    //执行人物左右的方法
    public void move(boolean up,boolean left,boolean right) {
        if(!up&&left&&!right) {         //左
            Lvelocity = 5;
            ice_judge();
            x -= Lvelocity;               //当小人与下挡板碰撞并且没有与左挡板发生碰撞时运行
            icePeople.setLocation(x, y);
            icePeople.setIcon(new ImageIcon("images/pPle/左跑冰人.gif"));
        }else if(!up&&!left&&right) {		//右
            Rvelocity = 5;
            ice_judge();
            x += Rvelocity;
            icePeople.setLocation(x, y);
            icePeople.setIcon(new ImageIcon("images/pPle/右跑冰人.gif"));
        }
        gain();
    }
    /*
     * 碰撞检测，碰到为true，并且在相应位置控制左右移动
     */
    private Boolean ice_judge() {
        People=new Rectangle(x,y+30,30,20);
        if (People.intersects(play.right_rectangle)) {
            jump = 0;
            Rvelocity = 0;
            if(People.intersects(play.rectangle[0]))return true;
            if(People.intersects(play.rectangle[10]))return true;
        }if (People.intersects(play.left_rectangle)){
            Lvelocity = 0;
            jump = 0;
            if(People.intersects(play.rectangle[5]))return true;
            if(People.intersects(play.rectangle[13]))return true;
            if (People.intersects(play.down_rectangle))
                return false;
        }if (People.intersects(play.down_rectangle)) {  //掉进水里?
            if(People.intersects(play.rectangle[1])&&(x+30)>900)Rvelocity=0;
            if((x+30)>180&&(x+30)<400) {
                isAlive=false;//第1个火坑
            }
            jump = 23;
            return true;
        }if (People.intersects(play.rectangle[0])){
            if((y+50)<620)jump = 25;
            if((y+50)>620)  Rvelocity = 0;
            if((y+50)>620&&!People.intersects(play.rectangle[1]))return false;
            return true;
        }if (People.intersects(play.rectangle[1])) {
            if((y+50)>669)  {
                Rvelocity = 0;
                return false;
            }
            return true;
        }if (People.intersects(play.rectangle[2])) {
            jump = 10;
            if((y+50)>645) Lvelocity = 0;
            if((y+50)>645)  Rvelocity = 0;
            if((y+50)>645)return false;
            return true;
        }if (People.intersects(play.rectangle[3])) {
            jump = 10;
            if((y+50)>645) Lvelocity = 0;
            if((y+50)>645)  Rvelocity = 0;
            if((x+30)>580&&(x+30)<780) {
                isAlive=false;//第2个火坑
            }
            if((y+50)>645)return false;
            return true;
        }if (People.intersects(play.rectangle[4])) {
            if((x+30)<151&&(y+50)>535)Lvelocity=0;
            if((y+50)>545)  Lvelocity = 0;
            jump = 12;
            if((x+30)<151) jump = 25;
            if (People.intersects(play.rectangle[5])) return true;
            if((y + 50)>560)return false;
            return true;
        }if (People.intersects(play.rectangle[5])) {
            if((y + 50)>468)Lvelocity = 0;
            if((y + 50)>485)return false;
            return true;
        }if (People.intersects(play.rectangle[6])) {
            jump = 25;
            if((y+50)>430) Lvelocity = 0;
            if((x+30)>230&&(x+30)<440) {
                isAlive=false;
            }
            if((y+50)>430)return false;
            return true;
        }if (People.intersects(play.rectangle[7])) {
            return true;
        }if (People.intersects(play.rectangle[8])) {
            if ((x + 30)>840)Rvelocity = 0;
            jump = 22;
            if((x + 30)>595&&(x + 30)<785) {
                isAlive=false;
            }
            return true;
        }if (People.intersects(play.rectangle[9])) {
            if((y + 50)<336)jump = 22;
            if((y + 50)>342)  Rvelocity = 0;
            if((y + 50)>340)return false;
            if (People.intersects(play.rectangle[10]))
                Rvelocity=0;
            if((y + 50)>340)return false;
            return true;
        }if (People.intersects(play.rectangle[10])) {
            if((y + 50)>260)  Rvelocity = 0;
            if((y + 50)>262)return false;
            return true;
        }if (People.intersects(play.rectangle[11])) {
            if (People.intersects(play.rectangle[12]))
                Lvelocity = 0;
            if((y+50)>195)return false;
            return true;
        }if (People.intersects(play.rectangle[12])) {
            jump = 12;
            if(y>162)Lvelocity = 0;
            if(People.intersects(play.rectangle[13]))Lvelocity = 0;
            return true;
        }if (People.intersects(play.rectangle[13])) {   //水门?
            jump = 12;
            if((x + 30) < 170&& (x +30)>130) {
                ice_flag=true;
//                verdict(1); //到达水门
            }
            return true;
        }
        if (People.intersects(play.Rplank[0])||People.intersects(play.Rplank[1])) {
            if((y + 50)>350) {Lvelocity = 0;Rvelocity = 0;}
            if((y + 50)>345)return false;
            return true;
        }
        return false;
    }
    public void gain() {               //获取得分
        Rectangle ice_People=new Rectangle(x,y + 30,38, 20);
        for(int i = 0;i < 8;i++) {
            if (ice_People.intersects(play.Ice_jewel[i])&&!eat_flag[i]) {     //吃到水钻石
                play.Ijewel[i].setVisible(false);
                eat_flag[i]=true;
                play.Ice_jewel[i] = new Rectangle(0,0,1,1);     //每吃掉一颗钻石就把相应的矩形块块移除
                score += 10;
            }
        }
    }
    //执行游戏胜利或者失败的方法
    public  void verdict(int k) {
        this.stop();
        String s1,s2;
        if(k == 1) {
            s1 = "胜利";
            s2 = "得分："+score+"\n您已经胜利了，游戏结束";
        }else {
            s1 = "失败";
            s2 = "得分："+score+"\n您已经失败了，游戏结束";
        }
        //结算分数上传数据库
        UserDao dao = new UserDao();
        dao.updateScore(username,user_score+score);

        int res = JOptionPane.showConfirmDialog(null, s2, s1, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.YES_OPTION){
            System.exit(0);
        }
//        int res = JOptionPane.showConfirmDialog(null, s2, s1, JOptionPane.YES_NO_OPTION);
//        if(res == JOptionPane.YES_OPTION){
//            play.dispose();
////            new Play(username,2, score);//重玩冰人
//            System.exit(0);
//        }
//        if(res == JOptionPane.NO_OPTION){
//            System.exit(0);
//        }
    }
    //控制小人上下移动的线程
    public void run() {
        while(isAlive) {
            if(up&& ice_judge()) {   //上
                jump = 20;
                ice_judge();
                while(jump >= 0) {
                    y -= jump;
                    icePeople.setLocation(x,y);
                    gain();
                    jump -= 3;
                    try {
                        Thread.sleep(100);
                    }catch(InterruptedException e){}
                }
            }
            int decline = 13;
            while(!ice_judge()) {
                if(!(People.intersects(play.down_rectangle)&&x>150&&x<395)
                        ||!(People.intersects(play.down_rectangle)&&x>570&&x<770))decline = 5;
                y += decline;
                icePeople.setLocation(x, y);
                gain();
                decline += 5;
                try {
                    Thread.sleep(80);
                }
                catch(InterruptedException e){}
            }
        }
    }
}
