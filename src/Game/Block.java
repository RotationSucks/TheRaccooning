package Game;

public class Block {
	
	private int		type	= 0;
	private int		angle	= 0;
	private boolean	flipped	= false;
	
	public Block() {
		this(0, 0, false);
	}
	
	public Block(int type, int angle, boolean flipped) {
		this.type = type;
		this.angle = angle;
		this.flipped = flipped;
	}
	
	public void flip() {
		this.flipped = !this.flipped;
	}
	
	public int getAngle() {
		return this.angle;
	}
	
	public int getType() {
		return this.type;
	}
	
	public boolean isFlipped() {
		return this.flipped;
	}
	
	public void set(int type, int angle, boolean flipped) {
		this.type = type;
		this.angle = angle;
		this.flipped = flipped;
	}
	
	public void setAngle(int angle) {
		this.angle = angle;
	}
	
	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
}
