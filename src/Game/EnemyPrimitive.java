package Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.newdawn.slick.SlickException;

public class EnemyPrimitive extends Enemy{

	// units are update cycles
	private final static int DIRECTION_SWITCH_MIN_TIME 	= 25;
	private final static int IDLE_WAITING_TIME 			= 100;
	
	private float	speed			= -5;
	private int		updateCoutner	= 0;
	private boolean	idle			= false;
	
	public EnemyPrimitive(Game game, float posX, float posY, float width, float height, float density, float friction, float restitution, String imgPath,
			BodyType bodyType) throws SlickException {
		super(game, posX, posY, width, height, density, friction, restitution, imgPath, bodyType);

		this.body.setFixedRotation(true);
	}

	@Override
	public void update() {
		if(!dead){ 
		
			if(this.isOnGround() && !idle){
				this.body.setLinearVelocity(new Vec2(speed, this.body.getLinearVelocity().y) );
//				this.body.applyLinearImpulse( new Vec2(speed, this.body.getLinearVelocity().y), this.body.getWorldCenter());
			}
			
			
			if(this.isOnWall()  && updateCoutner > DIRECTION_SWITCH_MIN_TIME){
				speed = -speed;
				idle = true;
				updateCoutner=0;
			}
			
			if ( idle && updateCoutner > IDLE_WAITING_TIME){
				this.idle = false;
				updateCoutner=0;
			}
		}
		
		++updateCoutner;
	}

}