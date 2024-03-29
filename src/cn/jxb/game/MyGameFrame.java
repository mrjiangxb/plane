package cn.jxb.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JFrame;

/**
 * 飞机游戏的主窗口
 * @author JXB
 */
public class MyGameFrame extends JFrame{
	
	Image planeImg = GameUtil.getImage("images/plane.png");
	Image bg = GameUtil.getImage("images/bg.jpg");

	Plane plane = new Plane(planeImg,250,250);
	Shell shell = new Shell();
	Shell[] shells = new Shell[40];
	
	Explode bao;
	Date startTime = new Date();
	Date endTime;
	int period; //游戏持续的时间
	
	@Override
	public void paint(Graphics g) {  //自动被调用   g相当于一支画笔
		Color c = g.getColor();
		
		g.drawImage(bg, 0, 0, null);
		
		plane.drawSelf(g);//画飞机
		
		for(int i=0; i<shells.length; i++){
			shells[i].draw(g);//画炮弹
			
			//飞机和炮弹的碰撞检测
			boolean peng = shells[i].getRect().intersects(plane.getRect());		
			if(peng){
				plane.live = false;
				if(bao==null){
					bao = new Explode(plane.x,plane.y);//加载爆炸特效
				
					endTime = new Date();
					period = (int)((endTime.getTime() - startTime.getTime())/1000);
				}
							
				bao.draw(g);			
			}
			//计时功能，给出提示
			if(!plane.live){
				g.setColor(Color.WHITE);
				Font f = new Font("宋体", Font.BOLD, 30);
				g.setFont(f);
				g.drawString("存活时间："+period+"秒", 150, 200);
			}
		}
		g.setColor(c);
	}
	
	/**
	 * 双缓冲解决图像闪烁问题
	 */
	private Image offScreenImage = null;
	
	public void updata(Graphics g){
		if(offScreenImage == null){
			offScreenImage = this.createImage(Constant.GAME_WIDTH,Constant.GAME_HEIGHT);
			
			Graphics gOff = offScreenImage.getGraphics();
			paint(gOff);
			g.drawImage(offScreenImage, 0, 0, null);
		}
	}
	
	class PaintThread extends Thread{ //反复重画窗口(刷新画面)
		@Override
		public void run() {
			while(true){
				repaint(); //重画
				
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//定义键盘监听的内部类(键盘控制飞机动作)
	class KeyMonitor extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			plane.addDirection(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			plane.minusDirection(e);
		}
	}
	
	
	/**
	 * 初始化窗口
	 */
	public void launchFrame(){
		this.setTitle("JXB作品");
		this.setBounds(430, 130, Constant.GAME_WIDTH, Constant.GAME_HEIGHT);
		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter(){ //点击右上角X号时能退出程序
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		new PaintThread().start();//启动重画窗口的线程
		addKeyListener(new KeyMonitor());//给窗口增加键盘的监听
		
		//初始化40个炮弹
		for(int i=0; i<shells.length; i++){
			shells[i] = new Shell();
		}
		
	}
	
	public static void main(String[] args) {
		MyGameFrame f = new MyGameFrame();
		f.launchFrame();
	}
	
	
}
