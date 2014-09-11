package computc.entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import computc.Camera;
import computc.worlds.Dungeon;
import computc.worlds.Tile;

public class OldMan extends Entity
{
	private Hero hero;
	private Animation animation;
	
	public OldMan(Dungeon dungeon, Hero hero, int tx, int ty) throws SlickException
	{
		super(dungeon, tx, ty);
		
		this.hero = hero;
		
		this.image = new Image("res/ancient.png").getSubImage(1, 1, 240, 104);
		this.animation =  new Animation(new SpriteSheet(this.image, 60, 104), 300);
	}
	
	public void update(int delta)
	{
		if(hero.getRoomyX() == 3 && hero.getRoomyY() == 1 && this.y < Tile.SIZE * 14.5)
		{
			this.y += .01 * delta;
		}
	}
	
	public void render(Graphics graphics, Camera camera)
	{
		int x = (int)(this.getX()) - this.getHalfWidth() - camera.getX();
		int y = (int)(this.getY()) - this.getHalfHeight() - camera.getY();
		
		this.animation.draw(x, y);
	}
}