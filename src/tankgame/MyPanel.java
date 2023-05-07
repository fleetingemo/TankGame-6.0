package tankgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Vector;

/**
 * @author xienan
 * @version 1.0
 * 坦克大战绘图区
 *
 *
 */
@SuppressWarnings({"all"})
// TODO KeyListener是监听器，可以监听键盘事件
// TODO 为了让Panel不停重绘子弹，需要将 MyPanel实现Runnable接口，当做一个线程
public class MyPanel extends JPanel implements KeyListener,Runnable{
    //初始化面板大小
    static int h = 800;
    static int w = 1000;

    //定义我方坦克
    HeroTank heroTank = null;

    //定义敌方坦克
    Vector<EnemyTank> enemyTanks = new Vector<>();
    int enemyTankSize = 3;//敌人坦克数量

    //定义一个存放Node的Vector，用户恢复敌人的坦克和坐标
    Vector<Node> nodes = new Vector<>();

    //定义一个Vector, 用于存放炸弹,当子弹击中坦克时，就加入爆炸效果
    Vector<Bomb> bombs = new Vector<>();

    //定义三张图片，用于显示爆炸效果
    Image image1 = null;
    Image image2 = null;
    Image image3 = null;

    public MyPanel(String key){
        //先判断记录文件是否存在，如果存在，就正常执行，否则，只能开启新游戏
        File file = new File(Recorder.getRecordFile());
        if (!file.exists() && key.equals("2")){
            System.out.println("这是第一局，只能开启新游戏");
            key = "1";
        }

        //将敌方坦克列表指向Recorder中的enemyTanks
        Recorder.setEnemyTanks(enemyTanks);

        //初始化一个自己的坦克
        heroTank = new HeroTank(500,100);
        //heroTank.setSpeed(2);//坦克速度默认为1

        switch (key){
            case "1":
                //初始化敌人的坦克
                for (int i = 0; i < enemyTankSize; i++) {
                    //创建敌人坦克
                    EnemyTank et = new EnemyTank((100 * (i + 1)), 0);
                    //将敌人坦克列表赋给每个坦克
                    et.setEnemyTanks(enemyTanks);
                    et.setType(1);
                    et.setDirect(2);

                    //创建敌人坦克线程
                    new Thread(et).start();

                    //给敌方创建一个子弹
                    Shot shot = new Shot(et.getX() + 20, et.getY() + 60, et.getDirect());
                    //将子弹加入地方的弹药仓shots中
                    et.shots.add(shot);
                    //启动敌方shot对象
                    new Thread(shot).start();

                    //将创建好的一个地方坦克加入坦克队列
                    enemyTanks.add(et);
                }
                break;
            case "2":
                //如果游戏开始用户想要恢复上一局游戏，则调用getNodesAndEnemyTankNumRec()方法恢复敌方坦克信息
                nodes = Recorder.getNodesAndEnemyTankNumRec();
                //继续上局游戏
                for (int i = 0; i < nodes.size(); i++) {
                    //取出上一局存档的敌人坦克，并创建
                    Node node = nodes.get(i);
                    EnemyTank et = new EnemyTank(node.getX(), node.getY());
                    //将敌人坦克列表赋给每个坦克
                    et.setEnemyTanks(enemyTanks);
                    et.setType(1);
                    et.setDirect(node.getDirect());

                    //创建敌人坦克线程
                    new Thread(et).start();

                    //给敌方创建一个子弹
                    Shot shot = new Shot(et.getX() + 20, et.getY() + 60, et.getDirect());
                    //将子弹加入地方的弹药仓shots中
                    et.shots.add(shot);
                    //启动敌方shot对象
                    new Thread(shot).start();

                    //将创建好的一个地方坦克加入坦克队列
                    enemyTanks.add(et);
                }
                break;
            default:
                System.out.println("你的输入有误");
        }

        image1 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("tankBomb1.png"));
        image2 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("tankBomb2.png"));
        image3 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("tankBomb3.png"));

        //播放指定音乐
        new AePlayWave("blyj.wav").start();
    }

    //编写方法，显示我方击毁敌方坦克信息
    public void showAchievement(Graphics g){
        g.setColor(Color.LIGHT_GRAY);
        g.fill3DRect(MyPanel.w, 0, 400, MyPanel.h, false);
        //画出玩家总成绩
        g.setColor(Color.white);
        Font font = new Font("宋体", Font.BOLD, 25);
        g.setFont(font);
        g.drawString("您累计击毁敌方坦克数量", MyPanel.w + 50, 30);
        drawTank(MyPanel.w + 50, 60, g, 0, 1);
        g.setColor(Color.white);
        g.drawString(Recorder.getShotEnemyTankNum() + "", MyPanel.w + 110, 100);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //设置面板大小
        g.fillRect(0,0,w, h);

        showAchievement(g);
        // 画出自己的坦克-封装方法
        if (heroTank != null && heroTank.isLive){
            drawTank(heroTank.getX(), heroTank.getY(), g, heroTank.getDirect(), heroTank.getType());
        }

//        //画我方的子弹，一颗
//        if(heroTank.shot != null && heroTank.shot.isLive){
//            //g.fill3DRect(heroTank.shot.x, heroTank.shot.y, 3,3,false);
//            g.draw3DRect(heroTank.shot.x, heroTank.shot.y, 2, 2,false);
//        }

        //绘制我方子弹（多颗）
        for (int i  = 0; i < heroTank.shots.size(); i++){
            Shot s = heroTank.shots.get(i);
            if(s != null && s.isLive){
                g.draw3DRect(s.x, s.y, 2, 2,false);
            }else {//如果子弹无效了，就移除
                heroTank.shots.remove(s);
            }
        }

        //如果bombs集合中有对象时，就画爆炸效果
        for (int i = 0; i < bombs.size(); i++){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //取出炸弹
            Bomb bomb = bombs.get(i);
            //根据当前这个bomb的life值画出对应图片
            if(bomb.life > 6){
                g.drawImage(image1, bomb.x,bomb.y,60,60,this);

            }else if(bomb.life > 3){
                g.drawImage(image2, bomb.x,bomb.y,60,60,this);
            }else {
                g.drawImage(image3, bomb.x,bomb.y,60,60,this);
            }
            //让生命值减少
            bomb.lifeDown();

            //如果生命周期为0，则删除炸弹
            if (bomb.life == 0){
                bombs.remove(bomb);
            }

        }

        //画敌人的坦克
        for (int i = 0; i < enemyTanks.size(); i++) {
            EnemyTank enemyTank = enemyTanks.get(i);
            if (enemyTank.isLive){
                drawTank(enemyTank.getX(), enemyTank.getY(), g, enemyTank.getDirect(),enemyTank.getType());
                //画出enemyTank所有子弹
                for (int j = 0; j < enemyTank.shots.size(); j ++){
                    //取出子弹
                    Shot shot = enemyTank.shots.get(j);
                    //绘制子弹
                    if(shot.isLive){
                        //System.out.println("敌方子弹被绘制");
                        g.draw3DRect(shot.x, shot.y, 2, 2, false);
                    }else {
                        //子弹不是活性的了
                        enemyTank.shots.remove(shot);
                    }
                }
            }

        }

    }

    /**
     * 画坦克
     * x 坦克左上角横坐标
     * y 坦克左上角纵坐标
     * g 画笔
     * direct 坦克方向
     * type 坦克类型
     * */
    public void drawTank(int x, int y, Graphics g, int direct, int type){
        switch (type){
            case 0://自己的坦克
                g.setColor(Color.CYAN);
                break;
            case 1://敌人坦克
                g.setColor(Color.yellow);
                break;

        }

        //根据不同的方向，绘制不同的坦克
        switch (direct){
            case 0://向上
                g.fill3DRect(x, y, 10, 60, false);
                g.fill3DRect(x + 10, y + 10, 20, 40, false);
                g.fill3DRect(x + 30, y, 10, 60, false);
                g.fillOval(x + 10, y + 20, 20, 20);
                g.drawLine(x + 20, y + 30, x + 20, y);
                break;

            case 1://向右
                g.fill3DRect(x, y, 60, 10, false);
                g.fill3DRect(x , y + 30, 60, 10, false);
                g.fill3DRect(x + 10, y + 10, 40, 20, false);
                g.fillOval(x + 20, y + 10, 20, 20);
                g.drawLine(x + 30, y + 20, x +60, y + 20);
                break;

            case 2://向下
                g.fill3DRect(x, y, 10, 60, false);
                g.fill3DRect(x + 10, y + 10, 20, 40, false);
                g.fill3DRect(x + 30, y, 10, 60, false);
                g.fillOval(x + 10, y + 20, 20, 20);
                g.drawLine(x + 20, y + 30, x + 20, y + 60);
                break;

            case 3://向左
                g.fill3DRect(x, y, 60, 10, false);
                g.fill3DRect(x , y + 30, 60, 10, false);
                g.fill3DRect(x + 10, y + 10, 40, 20, false);
                g.fillOval(x + 20, y + 10, 20, 20);
                g.drawLine(x + 30, y + 20, x, y + 20);
                break;
        }
    }

    //TODO 判断子弹是否击中坦克
    //  这里是在判断一颗的
    public void hitTank(Shot s, Tank tank){
        //判断s是否击中了坦克,不挂子弹的方向，只看子弹是否 落在 坦克周围的范围
        switch (tank.getDirect()){
            case 0://坦克向上
            case 2://坦克向下
                if(s.x > tank.getX() && s.x < tank.getX() + 40 && s.y >tank.getY() && s.y < tank.getY() + 60){
                    s.isLive = false;
                    tank.isLive = false;
                    enemyTanks.remove(tank);
                    if (tank instanceof EnemyTank){
                        Recorder.addShotEnemyTankNum();
                    }
                    Bomb bomb = new Bomb(tank.getX(), tank.getY());
                    bombs.add(bomb);
                }
                break;
            case 1://坦克向右
            case 3://坦克向左
                if(s.x > tank.getX() && s.x < tank.getX() + 60 && s.y > tank.getY() && s.y < tank.getY() + 40){
                    s.isLive = false;
                    tank.isLive = false;
                    enemyTanks.remove(tank);
                    if (tank instanceof EnemyTank){
                        Recorder.addShotEnemyTankNum();
                    }
                    Bomb bomb = new Bomb(tank.getX(), tank.getY());
                    bombs.add(bomb);
                }
        }
    }

    //TODO 如果我方可以发射多颗子弹，那么就需要判断我方所有子弹是否击中了敌方子弹
    public void hitEnemyTank(){
        for (int j = 0; j < heroTank.shots.size(); j++) {
            Shot shot = heroTank.shots.get(j);
            //判断是否击中了敌人坦克
            if (shot != null && shot.isLive){
                //遍历敌人所有的坦克
                for (int i = 0; i < enemyTanks.size(); i++){
                    EnemyTank enemyTank = enemyTanks.get(i);
                    hitTank(shot, enemyTank);
                }
            }
        }
    }

    //TODO 判断敌方子弹是否击中我方坦克，若击中则显示爆炸效果
    public void hitHeroTank(){
        //遍历敌人所有坦克
        for (int i = 0; i < enemyTanks.size(); i++){
            EnemyTank enemyTank = enemyTanks.get(i);
            //遍历敌人坦克的所有子弹
            for (int j = 0; j < enemyTank.shots.size(); j++) {
                Shot shot = enemyTank.shots.get(j);
                if (heroTank.isLive && shot.isLive){
                    hitTank(shot, heroTank);
                }
            }
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP){//向上移
            heroTank.setDirect(0);
            if(heroTank.getY() > 0){
                heroTank.moveUp();
            }
        }else if (e.getKeyCode() == KeyEvent.VK_RIGHT){//向右移
            heroTank.setDirect(1);
            if (heroTank.getX() + 60 < w){
                heroTank.moveRight();
            }
        }else if (e.getKeyCode() == KeyEvent.VK_DOWN){//向下移
            heroTank.setDirect(2);
            if (heroTank.getY() + 60 < h){
                heroTank.moveDown();
            }
        }else if(e.getKeyCode() == KeyEvent.VK_LEFT){//向左移
            heroTank.setDirect(3);
            if (heroTank.getX() > 0){
                heroTank.moveLeft();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_J){
            //TODO 发射一颗子弹的情况
//            if(heroTank.shot == null || !heroTank.shot.isLive){
//                heroTank.shotEnemyTank();
//            }

            //TODO 想同时发射多颗
            heroTank.shotEnemyTank();

        }
        hitHeroTank();
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //判断是否击中了敌人坦克
            hitEnemyTank();

            //判断我方坦克是否受到攻击
            hitHeroTank();

            this.repaint();
        }
    }
}
