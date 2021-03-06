package Game;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.lwjgl.openal.AL;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.ResourceLoader;

import ch.aplu.xboxcontroller.XboxController;
import ch.aplu.xboxcontroller.XboxControllerAdapter;

import com.google.gson.Gson;

public class Game extends BasicGame {
	
	private static enum Mode {
		PLAY, PAUSE, TEXTBOX
	}
	
	private static int						screenWidth			= 1600;
	private static int						screenHeight		= 900;
	private static boolean					fullScreen			= false;
	
	private static int						MAX_SPAWN_ENEMIES	= 30;
	private static boolean					debugView			= false;
	
	private static boolean					DOOMSDAY			= false;
	private static boolean					useZoomAreas		= true;
	private static float					DEFAULT_ZOOM		= 128f;
	private static float					targetZoom			= DEFAULT_ZOOM;
	private static float					zoom				= targetZoom;
	
	private static int						spreadBolts			= 0;
	
	private static final float				MANUAL_ZOOM_STEP	= 4f;
	private static double					laserAngle;
	private static double					laserTargetAngle;
	
	private static World					world;
	private static ArrayList<GameObject>	staticObjects		= new ArrayList<GameObject>();
	private static ArrayList<GameObject>	dynamicObjects		= new ArrayList<GameObject>();
	private static ArrayList<Enemy>			enemies				= new ArrayList<Enemy>();
	private static Tile[][]					tiles;
	private static Player					player;
	private static ArrayList<Part>			parts				= new ArrayList<Part>();
	private static ArrayList<Girder>		girders				= new ArrayList<Girder>();
	private static ArrayList<Generator>		generators			= new ArrayList<Generator>();
	private static ArrayList<Spike>			spikes				= new ArrayList<Spike>();
	private static ArrayList<Conveyor>		conveyors			= new ArrayList<Conveyor>();
	private static ArrayList<Tire>			tires				= new ArrayList<Tire>();
	// private static ArrayList<Bolt> bolts = new ArrayList<Bolt>();
	// private static ArrayList<Nut> nuts = new ArrayList<Nut>();
	// private static ArrayList<Shred> shreds = new ArrayList<Shred>();
	private static ArrayList<DropItem>		dropItems			= new ArrayList<DropItem>();
	
	private static ArrayList<DropItem>		dropItemsToRemove	= new ArrayList<DropItem>();
	
	private static ArrayList<GameObject>	objectsToAdd		= new ArrayList<GameObject>();
	private static ArrayList<GameObject>	objectsToRemove		= new ArrayList<GameObject>();
	
	private static ArrayList<Enemy>			enemiesToRemove		= new ArrayList<Enemy>();
	// private static ArrayList<Shred> shredsToRemove = new ArrayList<Shred>();
	private static Color					skyGradientColor1;
	
	private static Color					skyGradientColor2;
	private static Rectangle				skyRect;
	
	private static ShapeFill				skyGradient;
	
	private static int						doomsdayCounter		= 0;
	private static House					house;
	private static Camera					cam					= new Camera(0, 0);
	
	private static Level					level;
	
	private static XboxController			xbox;
	private float							BACKGROUND_SCALE	= 0.008f;
	private float							smoothBoltGUIAngle;
	
	private static Image[]					trashpile			= new Image[5];
	private static Image					pauseImage;
	
	private static SpriteSheet				digits;
	
	private static Laser					laser;
	
	private static final double				THUMBSTICK_DEADZONE	= 0.2d;
	private static final double				TRIGGER_DEADZONE	= 0.2d;
	
	private double							xboxLeftThumbDirection;
	private double							xboxRightThumbDirection;
	private double							xboxLeftThumbMagnitude;
	private double							xboxRightThumbMagnitude;
	private double							xboxLeftTrigger;
	private double							xboxRightTrigger;
	
	private Textbox							textbox				= new Textbox();
	
	private Music drama, bgmusic, dubstep;
	
	public static ArrayList<Conveyor> getConveyors() {
		return conveyors;
	}
	
	public static ArrayList<DropItem> getDropItems() {
		return dropItems;
	}
	
	public static ArrayList<DropItem> getDropItemsToRemove() {
		return dropItemsToRemove;
	}
	
	public static ArrayList<Enemy> getEnemiesToRemove() {
		return enemiesToRemove;
	}
	
	public static ArrayList<Generator> getGenerators() {
		return generators;
	}
	
	public static ArrayList<GameObject> getObjectsToAdd() {
		return objectsToAdd;
	}
	
	public static ArrayList<GameObject> getObjectsToRemove() {
		return objectsToRemove;
	}
	
	public static ArrayList<Spike> getSpikes() {
		return spikes;
	}
	
	public static void main(String[] args) throws SlickException {
		AppGameContainer game = new AppGameContainer(new Game());
		if (fullScreen) {
			screenWidth = game.getScreenWidth();
			screenHeight = game.getScreenHeight();
		}
		game.setDisplayMode(screenWidth, screenHeight, fullScreen);
		game.setMultiSample(16);
		game.setTargetFrameRate(60);
		game.setVSync(true);
		game.setShowFPS(false);
		game.start();
	}
	
	private Mode						curMode			= Mode.PLAY;
	
	private Color						multiplierColor	= new Color(245, 179, 141);
	private Color						earthColor		= new Color(164, 74, 13);
	
	public double						skyColorGlow	= 0;
	private SpriteSheet					energy;
	private float						deathFade;
	private TrueTypeFont				fontBig;
	private TrueTypeFont				fontSmall;
	private ArrayList<DialogTrigger>	dialogs			= new ArrayList<DialogTrigger>();
	private Boaris						boaris;
	
