package computc.entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import computc.worlds.Dungeon;

public abstract class Enemy extends Entity
{
	protected int health;
	protected int maxHealth;
	protected boolean dead;
	protected int damage;
	
	protected boolean blinkTimer;
	protected int blinkCooldown;
	
	protected boolean left;
    protected boolean right;
    protected boolean up;
    protected boolean down;
 
    protected boolean attacking;
    public boolean justDied;
	
	public Enemy(Dungeon dungeon, int rx, int ry, int tx, int ty) throws SlickException 
	{
		super(dungeon, rx, ry, tx, ty);
	}
	
	public boolean isDead()
	{
		return dead;
	}
	
	public int getDamage() 
	{
		return damage;
	}
	
	public void hit(int damage)
	{
		if(dead || blinking)
		{
			return;
		}
		
		health -= damage;
		if(health <= 0)
		{
			justDied = true;
			dead = true;
		}
		
		blinking = true;
	}
}