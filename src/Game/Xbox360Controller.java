package Game;

import org.jbox2d.common.Vec2;

import ch.aplu.xboxcontroller.XboxController;
import ch.aplu.xboxcontroller.XboxControllerAdapter;

public class Xbox360Controller extends XboxController {

	private static final double THUMBSTICK_DEADZONE = 0.2d;
	private static final double TRIGGER_DEADZONE = 0.2d;
	private static final double TRIGGER_PRESSED_VALUE = 0.7d; 
	
	public enum DPad {
		
		UP(0),
		UP_RIGHT(1),
		RIGHT(2),
		DOWN_RIGHT(3),
		DOWN(4),
		DOWN_LEFT(5),
		LEFT(6),
		UP_LEFT(7);
		
		private final int direction;
		
		private DPad(int direction) {
			this.direction = direction;
		}
		
		public int getVal() {
			return this.direction;
		}
		
	}
	
	private class Button {
		
		private boolean down = false;
		private boolean pressed = false;
		private boolean up = false;
		
		public void listen(boolean pressed) {
			this.pressed = pressed;
			if (this.pressed) {
				this.down = true;
			} else {
				this.up = true;
			}
		}
		
		public boolean isDown() {
			return this.down;
		}
		
		public boolean isPressed() {
			return this.pressed || this.down;
		}
		
		public boolean isUp() {
			return this.up;
		}
		
		public void resetStates() {
			this.down = false;
			this.up = false;
		}
		
	}
	
	private class Thumbstick {
		
		private double direction;
		private double magnitude;
		
		public double getDirection() {
			return direction;
		}
		
		public void setDirection(double direction) {
			this.direction = direction;
		}
		
		public double getMagnitude() {
			return magnitude;
		}
		
		public void setMagnitude(double magnitude) {
			this.magnitude = magnitude;
		}
		
		public boolean isTiltedLeft() {
			return Math.abs(magnitude) > THUMBSTICK_DEADZONE &&
					direction >= 225d &&
					direction <= 315d;
		}
		
		public boolean isTiltedRight() {
			return Math.abs(magnitude) > THUMBSTICK_DEADZONE &&
					direction >= 45d &&
					direction <= 135d;
		}
		
		public boolean isTiltedUp() {
			return Math.abs(magnitude) > THUMBSTICK_DEADZONE &&
					direction >= 315d &&
					direction <= 45d;
		}
		
		public boolean isTiltedDown() {
			return Math.abs(magnitude) > THUMBSTICK_DEADZONE &&
					direction >= 135d &&
					direction <= 225d;
		}
		
	}
	
	private class Trigger {
		
		private double value;
		
		private boolean down = false;
		private boolean pressed = false;
		private boolean up = false;

		public double getValue() {
			return value;
		}
		
		public void listen(double value) {
			this.value = value;
			if (this.pressed) {
				if (this.value <= TRIGGER_PRESSED_VALUE) {
					this.pressed = false;
					this.up = true;
				}
			} else {
				if (this.value > TRIGGER_PRESSED_VALUE) {
					this.pressed = true;
					this.down = true;
				}
			}
		}
		
		public boolean isDown() {
			return this.down;
		}
		
		public boolean isPressed() {
			return this.pressed || this.down;
		}
		
		public boolean isUp() {
			return this.up;
		}
		
		public void resetStates() {
			this.down = false;
			this.up = false;
		}
		
	}
	
	private Button buttonA = new Button();
	private Button buttonB = new Button();
	private Button buttonX = new Button();
	private Button buttonY = new Button();
	private Button buttonStart = new Button();
	private Button buttonBack = new Button();
	private Button buttonLeftThumbstick = new Button();
	private Button buttonRightThumbstick = new Button();
	private Button buttonLeftShoulder = new Button();
	private Button buttonRightShoulder = new Button();
	
	private Thumbstick thumbstickLeft = new Thumbstick();
	private Thumbstick thumbstickRight = new Thumbstick();
	private Trigger triggerLeft = new Trigger();
	private Trigger triggerRight = new Trigger();

	public Xbox360Controller() {
		setLeftThumbDeadZone(THUMBSTICK_DEADZONE);
		setRightThumbDeadZone(THUMBSTICK_DEADZONE);
		setLeftTriggerDeadZone(TRIGGER_DEADZONE);
		setRightTriggerDeadZone(TRIGGER_DEADZONE);
		this.addXboxControllerListener(new XboxControllerAdapter() {
			public void buttonA(boolean pressed) { buttonA.listen(pressed); }
			public void buttonB(boolean pressed) { buttonB.listen(pressed); }
			public void buttonX(boolean pressed) { buttonX.listen(pressed); }
			public void buttonY(boolean pressed) { buttonY.listen(pressed); }
			public void start(boolean pressed) { buttonStart.listen(pressed); }
			public void back(boolean pressed) { buttonBack.listen(pressed); }
			public void leftThumb(boolean pressed) { buttonLeftThumbstick.listen(pressed);	}
			public void rightThumb(boolean pressed) { buttonRightThumbstick.listen(pressed); }
			public void leftShoulder(boolean pressed) { buttonLeftShoulder.listen(pressed); }
			public void rightShoulder(boolean pressed) { buttonRightShoulder.listen(pressed); }
			public void dpad(int direction, boolean pressed) {
				
			}
			public void leftThumbDirection(double direction) { thumbstickLeft.setDirection(direction); }
			public void leftThumbMagnitude(double magnitude) { thumbstickLeft.setMagnitude(magnitude); }
			public void rightThumbDirection(double direction) { thumbstickRight.setDirection(direction); }
			public void rightThumbMagnitude(double magnitude) { thumbstickRight.setMagnitude(magnitude); }
			public void leftTrigger(double value) { triggerLeft.listen(value); }
			public void rightTrigger(double value) { triggerRight.listen(value); }
		});
	}
	
