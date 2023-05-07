package tankgame;

/**
 * @author xienan
 * @version 1.0
 * 我方子弹发射
 * 分析如何实现当用户按下J键，我方的坦克就发射一颗子弹
 * 1、当发射一颗子弹后，就相当于启动一个线程
 * 2、hero有子弹的对象，当按下J键后，我们就启动一个发射行为（线程），让子弹不停的移动，形成一个射击效果
 * 3、我们MyPanel需要不停重绘子弹，才能出现该效果
 * 4、当子弹移动到面板边界时，就应该销毁（把启动子弹的线程销毁）
 *
 * 我方子弹发射之后，必须等消亡后，才能发射新的子弹 => 扩展为发射多颗子弹,(假如想控制同一时刻最多只能发射8颗子弹，又该怎么办呢)
 * 在按下J键之后，我们需要判断hero对象子弹，是否已经销毁
 * 如果没有销毁，就不去触发shotEnemyTank
 * 如果销毁了，才去触发shotEnemyTank
 * 如果想要发射多颗子弹，可以用vector保存,因此在绘制我方子弹时，也需要遍历子弹集合
 *
 * 敌方子弹发射
 * 1、在敌人坦克类，使用Vector保存多个Shot
 * 2、当没创建一个Shot对象，给该敌人对象初始化一个Shot对象，同时启动Shot
 * 3、在绘制敌人坦克时，需要遍历敌人坦克对象Vector，绘制所有子弹，当子弹isLive == false时，就从Vector移除
 *
 *
 */
@SuppressWarnings({"all"})
public class Shot implements Runnable{
    int x;//子弹x坐标
    int y;//子弹y坐标
    int direct = 0; //子弹方向
    int speed = 4; //子弹速度
    boolean isLive = true; //记录子弹是否还存活

    public Shot(int x, int y, int direct) {
        this.x = x;
        this.y = y;
        this.direct = direct;
    }

    @Override
    public void run() {
        while (true){
            //线程休眠一下，不然看不到直线效果,会非常快
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (direct){
                case 0://上
                    y -= speed;
                    break;

                case 1://右
                    x += speed;
                    break;

                case 2://下
                    y += speed;
                    break;

                case 3://左
                    x -= speed;
                    break;
            }
            //System.out.println("子弹x:" + x + "子弹y:" + y);
            if (!(x >= 0 && x <= 1000 && y >= 0 && y <= 800 && isLive)){//子弹碰到边界,子弹碰到敌人坦克时，也应该结束
                isLive = false;
                break;
            }
        }
    }
}
