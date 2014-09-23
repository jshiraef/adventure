package computc.entities;

import org.jbox2d.dynamics.Body;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import computc.Direction;
import computc.worlds.Dungeon;
import computc.worlds.Room;

public class ChainEnd extends Entity{
	
	private boolean hit;
	private boolean remove;
	private Hero hero;
	
	private Body box2dBody;
	
	Image ironBall = new Image("res/ironBall.png");
	
	public ChainEnd(Dungeon dungeon, Room room, int tx, int ty, Direction direction, Body box2dBody, Hero hero) throws SlickException
	{
		super(dungeon, room, tx, ty);
		
		this.direction = direction;
		this.dungeon = dungeon;
		this.acceleration = 7f;
		
		this.image = ironBall;
		this.hero = hero;
		this.box2dBody = box2dBody;
		
		this.x = hero.getRoom().getX() + (box2dBody.getPosition().x * 30) + this.getHalfWidth();
		this.y = hero.getRoom().getY() + (box2dBody.getPosition().y * 30) + this.getHalfHeight();
		
		if(direction == Direction.NORTH)
		{
			
		}
		else if(direction == Direction.SOUTH)
		{
			
		}
		else if(direction == Direction.EAST)
		{
			
		}
		else if(direction == Direction.WEST)
		{
			
		}
		
		
	}
	
		public void update()
		{
		
			checkTileMapCollision();
			setPosition(xtemp, ytemp);
		
//			if(dx == 0 && !hit && (this.direction == Direction.EAST || this.direction == Direction.WEST))
//			{
//				setHit();
//			}
//			if(dy == 0 && !hit && (this.direction == Direction.NORTH || this.direction == Direction.SOUTH))
//			{
//				setHit();
//			}
			
			this.x = hero.getRoom().getX() + (box2dBody.getPosition().x * 30) + this.getHalfWidth();
			this.y = hero.getRoom().getY() + (box2dBody.getPosition().y * 30) + this.getHalfHeight();
			
			if(hit)
			{
				remove = true;
			}
		}
	
		public void setHit()
		{
			if(hit)
				return;
			hit = true;
			dx = 0; dy = 0;
		}
		
		public boolean shouldRemove()
		{
			return remove;
		}
		
		public float getX()
		{
			return x;
		}
		
		public float getY()
		{
			return y;
		}
		
		public Image getImage()
		{
			return this.image;
		}
	}


