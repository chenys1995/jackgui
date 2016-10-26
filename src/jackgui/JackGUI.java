package jackgui;

import java.util.Random;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;;

public class JackGUI implements ActionListener {
	class myButton extends JButton {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1838929864725400980L;

		public myButton(ImageIcon imageIcon) {
			// TODO Auto-generated constructor stub
			super(imageIcon);
		}

		public void setxy(int x, int y) {
			gridx = x;
			gridy = y;
		}

		public void setChar(int c) {
			if (c != 0) {
				character = c;
			}
		}

		public void setAngle(int angle) {
			this.angle = angle;
		}
		public void setDead() {
			IsDead = true;
			if(this.character == 6){
				this.setIcon(new ImageIcon("res/crossroad_0.png"));
				this.setDisabledIcon(new ImageIcon("res/crossroad_0.png"));
			}
			else {
				this.setIcon(new ImageIcon("res/triroad_"+this.angle+".png"));
				this.setDisabledIcon(new ImageIcon("res/triroad_"+this.angle+".png"));
			}
			mainwindow.revalidate();
			mainwindow.repaint();
		}
		boolean IsPeople = false, IsDead = false;
		int gridx, gridy, character, angle = 0;
	}

	private static JFrame mainwindow = new JFrame();
	myButton[] people = new myButton[9];
	myButton[] actions = new myButton[8];
	myButton Holmes = new myButton(new ImageIcon("res/Holmes.png"));
	myButton Watson = new myButton(new ImageIcon("res/Watson.png"));
	myButton dog = new myButton(new ImageIcon("res/dog.png"));
	myButton ok = new myButton(new ImageIcon("res/ok.png"));
	myButton displayJack = new myButton(new ImageIcon("res/ok.png"));
	JTextField round = new JTextField(10);
	JTextField score = new JTextField(10);  
	JTextField jackid = new JTextField(10);
	GridBagConstraints cons = new GridBagConstraints();
	int preClick;
	boolean[] Duplicate = new boolean[3];// [Holmes, Watson, Dog]
	int rotate, exchange, steps;
	
