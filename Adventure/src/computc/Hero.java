package computc;

import java.util.ArrayList;
import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Hero extends Entity
{
	public static boolean nextArea;
	private boolean dead = false;
	private int health;
	private int maxHealth;
	
	public Hero(World world, int tx, int ty) throws SlickException
	{
		super(world, tx, ty);
		
		this.image = new Image("res/hero.png");
		
		moveSpeed = 0.013f;
		maxSpeed = .2f;
		stopSpeed = 0.007f;
		health = 5;
	}
	
	public void update(Input input, int delta)
	{
		getNextPosition(input, delta);
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if (blinkTimer > 0)
			blinkTimer --;
		
		if(blinkTimer == 0)
			blinking = false;
	}
	
	public void render(Graphics graphics, Camera camera)
	{
		if(blinking) 
		{
			if(blinkTimer % 4 == 0) 
			{
				return;
			}
		}
			
		super.render(graphics, camera);
	}
	
	private void hit(int damage) 
	{
		if(blinking)
			return;
		health -= damage;
		
		if(health < 0)
			health = 0;
		
		if(health == 0) 
			dead = true;
		
		blinking = true;
		blinkTimer = 2000;
	}
	
	private void getNextPosition(Input input, int delta) 
	{
		
		if(input.isKeyDown(Input.KEY_UP)) 
		{
			dy -= moveSpeed * delta;
			if(dy < -maxSpeed)
			{
				dy = -maxSpeed * delta;
			}
		}
		else if(input.isKeyDown(Input.KEY_DOWN))
		{
			dy += moveSpeed * delta;
			if(dy > maxSpeed)
			{
				dy = maxSpeed * delta;
			}
		}
		
		else 
		{
			if (dy > 0) 
			{
				dy -= stopSpeed * delta;
				if(dy < 0)
				{
					dy = 0;
				}
			}
			else if (dy < 0)
			{
				dy += stopSpeed * delta;
				if(dy > 0) 
				{
					dy = 0;
				}
			}
		}

		 if(input.isKeyDown(Input.KEY_RIGHT))
		{
			dx += moveSpeed * delta;
			if(dx > maxSpeed) 
			{
				dx = maxSpeed * delta;
			}
		}
		 else if(input.isKeyDown(Input.KEY_LEFT)) 
		{
			dx -= moveSpeed * delta;
			if(dx < -maxSpeed)
			{
				dx = -maxSpeed * delta;
			}
		}
		else 
		{
			if (dx > 0) 
			{
				dx -= stopSpeed * delta;
				if(dx < 0)
				{
					dx = 0;
				}
			}
			else if (dx < 0)
			{
				dx += stopSpeed * delta;
				if(dx > 0) 
				{
					dx = 0;
				}
			}
		}
		
//		float step = this.moveSpeed * delta;
		
			if(input.isKeyDown(Input.KEY_UP))
				{
					this.direction = Direction.NORTH;
//					this.y -= step;
				}
			else if(input.isKeyDown(Input.KEY_DOWN))
				{
				this.direction = Direction.SOUTH;
//				this.y += step;
				}
		
			if(input.isKeyDown(Input.KEY_LEFT))
				{
				this.direction = Direction.WEST;
//				this.x -= step;
				}
			else if(input.isKeyDown(Input.KEY_RIGHT))
			{
				this.direction = Direction.EAST;
//				this.x += step;
			}
	}
	
	public void checkAttack(LinkedList<Enemy> enemies) 
	{
		for(int i = 0; i < enemies.size(); i++)
		{
			Enemy e = enemies.get(i);
			if(intersects(e)) {
				hit(e.getDamage());
			}
		}
	}
	
	public int getHealth() 
	{
		return health;
	}
	
	public boolean isDead() 
	{
		return dead;
	}
	
	public void setAlive()
	{
		dead = false;
	}
	
}