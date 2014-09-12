package computc.worlds;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Image;
import org.newdawn.slick.Graphics;

import computc.Camera;

public class Tile
{
	private Room room;
	
	private int tx;
	private int ty;
	
	public boolean isBlocked;
	public boolean isStairs;
	
	BodyDef wallBodyDef;
	Body wallBody;
	PolygonShape wallShape;
	
	public Tile(Room room, int tx, int ty)
	{
		this.room = room;
		
		this.tx = tx;
		this.ty = ty;
	}
	
	public void render(Graphics graphics, Camera camera)
	{
		int x = this.getX() - camera.getX();
		int y = this.getY() - camera.getY();
		
		if(this.isBlocked)
		{
			Tile.WALL_IMAGE.draw(x/30, y/30);
			wallBodyDef = new BodyDef();
		 	wallBodyDef.type = BodyType.STATIC;
			wallBody = this.room.dungeon.world.createBody(wallBodyDef);
			wallShape = new PolygonShape();
			wallShape.setAsBox(2.13f, 2.13f);
			
		}
		else if(this.isStairs)
		{
			Tile.STAIR_IMAGE.draw(x,y);
		}
		else
		{
			Tile.FLOOR_IMAGE.draw(x, y);
		}
	}
	
	/*
	 * Returns the horizontal position
	 * of this tile in units of pixels
	 * and relative to the dungeon.
	 * 
	 * @units_of		pixels
	 * @relative_to		dungeon
	 */
	public int getX()
	{
		return this.getTileyX() * Tile.SIZE + this.room.getX();
	}
	
	/*
	 * Returns the vertical position
	 * of this tile in units of pixels
	 * and relative to the dungeon.
	 * 
	 * @units_of		pixels
	 * @relative_to		dungeon
	 */
	public int getY()
	{
		return this.getTileyY() * Tile.SIZE + this.room.getY();
	}
	
	/*
	 * Returns the horizontal position
	 * of this tile in units of tiles
	 * and relative to the room.
	 * 
	 * @units_of		tiles
	 * @relative_to		room
	 */
	public int getTileyX()
	{
		return this.tx;
	}
	
	/*
	 * Returns the vertical position
	 * of this tile in units of tiles
	 * and relative to the room.
	 * 
	 * @units_of		tiles
	 * @relative_to		room
	 */
	public int getTileyY()
	{
		return this.ty;
	}

	/*
	 * Returns the horizontal dimension
	 * of this tile in units of pixels.
	 * 
	 * @units_of		pixels
	 */
	public int getWidth()
	{
		return Tile.SIZE;
	}
	
	/*
	 * Returns the vertical dimension
	 * of this tile in units of pixels.
	 * 
	 * @units_of		pixels
	 */
	public int getHeight()
	{
		return Tile.SIZE;
	}
	
	public static Image WALL_IMAGE;
	public static Image FLOOR_IMAGE;
	public static Image STAIR_IMAGE;
	
	public final static int SIZE = 64;
}