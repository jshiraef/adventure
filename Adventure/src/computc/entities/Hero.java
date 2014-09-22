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
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import computc.Camera;
import computc.Direction;
import computc.worlds.Dungeon;
import computc.worlds.Room;
import computc.worlds.Tile;

public class Hero extends Entity
{
	private boolean dead = false;
	
	private boolean newRoom = true;
	
	private boolean transition = true;
	
	private int roomLoadingCooldown;
	
	Image chainLink = new Image("res/links2.png");
	Image ironBall = new Image("res/ironball.png");
	
	
	// box2d "world"
	 private World world;
	 private final Vec2 gravity = new Vec2(0, .5f);
	 public  Set<Body> bodies = new HashSet<Body>();
	 private  Set<Body> staticBodies = new HashSet<Body>();
	 private Set<BodyDef> bodyDefinitions = new HashSet<BodyDef>();
	 
	 // box2d BodyDefinitions
	 BodyDef playerBodyDef, linkBodyDef, lastLinkBodyDef, wallBodyDef;
	 public Body playerBody, linkBody, lastLinkBody, wallBody;
	 FixtureDef chainProperties, wallProperties;
	 RevoluteJointDef joint;
	 
	 // box2d shapes
	 PolygonShape playerBoxShape, chainLinkShape;
	 CircleShape wallCollisionShape;
	 
	 private float anchorY;
	 private Vec2 anchor;
	 private Vec2 box2dPlayerPosition, chainTrailingPosition;
	
	public Hero(Dungeon dungeon, Room room, int tx, int ty) throws SlickException
	{
		super(dungeon, room.getRoomyX(), room.getRoomyY(), tx, ty);
		
		this.dungeon = dungeon;
		this.acceleration = 0.06f;
		this.deacceleration = 0.02f;
		this.maximumVelocity = 3f;
		
		this.currentHealth = this.maximumHealth = 15;
		
		this.image = new Image("res/hero.png");
		
		
		// box2d stuff (chain)
		this.world = new World(gravity);
		
		playerBody = null;
		
		setupChain();
		
		
	}
	
	public void render(Graphics graphics, Camera camera)
	{
		super.render(graphics, camera);
		
		if(!this.dungeon.getDebugDraw())
		{
			// draw chain - chain link rotation needs work
			for(Body body: bodies)
			{
				if(body.getType() == BodyType.DYNAMIC || body.getType() == BodyType.STATIC) 
				{
					Vec2 bodyPosition = body.getPosition().mul(30);
					if(!transition)
					{
					chainLink.draw(bodyPosition.x, bodyPosition.y);
					}
					chainLink.setRotation((float) Math.toDegrees(body.getAngle()));
				}
			}
			ironBall.draw(lastLinkBody.getPosition().x * 30, lastLinkBody.getPosition().y * 30);
		}
		
		// draw debug mode
		else this.dungeon.rigidBodyDebugDraw(bodies, staticBodies);
		
		if(blinking) 
		{
			if(blinkCooldown % 4 == 0) 
			{
				return;
			}
		}
			
		
		if(camera.getX() != this.getRoom().getX() || camera.getY() != this.getRoom().getY())
		{
			if(roomLoadingCooldown <= 0)
			{
				newRoom = true;
				roomLoadingCooldown = 200;
				
			}
			roomLoadingCooldown--;
			
			transition = true;
			lastLinkBody.setType(BodyType.STATIC);
		}
		else 
		{
			roomLoadingCooldown = 0;	
			transition = false;
			lastLinkBody.setType(BodyType.DYNAMIC);
			
		}
		
		// converts box2d position to hero's position on screen
		box2dPlayerPosition = new Vec2(this.getLocalX(camera)/30, this.getLocalY(camera)/30);
	}
	
