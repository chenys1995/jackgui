package jackgui;

import javax.swing.ImageIcon;
import javax.swing.JButton;

class myButton extends JButton {

	boolean IsPeople = false, IsDead = false,isUsed=false;
	int gridx, gridy, character, angle = 0;
	private static final long serialVersionUID = 1838929864725400980L;
	public myButton(ImageIcon imageIcon) {
		super(imageIcon);
	}
	public void setxy(int x, int y) {
		gridx = x;
		gridy = y;
	}

	public void setChar(int c) {
			character = c;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}
	public void setUsed(Boolean used) {
		this.isUsed = used;
	}
	public void setDead() {
		IsDead = true;
		if (this.character == 6) {
			this.setIcon(new ImageIcon("res/crossroad_360.png"));
			this.setDisabledIcon(new ImageIcon("res/crossroad_360.png"));
			this.angle = 360;
		} else {
			this.setIcon(new ImageIcon("res/triroad_" + this.angle + ".png"));
			this.setDisabledIcon(new ImageIcon("res/triroad_" + this.angle + ".png"));
		}
		//System.out.printf("%d is dead.\n", character);
	}
}
