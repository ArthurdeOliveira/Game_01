package com.ArthGames.gráficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.ArthGames.entities.Player;
import com.ArthGames.main.Game;

public class UI {
	
	public void render(Graphics g) {
	
		g.setColor(Color.RED);
		g.fillRect(8, 8,50, 9);
		g.setColor(Color.GREEN);
		g.fillRect(8, 8, (int) ((Game.player.life )*0.5), 9);
		g.setColor(Color.WHITE);
		g.setFont(new Font ("arial", Font.BOLD , 10) );
		g.drawString((int)Game.player.life +"/" + Game.player.maxLife, 13, 16);
		//g.setColor(Color.black);
		//g.drawRect(8, 8, 50, 8);
	}

}
