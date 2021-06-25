package BrickBreaker;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class BrickBreaker extends JFrame{

	private static final long serialVersionUID = 1L;
	static GameManager gManager = new GameManager();
	BrickBreaker(){
		setContentPane(gManager);
		addKeyListener(GameKeyManager);
		setSize(480,720);
		setTitle("Bolck Breaker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		setLayout(null);
	}
	
	public static void main(String[] args) throws InterruptedException {
		BrickBreaker s = new BrickBreaker();
		while(true) {
			s.repaint();
			gManager.Update();
			Thread.sleep(10);
		}
	}
	
	KeyListener GameKeyManager = new KeyListener() {
		
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
				
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			switch(e.getKeyCode())
			{
			case KeyEvent.VK_SPACE:
				gManager.GameStart();
				break;
			case KeyEvent.VK_RIGHT:
				gManager.PlayerInput(-5);
				break;
			case KeyEvent.VK_LEFT:
				gManager.PlayerInput(5);
				break;
			}
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			switch(e.getKeyCode())
			{
			case KeyEvent.VK_RIGHT:
				gManager.PlayerInput(5);
				break;
			case KeyEvent.VK_LEFT:
				gManager.PlayerInput(-5);
				break;
			}
			
		}
	};
	
}
class vector
{
	public float x;
	public float y; 
	public vector(int x, int y)
	{
		this.y = y;
		this.x = x;
	}
	public void setX(float x)
	{
		this.x = x;		
	}
	public void setY(float y)
	{
		this.y = y;		
	}
}

class GameManager extends JPanel{
	private enum STATE{START, PLAY, OVER, CLEAR}
	private	STATE gState;
	private float playerMoveSpeed;
	private float ballMoveSpeed;
	private vector player;
	private vector ball;
	private vector rotate;
	private ArrayList<vector> block;
	private AudioClip ac;
	private AudioClip fanfare;
	private Image img;
	public GameManager()
	{
		ac = Applet.newAudioClip(getClass().getResource("bounce.wav"));
		fanfare = Applet.newAudioClip(getClass().getResource("fanfare.wav"));
		try {
			String path = BrickBreaker.class.getResource("").getPath();
			System.out.println(path);
			ImageIcon imgIcon = new ImageIcon(path + "backGround.png");
			img = imgIcon.getImage(); 
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		setGame();
	}
	
	private void setGame(){
		player = new vector(240, 600);
		ball = new vector(240, 590);
		ballMoveSpeed = 3;
		rotate = new vector(1,-1);
		ballRotate(0);
		gState = STATE.START;
		block = new ArrayList<>();
		for(int i = 0; i < 6; i++)
			for(int j = 0; j < 6; j++)
				block.add(new vector(70+i*65, 50+j*45));
	}
	
	public void GameStart()
	{
		if(gState == STATE.CLEAR|| gState == STATE.OVER)
			setGame();
		gState = STATE.PLAY;
	}
	
	public void Update()
	{
		if(gState == STATE.PLAY)
		{
			BallMove();
			PlayerMove();
		}
	}
	
	public void PlayerInput(int x)
	{
		playerMoveSpeed += x;
		if(playerMoveSpeed > 5)
			playerMoveSpeed=5;
		if(playerMoveSpeed < -5)
			playerMoveSpeed =-5;
			
	}
	
	public void PlayerMove() {
		float temp = player.x + playerMoveSpeed;
		if(temp > 430)
			temp = 430;
		if(temp < 30)
			temp = 30;
		player.setX(temp) ;
	}
	
	private void BallHit()
	{
		if(ball.x > 460 || ball.x < 0)
		{
			rotate.setX(-rotate.x);
			ac.stop();
			ac.play();
			return;
		}
		if(ball.y< 0)
		{			
			rotate.setY(-rotate.y);
			ac.stop();
			ac.play();
			return;
		}
		if(player.y - ball.y < 5 && player.y - ball.y > -5)
			if(player.x - ball.x < 55 && player.x - ball.x > -55)
			{
				if(rotate.y > 0)
				{
					rotate.setY(-rotate.y);
					ballRotate(ballMoveSpeed*(ball.x - player.x)/55);
					ac.stop();
					ac.play();
					return;
				}
			}
		for(Iterator<vector>itr = block.iterator();itr.hasNext();)
		{
			vector temp = itr.next();
			if(temp.y - ball.y < -15 && temp.y - ball.y > -20 || temp.y - ball.y < 25 && temp.y - ball.y > 20)
				if(temp.x - ball.x < 30 && temp.x - ball.x > -30)
				{
					rotate.setY(-rotate.y);
					block.remove(temp);
					ballMoveSpeed += 0.1;
					if(block.isEmpty())
					{						
						gState = STATE.CLEAR;
						fanfare.play();
					}
					ac.stop();
					ac.play();
					return;
				}
			
			if(temp.x - ball.x < -30 && temp.x - ball.x > -35 || temp.x - ball.x < 35 && temp.x - ball.x > 30)
				if(temp.y - ball.y < 20 && temp.y - ball.y > -15)
				{
					rotate.setX(-rotate.x);
					block.remove(temp);
					ballMoveSpeed += 0.1;
					if(block.isEmpty())
					{
						gState = STATE.CLEAR;
						fanfare.play();
					}
					ac.stop();
					ac.play();
					return;
				}
		}
	}
	
	private void ballRotate(float force)
	{
		vector direction = new vector(0,0);
		rotate.setX(rotate.x+force);
		if(rotate.x > 0)
			direction.setX(1);
		else
			direction.setX(-1);
		if(rotate.y > 0)
			direction.setY(1);
		else
			direction.setY(-1);
		
		float size = rotate.x * rotate.x + rotate.y * rotate.y;
		rotate.setX(direction.x*(float)Math.sqrt(ballMoveSpeed*rotate.x*rotate.x/size));
		rotate.setY(direction.y*(float)Math.sqrt(ballMoveSpeed*rotate.y*rotate.y/size));
	}
	
	private void BallMove()
	{
		if(ball.y > 710)
		{
			gState = STATE.OVER;
			return;
		}
		BallHit();
		ball.setX(ball.x + rotate.x);
		ball.setY(ball.y + rotate.y);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, -30, null);
		if(gState == STATE.START )
		{
			g.setFont(new Font("Arial", Font.BOLD, 30));
			g.drawString("ENTER THE SPACE", 100, 360);
		}
		if(gState == STATE.CLEAR)
		{
			g.setFont(new Font("Arial", Font.BOLD, 50));
			g.drawString("CLEAR!", 150, 360);	
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString("RESTART TO SPACE", 140, 410);
		}
		if(gState == STATE.OVER)
		{
			g.setFont(new Font("Arial", Font.BOLD, 50));
			g.drawString("GAME OVER", 80, 360);			
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString("RESTART TO SPACE", 140, 410);			
		}
		drawPlayer(player, g);
		drawBall(ball, g);
		for(Iterator<vector>itr = block.iterator();itr.hasNext();)
		{
			drawBlock(itr.next(), g);
		}
	}
	
	private void drawBlock(vector loc, Graphics g)
	{
		g.fillRect(Math.round(loc.x-30), Math.round(loc.y-20), 60, 40);		
	}
	
	private void drawBall(vector loc, Graphics g)
	{
		g.fillOval(Math.round(loc.x-5), Math.round(loc.y), 10, 10);		
	}
	
	private void drawPlayer(vector loc, Graphics g)
	{
		g.fillRect(Math.round(loc.x-50), Math.round(loc.y), 100, 10);
		
	}
}