	public void resetStates() {
		buttonA.resetStates();
		buttonB.resetStates();
		buttonX.resetStates();
		buttonY.resetStates();
		buttonStart.resetStates();
		buttonBack.resetStates();
		buttonLeftThumbstick.resetStates();
		buttonRightThumbstick.resetStates();
		buttonLeftShoulder.resetStates();
		buttonRightShoulder.resetStates();
		triggerLeft.resetStates();
		triggerRight.resetStates();
	}
	
	public boolean isButtonADown() { return buttonA.isDown(); }
	public boolean isButtonBDown() { return buttonB.isDown(); }
	public boolean isButtonXDown() { return buttonX.isDown(); }
	public boolean isButtonYDown() { return buttonY.isDown(); }
	public boolean isButtonStartDown() { return buttonStart.isDown(); }
	public boolean isButtonBackDown() { return buttonBack.isDown(); }
	public boolean isButtonLeftThumbDown() { return buttonLeftThumbstick.isDown(); }
	public boolean isButtonRightThumbDown() { return buttonRightThumbstick.isDown(); }
	public boolean isButtonLeftShoulderDown() { return buttonLeftShoulder.isDown(); }
	public boolean isButtonRightShoulderDown() { return buttonRightShoulder.isDown(); }
	
	public boolean isButtonAPressed() { return buttonA.isPressed(); }
	public boolean isButtonBPressed() { return buttonB.isPressed(); }
	public boolean isButtonXPressed() { return buttonX.isPressed(); }
	public boolean isButtonYPressed() { return buttonY.isPressed(); }
	public boolean isButtonStartPressed() { return buttonStart.isPressed(); }
	public boolean isButtonBackPressed() { return buttonBack.isPressed(); }
	public boolean isButtonLeftThumbPressed() { return buttonLeftThumbstick.isPressed(); }
	public boolean isButtonRightThumbPressed() { return buttonRightThumbstick.isPressed(); }
	public boolean isButtonLeftShoulderPressed() { return buttonLeftShoulder.isPressed(); }
	public boolean isButtonRightShoulderPressed() { return buttonRightShoulder.isPressed(); }
	
	public boolean isButtonAUp() { return buttonA.isUp(); }
	public boolean isButtonBUp() { return buttonB.isUp(); }
	public boolean isButtonXUp() { return buttonX.isUp(); }
	public boolean isButtonYUp() { return buttonY.isUp(); }
	public boolean isButtonStartUp() { return buttonStart.isUp(); }
	public boolean isButtonBackUp() { return buttonBack.isUp(); }
	public boolean isButtonLeftThumbUp() { return buttonLeftThumbstick.isUp(); }
	public boolean isButtonRightThumbUp() { return buttonRightThumbstick.isUp(); }
	public boolean isButtonLeftShoulderUp() { return buttonLeftShoulder.isUp(); }
	public boolean isButtonRightShoulderUp() { return buttonRightShoulder.isUp(); }

	public double getLeftThumbMagnitude() { return thumbstickLeft.getMagnitude(); }
	public double getLeftThumbDirection() { return thumbstickLeft.getDirection(); }
	public boolean isLeftThumbTiltedLeft() { return thumbstickLeft.isTiltedLeft(); }
	public boolean isLeftThumbTiltedRight() { return thumbstickLeft.isTiltedRight(); }
	public boolean isLeftThumbTiltedUp() { return thumbstickLeft.isTiltedUp(); }
	public boolean isLeftThumbTiltedDown() { return thumbstickLeft.isTiltedDown(); }
	
	public double getRightThumbMagnitude() { return thumbstickRight.getMagnitude(); }
	public double getRightThumbDirection() { return thumbstickRight.getDirection(); }
	public boolean isRightThumbTiltedLeft() { return thumbstickRight.isTiltedLeft(); }
	public boolean isRightThumbTiltedRight() { return thumbstickRight.isTiltedRight(); }
	public boolean isRightThumbTiltedUp() { return thumbstickRight.isTiltedUp(); }
	public boolean isRightThumbTiltedDown() { return thumbstickRight.isTiltedDown(); }
	
	public boolean isRightTriggerDown() { return triggerRight.isDown(); }
	public boolean isLeftTriggerDown() { return triggerLeft.isDown(); }
	
	public boolean isRightTriggerPressed() { return triggerRight.isPressed(); }
	public boolean isLeftTriggerPressed() { return triggerLeft.isPressed(); }
	
	public boolean isRightTriggerUp() { return triggerRight.isUp(); }
	public boolean isLeftTriggerUp() { return triggerLeft.isUp(); }
	
	public double getLeftTriggerValue() { return triggerLeft.getValue(); }
	public double getRightTriggerValue() { return triggerRight.getValue(); }

	public double getLeftThumbX() {
		return Math.cos(Math.toRadians(getLeftThumbDirection() - 90d)) * getLeftThumbMagnitude();
	}
	public double getLeftThumbY() {
		return Math.sin(Math.toRadians(getLeftThumbDirection() - 90d)) * getLeftThumbMagnitude();
	}
	public double getRightThumbX() {
		return Math.cos(Math.toRadians(getRightThumbDirection() - 90d)) * getRightThumbMagnitude();
	}
	public double getRightThumbY() {
		return Math.sin(Math.toRadians(getRightThumbDirection() - 90d)) * getRightThumbMagnitude();
	}
	
}
