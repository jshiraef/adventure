package computc.states;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import computc.Camera;
import computc.Direction;
import computc.Game;
import computc.Menu;
import computc.entities.Hero;
import computc.entities.OldMan;
import computc.worlds.Dungeon;
import computc.worlds.Tile;

public class MainGameState extends BasicGameState
{
	public Hero hero;
	public Dungeon dungeon;
	public OldMan oldman;
	public Camera camera;
	public Menu menu;
	
	private Image menuBox;
	private Image largeTextBox;
	private int counter, counter2;
	Animation textBox;
	public Color textColor = Color.white;
	private boolean nextLevel = false;
	
	private int gravityCoolDown;
	
	public void init(GameContainer container, StateBasedGame game) throws SlickException
	{
		this.dungeon = new Dungeon();
		this.hero = new Hero(dungeon, dungeon.getRoom(4, 0), 5, 1);
		this.oldman = new OldMan(dungeon, hero, 38, 12);
		this.camera = new Camera(hero);
		this.menu = new Menu(dungeon, hero);
		
		this.menuBox = new Image("res/textBox.png");
		this.largeTextBox = new Image("res/largeTextBox.png");
		this.textBox = new Animation(new SpriteSheet(largeTextBox, 585, 100), 100);
		
		Tile.WALL_IMAGE = new Image("./res/wall.png");
		Tile.FLOOR_IMAGE = new Image("./res/floor.png");
		Tile.STAIR_IMAGE = new Image("./res/stairs.png");
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
	{
		Input input = container.getInput();
		
		if(!hero.isDead())
		{
			this.hero.update(input, delta);
			this.oldman.update(delta);
			this.dungeon.update(delta);
			this.camera.update(delta);
			
			hero.checkAttack(dungeon.thugs);
			
			if(dungeon.getTile(hero.getX(), hero.getY()).isStairs)
			{
				nextLevel = true;
			}
		}
		else
		{
			if(input.isKeyDown(Input.KEY_R))
			{
				Game.reset = true;
				this.hero = new Hero(dungeon, dungeon.getRoom(4, 0), 5, 1);
				this.camera = new Camera(hero);
				this.hero.setAlive();
			}
			if(input.isKeyDown(Input.KEY_Q))
			{
				System.exit(0);
			}
		}
		
			if(input.isKeyDown(Input.KEY_UP))
			{
				this.hero.getWorld().setGravity(new Vec2(0, 1f));
				
			}
			else if(input.isKeyDown(Input.KEY_DOWN))
			{
				this.hero.getWorld().setGravity(new Vec2(0, -1f));
			}

			if(input.isKeyDown(Input.KEY_LEFT))
			{
				this.hero.getWorld().setGravity(new Vec2(1f, 0));
			}
			else if(input.isKeyDown(Input.KEY_RIGHT))
			{
				this.hero.getWorld().setGravity(new Vec2(-1f, 0));
			}
			
			else for(Body body: hero.bodies)
			{
				body.setLinearDamping(10);
			}
			
			if(gravityCoolDown != 0)
			{
				gravityCoolDown--;
			}
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException
	{
		this.dungeon.render(graphics, camera);
		this.hero.render(graphics, camera);
		this.oldman.render(graphics, camera);
		this.menu.render(graphics, camera);
		
		/*if(hero.isDead() == true)
		{
			graphics.setColor(textColor);
			menuBox.draw(Room.WIDTH/5, Room.HEIGHT/3);
			graphics.drawString("Restart (R)", Room.WIDTH/3, Room.HEIGHT/3 + 10);
			graphics.drawString("Main Menu (M)", Room.WIDTH/3, Room.HEIGHT/3 + 30);
			graphics.drawString("Quit Game (Q)", Room.WIDTH/3, Room.HEIGHT/3 + 50);
		}
		
		if(nextLevel)
		{
			graphics.setColor(textColor);
			menuBox.draw(Room.WIDTH/5, Room.HEIGHT/3);
			graphics.drawString("Congrats! 1st floor complete!", Room.WIDTH/3, Room.HEIGHT/3 + 5);
			graphics.drawString("Restart (R)", Room.WIDTH/3, Room.HEIGHT/3 + 20);
			graphics.drawString("Main Menu (M)", Room.WIDTH/3, Room.HEIGHT/3 + 35);
			graphics.drawString("Quit Game (Q)", Room.WIDTH/3, Room.HEIGHT/3 + 50);
		}*/
	}
	
	public int getID()
	{
		return MainGameState.ID;
	}
	@Override
	
	public void keyPressed(int k, char c)
	{
		if(k == Input.KEY_W)
		{
			if(Mouse.getX() > this.hero.getRoomPositionX())
			{
			  Vec2 mousePosition = new Vec2(Mouse.getX() + 10000, Mouse.getY()).mul(0.5f).mul(1/30f);
			  Vec2 playerPosition = new Vec2(this.hero.playerBody.getPosition());
			  Vec2 force = mousePosition.sub(playerPosition);
			  this.hero.lastLinkBody.applyForce(force,  this.hero.lastLinkBody.getPosition());
			}
			else
			{
				Vec2 mousePosition = new Vec2(Mouse.getX() - 10000, Mouse.getY()).mul(0.5f).mul(1/30f);
				Vec2 playerPosition = new Vec2(this.hero.playerBody.getPosition());
				Vec2 force = mousePosition.sub(playerPosition);
				this.hero.lastLinkBody.applyForce(force,  this.hero.lastLinkBody.getPosition());
			}
		}
	}
	
	@Override
	public void keyReleased(int k, char c)
	{
		if(k == Input.KEY_UP)
		{
			
		}
		
		if(k == Input.KEY_DOWN)
		{
			
		}
		
		if(k == Input.KEY_LEFT)
		{
			
		}
		
		if(k == Input.KEY_RIGHT)
		{
			
		}
		
		if(k == Input.KEY_W)
		{
			if(Mouse.getX() > this.hero.getRoomPositionX())
			{
			  Vec2 mousePosition = new Vec2(Mouse.getX() - 1000000, Mouse.getY()).mul(0.5f).mul(1/30f);
			  Vec2 playerPosition = new Vec2(this.hero.playerBody.getPosition());
			  Vec2 force = mousePosition.sub(playerPosition);
			  this.hero.lastLinkBody.applyForce(force,  this.hero.lastLinkBody.getPosition());
			}
			else
			{
				Vec2 mousePosition = new Vec2(Mouse.getX() + 1000000, Mouse.getY()).mul(0.5f).mul(1/30f);
				Vec2 playerPosition = new Vec2(this.hero.playerBody.getPosition());
				Vec2 force = mousePosition.sub(playerPosition);
				this.hero.lastLinkBody.applyForce(force,  this.hero.lastLinkBody.getPosition());
			}
		}
	}
	
	public static final int ID = 0;
}