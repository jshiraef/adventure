package computc.worlds;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRectd;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import computc.Camera;
import computc.Game;
import computc.entities.BigThug;
import computc.entities.Enemy;
import computc.entities.Thug;

public class Dungeon
{
	private HashMap<String, Room> rooms = new HashMap<String, Room>();
	public LinkedList<Enemy> thugs; 
	public Point[] thug_positions_in_tiley_coordinates;
	
	public World world;
	public Vec2 gravity = new Vec2(0, .5f);
	
	public boolean debug = false;
	
	public Dungeon() throws SlickException
	{
		TiledMap tiled = new TiledMap("./res/dungeons/prototype.dungeon.tmx");
		
		int ROOMY_WIDTH = 9;
		int ROOMY_HEIGHT = 5;
		
		for(int rx = 0; rx < ROOMY_WIDTH; rx++)
		{
			for(int ry = 0; ry < ROOMY_HEIGHT; ry++)
			{
				Room room = new Room(this, rx, ry);
				
				for(int tx = 0; tx < Room.TILEY_WIDTH; tx++)
				{
					for(int ty = 0; ty < Room.TILEY_HEIGHT; ty++)
					{
						Tile tile = new Tile(room, tx, ty);

						int rxtx = (rx * Room.TILEY_WIDTH) + tx;
						int ryty = (ry * Room.TILEY_HEIGHT) + ty;
						int tid = tiled.getTileId(rxtx, ryty, 0);
						
						tile.isBlocked = (tid == 1);
						tile.isStairs = (tid == 3);
						
						room.setTile(tx, ty, tile);
					}
				}
				
				this.addRoom(room);
			}
		}
		
		for(int rx = 0; rx < ROOMY_WIDTH; rx++)
		{
			for(int ry = 0; ry < ROOMY_HEIGHT; ry++)
			{
				Room room = this.getRoom(rx, ry);
				
				if(!room.getTile(Room.TILEY_WIDTH / 2, 0).isBlocked)
				{
					room.connectNorthernRoom(this.getRoom(rx, ry - 1));
				}
				if(!room.getTile(Room.TILEY_WIDTH / 2, Room.TILEY_HEIGHT - 1).isBlocked)
				{
					room.connectSouthernRoom(this.getRoom(rx, ry + 1));
				}
				if(!room.getTile(Room.TILEY_WIDTH - 1, Room.TILEY_HEIGHT / 2).isBlocked)
				{
					room.connectEasternRoom(this.getRoom(rx + 1, ry));
				}
				if(!room.getTile(0, Room.TILEY_HEIGHT / 2).isBlocked)
				{
					room.connectWesternRoom(this.getRoom(rx - 1, ry));
				}
			}
		}
		
		this.thugs = new LinkedList<Enemy>();
		
		thug_positions_in_tiley_coordinates = new Point[] {
				new Point(34, 5),
				new Point(42, 2),
				new Point(39, 5),
				new Point(46, 2),
				new Point(52, 6),
				new Point(48, 1),
				new Point(47, 13),
				new Point(50, 15),
				new Point(59, 12),
				new Point(60, 14),
				new Point(61, 12),
				new Point(57, 20),
				new Point(63, 24),
				new Point(57, 24),
				new Point(49, 21),
				new Point(49, 25)
			};
		
		for(Point point : thug_positions_in_tiley_coordinates)
		{
			thugs.add(new Thug(this, point.x, point.y));
		}
		
		thugs.add(new BigThug(this, 36, 23));
		
		this.world = new World(gravity);
	}
	
	public void update(int delta) throws SlickException
	{
		for(int i = 0; i < thugs.size(); i++)
		{
			Enemy e = thugs.get(i);
			e.update(delta);
				if(e.isDead())
				{
					thugs.remove(i);
					i--;
				}
		}
		
		if (Game.reset)
		{
			thugs.clear();
			
			for(Point point : thug_positions_in_tiley_coordinates)
			{
				thugs.add(new Thug(this, point.x, point.y));
			}
			Game.reset = false;
		}
	}

	public void render(Graphics graphics, Camera camera)
	{
		for(Room room : this.getAllRooms())
		{
			room.render(graphics, camera);
		}
		
		for(Enemy thug : this.thugs)
		{
			thug.render(graphics, camera);
		}
	}
	
	public void addRoom(Room room)
	{
		int rx = room.getRoomyX();
		int ry = room.getRoomyY();
		
		if(this.hasRoom(rx, ry))
		{
			throw new DungeonException();
		}
		else
		{
			this.rooms.put(rx + ":" + ry, room);
		}
	}
	
	public Room getRoom(int rx, int ry)
	{
		return this.rooms.get(rx + ":" + ry);
	}
	
	public boolean hasRoom(int rx, int ry)
	{
		return this.rooms.containsKey(rx + ":" + ry);
	}
	
	public LinkedList<Room> getAllRooms()
	{
		return new LinkedList<Room>(this.rooms.values());
	}
	
	public Tile getTile(float x, float y)
	{
		int rx = (int)(Math.floor(x / Room.WIDTH));
		int ry = (int)(Math.floor(y / Room.HEIGHT));
		
		int tx = (int)(Math.floor((x - (rx * Room.WIDTH)) / Tile.SIZE));
		int ty = (int)(Math.floor((y - (ry * Room.HEIGHT)) / Tile.SIZE));
		
		return this.getRoom(rx, ry).getTile(tx, ty);
	}
	
	public void rigidBodyDebugDraw(Set<Body> bodies) 
	{
		glClear(GL_COLOR_BUFFER_BIT);
		for(Body body: bodies)
		{
			if(body.getType() == BodyType.DYNAMIC) 
			{
				glPushMatrix();
				Vec2 bodyPosition = body.getPosition().mul(30);
				glTranslatef(bodyPosition.x, bodyPosition.y, 0);
				glRotated(Math.toDegrees(body.getAngle()), 0, 0, 1);
				glRectd(-0.6f * 30, -0.125 * 30, 0.6f * 30, 0.125 * 30);
	//			System.out.println("the actual box 2d position of the body is: "  + body.getPosition().x + " , " + body.getPosition().y);
				System.out.println("this is happening");
				glPopMatrix();
			}
		}
	}
	
	public boolean toggleDebugDraw()
	{
		return debug = !debug;
	}
	
	public boolean getDebugDraw()
	{
		return debug;
	}
}