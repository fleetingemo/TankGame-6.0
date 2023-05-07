package tankgame;

import java.io.*;
import java.util.Vector;

/**
 * @author xienan
 * @version 1.0
 * 用于记录相关信息和文件交互
 */
@SuppressWarnings({"all"})

public class Recorder {
    //定义记录我方击毁敌人坦克数
    private static int  shotEnemyTankNum = 0;

    //定义IO，准备写数据到文件中
    private static String recordFile = "myRecorder.txt";
    private  static BufferedWriter bw = null;
    //读取文件
    private  static BufferedReader br = null;

    //定义一个Vector，指向MyPanel对象的 敌人坦克Vector
    private static Vector<EnemyTank> enemyTanks = null;

    //定义一个Node的Vector， 用于保存上一局还存活的敌人信息的Node
    private static Vector<Node> nodes = new Vector<>();

    public static void setEnemyTanks(Vector<EnemyTank> enemyTanks) {
        Recorder.enemyTanks = enemyTanks;
    }

    public static String getRecordFile() {
        return recordFile;
    }

    //用于读取recordFile,恢复相关信息
    //如果游戏启动后，用户选择继续上一局游戏，则启动该方法
    public static Vector<Node> getNodesAndEnemyTankNumRec(){
        try {
            br = new BufferedReader(new FileReader(recordFile));
            String line = br.readLine();
            shotEnemyTankNum = Integer.parseInt(line);
            //循环读取文件，生成nodes
            line = "";
            while ((line = br.readLine()) != null){
                String[] xyd = line.split(" ");
                Node node = new Node(Integer.parseInt(xyd[0]), Integer.parseInt(xyd[1]), Integer.parseInt(xyd[2]));
                nodes.add(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return nodes;
    }

    //写一个方法，当游戏退出时，我们将shotEnemyNum记录在recordFile中
    public static void keepRecord(){
        try {
            //保存击毁敌人坦克数量
            bw = new BufferedWriter(new FileWriter(recordFile));
            bw.write( shotEnemyTankNum + "\r\n");

            //TODO 保存敌方坦克上一局的位置和方向
            //TODO 思路：由于此类为静态类，不能访问敌人坦克列表，因此可以在此类中也定义一个列表，并将敌人坦克列表赋值给它
            for (int i = 0; i < enemyTanks.size(); i++){
                EnemyTank enemyTank = enemyTanks.get(i);
                if (enemyTank.isLive){         //虽然没有存活的坦克已经被移除列表了，但是还是建议判断一下
                    String record = enemyTank.getX() + " " + enemyTank.getY() + " " + enemyTank.getDirect() + "\r\n";
                    //写入到文件
                    bw.write(record);

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int getShotEnemyTankNum() {
        return shotEnemyTankNum;
    }

    public static void setShotEnemyTankNum(int shotEnemyTankNum) {
        Recorder.shotEnemyTankNum = shotEnemyTankNum;
    }

    //当我方击毁一辆对方坦克，就+1
    public static void addShotEnemyTankNum(){
        Recorder.shotEnemyTankNum++;
    }
}
