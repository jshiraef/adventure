package computc;

import java.awt.Point;
import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class World
{
	public Hero hero;
	public Thug practiceThug;
	public TiledRoom room;
	public LinkedList<Enemy> enemies;
	public Camera camera;
	private Image happyHeart;
	
	public World() throws SlickException
	{
		this.room = new TiledRoom();
		
		this.hero = new Hero(this, 5, 1);
		
		this.enemies = new LinkedList<Enemy>();
		for(Point point : new Point[] {new Point(1, 3), new Point(5, 7), new Point(8, 5), new Point(17, 7), new Point(16, 14),  new Point(5, 16),  new Point(5, 13),  new Point(5, 14), new Point(27, 12), new Point(27, 4), new Point(16, 22)})
		{
		 	this.enemies.add(new Thug(this, point.x, point.y));
		}
		
		this.camera = new Camera(hero);
		this.happyHeart = new Image("res/heart.png");
	}
	
	public void update(Input input, int delta)
	{
		if(!hero.isDead() == true)
		{
			this.camera.update(delta);
			this.hero.update(input, delta);
		
			// enemies
			for(int i = 0; i < enemies.size(); i++) 
			{
				Enemy e = enemies.get(i);
				e.update(delta);
					if(e.isDead())
					{
						enemies.remove(i);
						i--;
					}
			}
		
		hero.checkAttack(enemies);
		System.out.println(" hero's health: " + hero.getHealth());
		}
		
		else System.out.println("reset");
	}
	
	public void render(Graphics graphics)
	{
		this.room.render(graphics, camera);
		
		for(Enemy enemy : this.enemies)
		{
			enemy.render(graphics, camera);
		}
		
		for(int i = 0; i < hero.getHealth(); i++)
		{
			happyHeart.draw(50+(10*i), 50);
		}

		this.hero.render(graphics, camera);
	}
}