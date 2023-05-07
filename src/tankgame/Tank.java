package tankgame;

import java.util.Vector;

/**
 * @author xienan
 * @version 1.0
 * 坦克信息区
 *
 * * 让敌方坦克可以自由移动
 * 1、因为要让敌人可以自由移动，因此需要让敌人坦克当做线程使用
 * 2、我们需要让EnemyTank implement Runnable
 * 3、在run上写上我们相应的业务代码
 * 4、在创建敌人坦克时就启动线程
 */
@SuppressWarnings({"all"})
public class Tank {
    private int x;//横坐标
    private int y;//纵坐标
    private int direct = 0;//坦克方向 0上 1右 2下 3左
    private int speed = 2; //坦克速度
    private int type;//坦克类型
    public boolean isLive = true;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    //坦克向上移
    public void moveUp(){
        y -= speed;
    }

    //坦克向右移
    public void moveRight(){
        x += speed;
    }

    //坦克向上移
    public void moveLeft(){
        x -= speed;
    }
    //坦克向下移
    public void moveDown(){
        y += speed;
    }
    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public Tank(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    //根据当前的方向创建子弹
    public Shot createShot(int d){
        Shot shot = null;
        switch (d){//得到坦克不同的方向，创建不同的Shot对象
            case 0://上
                shot = new Shot(getX() + 20, getY(), 0);
                break;

            case 1://右
                shot = new Shot(getX() + 60, getY() + 20, 1);
                break;

            case 2://下
                shot = new Shot(getX() + 20, getY() + 60, 2);
                break;

            case 3://左
                shot = new Shot(getX(), getY() + 20, 3);
                break;
        }
        return shot;
    }
}

class HeroTank extends Tank{
    Shot shot = null;
    Vector<Shot> shots = new Vector<>();

    public HeroTank(int x, int y) {
        super(x, y);
    }

    public void shotEnemyTank(){
        if(shots.size() >= 10){
            System.out.println("最多同时只能发射10颗子弹");
            return;
        }
        //创建子弹对象，要根据当前Hero对象的位置和方法来创建Shot
//        switch (getDirect()){//得到Hero不同的方向，创建不同的Shot对象
//            case 0://上
//                shot = new Shot(getX() + 20, getY(), 0);
//                break;
//
//            case 1://右
//                shot = new Shot(getX() + 60, getY() + 20, 1);
//                break;
//
//            case 2://下
//                shot = new Shot(getX() + 20, getY() + 60, 2);
//                break;
//
//            case 3://左
//                shot = new Shot(getX(), getY() + 20, 3);
//                break;
//        }
        shot = createShot(getDirect());
        shots.add(shot);
        //启动线程
        new Thread(shot).start();
    }

}

class EnemyTank  extends Tank implements Runnable{
    public EnemyTank(int x, int y) {
        super(x, y);
    }
    //在敌人坦克类中，一个子弹列表
    Vector<Shot>  shots = new Vector<>();

    //需要让敌人坦克不重叠，先设置每个敌人坦克列表，即每个敌人坦克都可以得到当前敌方的坦克列表
    Vector<EnemyTank> enemyTanks = new Vector<>();
    //提供一个方法，可以将MyPanel的敌方列表成员，设置到敌方成员中
    public  void setEnemyTanks(Vector<EnemyTank> enemyTanks){
        this.enemyTanks = enemyTanks;
    }