	public Game() {
		super("The Raccooning");
	}
	
	public GameObject chooseTelekinesisTarget() {
		for (GameObject object : dynamicObjects) {
			
			float objectX = object.getBody().getPosition().x;
			float playerX = player.getBody().getPosition().x;
			float distance = Math.abs(objectX - playerX);
			float space = 5f;
			
			if (distance < space) {
				return object;
			}
		}
		return null;
	}
	
	private void doomsdaySky() {
		
		int max = 30;
		skyColorGlow += 0.01;
		double x = Math.sin(skyColorGlow) * max;
		
		Color skyGradientColor1 = new Color((int) (100 + x), 0, 0);
		Color skyGradientColor2 = new Color(0, 0, 0);
		
		skyGradient = new GradientFill(0, 0, skyGradientColor1, 0, screenHeight, skyGradientColor2);
		
	}
	
	private void drawBackgroundObjects(Graphics g) {
		if (level.getBackgroundObjects() != null) {
			for (BackgroundObject bo : level.getBackgroundObjects()) {
				g.pushTransform();
				g.translate(-cam.getX() * zoom * bo.getZ() + screenWidth / 2, -cam.getY() * zoom * bo.getZ() + screenHeight / 2);
				g.scale(zoom * bo.getZ(), zoom * bo.getZ());
				
				Image tmp = trashpile[bo.getType()];
				tmp = tmp.getFlippedCopy(bo.isFlipped(), false);
				tmp.setImageColor(1f - bo.getZ() + multiplierColor.r * bo.getZ(), 1f - bo.getZ() + multiplierColor.g * bo.getZ(),
						1f - bo.getZ() + multiplierColor.b * bo.getZ());
				tmp.draw(bo.getX() - tmp.getWidth() * BACKGROUND_SCALE / 2, bo.getY() - tmp.getHeight() * BACKGROUND_SCALE / 2,
						tmp.getWidth() * BACKGROUND_SCALE, tmp.getHeight() * BACKGROUND_SCALE);
				g.popTransform();
			}
		}
	}
	
	private void drawInterface() throws SlickException {
		int boltGUIAngle = player.getBoltCounter();
		Image nut = Images.getInstance().getImage("images/nut2.png");
		smoothBoltGUIAngle = Functions.curveValue(boltGUIAngle, smoothBoltGUIAngle, 10);
		nut.setRotation(smoothBoltGUIAngle * 30);
		nut.setAlpha(1f);
		nut.draw(screenWidth - 130, 36);
		drawRightAlignedDigits(boltGUIAngle, screenWidth - 150, 40);
		
		float energybar = ((float) player.getLaserTime() / (float) player.getMaxLaserTime()) * 325f;
		energy.getSprite(0, 0).draw(screenWidth - 360, 125, screenWidth - 360 + (325 - energybar), 125 + 42, 0, 0, 325 - energybar, 42);
		
		energy.getSprite(0, 1).draw(screenWidth - 360 + 325 - energybar, 125, screenWidth - 360 + 325, 125 + 42, 325 - energybar, 0, 325,
				42);
	}
	
	private void drawCheckpoints(Graphics g) {
		if (level.getCheckpoints() != null) {
			for (Checkpoint cp : level.getCheckpoints()) {
				g.pushTransform();
				g.translate(-cam.getX() * zoom + screenWidth / 2, -cam.getY() * zoom + screenHeight / 2);
				g.scale(zoom, zoom);
				g.drawRect(cp.getX1(), cp.getY1(), cp.getWidth(), cp.getHeight());
				g.popTransform();
			}
		}
	}
	
	private void drawRightAlignedDigits(int number, int x, int y) {
		String text = Integer.toString(number);
		while (text.length() < 4) {
			text = "0" + text;
		}
		for (int i = 0; i < text.length(); ++i) {
			int c = text.charAt(i) - 48;
			digits.getSprite(c, 0).draw(x + (i * 50 - text.length() * 50), y);
		}
	}
	
	private void drawZoomAreas(Graphics g) {
		if (level.getZoomAreas() != null) {
			for (ZoomArea za : level.getZoomAreas()) {
				g.pushTransform();
				g.translate(-cam.getX() * zoom + screenWidth / 2, -cam.getY() * zoom + screenHeight / 2);
				g.scale(zoom, zoom);
				g.drawRect(za.getX1(), za.getY1(), za.getWidth(), za.getHeight());
				g.popTransform();
			}
		}
	}
	
