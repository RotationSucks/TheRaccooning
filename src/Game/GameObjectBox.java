package Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class GameObjectBox extends GameObjectPolygon {
	
	protected float	width;
	protected float	height;
	
	public GameObjectBox(World world, float posX, float posY, float width, float height, float density, float friction, float restitution,
			String imgPath, BodyType bodyType) throws SlickException {
		this(world, posX, posY, width, height, density, friction, restitution, imgPath, bodyType, false);
	}
	
	public GameObjectBox(World world, float posX, float posY, float width, float height, float density, float friction, float restitution,
			String imgPath, BodyType bodyType, boolean isSensor) throws SlickException {
		super(world, posX, posY, new Vec2[] { new Vec2(-width * 0.5f, height * 0.5f), new Vec2(-width * 0.5f, -height * 0.5f),
				new Vec2(width * 0.5f, -height * 0.5f), new Vec2(width * 0.5f, height * 0.5f) }, density, friction, restitution, imgPath,
				bodyType, isSensor);
		this.width = width;
		this.height = height;
	}
	
	public void drawImage() {
		Vec2 position = this.getBody().getPosition();
		float angle = this.getBody().getAngle();
		this.getImage().setRotation((float) Math.toDegrees(angle));
		this.getImage().draw(position.x - this.width / 2, position.y - this.height / 2, this.width, this.height);
	}
	
	@Override
	public void drawOutline(Graphics g) {
		super.drawOutline(g);
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public float getWidth() {
		return this.width;
	}
}