	public void update(Input input, int delta)
	{						
		// binds the chain to the hero's position
		playerBody.setTransform(box2dPlayerPosition, 0);
		
		if(transition)
		{
			for(Body body: bodies)
			{
				body.setTransform(box2dPlayerPosition, 0);
			}
		}
		
		
		if(input.isKeyDown(Input.KEY_W)) 
		{
			if(Mouse.getX() > this.getRoomPositionX())
			{
			  Vec2 mousePosition = new Vec2(Mouse.getX() + 10000, Mouse.getY()).mul(0.5f).mul(1/30f);
			  Vec2 playerPosition = new Vec2(playerBody.getPosition());
			  Vec2 force = mousePosition.sub(playerPosition);
			  lastLinkBody.applyForce(force,  lastLinkBody.getPosition());
			}
			else
			{
				Vec2 mousePosition = new Vec2(Mouse.getX() - 10000, Mouse.getY()).mul(0.5f).mul(1/30f);
				Vec2 playerPosition = new Vec2(playerBody.getPosition());
				Vec2 force = mousePosition.sub(playerPosition);
				lastLinkBody.applyForce(force,  lastLinkBody.getPosition());
			}
		 }
		
//		System.out.println("mouse position is: " + Mouse.getX());
		
					
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
			
			if(dx > 0 || dx < 0 || dy > 0 || dy < 0)
			{
				
				if(newRoom)
				{
				loadRoomRigidBodies();
				newRoom = false;
				}
				
			}
	}
	
	// give walls rigidbodies for the chain to collide with
	public void loadRoomRigidBodies()
	{
		destroyRoomRigidBodies();
		
		for (int i = 0; i < Room.TILEY_WIDTH; i++)
		{
			for(int j = 0; j < Room.TILEY_HEIGHT; j++) 
			{
				if(this.getRoom().getTile((float)64*i, (float)64*j).isBlocked)
				{
				wallBodyDef = new BodyDef();
				wallBodyDef.type = BodyType.STATIC;
				wallCollisionShape = new CircleShape();
				wallCollisionShape.setRadius(.8f);
				wallBody = world.createBody(wallBodyDef);
				wallProperties = new FixtureDef();
				wallProperties.density = 1;
				wallProperties.restitution = .3f;
				wallProperties.shape = wallCollisionShape;
				wallBody.createFixture(wallProperties);
				Vec2 roomPosition = new Vec2((2.15f * i) + 1,(2.15f * j) + 1);
				wallBody.setTransform(roomPosition, 0);
				staticBodies.add(wallBody);
				}
			}
		}
	}
	
	// destroy all static rigidBodies
	public void destroyRoomRigidBodies()
	{	
		for(Body body: staticBodies)
		{
			world.destroyBody(body);
		}
		staticBodies.clear();
	}
	
	public void setupChain()
	{
		// setup Player's box2d body 
			playerBodyDef = new BodyDef();
			playerBodyDef.type = BodyType.KINEMATIC;
			playerBody = world.createBody(playerBodyDef);
			playerBoxShape = new PolygonShape();
			playerBoxShape.setAsBox(0.8f, 0.8f);
			playerBody.createFixture(playerBoxShape, 0.0f);
			
			chainLinkShape = new PolygonShape();
			chainLinkShape.setAsBox(0.5f, 0.060f);
				
		// setup chain Properties (FixtureDef)
			chainProperties = new FixtureDef();
			chainProperties.shape = chainLinkShape;
			chainProperties.density = 1.0f;
			chainProperties.friction = 0.002f;
							
		// joint setup
			joint = new RevoluteJointDef();
			joint.collideConnected = false;
			anchorY = this.getRoomPositionY()/30 - 3f;
			Body prevBody = playerBody;
			bodies.add(playerBody);
				
		// make chain links
			for (float i = this.getRoomPositionX()/30 - 12; i < this.getRoomPositionY()/30; i++)
				{
					if(i >= this.getRoomPositionY()/30 - 2)
					{
						lastLinkBodyDef = new BodyDef();
						lastLinkBodyDef.type = BodyType.DYNAMIC;
						lastLinkBodyDef.position.set(0.2f + i, anchorY);
						lastLinkBody = world.createBody(lastLinkBodyDef);
						lastLinkBody.createFixture(chainProperties);
						anchor = new Vec2(i, anchorY);
						
						joint.initialize(prevBody, lastLinkBody, anchor);
						world.createJoint(joint);
						prevBody = linkBody;
						bodies.add(lastLinkBody);
					}
					else
					{
					linkBodyDef = new BodyDef();
					linkBodyDef.type = BodyType.DYNAMIC;
					linkBodyDef.position.set(0.2f + i, anchorY);
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
	
	public void setNewRoom()
	{
		newRoom = true;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	private float speed = 0.25f;
}