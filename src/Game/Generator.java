package Game;

import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Generator extends GameObjectBox {
	
	private float		x, y;
	private int 		lifetimeCounter = 0;
	private int 		MAX_LIFETIME = 5000;
	
	private Animation	animation;
	private boolean repaired = false;
	
	public Generator(World world, float posX, float posY, float width, float height) throws SlickException {
		super(world, posX, posY, width, height, 0, 0, 0, null, BodyType.STATIC, true);
		img = Images.getInstance().getImage("images/generatorbroken.png");
		x = posX;
		y = posY;
		this.width = width;
		this.height = height;
		initializeSprite();
	}
	
	@Override
	public void drawImage() {
		if (repaired) {
			animation.draw(x - width * 0.5f, y - height * 0.5f, width, height);
		} else {
			img.draw(x - width * 0.5f, y - height * 0.5f, width, height);
		}		
	}
	
	public Animation getAnimation() {
		return animation;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	private void initializeSprite() {
		SpriteSheet spriteSheet = null;
		
		try {
			spriteSheet = Images.getInstance().getSpriteSheet("images/generatoranimated.png", 385, 568);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		animation = new Animation(spriteSheet, 100);
		animation.setPingPong(true);
		animation.start();
	}
	
	
	public void repair() {
		repaired = true;	
		lifetimeCounter = 0;
	}
	public boolean isRepaired() {
		return repaired;
	}
	
	public void update(){
		
		++lifetimeCounter;
		
		// FIXME checken ob spieler nicht gerade aufl�dt
		if(lifetimeCounter > MAX_LIFETIME){
			repaired = false;
		}
		
	}
}
