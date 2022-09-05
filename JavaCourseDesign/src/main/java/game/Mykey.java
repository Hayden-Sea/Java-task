package game;

import net.sf.json.JSONObject;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Mykey extends KeyAdapter{
    Play  play;
    int player_num;
    Mykey(Play p, int n){
        play=p;
        player_num = n;//操控的角色
    }
    //释放按钮后方向变量改变
    public void keyReleased(KeyEvent e){
        if(player_num ==1) {        //火人
            play.people1.up=false;
            play.people1.left=false;
            play.people1.right=false;
            //
            play.control(player_num,play.people1.up,play.people1.left,play.people1.right);
        }else if(player_num ==2) {       //冰人
            play.people2.up=false;
            play.people2.left=false;
            play.people2.right=false;
            //发送指令
            play.control(player_num,play.people2.up,play.people2.left,play.people2.right);
        }

    }
    public void keyPressed(KeyEvent e) {
        if(player_num ==1){      //火人的操控
            if(e.getKeyCode()==KeyEvent.VK_UP) {
                play.people1.up=true;
            }
            if(e.getKeyCode()==KeyEvent.VK_LEFT) {
                play.people1.left=true;
            }
            if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
                play.people1.right=true;
            }
            //调用移动方向的方法
//            play.people1.move(play.people1.up,play.people1.left,play.people1.right);
            //发送指令
            play.control(player_num,play.people1.up,play.people1.left,play.people1.right);
        }else if(player_num ==2) {
            if(e.getKeyCode()==KeyEvent.VK_UP) {
                play.people2.up=true;
            }
            if(e.getKeyCode()==KeyEvent.VK_LEFT) {
                play.people2.left=true;
            }
            if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
                play.people2.right=true;
            }
//            play.people2.move(play.people2.up,play.people2.left,play.people2.right);
            //发送指令
            play.control(player_num,play.people2.up,play.people2.left,play.people2.right);
        }
    }
}