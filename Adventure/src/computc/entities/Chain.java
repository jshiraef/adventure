package computc.entities;

import java.util.HashSet;
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

import computc.worlds.Room;

public class Chain 
{
	protected float x;
	protected float y;
	
	private int meterToPixel;
	
	 public  Set<Body> bodies = new HashSet<Body>();
	 public  Set<Body> staticBodies = new HashSet<Body>();
	 
	 private World world;
	 
	 // box2d BodyDefinitions
	 BodyDef playerBodyDef, linkBodyDef, lastLinkBodyDef, wallBodyDef;
	 Body playerBody, linkBody, lastLinkBody, wallBody;
	 FixtureDef chainProperties, wallProperties;
	 RevoluteJointDef joint;
	 
	 // box2d shapes
	 PolygonShape playerBoxShape, chainLinkShape;
	 CircleShape wallCollisionShape;
	 
	 private float anchorY;
	 private Vec2 anchor;
	 
	 private Entity entity;
	
	public Chain(World world, Entity entity)
	{
		this.world = world;
		this.entity = entity;
		
		this.meterToPixel = 30;
		
		
		
		this.x = (entity.getX() + entity.getHalfWidth())/30;
		this.y = (entity.getY() + entity.getHalfWidth())/30;
		
		System.out.println("the chain's entity x & y is: " + this.entity.getX() + " , " + this.entity.getY());
	}
	
	public void update(int delta)
	{
	// converts box2d position to hero's position on screen
		Vec2 box2DplayerPosition = new Vec2(entity.getX()/entity.getRoomyX() - Room.WIDTH, entity.getRoomPositionY()/30);
						
	// binds the chain to the hero's position
//		playerBody.setTransform(new Vec2(this.x, this.y), 0);
		
//		System.out.println("the chain's playerbody is: " + playerBody.getPosition().x + " , " + playerBody.getPosition().y);
//		System.out.println("the chain's x is: " + this.x + " , " + this.y);
				
				
		if(Mouse.isButtonDown(0)) 
		{
			if(Mouse.getX() > entity.getRoomPositionX())
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
	}
	
	public void setupChain(World world)
	{
		// setup Player's box2d body 
			playerBodyDef = new BodyDef();
			playerBodyDef.type = BodyType.STATIC;
			playerBodyDef.position.set(this.x, this.y);
			playerBody = world.createBody(playerBodyDef);
			playerBoxShape = new PolygonShape();
			playerBoxShape.setAsBox(0.8f, 0.8f);
			playerBody.createFixture(playerBoxShape, 0.0f);
			
			chainLinkShape = new PolygonShape();
			chainLinkShape.setAsBox(0.3f, 0.060f);
				
		// setup chain Properties (FixtureDef)
			chainProperties = new FixtureDef();
			chainProperties.shape = chainLinkShape;
			chainProperties.density = 1.0f;
			chainProperties.friction = 0.002f;
							
		// joint setup
			joint = new RevoluteJointDef();
			joint.collideConnected = false;
			anchorY = entity.getY()/30 - 3f;
			Body prevBody = playerBody;
			bodies.add(playerBody);
				
		// make chain links
			for (float i = entity.getX()/30 - 12; i < entity.getY()/30; i++)
				{
					if(i >= entity.getRoomPositionY()/30 - 1)
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
						System.out.println("This did happen!");
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
	
	public void loadRoomRigidBodies(World world)
	{
		destroyRoomRigidBodies(world);
		
		for (int i = 0; i < Room.TILEY_WIDTH; i++)
		{
			for(int j = 0; j < Room.TILEY_HEIGHT; j++) 
			{
				if(entity.getRoom().getTile((float)64*i, (float)64*j).isBlocked)
				{
				wallBodyDef = new BodyDef();
				wallBodyDef.type = BodyType.STATIC;
				wallCollisionShape = new CircleShape();
				wallCollisionShape.setRadius(1.1f);
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
	
	public void destroyRoomRigidBodies(World world)
	{	
		for(Body body: staticBodies)
		{
			world.destroyBody(body);
		}
		staticBodies.clear();
	}

}
