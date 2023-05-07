package tankgame;

/**
 * @author xienan
 * @version 1.0
 * 演示爆炸效果
 */
@SuppressWarnings({"all"})
public class Bomb {
    int x, y;//炸弹的坐标
    int life = 9; //炸弹生命周期
    boolean isLive = true; //是否存活

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void lifeDown(){
        if (life > 0){
            life--;
        }else{
            isLive = false;
        }
    }
}
