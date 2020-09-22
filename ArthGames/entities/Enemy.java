package com.ArthGames.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.ArthGames.main.Game;
import com.ArthGames.main.Sound;
import com.ArthGames.world.AStar;
import com.ArthGames.world.Camera;
import com.ArthGames.world.Vector2i;
import com.ArthGames.world.World;

public class Enemy extends Entity {
	
	private double speed = 0.7;
	
	private int frames = 0,maxFrames = 10,index = 0, maxIndex = 3;
	
	private BufferedImage[] sprites;
	
	private int life = 10;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[4];
		sprites[0] = Game.spritesheet.getSprite(96, 64, 16, 32);
		sprites[1] = Game.spritesheet.getSprite(112, 64, 16, 32);
		sprites[2] = Game.spritesheet.getSprite(128, 64, 16, 32);
		sprites[3] = Game.spritesheet.getSprite(144, 64, 16, 32);
		
	}
	
	public void update() {
		depth = 0;
		/*
			if(this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 150) { 
		if (isCollidingWithPlayer() == false) {
		if ((int)x < Game.player.getX() && World.isFree((int) (x+speed), this.getY())
				&& !isColliding((int) (x+speed), this.getY())){
			x+=speed;
		}else if ((int)x > Game.player.getX() && World.isFree((int) (x-speed), this.getY())
				&& !isColliding((int) (x-speed), this.getY()) ) {
			x-= speed;
		}if ((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
				&& !isColliding(this.getX(), (int)(y+speed)) ) {
	 		y+=speed;
		}else if ((int)y > Game.player.getY() && World.isFree( this.getX(), (int) (y-speed))
				&& !isColliding(this.getX(), (int) (y-speed))) {
			y-=speed;
		}
		} else {
				//estamos colidindo
			if (Game.rand.nextInt(100) < 10) {
				Sound.hurt.play();
			Game.player.life-=Game.rand.nextInt(5);
			Game.player.isDamaged = true;
			//System.out.println(Game.player.life);
			}
		}
		}else {
			
		}
		 */
		 	
		
		///*
		if(this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 150) { 
			Vector2i start = new Vector2i( (int)(x/16), (int)(y/16));
			Vector2i end = new Vector2i( (int)(Game.player.x/16), (int)(Game.player.y/16));
			path = AStar.findPath(Game.world, start, end);
		
			//estamos colidindo
		if (isCollidingWithPlayer())
		if(new Random().nextInt(100) < 10) {
			Sound.hurt.play();
			Game.player.life-=Game.rand.nextInt(5);
			Game.player.isDamaged = true;
		//System.out.println(Game.player.life);
		}
			if(new Random().nextInt(100) < 80)
			followPath(path);
		}	//*/
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index > maxIndex) {
					index =0;
				}
			}	
		
				collidingPower();
				
				if (life <= 0) {
					DestroySelf();
					return;
				}
				
				if(isDamaged) {
					this.damageCurrent++;
					if(this.damageCurrent == damageFrames) {
						this.damageCurrent = 0;
						isDamaged = false;
					}
				}
		}

	
	public void DestroySelf() {
		Game.entities.remove(this);
		Game.enemies.remove(this);
	}
	
		public void collidingPower() {
			for(int i = 0;i < Game.shots.size(); i++) {
				Entity e = Game.shots.get(i);
				if(e instanceof Shot) {
					
					if (Entity.isColidding(this, e) ) {
						Sound.enemyhurt.play();
						isDamaged = true;
						life--;
						Game.shots.remove(i);
						return ;
					}
				}
			}
			
		
		}
	
	public boolean isCollidingWithPlayer(){
		Rectangle enemyCurrent = new Rectangle(this.getX(), this.getY(), World.TILE_SIZE, World.TILE_SIZE);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), Game.player.getWidht(), Game.player.getHeight()); 
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColliding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext, ynext, World.TILE_SIZE, World.TILE_SIZE);
		for(int i = 0; i < Game.enemies.size() ; i++) {
			Enemy e = Game.enemies.get(i);
			if (e == this) 
				continue;
			Rectangle targetEnemy  = new Rectangle(e.getX(), e.getY(), World.TILE_SIZE, World.TILE_SIZE);
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		return false;
	}
	public void render(Graphics g) {
		if(!isDamaged) {
		g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
	}else{
		g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
	}
	
	
}