	public ArrayList<GameObject> getDynamicObjects() {
		return dynamicObjects;
	}
	
	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}
	
	public ArrayList<Part> getParts() {
		return parts;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void getRidOfPart(Part part) {
		// this.parts.remove(part);
	}
	
	public World getWorld() {
		return world;
	}
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		Sounds.getInstance().loadAudioFiles();
		
		drama = new Music("audio/drama.ogg");
		bgmusic = new Music("audio/menumelody.ogg");
		dubstep = new Music("audio/dubstep.ogg");
		
		loadFonts();
		
		Sounds.getInstance().play("impact", 1f, 0.8f);
		
		setupXBox();
		
		for (int i = 0; i <= 4; ++i) {
			trashpile[i] = Images.getInstance().getImage("images/background" + (i + 1) + ".png");
		}
		
		pauseImage = Images.getInstance().getImage("images/Pause.png");
		digits = Images.getInstance().getSpriteSheet("images/digits.png", 50, 80);
		energy = Images.getInstance().getSpriteSheet("images/energy.png", 325, 42);
		
		world = new World(new Vec2(0f, 20f), false);
		
		// load the level
		try {
			String json = readFile("levels/level2.json");
			level = new Gson().fromJson(json, Level.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		house = new House(world, -21.03f, level.getHeight() - 0.88f);
		
		// create the tiles
		tiles = new Tile[level.getWidth()][level.getHeight()];
		for (int x = 0; x < level.getWidth(); ++x) {
			for (int y = 0; y < level.getHeight(); ++y) {
				Block b = level.getBlock(x, y);
				if (b.getType() > 0) {
					Tile newTile = new Tile(world, x, y, b.getType(), 180 + b.getAngle(), !b.isFlipped());
					tiles[x][y] = newTile;
					
					// SPIKES
					if (isSpike(b.getType())) {
						Spike newSpike = new Spike(world, x, y, b.getType(), 180 + b.getAngle(), !b.isFlipped());
						spikes.add(newSpike);
					}
				}
			}
		}
		
		if (level.getGirders() != null)
			for (Girder gi : level.getGirders()) {
				girders.add(new Girder(world, gi.getX(), gi.getY(), 7.75f));
			}
		
		if (level.getGenerators() != null)
			for (Generator ge : level.getGenerators()) {
				generators.add(new Generator(world, ge.getX(), ge.getY(), 1.25f, 1.5f));
			}
		
		if (level.getConveyors() != null)
			for (Conveyor ge : level.getConveyors()) {
				Conveyor newConveyor = new Conveyor(world, ge.getX(), ge.getY(), 1.8f, 0.4f, 0.5f, 0.5f, 0.5f, ge.getLeft());
				conveyors.add(newConveyor);
			}
		
		if (level.getTires() != null)
			for (Tire e : level.getTires()) {
				tires.add(new Tire(world, e.getX(), e.getY(), 0.7321f));
			}
		
		if (level.getParts() != null)
			for (Part e : level.getParts()) {
				parts.add(new Part(world, this, e.getX(), e.getY()));
			}
		
		if (level.getDialogTriggers() != null)
			for (DialogTrigger e : level.getDialogTriggers()) {
				dialogs.add(new DialogTrigger(e.getX1(), e.getY1(), e.getX2(), e.getY2()));
			}
		
		// player = new Player(this, 5f, 3f);
		player = new Player(this, -16.454136f, 23.8f);
		boaris = new Boaris(-6f, 28.6f);
		cam.setPosition(new Vec2(-16.454136f, 23.8f));
		deathFade = 1f;
		smoothBoltGUIAngle = player.getBoltCounter();
		laser = new Laser(world, 0f, 0f);
		player.setLaser(laser);
		
		// player = new Player(world, 25f, 0f);
		world.setContactListener(new MyContactListener(this));
		
		// create the enemies
		if (level.getEnemies() != null)
			for (SpawnPoint e : level.getEnemies()) {
				switch (e.getType()) {
					case 0:
						enemies.add(new DumbPig(this, e.getX(), e.getY(), 0.5f, 0.5f, 3.3f, 0.3f, 0.3f, null, BodyType.DYNAMIC));
						break;
					case 1:
						enemies.add(new SmartPig(this, e.getX(), e.getY(), 0.5f, 0.5f, 3.3f, 0.3f, 0.3f, null, BodyType.DYNAMIC));
						break;
				}
			}
		
		initNormalSky();
		
	}
	
	private void setupXBox() {
		// xbox = new XboxController();
		xbox = new XboxController(is64bit() ? "xboxcontroller64" : "xboxcontroller", 1, 50, 50);
		xbox.setLeftThumbDeadZone(THUMBSTICK_DEADZONE);
		xbox.setRightThumbDeadZone(THUMBSTICK_DEADZONE);
		xbox.setLeftTriggerDeadZone(TRIGGER_DEADZONE);
		xbox.setRightTriggerDeadZone(TRIGGER_DEADZONE);
		xbox.addXboxControllerListener(new XboxControllerAdapter() {
			public void buttonA(boolean pressed) {
				if (curMode == Mode.PLAY) {
					if (pressed)
						actionJump();
					else
						actionCancelJump();
				}
				if (curMode == Mode.TEXTBOX) {
					if (pressed)
						actionTextBoxOK();
				}
			}
			
			public void rightShoulder(boolean pressed) {
				if (curMode == Mode.PLAY) {
					if (pressed)
						actionJump();
					else
						actionCancelJump();
				}
			}
			
			public void buttonB(boolean pressed) {
				if (curMode == Mode.PLAY) {
					if (pressed)
						actionGroundpound();
				}
			}
			
			public void buttonX(boolean pressed) {
				if (curMode == Mode.PLAY) {
					if (pressed) {
						actionTailwhip();
					}
				}
			}
			
			public void buttonY(boolean pressed) {
				if (curMode == Mode.PLAY) {
					if (pressed)
						actionLaserStart();
					else
						actionLaserEnd();
				}
			}
			
			public void start(boolean pressed) {
				if (pressed) {
					actionPause();
				}
			}
			
			public void leftThumb(boolean pressed) {
				if (pressed)
					actionDebugView();
			}
			
			public void leftThumbDirection(double direction) {
				xboxLeftThumbDirection = direction;
			}
			
			public void leftThumbMagnitude(double magnitude) {
				xboxLeftThumbMagnitude = magnitude;
			}
			
			public void leftTrigger(double value) {
				xboxLeftTrigger = value;
			}
			
			public void rightThumbMagnitude(double magnitude) {
				xboxRightThumbMagnitude = magnitude;
			}
			
			public void rightTrigger(double value) {
				xboxRightTrigger = value;
			}
			
			public void rightThumb(boolean pressed) {
				if (pressed)
					actionDoomsday();
			}
			
			public void rightThumbDirection(double direction) {
				xboxRightThumbDirection = direction;
			}
		});
	}
	
	protected void actionTextBoxOK() {
		if (textbox.getText() == "...and crush Boaris with all my wrath!!!") {
			bgmusic.loop();
		}
		if (textbox.hasNext()) {
			textbox.nextText();
		} else {
			curMode = Mode.PLAY;
		}
	}
	
	private void initNormalSky() {
		skyGradientColor1 = new Color(112, 149, 163);
		skyGradientColor2 = new Color(152, 199, 193);
		
		skyRect = new Rectangle(0, 0, screenWidth, screenHeight);
		skyGradient = new GradientFill(0, 0, skyGradientColor1, 0, screenHeight, skyGradientColor2);
		
	}
	
	private boolean isSpike(int type) {
		if ((type >= 50 && type <= 55) || (type == 58) || (type >= 38 && type <= 41) || (type >= 45 && type <= 46)) {
			return true;
		}
		return false;
	}
	
	public void killLaser() {
		if (player.isLaserActive()) {
			
			player.destroyLaser();
			player.setWaitingForLaserToBeKilled(false);
			
		}
	}
	
	private void pauseAnimations() {
		generators.get(0).getAnimation().stop();
		for (Enemy e : enemies) {
			e.getCurrentAnimation().stop();
		}
		player.getCurrentAnimation().stop();
		player.getLaser().getAnimation().stop();
	}
	
	// public static ArrayList<Bolt> getBolts() {
	// return bolts;
	// }
	
	// public static ArrayList<Nut> getNuts() {
	// return nuts;
	// }
	
	public void processInput(Input input) throws SlickException {
		
		if (input.isKeyDown(Input.KEY_B)) {
			textbox.setDialog(0);
			curMode = Mode.TEXTBOX;
		}
		
		if (input.isKeyPressed(Input.KEY_SPACE) || input.isKeyPressed(Input.KEY_W) || input.isKeyPressed(Input.KEY_UP)) {
			actionJump();
		}
		
		if (isTiltedLeft(xboxLeftThumbDirection, xboxLeftThumbMagnitude) || input.isKeyDown(Input.KEY_LEFT) || input.isKeyDown(Input.KEY_A)) {
			actionLeft();
		}
		
		if (input.isKeyDown(Input.KEY_LEFT) || input.isKeyDown(Input.KEY_A)) {
			if (player.isLookingLeft()) {
				player.accelerate(1f);
			}
		}
		
		if (isTiltedRight(xboxLeftThumbDirection, xboxLeftThumbMagnitude) || input.isKeyDown(Input.KEY_RIGHT)
				|| input.isKeyDown(Input.KEY_D)) {
			actionRight();
		}
		
		if (input.isKeyDown(Input.KEY_RIGHT) || input.isKeyDown(Input.KEY_D)) {
			if (!player.isLookingLeft()) {
				player.accelerate(1f);
			}
		}
		
		if (input.isKeyPressed(Input.KEY_DOWN) || input.isKeyPressed(Input.KEY_S)) {
			actionGroundpound();
		}
		
		if (input.isKeyPressed(Input.KEY_Z)) {
			useZoomAreas = !useZoomAreas;
		}
		
		if (level.getCheckpoints() != null)
			for (Checkpoint cp : level.getCheckpoints()) {
				if (!player.isDead() && cp.isInArea(player.getBody().getPosition().x, player.getBody().getPosition().y)) {
					player.setCheckpoint(cp);
				}
			}
		
		int di = 0;
		for (DialogTrigger d : dialogs) {
			Vec2 pos = player.getBody().getPosition();
			if (!d.isUsed() && d.isInArea(pos.x, pos.y)) {
				textbox.setDialog(di);
				curMode = Mode.TEXTBOX;
				d.setUsed(true);
				if (di == 1) {
					Sounds.getInstance().loopMusic("drama", 1f, 1f);
				}
			}
			++di;
		}
		
		if (input.isKeyPressed(Input.KEY_X)) {
			if (!dynamicObjects.isEmpty()) {
				// GameObject o = crates.get(0);
				// o.getBody().setLinearVelocity( new Vec2(0, 14 ) );
				// world.destroyBody( o.getBody() );
				// crates.remove(o);
			}
			for (GameObject o : dynamicObjects) {
				
				float oX = o.getBody().getPosition().x;
				float pX = player.getBody().getPosition().x;
				float distance = Math.abs(oX - pX);
				float space = 5f;
				
				if (distance < space) {
					o.getBody().setLinearVelocity(new Vec2(o.getBody().m_linearVelocity.x, (space - distance) * 1.5f));
				}
			}
			world.setGravity(new Vec2(0f, 0f));
		}
		if (input.isKeyDown(Input.KEY_Y)) {
			for (int i = 0; i < 10; ++i) {
				if (!dynamicObjects.isEmpty()) {
					GameObject o = dynamicObjects.get(0);
					world.destroyBody(o.getBody());
					dynamicObjects.remove(o);
				} else {
					break;
				}
			}
			
			world.setGravity(new Vec2(0f, 20f));
		}
		if (input.isKeyDown(Input.KEY_C)) {
			float max_size = 0.2f;
			float min_size = 0.05f;
			float size = (float) Math.random() * (max_size - min_size) + min_size;
			
			for (int i = 0; i < 4; ++i) {
				spreadBolts(true);
			}
			
			// enemies.add(new SmartPig(this, player.getBody().getPosition().x +
			// 5f, player.getBody().getPosition().y - 5f, 0.5f, 0.5f, 3.3f,
			// 0.3f, 0.3f, null, BodyType.DYNAMIC));
		}
		
		if (input.isKeyPressed(Input.KEY_O)) {
			actionDoomsday();
		}
		if (input.isKeyPressed(Input.KEY_ENTER)) {
			actionDebugView();
		}
		
		if (input.isKeyDown(Input.KEY_N)) {
			laserAngle = laserTargetAngle = 0d;
		}
		
		if (input.isKeyDown(Input.KEY_H)) {
			actionTailwhip();
		}
		
		boolean shootkeyDown = false;
		
		if (input.isKeyDown(Input.KEY_N)) {
			shootkeyDown = true;
		}
		
		if (input.isKeyPressed(Input.KEY_J)) {
			actionLaserStart();
		}
		
	}
	
	private void actionCancelJump() {
		if (player.getBody().getLinearVelocity().y < 0f && !player.isOnGround())
			player.cancelJump();
	}
	
	private void actionLaserEnd() {
		if (player.isBiting()) {
			player.stopBiting();
		}
		if (player.isLaserStarted()) {
			player.setWaitingForLaserToBeKilled(true);
		}
	}
	
	private void actionLaserStart() {
		player.setWaitingForLaserToBeKilled(false);
		player.bite();
		player.initializeLaser();
	}
	
	private void actionDebugView() {
		debugView = !debugView;
	}
	
	private void actionDoomsday() {
		if (!DOOMSDAY) {
			initDoomsday();
		} else {
			endDoomsday();
		}
	}
	
	private void initDoomsday() {
		DOOMSDAY = true;
		doomsdayCounter = 0;
		dubstep.loop();
		// TODO start dub step
		
	}
	
	private void endDoomsday() {
		printDoomsdayStatistics();
		DOOMSDAY = false;
		bgmusic.loop();
	}
	
	private void printDoomsdayStatistics() {
		
		Statistics.getInstance().printStats();
		
	}
	
	private void actionTailwhip() {
		player.tailwhipInit();
	}
	
	private void actionGroundpound() {
		if (!player.isOnGround()) {
			player.groundpoundInit();
		}
		// } else if (player.isOnWall()){
		// if(player.leftWallColliding()){
		// // XXX magic numbers
		// player.getBody().setLinearVelocity(new Vec2(3,0));
		// } else if(player.rightWallColliding()){
		// player.getBody().setLinearVelocity(new Vec2(-3,0));
		// }
		// }
	}
	
	private void actionRight() {
		player.setLeft(false);
		player.accelerate((float) Math.abs(polarToX(xboxLeftThumbDirection, xboxLeftThumbMagnitude)));
	}
	
	private void actionLeft() {
		player.setLeft(true);
		player.accelerate((float) Math.abs(polarToX(xboxLeftThumbDirection, xboxLeftThumbMagnitude)));
	}
	
	private void actionJump() {
		player.jump();
	}
	
	private String readFile(String file) throws IOException {
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		return stringBuilder.toString();
	}
	
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		
		g.fill(skyRect, skyGradient);
		
		drawBackgroundObjects(g);
		
		g.pushTransform();
		g.translate(-cam.getX() * zoom + screenWidth / 2f, -cam.getY() * zoom + screenHeight / 2f);
		g.scale(zoom, zoom);
		
		g.setColor(earthColor);
		g.fillRect(-21.5f, level.getHeight() - 1f, level.getWidth() + 21f, 10f);
		g.setColor(Color.white);
		
		for (Tire t : tires) {
			t.draw(g, debugView);
		}
		
		for (Generator ge : generators) {
			ge.draw(g, debugView);
		}
		
		house.draw(g, debugView);
		
		for (GameObject staticObj : staticObjects) {
			staticObj.draw(g, debugView);
		}
		
		for (DropItem d : dropItems) {
			d.draw(g, debugView);
		}
		
		for (Part part : parts) {
			part.draw(g, debugView);
		}
		
		for (Enemy enemy : enemies) {
			if (!enemy.getsGrilled) {
				enemy.draw(g, debugView);
			}
		}
		
		boaris.draw(debugView);
		
		player.draw(g, debugView);
		
		for (int x = getBlockX(-zoom); x <= getBlockX(screenWidth); ++x) {
			for (int y = getBlockY(-zoom * 2); y <= getBlockY(screenHeight); ++y) {
				if (tiles[x][y] != null) {
					tiles[x][y].draw(g, debugView);
				}
			}
		}
		
		for (Spike spike : spikes) {
			spike.draw(g, debugView);
		}
		
		for (Girder sb : girders) {
			sb.draw(g, debugView);
		}
		
		for (Spike s : spikes) {
			s.draw(g, debugView);
		}
		
		for (Conveyor c : conveyors) {
			c.draw(g, debugView);
		}
		
		house.drawFront(g, debugView);
		
		if (player.isLaserActive()) {
			laser.draw(g, debugView);
		}
		
		for (Enemy enemy : enemies) {
			if (enemy.getsGrilled || enemy.isDead()) {
				enemy.draw(g, debugView);
			}
		}
		
		g.popTransform();
		
		// death fade
		int numframes = 10;
		if (player.getDeathTimeCounter() > player.getDeathWaitTime() - numframes
				&& player.getDeathTimeCounter() <= player.getDeathWaitTime()) {
			if ((deathFade += 1f / numframes) > 1f)
				deathFade = 1f;
			if (player.getDeathTimeCounter() == player.getDeathWaitTime())
				zoom = DEFAULT_ZOOM;
		} else {
			if ((deathFade -= 1f / numframes) < 0f)
				deathFade = 0f;
		}
		g.setColor(new Color(0f, 0f, 0f, deathFade));
		g.fillRect(0, 0, screenWidth, screenHeight);
		// death fade end
		
		drawInterface();
		
		if (curMode == Mode.TEXTBOX) {
			if (textbox.getText() != null)
				drawAlignedString(fontSmall, 4, textbox.getText(), screenWidth / 2, screenHeight - 96, Color.black);
			drawAlignedString(fontSmall, 4, textbox.getText(), screenWidth / 2, screenHeight - 100, Color.white);
		}
		
		if (curMode == Mode.PAUSE) {
			g.setColor(new Color(0f, 0f, 0f, 0.3f));
			g.fillRect(0, 0, screenWidth, screenHeight);
			// pauseImage.drawCentered(screenWidth * 0.5f, screenHeight * 0.4f);
			drawAlignedString(fontBig, 4, "PAUSE", screenWidth / 2, (int) (screenHeight * 0.3), Color.white);
		}
		g.setColor(Color.white);
		
		// drawZoomAreas(g);
		// drawCheckpoints(g);
		
		// GUI
		
		// g.setColor(Color.white);
		// // scale pixel size : box2d:size
		// g.drawString("10px", 0, 50);
		// g.drawRect(50, 50, 10, 1);
		// g.drawRect(50, 55, 10 * zoom, 1);
		// g.setColor(Color.white);
		// g.drawString("Count: " + world.getBodyCount(), 0, 0);
		//
		// g.drawString("ShootingDirection: " + player.getShootingDirection(),
		// 200, 10);
		// g.drawString("ShootingPower: " + player.getShootingPower(), 200, 30);
		// g.drawString("pos: " + player.getBody().getPosition(), 200, 50);
		// g.drawString("pigs: " + player.getPigCounter(), 10, 30);
		// g.drawString("laserc: " + player.getLaserTime(), 10, 30);
		//
		// g.drawString("left thumbstick angle: " + xbox.getLeftThumbDirection()
		// + "\n" + "laser angle: " + laserAngle
		// + "\nlaser target angle: " + laserTargetAngle + "\ntiles drawn: " +
		// tilesDrawn, 10, 50);
		if (curMode == Mode.PAUSE) {
			Statistics.getInstance().drawStats(g);
		}
	}
	
	// public static ArrayList<Shred> getShreds() {
	// return shreds;
	// }
	//
	// public static ArrayList<Shred> getShredsToRemove() {
	// return shredsToRemove;
	// }
	
	private void restartAnimations() {
		generators.get(0).getAnimation().start();
		for (Enemy e : enemies) {
			e.getCurrentAnimation().start();
		}
		player.getCurrentAnimation().start();
		player.getLaser().getAnimation().start();
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		
		if (curMode != Mode.PAUSE) {
			Statistics.getInstance().update();
		}
		Input input = gc.getInput();
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			actionPause();
		}
		
		boaris.update();
		
		if (input.isKeyPressed(Input.KEY_COMMA)) {
			Checkpoint cp = player.getCheckpoint();
			cp = level.getCheckpoints().get((level.getCheckpoints().indexOf(cp) + 1) % level.getCheckpoints().size());
			player.setCheckpoint(cp);
			player.revive();
			curMode = Mode.PLAY;
		}
		if (input.isKeyPressed(Input.KEY_PERIOD)) {
			Checkpoint cp = player.getCheckpoint();
			cp = level.getCheckpoints().get(
					(level.getCheckpoints().indexOf(cp) + level.getCheckpoints().size() - 1) % level.getCheckpoints().size());
			player.setCheckpoint(cp);
			player.revive();
			curMode = Mode.PLAY;
		}
		
		// TODO Kamera Smoothness muss auch angepasst werden, je nach Zoom
		if (useZoomAreas) {
			float biggestZoom = 0f;
			if (player.isBiting() || player.isRepairing() || player.isDead()) {
				targetZoom = 320f;
			} else {
				if (level.getZoomAreas() != null) {
					for (ZoomArea za : level.getZoomAreas()) {
						Vec2 pos = player.getBody().getPosition();
						if (za.isInArea(pos.x, pos.y)) {
							if (za.getZoom() > biggestZoom) {
								biggestZoom = za.getZoom();
							}
						}
					}
				}
				if (biggestZoom > 0.001f) {
					targetZoom = biggestZoom;
				} else {
					targetZoom = DEFAULT_ZOOM;
				}
			}
			zoom = Functions.curveValue(targetZoom, zoom, 30);
		} else {
			if (input.isKeyDown(Input.KEY_E)) {
				if (zoom < 200) {
					zoom += MANUAL_ZOOM_STEP;
				}
			}
			if (input.isKeyDown(Input.KEY_R)) {
				if (zoom - MANUAL_ZOOM_STEP > MANUAL_ZOOM_STEP) {
					zoom -= MANUAL_ZOOM_STEP;
				}
			}
		}
		
		if (curMode == Mode.PLAY || curMode == Mode.TEXTBOX) {
			if (curMode == Mode.PLAY)
				processInput(input);
			
			house.updateAnimations();
			
			laserTargetAngle = Math.toRadians(xboxLeftThumbDirection - 90d);
			laserAngle = Functions.curveAngle(laserAngle, laserTargetAngle, 0.1f);
			laser.position(player.getBody().getPosition().x, player.getBody().getPosition().y - 0.4f, (float) laserAngle);
			
			player.update();
			
			for (Enemy enemy : enemies) {
				enemy.update();
			}
			
			world.setGravity(new Vec2(0f, (float) ((1d - xboxLeftTrigger) * 20f + ((float) xboxRightTrigger * -20f))));
			
			float targetCamX, targetCamY;
			if (player.isLaserActive()) {
				targetCamX = (float) (player.getBody().getPosition().x + polarToX(xboxLeftThumbDirection, xboxLeftThumbMagnitude) * 4f);
				targetCamY = (float) (player.getBody().getPosition().y + polarToY(xboxLeftThumbDirection, xboxLeftThumbMagnitude) * 4f);
			} else {
				targetCamX = (float) (player.getBody().getPosition().x + polarToX(xboxRightThumbDirection, xboxRightThumbMagnitude) * 3f);
				targetCamY = (float) (player.getBody().getPosition().y + polarToY(xboxRightThumbDirection, xboxRightThumbMagnitude) * 3f);
			}
			
			if (player.isDead()) {
				targetCamX = (float) (player.getBody().getPosition().x);
				targetCamY = (float) (player.getBody().getPosition().y);
			}
			
			if (curMode == Mode.TEXTBOX) {
				if (textbox.getText() == "Farewell! Hahaha...") {
					house.startBloodBath();
					boaris.startMoving();
				}
				switch (textbox.getCamFocus()) {
					case 0:
						targetCamX = (float) (player.getBody().getPosition().x);
						targetCamY = (float) (player.getBody().getPosition().y);
						break;
					case 1:
						targetCamX = (float) (-6f);
						targetCamY = (float) (28.6f);
						break;
					case 2:
						targetCamX = (float) (-6.268761f);
						targetCamY = (float) (27.005844f);
						break;
					case 3:
						targetCamX = (float) (-7f);
						targetCamY = (float) (22f);
						break;
				}
			}
			
			if (targetCamY > level.getHeight() - screenHeight / 2 / zoom)
				targetCamY = level.getHeight() - screenHeight / 2 / zoom;
			
			if (targetCamX > level.getWidth() - screenWidth / 2 / zoom)
				targetCamX = level.getWidth() - screenWidth / 2 / zoom;
			
			if (targetCamX < -13 - screenWidth / 2 / zoom)
				targetCamX = -13 - screenWidth / 2 / zoom;
			
			cam.follow(targetCamX, targetCamY, 10);
			
			if (DOOMSDAY) {
				cam.wiggle((player.isLaserActive()) ? 1f : 0.5f);
				
				if (doomsdayCounter % 450 == 0 && enemies.size() < MAX_SPAWN_ENEMIES) {
					enemies.add(new SmartPig(this, player.getBody().getPosition().x - 5f, player.getBody().getPosition().y - 5f, 0.5f,
							0.5f, 3.3f, 0.3f, 0.3f, null, BodyType.DYNAMIC));
					enemies.add(new SmartPig(this, player.getBody().getPosition().x + 5f, player.getBody().getPosition().y - 5f, 0.5f,
							0.5f, 3.3f, 0.3f, 0.3f, null, BodyType.DYNAMIC));
					enemies.add(new DumbPig(this, player.getBody().getPosition().x - 5f, player.getBody().getPosition().y - 5f, 0.5f, 0.5f,
							3.3f, 0.3f, 0.3f, null, BodyType.DYNAMIC));
					enemies.add(new DumbPig(this, player.getBody().getPosition().x + 5f, player.getBody().getPosition().y - 5f, 0.5f, 0.5f,
							3.3f, 0.3f, 0.3f, null, BodyType.DYNAMIC));
				}
				
				++doomsdayCounter;
				
				if (player.isDead()) {
					endDoomsday();
				}
			} else {
				cam.wiggle((player.isLaserActive()) ? 1f : 0f);
			}
			
			for (GameObject o : objectsToRemove) {
				world.destroyBody(o.getBody());
				
				if (o instanceof Enemy) {
					for (int i = 0; i < 7; ++i) {
						
						float power = 10f;
						Vec2 direction = new Vec2((float) (Math.random() * power * 2 - power), -((float) (Math.random() * power)));
						
						Nut nut = new Nut(this, getWorld(), o.getBody().getPosition().add(new Vec2(0, 0)), "images/nut"
								+ ((int) (Math.random() * 3) + 1) + ".png");
						nut.getBody().setLinearVelocity(direction);
						// getNuts().add(nut);
						getDropItems().add(nut);
						
						Bolt bolt = new Bolt(this, getWorld(), o.getBody().getPosition().add(new Vec2(0, 0)), "images/bolt"
								+ ((int) (Math.random() * 3) + 1) + ".png");
						bolt.getBody().setLinearVelocity(direction);
						// getBolts().add(bolt);
						getDropItems().add(bolt);
						
						Shred shred = new Shred(this, getWorld(), o.getBody().getPosition().add(new Vec2(0, 0)), "images/shred"
								+ ((int) (Math.random() * 3) + 1) + ".png", ((Enemy) o).getPigSize());
						shred.getBody().setLinearVelocity(direction);
						// getShreds().add(shred);
						getDropItems().add(shred);
						
					}
				}
			}
			objectsToRemove.clear();
			
			for (Enemy e : enemiesToRemove) {
				
				enemies.remove(e);
			}
			enemiesToRemove.clear();
			
			for (DropItem d : dropItemsToRemove) {
				dropItems.remove(d);
			}
			dropItemsToRemove.clear();
			
			for (Part p : parts) {
				p.update();
			}
			
			for (DropItem d : dropItems) {
				d.update();
			}
			
			Iterator iterator = player.getDropItemsToCollect().iterator();
			while (iterator.hasNext()) {
				DropItem dropItem = (DropItem) iterator.next();
				if (dropItem.isCollectable()) {
					dropItem.collect();
					iterator.remove();
					getObjectsToRemove().add(dropItem);
				}
			}
			
			// for (GameObject o : objectsToAdd){
			// world.destroyBody(o.getBody());
			// }
			// objectsToAdd.clear();
			
			if (player.isWaitingForLaserToBeKilled()) {
				killLaser();
			}
			
			if (spreadBolts != 0 && player.isRepairing()) {
				
				for (int i = 0; i < 1; ++i) {
					if (spreadBolts > 0 && spreadBolts % 10 == 0) {
						spreadBolts(false);
					} else {
						break;
					}
				}
				--spreadBolts;
			} else {
				spreadBolts = 0;
			}
			
			for (Generator g : generators) {
				g.update();
			}
			
			world.step(delta / 1000f, 18, 6);
			
		}
		if (DOOMSDAY) {
			doomsdaySky();
		} else {
			initNormalSky();
		}
		
	}
	
	private double polarToY(double direction, double magnitude) {
		return Math.sin(Math.toRadians(direction - 90d)) * magnitude;
	}
	
	private double polarToX(double direction, double magnitude) {
		return Math.cos(Math.toRadians(direction - 90d)) * magnitude;
	}
	
	private void actionPause() {
		if (curMode == Mode.PLAY) {
			curMode = Mode.PAUSE;
			pauseAnimations();
		} else {
			curMode = Mode.PLAY;
			restartAnimations();
		}
	}
	
	private float getWorldX(float screenX) {
		return screenX / zoom + cam.getX() - screenWidth / 2 / zoom;
	}
	
	private float getWorldY(float screenY) {
		return screenY / zoom + cam.getY() - screenHeight / 2 / zoom;
	}
	
	private int getBlockX(float screenX) {
		float blockX = (screenX - screenWidth / 2) / zoom + 0.5f + cam.getX();
		return (int) Math.max(0, Math.min(blockX, level.getWidth() - 1));
	}
	
	private int getBlockY(float screenY) {
		float blockY = (screenY - screenHeight / 2) / zoom + 0.5f + cam.getY();
		return (int) Math.max(0, Math.min(blockY, level.getHeight() - 1));
	}
	
	public boolean isTiltedDown(double direction, double magnitude) {
		return Math.abs(magnitude) > THUMBSTICK_DEADZONE && direction >= 135d && direction <= 225d;
	}
	
	public boolean isTiltedLeft(double direction, double xboxLeftThumbMagnitude2) {
		return Math.abs(xboxLeftThumbMagnitude2) > THUMBSTICK_DEADZONE && direction >= 225d && direction <= 315d;
	}
	
	public boolean isTiltedRight(double direction, double magnitude) {
		return Math.abs(magnitude) > THUMBSTICK_DEADZONE && direction >= 45d && direction <= 135d;
	}
	
	public boolean isTiltedUp(double direction, double magnitude) {
		return Math.abs(magnitude) > THUMBSTICK_DEADZONE && direction >= 315d && direction <= 45d;
	}
	
	public void spreadBolts(boolean collectable) throws SlickException {
		if (collectable) {
			dropItems.add(new Bolt(this, world, player.getBody().getPosition().add(new Vec2(0, -1)), "images/bolt"
					+ ((int) (Math.random() * 3) + 1) + ".png"));
			dropItems.add(new Nut(this, world, player.getBody().getPosition().add(new Vec2(0, -1)), "images/nut"
					+ ((int) (Math.random() * 3) + 1) + ".png"));
		} else {
			
			DropItem newDropItem;
			
			if (Math.random() < 0.5d) {
				newDropItem = new GeneratorBolt(this, world, player.getBody().getPosition().add(new Vec2(0, -0f)), "images/bolt"
						+ ((int) (Math.random() * 3) + 1) + ".png");
			} else {
				newDropItem = new GeneratorNut(this, world, player.getBody().getPosition().add(new Vec2(0, -0f)), "images/nut"
						+ ((int) (Math.random() * 3) + 1) + ".png");
			}
			
			float factor = 7f;
			newDropItem.getBody().setLinearVelocity(new Vec2((float) (Math.random() - 0.5f) * factor, -factor));
			dropItems.add(newDropItem);
		}
	}
	
	public void addSpreadBolts(int spreadBolts) {
		this.spreadBolts += spreadBolts;
	}
	
	static boolean is64bit() {
		return System.getProperty("sun.arch.data.model").equals("64");
	}
	
	public ArrayList<Tire> getTires() {
		return tires;
	}
	
	public Camera getCamera() {
		return cam;
	}
	
	// align defines the anchor point
	// 0 1 2
	// 3 4 5
	// 6 7 8
	private void drawAlignedString(TrueTypeFont font, int align, String text, int x, int y, Color color) {
		font.drawString(x - (align % 3) * font.getWidth(text) / 2, y - (align / 3) * font.getHeight(text) / 2, text, color);
	}
	
	private void loadFonts() {
		try {
			InputStream inputStream = ResourceLoader.getResourceAsStream("etc/Second-Chances-Solid.ttf");
			Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			fontSmall = new TrueTypeFont(awtFont.deriveFont(40f), true);
			fontBig = new TrueTypeFont(awtFont.deriveFont(60f), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
