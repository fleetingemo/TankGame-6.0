package tankgame;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

/**
 * @author xienan
 * @version 1.0
 * 坦克大战游戏
 */
@SuppressWarnings({"all"})
public class TankGame extends JFrame {
    MyPanel myPanel = null;
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        TankGame tankGame = new TankGame();
    }

    public TankGame(){
        System.out.println("请输入选择   1：开始新游戏   2：继续上一局游戏");
        String key = scanner.next();
        myPanel = new MyPanel(key);
        new Thread(myPanel).start();

        this.add(myPanel);
        this.setSize(MyPanel.w + 400,MyPanel.h);
        this.addKeyListener(myPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        //在JFrame 中增加相应关闭窗口的处理
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Recorder.keepRecord();
                System.exit(0);
            }
        });
    }
}
