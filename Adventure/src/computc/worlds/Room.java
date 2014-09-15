package computc.worlds;

import java.io.File;
import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import computc.Camera;
import computc.Direction;

public class Room
{
	private int rx, ry;
	private Tile[][] tiles;
	public Dungeon dungeon;
	
	public boolean visited;
	
	public Room westernRoom;
	public Room easternRoom;
	public Room southernRoom;
	public Room northernRoom;
	
	public Room(Dungeon dungeon, int rx, int ry) throws SlickException
	{
		this.dungeon = dungeon;
		
		this.rx = rx;
		this.ry = ry;
		
		this.tiles = new Tile[Room.TILEY_WIDTH][Room.TILEY_HEIGHT];
		
		//this.loadLayout(Room.getRandomLayout());
	}
	
	public void loadLayout(String layout) throws SlickException
	{
		TiledMap tmx = new TiledMap(layout);
		
		for(int tx = 0; tx < this.getTileyWidth(); tx++)
		{
			for(int ty = 0; ty < this.getTileyHeight(); ty++)
			{
				int tid = tmx.getTileId(tx, ty, 0);
				Tile tile = new Tile(this, tx, ty);
				tile.isBlocked = (tid == 1);
				
				this.setTile(tx, ty, tile);
			}
		}
	}
	
	public void render(Graphics graphics, Camera camera)
	{
		for(int tx = 0; tx < this.getTileyWidth(); tx++)
		{
			for(int ty = 0; ty < this.getTileyHeight(); ty++)
			{
				this.tiles[tx][ty].render(graphics, camera);
			}
		}
	}
	
	/*
	 * Returns the horizontal position
	 * of this room in units of pixels
	 * and relative to the dungeon.
	 * 
	 * @units_of		pixels
	 * @relative_to		dungeon
	 */
	public int getX()
	{
		return this.getRoomyX() * Room.WIDTH;
	}

	/*
	 * Returns the vertical position
	 * of this room in units of pixels
	 * and relative to the dungeon.
	 * 
	 * @units_of		pixels
	 * @relative_to		dungeon
	 */
	public int getY()
	{
		return this.getRoomyY() * Room.HEIGHT;
	}

	/*
	 * Returns the horizontal position
	 * of this room in units of rooms
	 * and relative to the dungeon.
	 * 
	 * @units_of		rooms
	 * @relative_to		dungeon
	 */
	public int getRoomyX()
	{
		return this.rx;
	}

	/*
	 * Returns the vertical position
	 * of this room in units of rooms
	 * and relative to the dungeon.
	 * 
	 * @units_of		rooms
	 * @relative_to		dungeon
	 */
	public int getRoomyY()
	{
		return this.ry;
	}

	/*
	 * Returns the horizontal dimension
	 * of this room in units of pixels.
	 * 
	 * @units_of		pixels
	 */
	public int getWidth()
	{
		return Room.WIDTH;
	}

	/*
	 * Returns the vertical dimension
	 * of this room in units of pixels.
	 * 
	 * @units_of		pixels
	 */
	public int getHeight()
	{
		return Room.HEIGHT;
	}

	/*
	 * Returns the horizontal dimension
	 * of this room in units of tiles.
	 * 
	 * @units_of		tiles
	 */
	public int getTileyWidth()
	{
		return Room.TILEY_WIDTH;
	}
	
	/*
	 * Returns the vertical dimension
	 * of this room in units of tiles.
	 * 
	 * @units_of		tiles
	 */
	public int getTileyHeight()
	{
		return Room.TILEY_HEIGHT;
	}
	
	/*
	 * Returns the tile at the specified
	 * position in units of tiles and
	 * relative to the room.
	 * 
	 * @units_of		tiles
	 * @relative_to		room
	 */
	public Tile getTile(int tx, int ty)
	{
		return this.tiles[tx][ty];
	}
	
	/*
	 * Returns the tile at the specified
	 * position in units of pixels and
	 * relative to the room.
	 * 
	 * @units_of		pixels
	 * @relative_to		room
	 */
	public Tile getTile(float x, float y)
	{
		int tx = (int)(Math.floor(x / Tile.SIZE));
		int ty = (int)(Math.floor(y / Tile.SIZE));
		
		return this.tiles[tx][ty];
	}
	
	/*
	 * Sets a tile at the specified
	 * position in units of tiles
	 * and relative to the room.
	 * 
	 * @units_of		tiles
	 * @relative_to		room
	 */
	public void setTile(int tx, int ty, Tile tile)
	{
		this.tiles[tx][ty] = tile;
	}
	
	/*
	 * Returns the room that is north
	 * of this room, or null if there
	 * is no such room.
	 */
	public Room getNorthernRoom()
	{
		return this.northernRoom;
	}

