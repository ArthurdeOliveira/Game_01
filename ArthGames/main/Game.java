package com.ArthGames.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
//import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
//import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.ArthGames.entities.Enemy;
import com.ArthGames.entities.Entity;
import com.ArthGames.entities.Player;
import com.ArthGames.entities.Shot;
import com.ArthGames.gráficos.Spritesheet;
import com.ArthGames.gráficos.UI;
import com.ArthGames.world.World;
import com.ArthGames.main.Game;


public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//código pronto para iniciar uma janela
	public static JFrame frame; 
	private Thread thread;
	private boolean isRunning= true;
	public static final int WIDTH = 260;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;
	
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<Shot> shots;
	
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI ui;
	
	public int xx, yy;
	
	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("Minecraft.ttf");
	public static Font newfont;
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0, maxFramesGameOver = 25;
	private boolean restartGame = false;
	
	public Menu menu;
	
	public static int[] pixels;
	public static BufferedImage light;	
	public static int[] lightPixels;
	public static int[] minimapPixels;

	
	public boolean saveGame = false;
		
	public int mx, my;
	
	public static BufferedImage minimap;
	 
	public Game() {	
		Sound.music.loop();
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
		//setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		//Inicializando objetos:
		
		ui = new UI();
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		try {
			light = ImageIO.read(getClass().getResource("/light.png"));
		} catch (IOException e1) {			e1.printStackTrace();
		}
		lightPixels = new int[light.getWidth() * light.getHeight()];
		light.getRGB(0, 0, light.getWidth(), light.getHeight(), lightPixels, 0, light.getWidth());
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		shots = new ArrayList<Shot>();
		
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 32, spritesheet.getSprite(32, 0, 16, 32));
		entities.add(player);
		world = new World("/level1.png");
		
		minimap = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB );
		minimapPixels =  ((DataBufferInt)minimap.getRaster().getDataBuffer()).getData();
		
		try {
			newfont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(36f);
		} catch (FontFormatException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		menu = new Menu();
	}
	
	public void initFrame() {
		
		frame = new JFrame("Big-Head Boy Adventures"); 
		frame.add(this);
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.pack();
		Image imagem = null;
		try {
			imagem = ImageIO.read(getClass().getResource("/icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage(getClass().getResource("/Cursor.png"));
		Cursor c =  toolkit.createCustomCursor(image, new Point(0,0), "img");
		frame.setCursor(c);
		frame.setIconImage(imagem);
		frame.setAlwaysOnTop(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start(){
		thread = new Thread(this);
		thread.start();
		isRunning = true;
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

	public void update(){
		if (gameState == "NORMAL") {
			xx++;
				if(saveGame) {
					saveGame = false;
					String[] opt1 = {"level"};
					int[] opt2 = {CUR_LEVEL};
					Menu.saveGame(opt1, opt2, 10);
					System.out.println("Jogo salvo com sucesso!");
				}
			restartGame = false;
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			
			e.update();
		}
		
		for(int i = 0; i < shots.size(); i++) {
			shots.get(i).update();
		}
		
		if (enemies.size() == 0) {
			// Trocar de level	
			CUR_LEVEL++;
			if(CUR_LEVEL > MAX_LEVEL) {
				CUR_LEVEL = 1;
			}
			String newWorld = "level"+ CUR_LEVEL + ".png";
			World.restartGame(newWorld);
		}
	}else if (gameState == "GAME_OVER") {
		framesGameOver ++;
		if(framesGameOver == maxFramesGameOver) {
			framesGameOver = 0;
			if(showMessageGameOver) {
				showMessageGameOver = false;
			}else 
				showMessageGameOver = true;
		}
		if(restartGame) {
			restartGame = false;
			gameState = "NORMAL";
			CUR_LEVEL = 1;
				String newWorld = "level"+ CUR_LEVEL + ".png";
				World.restartGame(newWorld);
		}
}else if (gameState == "MENU") {
		menu.update();
		}
	}
	
/*public void applyLight() {
		

	for(int xx =0; xx < Toolkit.getDefaultToolkit().getScreenSize().width; xx++) {
		for(int yy = 0; yy < Toolkit.getDefaultToolkit().getScreenSize().height; yy++) {
			if(lightPixels[xx+(yy * Toolkit.getDefaultToolkit().getScreenSize().width)] == 0xffffffff) {
				pixels[xx+(yy*Toolkit.getDefaultToolkit().getScreenSize().width)] = 0;
			}
		}
	}
	
	}*/
	
	public void render() {
		
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT); 
		
		
	
		  //Graphics2D g2 =  (Graphics2D) g;
		world.render(g);
		Collections.sort(entities, Entity.nodeSorter);
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < shots.size(); i++) {
			shots.get(i).render(g);
		}
		//applyLight();
		ui.render(g);
		 /***/
		g.dispose();
		g = bs.getDrawGraphics();
		//drawRectExemple(xx, yy);
		g.drawImage(image, 0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, null);
		g.setFont(newfont);
		g.setColor(Color.white);
		g.drawString("Power: " + player.ammo, 700, 30);
		
		
		if (gameState == "GAME_OVER") {	
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0 , 0, 100));
			g2.fillRect(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
			g.setFont(newfont);
			g.setColor(Color.white);
			g.drawString("Game Over!" ,(Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - 100, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - 20);
			
			if(showMessageGameOver) {
			g.drawString(">Pressione Enter para reiniciar<" ,(Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - 250, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 + 20 );
			}
		}else if(gameState == "MENU") {
			menu.render(g);
			
		}	
		//World.renderMinimap();
		//
		g.drawImage(minimap, 70, 80,World.WIDTH*3,World.HEIGHT*3, null);
		
		/*Graphics2D g2 = (Graphics2D) g;
		double angleMouse = Math.atan2(my - 224, mx - 248);
		g2.rotate(angleMouse, 200+24,200+48);
		g.setColor(Color.black);
		g.fillRect(200, 200, 48, 96);
		*/
		bs.show();
		
		
		
		
				
	}
	
	
	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		double amountOfUpdates = 60.0;
		double ns = 1000000000 /  amountOfUpdates;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis(); 
		while (isRunning) {
				long now = System.nanoTime();
				delta+= (now - lastTime) /  ns;
				lastTime = now;
				if (delta>= 1) {
					update();
					render();	
					frames++;
					delta--;
				}
					
				if (System.currentTimeMillis() - timer >= 1000) {
					 System.out.println("FPS: "+ frames );
					 frames = 0;
					 timer+=1000;
				}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//movimentação do Player
		
		if (e.getKeyCode() == KeyEvent.VK_Z) {
			player.jump = true;
		}
	if(e.getKeyCode() == KeyEvent.VK_RIGHT  || e.getKeyCode() == KeyEvent.VK_D) {
		player.right = true;
	}
	else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
		player.left = true;
	}
	if (e.getKeyCode() == KeyEvent.VK_UP|| e.getKeyCode() == KeyEvent.VK_W) {
		player.up = true;
		if (gameState == "MENU") {
			menu.up = true;
		}
		
	}else if (e.getKeyCode() == KeyEvent.VK_DOWN|| e.getKeyCode() == KeyEvent.VK_S) {
		player.down = true;
		if (gameState == "MENU") {
			menu.down = true;
		}
	}
	
	if(e.getKeyCode() == KeyEvent.VK_SPACE) {
		player.shoot = true;
	}
	if (e.getKeyCode() == KeyEvent.VK_ENTER ) {
		restartGame = true;
		if(gameState == "MENU") {
				menu.enter = true;
		}
	}
	if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
		gameState = "MENU";
		Menu.pause = true;
	}
	if(e.getKeyCode() == KeyEvent.VK_I) {
		if(gameState == "NORMAL")
			this.saveGame = true;
	}
	
	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT  || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP|| e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}else if (e.getKeyCode() == KeyEvent.VK_DOWN|| e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.shoot = false;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		 player.mouseShoot = true;
		 player.mx = (e.getX() /3 );
		 player.my = (e.getY() /3);
		 System.out.println(player.mx);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		 mx = e.getX();
		 my = e.getY();
		
	}

}
