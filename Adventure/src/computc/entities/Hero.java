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

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import computc.Camera;
import computc.Direction;
import computc.worlds.Dungeon;
import computc.worlds.Room;

public class Hero extends Entity
{
	private boolean dead = false;
	
	Image chainLink = new Image("res/links.png");
	
	
	// box2d "world"
	 private World world;
	 
	 private final Vec2 gravity = new Vec2(0, .5f);
	 
	 private  Set<Body> bodies = new HashSet<Body>();
	 
	  BodyDef playerBodyDef, linkBodyDef;
	  Body playerBody, linkBody;
	  PolygonShape playerBoxShape, chainLinkShape;
	 FixtureDef chainProperties;
	 RevoluteJointDef joint;
	 
	 private float anchorY;
	 private Vec2 anchor;
	
	public Hero(Dungeon dungeon, Room room, int tx, int ty) throws SlickException
	{
		super(dungeon, room.getRoomyX(), room.getRoomyY(), tx, ty);
		
		this.dungeon = dungeon;
		this.acceleration = 0.06f;
		this.deacceleration = 0.02f;
		this.maximumVelocity = 3f;
		
		this.currentHealth = this.maximumHealth = 3;
		
		this.image = new Image("res/hero.png");
		
		
		// box2d stuff (chain)
		this.world = new World(gravity);
		
		playerBody = null;
		
		// setup Player's box2d body 
	 	playerBodyDef = new BodyDef();
	 	playerBodyDef.type = BodyType.STATIC;
		playerBody = world.createBody(playerBodyDef);
		playerBoxShape = new PolygonShape();
		playerBoxShape.setAsBox(1.6f, 1.6f);
		playerBody.createFixture(playerBoxShape, 0.0f);
	
	
		chainLinkShape = new PolygonShape();
		chainLinkShape.setAsBox(0.6f, 0.125f);
		
		// setup chain Properties ( FixtureDef)
		chainProperties = new FixtureDef();
		chainProperties.shape = chainLinkShape;
		chainProperties.density = 20.0f;
		chainProperties.friction = 0.2f;
					
		// joint setup
		joint = new RevoluteJointDef();
		joint.collideConnected = false;
		anchorY = this.getRoomPositionY()/30f;
		Body prevBody = playerBody;
		bodies.add(playerBody);
		
		// make chain links
		for (float i = this.getRoomPositionX()/30; i < this.getRoomPositionY()/30 + 15; i++)
		{
			linkBodyDef = new BodyDef();
			linkBodyDef.type = BodyType.DYNAMIC;
			linkBodyDef.position.set(0.5f + i, anchorY);
			linkBody = world.createBody(linkBodyDef);	
			linkBody.createFixture(chainProperties);
			anchor = new Vec2(i, anchorY);
			
			// initialize joint
			joint.initialize(prevBody, linkBody, anchor);
			world.createJoint(joint);
			prevBody = linkBody;
			bodies.add(linkBody);
		}
		
	}
	
	public void render(Graphics graphics, Camera camera)
	{
		super.render(graphics, camera);
		
		if(!this.dungeon.getDebugDraw())
		{
			// draw chain - chain link rotation needs work
			for(Body body: bodies)
			{
				if(body.getType() == BodyType.DYNAMIC) 
				{
					Vec2 bodyPosition = body.getPosition().mul(30);
					chainLink.draw(bodyPosition.x, bodyPosition.y);
					System.out.println("the link should be drawn at" + bodyPosition.x + " , " + bodyPosition.y);
					chainLink.setRotation((float) Math.toDegrees(body.getAngle()));
				}
			}
		}
		
		// draw debug mode
		else this.dungeon.rigidBodyDebugDraw(bodies);
		
		if(blinking) 
		{
			if(blinkCooldown % 4 == 0) 
			{
				return;
			}
		}
			
		
	}
	
	public void update(Input input, int delta)
	{
		// converts box2d position to hero's position on screen
		Vec2 box2DplayerPosition = new Vec2(this.getRoomPositionX()/30 - 12, this.getRoomPositionY()/30 - 4);
				
		// binds the chain to the hero's position
		playerBody.setTransform(box2DplayerPosition, 0);
					
		// the update method for the box2d world
		world.step(1/60f, 8, 3);
		
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
	
		this.dungeon.getRoom(this.getRoomyX(), this.getRoomyY()).visited = true;
		
		super.update(delta);
		
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
	
	private float speed = 0.25f;
}