	/*
	 * Returns the room that is south
	 * of this room, or null if there
	 * is no such room.
	 */
	public Room getSouthernRoom()
	{
		return this.southernRoom;
	}

	/*
	 * Returns the room that is east
	 * of this room, or null if there
	 * is no such room.
	 */
	public Room getEasternRoom()
	{
		return this.easternRoom;
	}

	/*
	 * Returns the room that is west
	 * of this room, or null if there
	 * is no such room.
	 */
	public Room getWesternRoom()
	{
		return this.westernRoom;
	}

	/*
	 * Returns true if there is a room
	 * to the north of this room, or
	 * false if there is no such room.
	 */
	public boolean hasNorthernRoom()
	{
		return this.northernRoom != null;
	}

	/*
	 * Returns true if there is a room
	 * to the south of this room, or
	 * false if there is no such room.
	 */
	public boolean hasSouthernRoom()
	{
		return this.southernRoom != null;
	}

	/*
	 * Returns true if there is a room
	 * to the east of this room, or
	 * false if there is no such room.
	 */
	public boolean hasEasternRoom()
	{
		return this.easternRoom != null;
	}

	/*
	 * Returns true if there is a room
	 * to the west of this room, or
	 * false if there is no such room.
	 */
	public boolean hasWesternRoom()
	{
		return this.westernRoom != null;
	}

	/*
	 * Sets the room to the north of
	 * this room, and opens a door
	 * to reach that room. 
	 */
	private void setNorthernRoom(Room room)
	{
		this.northernRoom = room;
		
		int tx = Room.TILEY_WIDTH / 2, ty = 0;
		this.tiles[tx][ty] = new Tile(this, tx, ty);
	}

	/*
	 * Sets the room to the south of
	 * this room, and opens a door
	 * to reach that room. 
	 */
	private void setSouthernRoom(Room room)
	{
		this.southernRoom = room;
		
		int tx = Room.TILEY_WIDTH / 2, ty = Room.TILEY_HEIGHT - 1;
		this.tiles[tx][ty] = new Tile(this, tx, ty);
	}

	/*
	 * Sets the room to the east of
	 * this room, and opens a door
	 * to reach that room. 
	 */
	private void setEasternRoom(Room room)
	{
		this.easternRoom = room;
		
		int tx = Room.TILEY_WIDTH - 1, ty = Room.TILEY_HEIGHT / 2;
		this.tiles[tx][ty] = new Tile(this, tx, ty);
	}

	/*
	 * Sets the room to the west of
	 * this room, and opens a door
	 * to reach that room. 
	 */
	private void setWesternRoom(Room room)
	{
		this.westernRoom = room;
		
		int tx = 0, ty = Room.TILEY_HEIGHT / 2;
		this.tiles[tx][ty] = new Tile(this, tx, ty);
	}
	
	/*
	 * Executes the relevant subroutines
	 * to connect a room to the north.
	 */
	public void connectNorthernRoom(Room that)
	{
		this.setNorthernRoom(that);
		that.setSouthernRoom(this);
	}
	
	/*
	 * Executes the relevant subroutines
	 * to connect a room to the south.
	 */
	public void connectSouthernRoom(Room that)
	{
		this.setSouthernRoom(that);
		that.setNorthernRoom(this);
	}

	/*
	 * Executes the relevant subroutines
	 * to connect a room to the east.
	 */
	public void connectEasternRoom(Room that)
	{
		this.setEasternRoom(that);
		that.setWesternRoom(this);
	}

	/*
	 * Executes the relevant subroutines
	 * to connect a room to the west.
	 */
	public void connectWesternRoom(Room that)
	{
		this.setWesternRoom(that);
		that.setEasternRoom(this);
	}

	/*
	 * Executes the relevant subroutines
	 * to connect a room in any direction.
	 */
	public void connectRoom(Direction direction, Room room)
	{
		if(direction == Direction.NORTH)
		{
			this.connectNorthernRoom(room);
		}
		else if(direction == Direction.SOUTH)
		{
			this.connectSouthernRoom(room);
		}
		else if(direction == Direction.EAST)
		{
			this.connectEasternRoom(room);
		}
		else if(direction == Direction.WEST)
		{
			this.connectWesternRoom(room);
		}
	}

	/*
	 * Returns the filepath to a room
	 * in the resources directory.
	 */
	public static String getRandomLayout()
	{
		Random random = new Random();
		File[] list = new File("./res/rooms").listFiles();
		return "./res/rooms/" + list[random.nextInt(list.length)].getName();
	}
	
	public final static int TILEY_WIDTH = 11;
	public final static int TILEY_HEIGHT = 9;
	public final static int WIDTH = Room.TILEY_WIDTH * Tile.SIZE;
	public final static int HEIGHT = Room.TILEY_HEIGHT * Tile.SIZE;
}