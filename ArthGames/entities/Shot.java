package com.ArthGames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.ArthGames.main.Game;
import com.ArthGames.world.Camera;

public class Shot  extends Entity{
	
	private double dx;
	private double dy;
	private double spd = 4;
	
	private int life = 50, curLife = 0;
	
	public static BufferedImage shot;
	
	public Shot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		shot = Game.spritesheet.getSprite(144, 128, 16, 16);
		this.dx = dx;
		this.dy = dy;
	}
	
	
	public void update() {
		x+=dx * spd;
		y+=dy * spd;
		 curLife++;
		 if(curLife == life) {
			 Game.shots.remove(this);
			 return;
		 }
	}

	public void render(Graphics g) {
		g.drawImage(shot, this.getX() - Camera.x,this.getY() - Camera.y, 16, 16, null);
		//g.setColor((Color.yellow));
		//g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
	
}
