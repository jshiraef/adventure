package computc.entities;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRectd;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import computc.Camera;
import computc.Direction;
import computc.worlds.Dungeon;
import computc.worlds.Room;
import computc.worlds.Tile;

public class Hero extends Entity
{
	private boolean dead = false;
	protected boolean chainAttack;
	protected int chainAttackCooldown;
	
	Image ironBall = new Image("res/ironball.png");
	
	
	// box2d "world"
	 
	 private final Vec2 gravity = new Vec2(0, .5f);
	 private Vec2 box2dPlayerPosition;
	 
	 
	 public Chain chain;
	 private ChainEnd ball;
	 private int ballDamage;
	
	
	public Hero(Dungeon dungeon, Room room, int tx, int ty) throws SlickException
	{
		super(dungeon, room.getRoomyX(), room.getRoomyY(), tx, ty);
		
		this.dungeon = dungeon;
		this.acceleration = 0.06f;
		this.deacceleration = 0.02f;
		this.maximumVelocity = 3f;
		
		this.ballDamage = 2;
		
		this.currentHealth = this.maximumHealth = 3;
		
		this.image = new Image("res/hero.png");
		
		
		// box2d stuff (chain)
		this.world = new World(gravity);
		
		// converts box2d position to hero's position on screen
		box2dPlayerPosition = new Vec2(this.getRoomPositionX()/30, this.getRoomPositionY()/30);
		
		if(this.dungeon.chainEnabled)
		{
		this.chain = new Chain(this.world, this);
		this.chain.playerBody.setTransform(box2dPlayerPosition, 0);
		this.ball = new ChainEnd(this.dungeon, this.getTileyX(), this.getTileyY(), this.direction, this.chain, this);
		}
	}
	
	public void render(Graphics graphics, Camera camera)
	{
		if(this.dungeon.chainEnabled)
		{
		this.chain.render(graphics, camera);
		ironBall.draw(this.chain.lastLinkBody.getPosition().x * 30, (this.chain.lastLinkBody.getPosition().y * 30));
		}
		
		if(blinking) 
		{
			if(blinkCooldown % 4 == 0) 
			{
				return;
			}
		}	
		
		super.render(graphics, camera);
		
		// converts box2d position to hero's position on screen
		box2dPlayerPosition = new Vec2(this.getLocalX(camera)/30, this.getLocalY(camera)/30);
	}
	
	public void update(Input input, int delta)
	{						
//		System.out.println("the ball at the end of the chain's x & y are: " + this.ball.x + " , " + this.ball.y);
		
		getNextPosition(input, delta);
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(input.isKeyDown(Input.KEY_Z))
		{
			maximumVelocity = 6f;
		}
		else
		{
			maximumVelocity = 3f;
		}
		
		
		if (blinkCooldown > 0)
		{
			blinkCooldown --;
		}
		
		if(blinkCooldown == 0)
		{
			blinking = false;
		}
		
		
		// chain stuff
		if(this.dungeon.chainEnabled)
		{
		
			this.chain.playerBody.setTransform(box2dPlayerPosition, 0);
		
			if(chainAttackCooldown > 0)
			{
				chainAttackCooldown -= delta;
			}
		
			if(chainAttackCooldown <= 0)
			{
				chainAttack = false;
				chainAttackCooldown = 0;
			}
		
			this.ball.update();
		
			this.chain.update(input, delta);
		
		}
	
		this.dungeon.getRoom(this.getRoomyX(), this.getRoomyY()).visited = true;
		
		super.update(delta);
		
		// the update method for the box2d world
		world.step(1/60f, 8, 3);
		
	}
	
	// movement method
	private void getNextPosition(Input input, int delta)
	{
		if(input.isKeyDown(Input.KEY_UP)) 
		{
			dy -= acceleration * delta;
			if(dy < -maximumVelocity)
			{
				dy = -maximumVelocity;
			}
		}
		else if(input.isKeyDown(Input.KEY_DOWN))
		{
			dy += acceleration * delta;
			
			if(dy > maximumVelocity)
			{
				dy = maximumVelocity;
			}
		}
		
		else //if neither KEY_UP nor KEY_DOWN
		{
			if (dy > 0) 
			{
				dy -= deacceleration * delta;
				if(dy < 0)
				{
					dy = 0;
				}
			}
			else if (dy < 0)
			{
				dy += deacceleration * delta;
				if(dy > 0) 
				{
					dy = 0;
				}
			}
		}

		 if(input.isKeyDown(Input.KEY_RIGHT))
		{
			dx += acceleration * delta;
			if(dx > maximumVelocity) 
			{
				dx = maximumVelocity;
			}
		}
		 else if(input.isKeyDown(Input.KEY_LEFT)) 
		{
			dx -= acceleration * delta;
			if(dx < -maximumVelocity)
			{
				dx = -maximumVelocity;
			}
		}
		else //if neither KEY_RIGHT nor KEY_LEFT
		{
			if (dx > 0) 
			{
				dx -= deacceleration * delta;
				if(dx < 0)
				{
					dx = 0;
				}
			}
			else if (dx < 0)
			{
				dx += deacceleration * delta;
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
//				this.y -= step;
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
			
			if(input.isKeyDown(Input.KEY_D))
			{
				this.dungeon.toggleDebugDraw();
			}
			
	}
	
	private void hit(int damage)
	{
		if(blinking)
		{
			return;
		}
		
		currentHealth -= damage;
		
		if(currentHealth < 0)
		{
			currentHealth = 0;
		}
		
		if(currentHealth == 0)
		{
			dead = true;
		}
		
		blinking = true;
		blinkCooldown = 100;
	}
	
	public void checkAttack(LinkedList<Enemy> enemies)
	{
		for(int i = 0; i < enemies.size(); i++)
		{
			Enemy e = enemies.get(i);
			if(intersects(e))
			{
				hit(e.getDamage());
				e.maximumVelocity = .3f;
			}
			
			if(chainAttack)
			{
				if(this.ball.intersects(e))
				{
					e.hit(ballDamage);
					break;
				}
			}
		}
	}
	
	public int getHealth()
	{
		return currentHealth;
	}
	
	public boolean isDead()
	{
		return dead;
	}
	
	public void setAlive()
	{
		dead = false;
	}
	
	public void setChainAttack()
	{
		chainAttack = true;
		chainAttackCooldown = 200;
	}
	
	public boolean getChainAttack()
	{
		return chainAttack;
	}
	
	private float speed = 0.25f;
}