    //编写方法，判断当前敌方坦克是否和其他地方其他坦克发射重叠
    public boolean isTouchEnemyTank(){
        //判断当前敌人坦克（this）的方向
        switch (this.getDirect()){
            case 0://上
                for (int i = 0; i < enemyTanks.size(); i++){
                    EnemyTank enemyTank = enemyTanks.get(i);
                    if(enemyTank != this){
                        //其余敌人坦克上/下
                        if (enemyTank.getDirect() == 0 || enemyTank.getDirect() == 2){
                            if(this.getX() >= enemyTank.getX() && this.getX() <= enemyTank.getX() + 40 && this.getY() >= enemyTank.getY() && this.getY() <= enemyTank.getY() + 60){
                                return true;
                            }
                            if(this.getX() + 40 >= enemyTank.getX() && this.getX() + 40 <= enemyTank.getX() + 40 && this.getY() >= enemyTank.getY() && this.getY() <= enemyTank.getY() + 60){
                                return true;
                            }
                        }
                        //其余敌人坦克左/右
                        if (enemyTank.getDirect() == 1 || enemyTank.getDirect() == 3){
                            if(this.getX() >= enemyTank.getX() && this.getX() <= enemyTank.getX() + 60 && this.getY() >= enemyTank.getY() && this.getY() <= enemyTank.getY() + 40){
                                return true;
                            }
                            if(this.getX() + 40 >= enemyTank.getX() && this.getX() + 40 <= enemyTank.getX() + 60 && this.getY() >= enemyTank.getY() && this.getY() <= enemyTank.getY() + 40){
                                return true;
                            }
                        }
                    }
                }
                break;

            case 1://右
                for (int i = 0; i < enemyTanks.size(); i++){
                    EnemyTank enemyTank = enemyTanks.get(i);
                    if(enemyTank != this){
                        //其余敌人坦克上/下
                        if (enemyTank.getDirect() == 0 || enemyTank.getDirect() == 2){
                            if(this.getX() + 60 >= enemyTank.getX() && this.getX() + 60 <= enemyTank.getX() + 40 && this.getY() >= enemyTank.getY() && this.getY() <= enemyTank.getY() + 60){
                                return true;
                            }
                            if(this.getX() + 60 >= enemyTank.getX() && this.getX() + 60 <= enemyTank.getX() + 40 && this.getY() + 40 >= enemyTank.getY() && this.getY() + 40 <= enemyTank.getY() + 60){
                                return true;
                            }
                        }
                        //其余敌人坦克左/右
                        if (enemyTank.getDirect() == 1 || enemyTank.getDirect() == 3){
                            if(this.getX() + 60 >= enemyTank.getX() && this.getX() + 60 <= enemyTank.getX() + 60 && this.getY() >= enemyTank.getY() && this.getY() <= enemyTank.getY() + 40){
                                return true;
                            }
                            if(this.getX() + 60 >= enemyTank.getX() && this.getX() + 60 <= enemyTank.getX() + 60 && this.getY() + 40 >= enemyTank.getY() && this.getY() + 40 <= enemyTank.getY() + 40){
                                return true;
                            }
                        }
                    }
                }
                break;

            case 2://下
                for (int i = 0; i < enemyTanks.size(); i++){
                    EnemyTank enemyTank = enemyTanks.get(i);
                    if(enemyTank != this){
                        //其余敌人坦克上/下
                        if (enemyTank.getDirect() == 0 || enemyTank.getDirect() == 2){
                            if(this.getX() >= enemyTank.getX() && this.getX()  <= enemyTank.getX() + 40 && this.getY() + 60 >= enemyTank.getY() && this.getY() + 60 <= enemyTank.getY() + 60){
                                return true;
                            }
                            if(this.getX() + 40 >= enemyTank.getX() && this.getX() + 40 <= enemyTank.getX() + 40 && this.getY() + 60 >= enemyTank.getY() && this.getY() + 60 <= enemyTank.getY() + 60){
                                return true;
                            }
                        }
                        //其余敌人坦克左/右
                        if (enemyTank.getDirect() == 1 || enemyTank.getDirect() == 3){
                            if(this.getX() >= enemyTank.getX() && this.getX() <= enemyTank.getX() + 60 && this.getY() + 60 >= enemyTank.getY() && this.getY() + 60 <= enemyTank.getY() + 40){
                                return true;
                            }
                            if(this.getX() + 40 >= enemyTank.getX() && this.getX() + 40 <= enemyTank.getX() + 60 && this.getY() + 60 >= enemyTank.getY() && this.getY() + 60 <= enemyTank.getY() + 40){
                                return true;
                            }
                        }
                    }
                }
                break;

            case 3://左
                for (int i = 0; i < enemyTanks.size(); i++){
                    EnemyTank enemyTank = enemyTanks.get(i);
                    if(enemyTank != this){
                        //其余敌人坦克上/下
                        if (enemyTank.getDirect() == 0 || enemyTank.getDirect() == 2){
                            if(this.getX() >= enemyTank.getX() && this.getX() <= enemyTank.getX() + 40 && this.getY() >= enemyTank.getY() && this.getY() <= enemyTank.getY() + 60){
                                return true;
                            }
                            if(this.getX() >= enemyTank.getX() && this.getX() <= enemyTank.getX() + 40 && this.getY() + 40 >= enemyTank.getY() && this.getY() + 40 <= enemyTank.getY() + 60){
                                return true;
                            }
                        }
                        //其余敌人坦克左/右
                        if (enemyTank.getDirect() == 1 || enemyTank.getDirect() == 3){
                            if(this.getX() >= enemyTank.getX() && this.getX() <= enemyTank.getX() + 60 && this.getY() >= enemyTank.getY() && this.getY() <= enemyTank.getY() + 40){
                                return true;
                            }
                            if(this.getX() >= enemyTank.getX() && this.getX() <= enemyTank.getX() + 60 && this.getY() + 40 >= enemyTank.getY() && this.getY() + 40 <= enemyTank.getY() + 40){
                                return true;
                            }
                        }
                    }
                }
                break;
        }
        return false;
    }


    @Override
    public void run() {
        while (true){
            //如果敌方坦克还活着，并且发射的子弹颗数小于一定数量（3），就给它创建子弹，并启动
            if(isLive && shots.size() < 3){
                Shot s = createShot(getDirect());
                shots.add(s);
                new Thread(s).start();
            }
            //根据坦克方向来继续移动
            //让坦克在固定方向移动30步再随机变换方向
            switch (getDirect()){
                case 0://向上
                    for (int i = 0; i < 30; i++) {
                        if(getY() > 0 && !isTouchEnemyTank()){
                            moveUp();
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    break;

                case 1://向右
                    for (int i = 0; i < 30; i++) {
                        if(getX() + 60 < MyPanel.w && !isTouchEnemyTank()){
                            moveRight();
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    break;

                case 2://向下
                    for (int i = 0; i < 30; i++) {
                        if (getY() + 60 < MyPanel.h && !isTouchEnemyTank()){
                            moveDown();
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    break;

                case 3://向左
                    for (int i = 0; i < 30; i++) {
                        if(getX() > 0 && !isTouchEnemyTank()){
                            moveLeft();
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
            }

            //然后随机改变坦克方向
            setDirect((int)(Math.random() * 4));
            //一旦写并发程序，一定要考虑，该线程什么时候退出
            if(!isLive){
                break;//退出线程
            }
        }
    }
}