package Entity;

import TileMap.*;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject{

	//player stuff
	private int health;
	private int maxHealth;
	private int fire;
	private int maxFire;
	private boolean dead;
	private boolean flinching;
	private long flinchTimer;
	
	//fireball
	private boolean firing;
	private int fireCost;
	private int fireBallDamage;
	private ArrayList<FireBall> fireBalls;
	
	//scratch
	private boolean scratching;
	private int scratchDamage;
	private int scratchRange;
	
	//gliding
	private boolean gliding;
	
	//animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {2, 8, 1, 2, 4, 2, 5};
	
	//animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int FIREBALL = 5;
	private static final int SCRATCHING = 6;
	
	public Player(TileMap tm) {
		super(tm);
		
		width = 30;
		height = 30;
		cwidth = 20;
		cheight = 20;
		
		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		facingRight = true;
		
		health = maxHealth = 5;
		fire = maxFire = 2500;
		
		fireCost = 200;
		fireBallDamage = 5;
		fireBalls = new ArrayList<FireBall>();
		
		scratchDamage = 8;
		scratchRange = 40;
		
		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/kirby.png"));
			
			sprites = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < 7; i++) {
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for (int j = 0; j < numFrames[i]; j++) {
					if (i != SCRATCHING) {
						bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
					}
					else {
						bi[j] = spritesheet.getSubimage(j * width * 2, i * height, width * 2, height);
					}
				}
				
				sprites.add(bi);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAnimation = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public int getFire() {
		return fire;
	}
	
	public int getMaxFire() {
		return maxFire;
	}
	
	public void setFiring() {
		firing = true;
	}
	
	public void setScratching() {
		scratching = true;
	}
	
	public void setGliding(boolean b) {
		gliding = b;
	}
	
	private void getNextPosition() {
		//movement
		if(left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		}
		
		else if(right) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
		}
		
		else {
			if (dx > 0) {
				dx -= stopSpeed;
				if (dx < 0) {
					dx = 0;
				}
			}
			else if (dx < 0){
				dx += stopSpeed;
				if (dx > 0) {
					dx = 0;
				}
			}
		}
		
		//cannot attack while moving, except in air
		if ((currentAnimation == SCRATCHING || currentAnimation == FIREBALL) && !(jumping || falling)) {
			dx = 0;
		}
		
		//jumping
		if (jumping && !falling) {
			dy = jumpStart;
			falling = true;
		}
		
		//falling
		if (falling) {
			if (dy > 0 && gliding) {
				dy += fallSpeed * 0.1;
			}
			
			else {
				dy += fallSpeed;
			}
			
			if (dy > 0) jumping = false;
			if (dy < 0 && !jumping) dy += stopJumpSpeed;
			
			if (dy > maxFallSpeed) dy = maxFallSpeed;
		}
		
	}
	
	public void update() {
		//update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		//check attack has stopped
		if (currentAnimation == SCRATCHING) {
			if (animation.hasPlayedOnce()) scratching = false;
		}
		
		if (currentAnimation == FIREBALL) {
			if(animation.hasPlayedOnce()) firing = false;
		}
		
		//fireball attack
		fire += 1;
		if (fire > maxFire) fire = maxFire;
		if(firing && currentAnimation != FIREBALL) {
			if (fire > fireCost) {
				fire -= fireCost;
				FireBall fb = new FireBall(tileMap, facingRight);
				fb.setPosition(x, y);
				fireBalls.add(fb);
			}
		}
		
		//update fireballs
		for (int i = 0; i < fireBalls.size(); i++) {
			fireBalls.get(i).update();
			if (fireBalls.get(i).shouldRemove()) {
				fireBalls.remove(i);
				i--;
			}
		}
		
		//set animations
		if (scratching) {
			if (currentAnimation != SCRATCHING) {
				currentAnimation = SCRATCHING;
				animation.setFrames(sprites.get(SCRATCHING));
				animation.setDelay(50);
				width = 60;
			}
		}
		else if (firing) {
			if (currentAnimation != FIREBALL) {
				currentAnimation = FIREBALL;
				animation.setFrames(sprites.get(FIREBALL));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if (dy > 0) {
			if (gliding) {
				if (currentAnimation != GLIDING) {
					currentAnimation = GLIDING;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(100);
					width = 30;
				}
			}
			else if (currentAnimation != FALLING) {
				currentAnimation = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width = 30;
			}
		}
		
		else if (dy < 0) {
			if (currentAnimation != JUMPING) {
				currentAnimation = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
				width = 30;
			}
		}
		
		else if (left || right) {
			if (currentAnimation != WALKING) {
				currentAnimation = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width = 30;
			}
		}
		
		else {
			if (currentAnimation != IDLE) {
				currentAnimation = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width = 30;
			}
		}
		
		animation.update();
		
		//set direction
		if (currentAnimation != SCRATCHING && currentAnimation != FIREBALL) {
			if (right) facingRight = true;
			if (left) facingRight = false;
		}
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();
		
		//draw fireballs
		for (int i = 0; i < fireBalls.size(); i++) {
			fireBalls.get(i).draw(g);
		}
		
		//draw player
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed / 100 % 2 == 0)
				return;
		}
		
		if (facingRight) {
			g.drawImage(animation.getImage(), (int)(x + xmap - width / 2), (int)(y + ymap - height / 2), null);
		}
		
		else {
			g.drawImage(animation.getImage(), (int)(x + xmap - width / 2 + width), (int)(y + ymap - height / 2), -width, height, null);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
