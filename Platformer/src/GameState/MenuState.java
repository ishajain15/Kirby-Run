package GameState;

import java.awt.*;
import java.awt.event.KeyEvent;

import TileMap.Background;

public class MenuState extends GameState {
	
	private Background bg;
	
	private int currentChoice = 0;
	private String[] options = {"Start", "Help", "Quit"};
	
	private Color titleColor;
	private Font titleFont;
	
	private Font font;
	
	public MenuState(GameStateManager gsm) {
		this.gsm = gsm;
		
		try {
			bg = new Background("/Backgrounds/pixil-frame-0 (10).png", 1);
			bg.setVector(-0.1, 0);
			
			titleColor = new Color(255, 255, 255);
			titleFont = new Font("Century Gothic", Font.BOLD, 28);
			
			font = new Font("Arial", Font.BOLD, 12);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}

	public void init() {
		
	}

	public void update() {
		bg.update();
	}

	public void draw(Graphics2D g) {
		//draw bg
		bg.draw(g);
		//draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("Kirby Run", 100, 70);
		//draw menu options
		g.setFont(font);
		for (int i = 0; i < options.length; i++) {
			if (i == currentChoice) {
				g.setColor(Color.BLACK);
			}
			else {
				g.setColor(Color.WHITE);
			}
			g.drawString(options[i], 145, 140 + i * 15);
		}
	}

	private void select() {
		if (currentChoice == 0) {
			//start
			gsm.setState(GameStateManager.LEVEL1STATE);
		}
		
		if (currentChoice == 1) {
			//help
		}
		
		if (currentChoice == 2) {
			//quit
			System.exit(0);
		}
	}
	
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_ENTER) {
			select();
		}
		if (k == KeyEvent.VK_UP) {
			currentChoice--;
			if (currentChoice == -1) {
				currentChoice = options.length - 1;
			}
		}
		if (k == KeyEvent.VK_DOWN) {
			currentChoice++;
			if (currentChoice == options.length) {
				currentChoice = 0;
			}
		}
	}

	public void keyReleased(int k) {
		
	}
	
	
}
