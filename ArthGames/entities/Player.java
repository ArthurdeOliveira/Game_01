package com.ArthGames.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.ArthGames.gráficos.Spritesheet;
import com.ArthGames.main.Game;
import com.ArthGames.main.Sound;
import com.ArthGames.world.Camera;
import com.ArthGames.world.World;

public class Player extends Entity {

	public boolean right, left, up, down;
	public double speed = 1.5;
	
	
	private int frames = 0,maxFrames = 10,index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage[] upPlayer;
	private BufferedImage[] downPlayer;
	private BufferedImage[] lastDir;
	private BufferedImage damage;
	
	private boolean power = false;
	
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0; 
	
	public boolean shoot = false, mouseShoot = false;
	
	
	public int life = 100, maxLife = 100;
	public int mx, my;
	
	public boolean jump = false, isJumping = false, jumpUp = false,jumpDown = false;
	
	public static int z = 0;
	
	public int jumpFrames = 50, jumpCur 	= 0, jumpSpd = 2;
	
	
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		downPlayer = new BufferedImage[4];
		upPlayer = new BufferedImage[4];
		damage = Game.spritesheet.getSprite(0, 96, 16, 32);
		
	
		lastDir = downPlayer;
		
		for(int i = 0; i < 4; i++) {
		rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 32);
		}
		for(int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 32, 16, 32);
			}
		for(int i = 0; i < 4; i++) {
			downPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 64, 16, 32);
			}
		for(int i = 0; i < 4; i++) {
			upPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 96, 16, 32);
			}
	}

	public void update() {
		depth = 1;
		if(jump) {
			if(isJumping == false ) {
			jump = false;
			isJumping = true;
			 jumpUp = true;
			}
		}
			if(isJumping) {
				
					if (jumpUp){	
					jumpCur+= jumpSpd;
				}else if(jumpDown) {
					jumpCur -=jumpSpd;
					if(jumpCur <= 0) {
						isJumping = false;
						jumpDown = false;
					}
				}
					z = jumpCur;
					if(jumpCur >= jumpFrames) {
					
					jumpUp = false;
					jumpDown = true;
					}
				}
			
		
		
		moved = false;
		if (right && World.isFree ( (int) (x + speed), this.getY())) {
		lastDir = rightPlayer;
			moved = true;
			x+=speed;
		}
		else if (left && World.isFree( (int) (x - speed), this.getY())) {
			lastDir = leftPlayer;
			moved = true;
			x-=speed;
		}
		if (up && World.isFree(this.getX(), (int)(y - speed))) {
			lastDir = upPlayer;
			moved = true;
			y-=speed;
		}
		else if (down && World.isFree(this.getX(), (int)( y + speed))) {
			lastDir = downPlayer;
			moved = true;
			y+=speed;
		}
		
		if (moved) {
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index > maxIndex) {
					index =0;
				}
			}
		}
		
		checkCollisionLifePack();
		checkCollisionAmmo();
		checkCollisionPower();
		
		if(isDamaged) {
			this.damageFrames++;
			if(damageFrames ==  15) {
				damageFrames = 0;
				isDamaged = false;
			}
		}
		
		
		if(ammo > 0 && power && shoot ) {
			Sound.shoot.play();
			//Atirar	
			ammo--;
			shoot = false;
			int dx = 0; 
			int dy = 0;
			
			if (right || lastDir == rightPlayer) {
				 dx	= 1;
			}
			else if (left || lastDir == leftPlayer) {
				dx	= -1;
			}
			 if (up || lastDir == upPlayer) {
				 dy	= -1;
			}
			else if (down || lastDir == downPlayer) {
				 dy	= 1;
				 
			}		
			Shot shot = new Shot(this.getX(), this.getY(), 3, 3, Shot.shot, dx, dy);
			Game.shots.add(shot);		
		}
	
		if( ammo > 0 && power  && mouseShoot) {
			//Atirar	
			Sound.shoot.play();
			ammo--;
			mouseShoot = false;
			double angle = 0;
			
				angle = Math.atan2(my - (this.getY() - Camera.y),mx - (this.getX() - Camera.x));
		
				
			
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			/*
			double angle = Math.atan2(this.getY()+16 - Camera.y - my , this.getX()+8 - Camera.x - mx  );
			System.out.println	(angle);
			double dx =Math.cos(angle); 
			double dy =Math.sin(angle);
			*/
			
		
			Shot shot = new Shot(this.getX(), this.getY(), 3, 3, Shot.shot, dx , dy);
			Game.shots.add(shot);		
			}		
		
		

		if (life <= 0) {
			//Game Over!
			life = 0;
			Game.gameState = "GAME_OVER";
		}
			
			updateCamera();
			
		}
		
		public void updateCamera() {
			Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2),0,World.WIDTH*16 - Game.WIDTH);
			Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2),0,World.HEIGHT*16 - Game.HEIGHT);
		}
	
	public void checkCollisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++ ) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if (Entity.isColidding(this, atual)) {
					ammo+=20;
					
					Game.entities.remove(i);
				}
			}
		}			
	}
	public void checkCollisionPower() {
		for(int i = 0; i < Game.entities.size(); i++ ) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Weapon) {
				if (Entity.isColidding(this, atual)) {
					power = true;
					
					Game.entities.remove(i);
				}
			}
		}			
	}
	
	public void checkCollisionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++ ) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Lifepack) {
				if (life < 100) {
				if (Entity.isColidding(this, atual)) {
					life+= 15;
					if (life > 100)
						life = 100;
					Game.entities.remove(i);
					}
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if (!isDamaged) {
		if (right) {
			g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY()- Camera.y - z, null);
			if(power) {
				//desenhar
				g.drawImage(Entity.POWER, this.getX() - Camera.x,this.getY()-5	  - Camera.y- z, null);
			}
		}
		else if (left) {
			g.drawImage(leftPlayer[index], this.getX()- Camera.x, this.getY()- Camera.y- z, null);
			if(power) {
				//desenhar
				g.drawImage(Entity.POWER, this.getX() - Camera.x,this.getY() -5 - Camera.y- z, null);
				
			}
		}
		else if (down) {
			g.drawImage(downPlayer[index], this.getX()- Camera.x, this.getY()- Camera.y- z, null);
			if(power) {
				//desenhar
				g.drawImage(Entity.POWER, this.getX() - Camera.x,this.getY()- 5 - Camera.y- z, null);
			}
		}
		else if (up) {
			g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y- z, null);
			if(power) {
				//desenhar
				g.drawImage(Entity.POWER, this.getX() - Camera.x,this.getY()- 5 - Camera.y- z, null);
			}
		}
		else {
			g.drawImage(lastDir[index], this.getX()- Camera.x, this.getY() - Camera.y- z, null);
			if(power) {
				//desenhar
				g.drawImage(Entity.POWER, this.getX() - Camera.x,this.getY()- 5 - Camera.y- z, null);
			}
		}
	}else {
		g.drawImage(damage,this.getX() - Camera.x, this.getY() - Camera.y - z, null);
		if(power) {
		g.drawImage(Entity.POWER, this.getX() - Camera.x,this.getY()- 5 - Camera.y -z, null);
		}
	}
	}
}