	public JackGUI() {

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Holmes || e.getSource() == Watson || e.getSource() == dog) {
			if (steps > 0) {
				ok.setEnabled(true);
				this.movepos((myButton) e.getSource());
				cons.gridx = ((myButton) e.getSource()).gridx;
				cons.gridy = ((myButton) e.getSource()).gridy;
				mainwindow.remove((myButton) e.getSource());
				mainwindow.add((myButton) e.getSource(), cons);
				mainwindow.revalidate();
				mainwindow.repaint();
				steps--;
			} 
		}
		if (e.getSource() == actions[0]) {
			preClick = 0;
			steps = 2;
			Watson.setEnabled(true);
		} else if (e.getSource() == actions[1] || e.getSource() == actions[2]) {// 2 rotation
			preClick = e.getSource() == actions[1]? 1 : 2;
			for (int i = 0; i < 9; i++)
				people[i].setEnabled(true);
			ok.setEnabled(true);
			rotate = 3;
			exchange = -1;
		} else if (e.getSource() == actions[3]) {
			preClick = 3;
			steps = 2;
			Holmes.setEnabled(true);
		} else if (e.getSource() == actions[4]) {
			preClick = 4;
			steps = 2;
			dog.setEnabled(true);
		} else if (e.getSource() == actions[5]) {
			preClick = 5;
			steps = 1;
			dog.setEnabled(true);
			Watson.setEnabled(true);
			Holmes.setEnabled(true);
		} else if (e.getSource() == actions[6]) {// exchange 2 people;
			preClick = 6;
			for (int i = 0; i < 9; i++)
				people[i].setEnabled(true);
			ok.setEnabled(true);
			exchange = 2;
			rotate = -1;
		} else if(e.getSource() == actions[7]){
			preClick = 7;
		}
		else if (e.getSource() == ok) {
			actions[preClick].setEnabled(false);
			if(steps!=-1){
				((myButton) e.getSource()).setEnabled(false);
				if(Holmes.isEnabled()){
					Holmes.setEnabled(false);
				}
				else if(Watson.isEnabled()){
					Watson.setEnabled(false);
				}
				else {
					dog.setEnabled(false);
				}
				steps = -1;
				return;
			}
			else if (exchange == -1) {
				for (int i = 0; i < 9; i++) {
					if (people[i].isEnabled())
						people[i].setEnabled(false);
					rotate = 0;
				}
			} else if (rotate == -1) {
				int x = -1, y = -1;
				for (int i = 0; i < 9; i++) {
					if (x == -1 && !people[i].isEnabled()) {
						x = i;
						// System.out.printf("x: %d\n",x);
					} else if (x != -1 && !people[i].isEnabled()) {
						y = i;
						// System.out.printf("y: %d\n",y);
					}
				}

				cons.gridx = people[x].gridx;
				cons.gridy = people[x].gridy;
				mainwindow.add(people[y], cons);
				cons.gridx = people[y].gridx;
				cons.gridy = people[y].gridy;
				mainwindow.add(people[x], cons);
				mainwindow.revalidate();
				mainwindow.repaint();
				// System.out.println("Swap success\n");
				rotate = 0;
			}
			for (int i = 0; i < 9; i++) {
				people[i].setEnabled(false);
			}
		}
		for (int i = 0; i < 9; i++) {
			if (e.getSource() == people[i] && people[i].isEnabled()) {
				if (rotate > 0) {
					for (int j = 0; j < 9; j++)
						if (j != i)
							people[j].setEnabled(false);
					people[i].setAngle((people[i].angle + 90) % 360);
					String Path ,Selector = "_" + people[i].angle + ".png";
					if(!people[i].IsDead)
						Path = "res/" + people[i].character;
					else{
						if(people[i].character != 6)
							Path = "res/triroad";
						else 
							Path = "res/crossroad";
					}
					people[i].setIcon(new ImageIcon(Path + Selector));
					people[i].setDisabledIcon(new ImageIcon(Path + Selector));
					mainwindow.revalidate();
					mainwindow.repaint();
					rotate--;
				} else if (exchange > 0) {
					people[i].setEnabled(false);
					mainwindow.remove(people[i]);
					exchange--;
				}
			}
		}
	}

	public void AI_select(myButton e) {

		if (e == Holmes || e == Watson || e == dog) {
			if (steps > 0) {
				this.movepos((myButton) e);
				cons.gridx = ((myButton) e).gridx;
				cons.gridy = ((myButton) e).gridy;
				mainwindow.remove((myButton) e);
				mainwindow.add((myButton) e, cons);
				mainwindow.revalidate();
				mainwindow.repaint();
			} else {
				steps = -1;
				return;
			}
			steps--;
		}
		if (e == actions[0]) {
			steps = 2;
			Watson.setEnabled(true);
		} else if (e == actions[1] || e == actions[2]) {// 2 rotation
			for (int i = 0; i < 9; i++)
				people[i].setEnabled(true);
			ok.setEnabled(true);
			rotate = 3;
			exchange = -1;
		} else if (e == actions[3]) {
			steps = 2;
			Holmes.setEnabled(true);
		} else if (e == actions[4]) {
			steps = 2;
			dog.setEnabled(true);
		} else if (e == actions[5]) {
			steps = 1;
			dog.setEnabled(true);
			Watson.setEnabled(true);
			Holmes.setEnabled(true);
		} else if (e == actions[6]) {// exchange 2 people;
			for (int i = 0; i < 9; i++)
				people[i].setEnabled(true);
			ok.setEnabled(true);
			exchange = 2;
			rotate = -1;
		} else if (e == ok) {
			if (exchange == -1) {
				for (int i = 0; i < 9; i++) {
					if (people[i].isEnabled())
						people[i].setEnabled(false);
					rotate = 0;
				}
			} else if (rotate == -1) {
				int x = -1, y = -1;
				for (int i = 0; i < 9; i++) {
					if (x == -1 && !people[i].isEnabled()) {
						x = i;
						// System.out.printf("x: %d\n",x);
					} else if (x != -1 && !people[i].isEnabled()) {
						y = i;
						// System.out.printf("y: %d\n",y);
					}
				}

				cons.gridx = people[x].gridx;
				cons.gridy = people[x].gridy;
				mainwindow.add(people[y], cons);
				cons.gridx = people[y].gridx;
				cons.gridy = people[y].gridy;
				mainwindow.add(people[x], cons);
				mainwindow.revalidate();
				mainwindow.repaint();
				// System.out.println("Swap success\n");
				rotate = 0;
			}
			for (int i = 0; i < 9; i++) {
				people[i].setEnabled(false);
			}
		}
		for (int i = 0; i < 9; i++) {
			if (e == people[i] && people[i].isEnabled()) {
				if (rotate > 0) {
					for (int j = 0; j < 9; j++)
						if (j != i)
							people[j].setEnabled(false);
					people[i].setAngle((people[i].angle + 90) % 360);
					String Path = "res/" + people[i].character, Selector = "_" + people[i].angle + ".png";
					people[i].setIcon(new ImageIcon(Path + Selector));
					people[i].setDisabledIcon(new ImageIcon(Path + Selector));
					mainwindow.revalidate();
					mainwindow.repaint();
					rotate--;
				} else if (exchange > 0) {
					people[i].setEnabled(false);
					mainwindow.remove(people[i]);
					exchange--;
				}
			}
		}
	}
	public void AI_exchange(myButton action,myButton people1,myButton people2){
		AI_select(action);
		AI_select(people1);
		AI_select(people2);
	}
	public void movepos(myButton b) {
		// 1st row :right
		if (b.gridy == 0 && b.gridx > 0 && b.gridx < 4) {
			if (b.gridx != 3)
				b.gridx = b.gridx + 1;
			else {
				b.gridx = b.gridx + 1;
				b.gridy = b.gridy + 1;
			}
		}
		// 4th row :left
		else if (b.gridy == 4 && b.gridx > 0 && b.gridx <= 4) {
			if (b.gridx != 1)
				b.gridx = b.gridx - 1;
			else {
				b.gridx = b.gridx - 1;
				b.gridy = b.gridy - 1;
			}
		}
		// 1st column:up
		else if (b.gridx == 0 && b.gridy <= 4 && b.gridy > 0) {
			if (b.gridy != 1)
				b.gridy = b.gridy - 1;
			else {
				b.gridx = b.gridx + 1;
				b.gridy = b.gridy - 1;
			}
		}
		// 4th column:down
		else if (b.gridx == 4 && b.gridy < 4 && b.gridy >= 0) {
			if (b.gridy != 3)
				b.gridy = b.gridy + 1;
			else {
				b.gridx = b.gridx - 1;
				b.gridy = b.gridy + 1;
			}
		}
	}

	public void onCreate() {
		mainwindow.setSize(800, 800);
		mainwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainwindow.getContentPane().setLayout(new GridBagLayout());
		Random rand = new Random();
		List<Integer> order = new ArrayList<Integer>(), angle = new ArrayList<Integer>();
		for (int i = 0; i < 9; i++) {
			order.add(i);
			angle.add(rand.nextInt(4) * 90);
		}
		java.util.Collections.shuffle(order);
		int c = 0, act = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				cons.gridx = j;
				cons.gridy = i;
				cons.gridwidth = 1;
				cons.gridheight = 1;
				cons.weightx = 0;
				cons.weighty = 0;
				cons.fill = GridBagConstraints.BOTH;
				cons.anchor = GridBagConstraints.CENTER;
				if (i > 0 && i < 4 && j > 0 && j < 4) {
					String path = "res/" + (((Integer) order.toArray()[c]) + 1),
							Selector = "_" + ((Integer) angle.toArray()[c]) + ".png";
					// System.out.printf("selector: %s\n", path+Selector);
					people[c] = new myButton(new ImageIcon(path + Selector));
					people[c].setChar((((Integer) order.toArray()[c]) + 1));
					people[c].setAngle(((Integer) angle.toArray()[c]));
					people[c].setDisabledIcon(new ImageIcon(path + Selector));
					people[c].addActionListener(this);
					people[c].IsPeople = true;
					people[c].setxy(j, i);
					mainwindow.add(people[c], cons);
					c++;
				} else if (i == 1 && j == 0) {
					Holmes.setxy(j, i);
					Holmes.addActionListener(this);
					mainwindow.add(Holmes, cons);
				} else if (i == 2 && j == 4) {
					Watson.setxy(j, i);
					Watson.addActionListener(this);
					mainwindow.add(Watson, cons);
				} else if (i == 3 && j == 0) {
					dog.setxy(j, i);
					dog.addActionListener(this);
					mainwindow.add(dog, cons);
				} else if (i > 4 && i< 7 && j < 4) {
					actions[act] = new myButton(new ImageIcon("res/act" + act + ".png"));
					actions[act].setxy(j, i);
					mainwindow.add(actions[act], cons);
					actions[act].addActionListener(this);
					act++;
				} else if (i == 5 && j == 4) {
					ok.setxy(j, i);
					ok.addActionListener(this);
					mainwindow.add(ok, cons);
				} else if (i == 7 && j == 0) {
					Font f = new Font("SansSerif", Font.BOLD, 18);
					round.setEditable(false);
					round.setFont(f);
					round.setText("round 1");
					mainwindow.add(round, cons);
				}
				else if (i == 7 && j == 1) {
					Font f = new Font("SansSerif", Font.BOLD, 18);
					score.setEditable(false);
					score.setFont(f);
					score.setText("Jack score 0");
					mainwindow.add(score, cons);
				}
				else if(i==7 && j==2){
					Font f = new Font("SansSerif", Font.BOLD, 18);
					jackid.setEditable(false);
					jackid.setFont(f);
					mainwindow.add(jackid, cons);
				}
				else if(i==7 && j==2){
					Font f = new Font("SansSerif", Font.BOLD, 18);
					jackid.setEditable(false);
					jackid.setFont(f);
					mainwindow.add(jackid, cons);
				}
				else if(i==7 && j==3){
					displayJack.setxy(j, i);
					displayJack.setEnabled(false);
					mainwindow.add(displayJack, cons);
				}
			}
		}
		for (int i = 0; i < 9; i++)
			people[i].setEnabled(false);
		Holmes.setEnabled(false);
		Watson.setEnabled(false);
		dog.setEnabled(false);
		ok.setEnabled(false);
		mainwindow.setVisible(true);
	}
	public void round_done(){
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JackGUI gui = new JackGUI();
		gui.onCreate();
		int round = 1,score = 0;
		int act_num = 1;
		Random rand = new Random();
		int jack = rand.nextInt(9)+1;
		gui.jackid.setText("jack is ");
		//System.out.printf("%d\n", jack);
		gui.displayJack.setDisabledIcon(new ImageIcon(("res/"+jack+"_0.png")));
		boolean[] action_used = new boolean[4];
		for(int i=0;i<4;i++){
			int a=rand.nextInt(2);
			if(a == 0){
				action_used[i] = true;
				gui.actions[i].setEnabled(true);
				gui.actions[i+4].setEnabled(false);
			}
			else{
				action_used[i] = false;
				gui.actions[i].setEnabled(false);
				gui.actions[i+4].setEnabled(true);
			}
		}
		boolean done = true;
		for(int i=0;i<8;i++){
			if(gui.actions[i].isEnabled()){
				done = false;
				break;
			}
		}
		System.out.printf("%d\n", gui.people[2].character);
		gui.people[2].setDead();
		if(done){
			round++;
			gui.round_done();
			
		}
		
		
		// System.out.printf("%d\n", d.get_deg());
	}
}
