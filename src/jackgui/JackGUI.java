package jackgui;

import java.util.Random;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
public class JackGUI implements ActionListener, Cloneable {
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
			if (this.character == 6) {
				this.setIcon(new ImageIcon("res/crossroad_360.png"));
				this.setDisabledIcon(new ImageIcon("res/crossroad_360.png"));
				this.angle = 360;
			} else {
				this.setIcon(new ImageIcon("res/triroad_" + this.angle + ".png"));
				this.setDisabledIcon(new ImageIcon("res/triroad_" + this.angle + ".png"));
			}
			mainwindow.revalidate();
			mainwindow.repaint();
			System.out.printf("%d is dead.\n", character);
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
	Stack<myButton> card=new Stack<myButton>();
	JTextField rounds = new JTextField(10);
	JTextField scores = new JTextField(10);
	JTextField jackid = new JTextField(10);
	GridBagConstraints cons = new GridBagConstraints();
	int preClick;
	int jack;
	int round, move, score;
	boolean[] action_used = new boolean[4];
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
			preClick = e.getSource() == actions[1] ? 1 : 2;
			for (int i = 0; i < 9; i++)
				people[i].setEnabled(true);
			ok.setEnabled(true);
			rotate = 3;
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
		} else if (e.getSource() == actions[7]) {
			preClick = 7;
			myButton p = card.pop();
			if(!p.IsDead)p.setDead();
			actions[7].setEnabled(false);
			ok.setEnabled(true);
		} else if (e.getSource() == ok) {

			System.out.printf("preClick: %d\n", preClick);
			actions[preClick].setEnabled(false);
			if (preClick == 0 || preClick == 3 || preClick == 4 || preClick == 5) {
				Holmes.setEnabled(false);
				Watson.setEnabled(false);
				dog.setEnabled(false);
				steps = -1;
			} else if (preClick == 1 || preClick == 2) {
				for (int i = 0; i < 9; i++) {
					if (people[i].isEnabled()) {
						people[i].setEnabled(false);
					}
				}
				rotate = 0;
			} else if (preClick == 6) {
				int x = -1, y = -1;
				for (int i = 0; i < 9; i++) {
					if (x == -1 && !people[i].isEnabled()) {
						x = i;
						mainwindow.remove(people[i]);
						//System.out.printf("x: %d\n", x);
					} else if (x != -1 && !people[i].isEnabled()) {
						y = i;
						mainwindow.remove(people[i]);
						//System.out.printf("y: %d\n", y);
						break;
					}
				}
				System.out.printf("x: %d ,y: %d\n", x, y);
				Swap(people[x], people[y]);
				// System.out.println("Swap success\n");
			}
			for (int i = 0; i < 9; i++) {
				people[i].setEnabled(false);
			}
			move++;
			if (move == 5) {
				move = 1;
				round_done();
			}
		}
		for (int i = 0; i < 9; i++) {
			if (e.getSource() == people[i] && people[i].isEnabled()) {
				if (rotate > 0) {
					for (int j = 0; j < 9; j++)
						if (j != i)
							people[j].setEnabled(false);
					people[i].setAngle((people[i].angle + 90) % 360);
					String Path, Selector = "_" + people[i].angle + ".png";
					if (!people[i].IsDead)
						Path = "res/" + people[i].character;
					else {
						if (people[i].character != 6)
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
					//mainwindow.remove(people[i]);
					exchange--;
				}
			}
		}
	}

	public void Swap(myButton x, myButton y) {
		cons.gridx = x.gridx;
		cons.gridy = x.gridy;
		mainwindow.add(y, cons);
		cons.gridx = y.gridx;
		cons.gridy = y.gridy;
		mainwindow.add(x, cons);
		int t = x.gridx, v = x.gridy;
		x.gridx = y.gridx;
		x.gridy = y.gridy;
		y.gridx = t;
		y.gridy = v;
		mainwindow.revalidate();
		mainwindow.repaint();
	}

	public void Spin(myButton action, myButton p, int angle) {
		// if(action.isEnabled()) // //comment for test
		{
			p.setAngle(angle);
			String Path, Selector = "_" + p.angle + ".png";
			if (!p.IsDead)
				Path = "res/" + p.character;
			else {
				if (p.character != 6)
					Path = "res/triroad";
				else
					Path = "res/crossroad";
			}
			p.setIcon(new ImageIcon(Path + Selector));
			p.setDisabledIcon(new ImageIcon(Path + Selector));
			mainwindow.revalidate();
			mainwindow.repaint();
		}
	}

	public void Move(myButton action, myButton sel, int _steps) {
		// if (action.isEnabled()) // comment for extension in the future
		// Doesn't check steps for action limit.
		myButton p = action == actions[0] ? Watson
				: action == actions[3] ? Holmes : action == actions[4] ? dog : action == actions[5] ? sel : null;
		if (p == null)
			return;
		{
			for (int i = 0; i < _steps; i++) {
				this.movepos(p);
			}
			cons.gridx = p.gridx;
			cons.gridy = p.gridy;
			mainwindow.remove(p);
			mainwindow.add(p, cons);
			mainwindow.revalidate();
			mainwindow.repaint();
		}
	}

	public int priority_of(myButton act) {
		if (act == actions[0] || act == actions[3] || act == actions[4])
			return 1;
		else if (act == actions[1] || act == actions[2])
			return 2;
		else if (act == actions[5])
			return 3;
		else if (act == actions[7])
			return 3;
		else
			return 0;
	}

	public void agent(JackGUI gui, int do_act) {
		int H = -1;
		//choose 1 or 2 actions
		for (int i = 0; i < do_act; i++) {
			//select the highest priority action.
			for (int j = 0; j < 8; j++) {
				if (actions[j].isEnabled()) {
					if (H != -1)
						H = priority_of(actions[H]) < priority_of(actions[j]) ? j : H;
					else
						H = j;
				}
			}
			//judge the most suitable result
			JackGUI g = null;
			try {
				g = (JackGUI) gui.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int dead = 0;
			switch (H) {
			case 0:
				g.Move(g.actions[0], null, 1);
				dead = g.num_seen();
				g.Move(g.actions[0], null, 2);
				// Select the most average cases |(4 or 5)-4.5| = 0.5. 
				// Other cases > 0.5.
				if (Math.abs(dead - 4.5) < Math.abs(g.num_seen() - 4.5))
					Move(actions[0], null, 1);
				else
					Move(actions[0], null, 2);
				break;
			case 1:case 2:
				int s = -1,a=0;
				for (int p = 0; p < 9; p++) {
					for(int pi = 90 ; pi <360;pi+=90){
						g.Spin(g.actions[1], g.people[p], pi);
						if(Math.abs(dead - 4.5) < Math.abs(g.num_seen() - 4.5)) {
							s = p;
							a = pi;
						}
						else {
							dead = g.num_seen() ;
						}
					}
				}
				Spin(actions[1],people[s],a);
				break;
			case 3:
				g.Move(g.actions[3], null, 1);
				dead = g.num_seen();
				g.Move(g.actions[3], null, 2);
				// Select the most average cases |(4 or 5)-4.5| = 0.5. 
				// Other cases > 0.5.
				if (Math.abs(dead - 4.5) < Math.abs(g.num_seen() - 4.5))
					Move(actions[3], null, 1);
				else
					Move(actions[3], null, 2);
				break;
			case 4:
				g.Move(g.actions[4], null, 1);
				dead = g.num_seen();
				g.Move(g.actions[4], null, 2);
				// Select the most average cases |(4 or 5)-4.5| = 0.5. 
				// Other cases > 0.5.
				if (Math.abs(dead - 4.5) < Math.abs(g.num_seen() - 4.5))
					Move(actions[4], null, 1);
				else
					Move(actions[4], null, 2);
				break;
			case 5:
				myButton p =null;
				int sp=0,t;
				dead =  g.num_seen();
				g.Move(g.actions[5], g.Holmes, 1);
				t = g.num_seen();
				if (Math.abs(dead - 4.5) < Math.abs(t - 4.5)){
					dead = t;
					p = Holmes;
					sp = 1;
				}
				g.Move(g.actions[5], g.Watson, 1);
				t = g.num_seen();
				if (Math.abs(dead - 4.5) < Math.abs(t - 4.5)){
					dead = t;
					p = Watson;
					sp = 1;
				}
				g.Move(g.actions[5], g.dog, 1);
				t = g.num_seen();
				if (Math.abs(dead - 4.5) < Math.abs(t - 4.5)){
					dead = t;
					p = dog;
					sp = 1;
				}
				if(sp > 0)
					Move(actions[5],p,sp);
				break;
			case 6:
				dead =0;
				int t1 = -1,t2 = -1;
				for(int y=0;y<9;y++){
					for(int x =y+1;x<9;x++){
						if(x!=y){
							g.Swap(g.people[x],people[y]);
							t = g.num_seen();
							if (Math.abs(dead - 4.5) < Math.abs(t - 4.5)){
								dead = t;
								t1 =x;
								t2 =y;
							}
						}
					}
				}
				Swap(people[t1],people[t2]);
				break;
			case 7:
				myButton b = card.pop();
				if(!b.IsDead)b.setDead();
				actions[7].setEnabled(false);
				break;
			}
		}

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
		round = 1;
		move = 1;
		score = 0;

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
				} else if (i > 4 && i < 7 && j < 4) {
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
					rounds.setEditable(false);
					rounds.setFont(f);
					String lol = "round " + round;
					rounds.setText(lol);
					mainwindow.add(rounds, cons);
				} else if (i == 7 && j == 1) {
					Font f = new Font("SansSerif", Font.BOLD, 18);
					scores.setEditable(false);
					scores.setFont(f);
					String lol = "Jack score " + score;
					scores.setText(lol);
					mainwindow.add(scores, cons);
				} else if (i == 7 && j == 2) {
					Font f = new Font("SansSerif", Font.BOLD, 18);
					jackid.setEditable(false);
					jackid.setFont(f);
					mainwindow.add(jackid, cons);
				} else if (i == 7 && j == 2) {
					Font f = new Font("SansSerif", Font.BOLD, 18);
					jackid.setEditable(false);
					jackid.setFont(f);
					mainwindow.add(jackid, cons);
				} else if (i == 7 && j == 3) {
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
		for (int i = 0; i < 4; i++) {
			int a = rand.nextInt(2);
			if (a == 0) {
				action_used[i] = true;
				actions[i].setEnabled(true);
				actions[i + 4].setEnabled(false);
			} else {
				action_used[i] = false;
				actions[i].setEnabled(false);
				actions[i + 4].setEnabled(true);
			}
		}
		for(int i=0;i<9;i++){
			card.push(people[(Integer) order.toArray()[i]]);
		}
		jack = card.pop().character;
	}

	public void round_done() {
		myButton [] jesus = new myButton [9];
		int [] bible = new int [9];
		for(int i = 0 ;i  < 9; i ++){
			if(people[i].gridx == 1 && people[i].gridy == 1){
				jesus[0] =people[i] ;
				bible[0] = i;
				continue;
			}
			else if(people[i].gridx == 2 && people[i].gridy == 1){
				jesus[1] =people[i] ;
				bible[1] = i;
				continue;
			}
			else if(people[i].gridx == 3 && people[i].gridy == 1){
				jesus[2] =people[i] ;
				bible[2] = i;
				continue;
			}
			else if(people[i].gridx == 1 && people[i].gridy == 2){
				jesus[3] =people[i] ;
				bible[3] = i;
				continue;
			}
			else if(people[i].gridx == 2 && people[i].gridy == 2){
				jesus[4] =people[i] ;
				bible[4] = i;
				continue;
			}
			else if(people[i].gridx == 3 && people[i].gridy == 2){
				jesus[5] =people[i] ;
				bible[5] = i;
				continue;
			}
			else if(people[i].gridx == 1 && people[i].gridy == 3){
				jesus[6] =people[i] ;
				bible[6] = i;
				continue;
			}
			else if(people[i].gridx == 2 && people[i].gridy == 3){
				jesus[7] =people[i] ;
				bible[7] = i;
				continue;
			}
			else if(people[i].gridx == 3 && people[i].gridy == 3){
				jesus[8] =people[i] ;
				bible[8] = i;
				continue;
			}
		}
		System.out.printf("in round_done\n");
		for(int i = 0; i < 9;i++)System.out.printf("%d 's angle :%d\n",jesus[i].character,jesus[i].angle);
		if (round % 2 == 1) {
			for (int i = 0; i < 4; i++) {
				if (action_used[i] == true) {
					actions[i + 4].setEnabled(true);
				} else {
					actions[i].setEnabled(true);
				}
			}
		} else {
			Random rand = new Random();
			for (int i = 0; i < 4; i++) {
				int a = rand.nextInt(2);
				if (a == 0) {
					action_used[i] = true;
					actions[i].setEnabled(true);
					actions[i + 4].setEnabled(false);
				} else {
					action_used[i] = false;
					actions[i].setEnabled(false);
					actions[i + 4].setEnabled(true);
				}
			}
		}
		round++;
		boolean seen[] = new boolean[9];
		for (int i = 0; i < 9; i++)
			seen[i] = false;
		//for dog
		//left
		if (dog.gridx == 0 && dog.gridy == 1) {
			if (jesus[0].angle == 90)
				;
			else if (jesus[0].angle == 270 || jesus[1].angle == 90) {
				seen[0] = true;
			} else if (jesus[1].angle == 270 || jesus[2].angle == 90) {
				seen[0] = true;
				seen[1] = true;
			} else {
				seen[0] = true;
				seen[1] = true;
				seen[2] = true;
			}
		} else if (dog.gridx == 0 && dog.gridy == 2) {
			if (jesus[3].angle == 90)
				;
			else if (jesus[3].angle == 270 || jesus[4].angle == 90) {
				seen[3] = true;
			} else if (jesus[4].angle == 270 || jesus[5].angle == 90) {
				seen[3] = true;
				seen[4] = true;
			} else {
				seen[3] = true;
				seen[4] = true;
				seen[5] = true;
			}
		} else if (dog.gridx == 0 && dog.gridy == 3) {
			if (jesus[6].angle == 90)
				;
			else if (jesus[6].angle == 270 || jesus[7].angle == 90) {
				seen[6] = true;
			} else if (jesus[7].angle == 270 || jesus[8].angle == 90) {
				seen[7] = true;
				seen[8] = true;
			} else {
				seen[6] = true;
				seen[7] = true;
				seen[8] = true;
			}
		}
		//up
		else if (dog.gridx == 1 && dog.gridy == 0) {
			if (people[0].angle == 180)
				;
			else if (jesus[0].angle == 0 || jesus[3].angle == 180) {
				seen[0] = true;
			} else if (jesus[3].angle == 0 || jesus[6].angle == 180) {
				seen[0] = true;
				seen[3] = true;
			} else {
				seen[0] = true;
				seen[3] = true;
				seen[6] = true;
			}
		} else if (dog.gridx == 2 && dog.gridy == 0) {
			if (jesus[1].angle == 180)
				;
			else if (jesus[1].angle == 0 || jesus[4].angle == 180) {
				seen[1] = true;
			} else if (jesus[4].angle == 0 || jesus[7].angle == 180) {
				seen[1] = true;
				seen[4] = true;
			} else {
				seen[1] = true;
				seen[4] = true;
				seen[7] = true;
			}
		} else if (dog.gridx == 3 && dog.gridy == 0) {
			if (jesus[2].angle == 180)
				;
			else if (jesus[2].angle == 0 || jesus[5].angle == 180) {
				seen[2] = true;
			} else if (jesus[5].angle == 0 || jesus[8].angle == 180) {
				seen[2] = true;
				seen[5] = true;
			} else {
				seen[2] = true;
				seen[5] = true;
				seen[8] = true;
			}
		}
		//right
		else if (dog.gridx == 4 && dog.gridy == 1) {
			if (jesus[2].angle == 270)
				;
			else if (jesus[2].angle == 90 || jesus[1].angle == 270) {
				seen[2] = true;
			} else if (jesus[1].angle == 90 || jesus[0].angle == 270) {
				seen[2] = true;
				seen[1] = true;
			} else {
				seen[2] = true;
				seen[1] = true;
				seen[0] = true;
			}
		} else if (dog.gridx == 4 && dog.gridy == 2) {
			if (jesus[5].angle == 270)
				;
			else if (jesus[5].angle == 90 || jesus[4].angle == 270) {
				seen[5] = true;
			} else if (jesus[4].angle == 90 || jesus[3].angle == 270) {
				seen[5] = true;
				seen[4] = true;
			} else {
				seen[5] = true;
				seen[4] = true;
				seen[3] = true;
			}
		} else if (dog.gridx == 4 && dog.gridy == 3) {
			if (jesus[8].angle == 270)
				;
			else if (jesus[8].angle == 90 || jesus[7].angle == 270) {
				seen[8] = true;
			} else if (jesus[7].angle == 90 || jesus[6].angle == 270) {
				seen[8] = true;
				seen[7] = true;
			} else {
				seen[8] = true;
				seen[7] = true;
				seen[6] = true;
			}
		}
		//down
		else if (dog.gridx == 1 && dog.gridy == 4) {
			if (jesus[6].angle == 0)
				;
			else if (jesus[6].angle == 180 || jesus[3].angle == 0) {
				seen[6] = true;
			} else if (jesus[3].angle == 180 || jesus[0].angle == 0) {
				seen[6] = true;
				seen[3] = true;
			} else {
				seen[6] = true;
				seen[3] = true;
				seen[0] = true;
			}
		} else if (dog.gridx == 2 && dog.gridy == 4) {
			if (jesus[7].angle == 0)
				;
			else if (jesus[7].angle == 180 || jesus[4].angle == 0) {
				seen[7] = true;
			} else if (jesus[4].angle == 180 || jesus[1].angle == 0) {
				seen[7] = true;
				seen[4] = true;
			} else {
				seen[7] = true;
				seen[4] = true;
				seen[1] = true;
			}
		} else if (dog.gridx == 3 && dog.gridy == 4) {
			if (jesus[8].angle == 0)
				;
			else if (jesus[8].angle == 180 || jesus[5].angle == 0) {
				seen[8] = true;
			} else if (jesus[5].angle == 180 || jesus[2].angle == 0) {
				seen[8] = true;
				seen[5] = true;
			} else {
				seen[8] = true;
				seen[5] = true;
				seen[2] = true;
			}
		}
		//for Holmes
		//left
		if (Holmes.gridx == 0 && Holmes.gridy == 1) {
			if (jesus[0].angle == 90)
				;
			else if (jesus[0].angle == 270 || jesus[1].angle == 90) {
				seen[0] = true;
			} else if (jesus[1].angle == 270 || jesus[2].angle == 90) {
				seen[0] = true;
				seen[1] = true;
			} else {
				seen[0] = true;
				seen[1] = true;
				seen[2] = true;
			}
		} else if (Holmes.gridx == 0 && Holmes.gridy == 2) {
			if (jesus[3].angle == 90)
				;
			else if (jesus[3].angle == 270 || jesus[4].angle == 90) {
				seen[3] = true;
			} else if (jesus[4].angle == 270 || jesus[5].angle == 90) {
				seen[3] = true;
				seen[4] = true;
			} else {
				seen[3] = true;
				seen[4] = true;
				seen[5] = true;
			}
		} else if (Holmes.gridx == 0 && Holmes.gridy == 3) {
			if (jesus[6].angle == 90)
				;
			else if (jesus[6].angle == 270 || jesus[7].angle == 90) {
				seen[6] = true;
			} else if (jesus[7].angle == 270 || jesus[8].angle == 90) {
				seen[6] = true;
				seen[7] = true;
			} else {
				seen[6] = true;
				seen[7] = true;
				seen[8] = true;
			}
		}
		//up
		else if (Holmes.gridx == 1 && Holmes.gridy == 0) {
			if (jesus[0].angle == 180)
				;
			else if (jesus[0].angle == 0 || jesus[3].angle == 180) {
				seen[0] = true;
			} else if (jesus[3].angle == 0 || jesus[6].angle == 180) {
				seen[0] = true;
				seen[3] = true;
			} else {
				seen[0] = true;
				seen[3] = true;
				seen[6] = true;
			}
		} else if (Holmes.gridx == 2 && Holmes.gridy == 0) {
			if (jesus[1].angle == 180)
				;
			else if (jesus[1].angle == 0 || jesus[4].angle == 180) {
				seen[1] = true;
			} else if (jesus[4].angle == 0 || jesus[7].angle == 180) {
				seen[1] = true;
				seen[4] = true;
			} else {
				seen[1] = true;
				seen[4] = true;
				seen[7] = true;
			}
		} else if (Holmes.gridx == 3 && Holmes.gridy == 0) {
			if (jesus[2].angle == 180)
				;
			else if (jesus[2].angle == 0 || jesus[5].angle == 180) {
				seen[2] = true;
			} else if (jesus[5].angle == 0 || jesus[8].angle == 180) {
				seen[2] = true;
				seen[5] = true;
			} else {
				seen[2] = true;
				seen[5] = true;
				seen[8] = true;
			}
		}
		//right
		else if (Holmes.gridx == 4 && Holmes.gridy == 1) {
			if (jesus[2].angle == 270)
				;
			else if (jesus[2].angle == 90 || jesus[1].angle == 270) {
				seen[2] = true;
			} else if (jesus[1].angle == 90 || jesus[0].angle == 270) {
				seen[2] = true;
				seen[1] = true;
			} else {
				seen[2] = true;
				seen[1] = true;
				seen[0] = true;
			}
		} else if (Holmes.gridx == 4 && Holmes.gridy == 2) {
			if (jesus[5].angle == 270)
				;
			else if (jesus[5].angle == 90 || jesus[4].angle == 270) {
				seen[5] = true;
			} else if (jesus[4].angle == 90 || jesus[3].angle == 270) {
				seen[5] = true;
				seen[4] = true;
			} else {
				seen[5] = true;
				seen[4] = true;
				seen[3] = true;
			}
		} else if (Holmes.gridx == 4 && Holmes.gridy == 3) {
			if (jesus[8].angle == 270)
				;
			else if (jesus[8].angle == 90 || jesus[7].angle == 270) {
				seen[8] = true;
			} else if (jesus[7].angle == 90 || jesus[6].angle == 270) {
				seen[8] = true;
				seen[7] = true;
			} else {
				seen[8] = true;
				seen[7] = true;
				seen[6] = true;
			}
		}
		//down
		else if (Holmes.gridx == 1 && Holmes.gridy == 4) {
			if (jesus[6].angle == 0)
				;
			else if (jesus[6].angle == 180 || jesus[3].angle == 0) {
				seen[6] = true;
			} else if (jesus[3].angle == 180 || jesus[0].angle == 0) {
				seen[6] = true;
				seen[3] = true;
			} else {
				seen[6] = true;
				seen[3] = true;
				seen[0] = true;
			}
		} else if (Holmes.gridx == 2 && Holmes.gridy == 4) {
			if (jesus[7].angle == 0)
				;
			else if (jesus[7].angle == 180 || jesus[4].angle == 0) {
				seen[7] = true;
			} else if (jesus[4].angle == 180 || jesus[1].angle == 0) {
				seen[7] = true;
				seen[4] = true;
			} else {
				seen[7] = true;
				seen[4] = true;
				seen[1] = true;
			}
		} else if (Holmes.gridx == 3 && Holmes.gridy == 4) {
			if (jesus[8].angle == 0)
				;
			else if (jesus[8].angle == 180 || jesus[5].angle == 0) {
				seen[8] = true;
			} else if (jesus[5].angle == 180 || jesus[2].angle == 0) {
				seen[8] = true;
				seen[5] = true;
			} else {
				seen[8] = true;
				seen[5] = true;
				seen[2] = true;
			}
		}
		//for Watson
		//left
		if (Watson.gridx == 0 && Watson.gridy == 1) {
			if (jesus[0].angle == 90)
				;
			else if (jesus[0].angle == 270 || jesus[1].angle == 90) {
				seen[0] = true;
			} else if (jesus[1].angle == 270 || jesus[2].angle == 90) {
				seen[0] = true;
				seen[1] = true;
			} else {
				seen[0] = true;
				seen[1] = true;
				seen[2] = true;
			}
		} else if (Watson.gridx == 0 && Watson.gridy == 2) {
			if (jesus[3].angle == 90)
				;
			else if (people[3].angle == 270 || jesus[4].angle == 90) {
				seen[3] = true;
			} else if (jesus[4].angle == 270 || jesus[5].angle == 90) {
				seen[3] = true;
				seen[4] = true;
			} else {
				seen[3] = true;
				seen[4] = true;
				seen[5] = true;
			}
		} else if (Watson.gridx == 0 && Watson.gridy == 3) {
			if (jesus[6].angle == 90)
				;
			else if (jesus[6].angle == 270 || jesus[7].angle == 90) {
				seen[6] = true;
			} else if (jesus[7].angle == 270 || jesus[8].angle == 90) {
				seen[6] = true;
				seen[7] = true;
			} else {
				seen[6] = true;
				seen[7] = true;
				seen[8] = true;
			}
		}
		//up
		else if (Watson.gridx == 1 && Watson.gridy == 0) {
			if (jesus[0].angle == 180)
				;
			else if (jesus[0].angle == 0 || jesus[3].angle == 180) {
				seen[0] = true;
			} else if (jesus[3].angle == 0 || jesus[6].angle == 180) {
				seen[0] = true;
				seen[3] = true;
			} else {
				seen[0] = true;
				seen[3] = true;
				seen[6] = true;
			}
		} else if (Watson.gridx == 2 && Watson.gridy == 0) {
			if (jesus[1].angle == 180)
				;
			else if (jesus[1].angle == 0 || jesus[4].angle == 180) {
				seen[1] = true;
			} else if (jesus[4].angle == 0 || jesus[7].angle == 180) {
				seen[1] = true;
				seen[4] = true;
			} else {
				seen[1] = true;
				seen[4] = true;
				seen[7] = true;
			}
		} else if (Watson.gridx == 3 && Watson.gridy == 0) {
			if (jesus[2].angle == 180)
				;
			else if (jesus[2].angle == 0 || jesus[5].angle == 180) {
				seen[2] = true;
			} else if (jesus[5].angle == 0 || jesus[8].angle == 180) {
				seen[2] = true;
				seen[5] = true;
			} else {
				seen[2] = true;
				seen[5] = true;
				seen[8] = true;
			}
		}
		//right
		else if (Watson.gridx == 4 && Watson.gridy == 1) {
			if (jesus[2].angle == 270)
				;
			else if (jesus[2].angle == 90 || jesus[1].angle == 270) {
				seen[2] = true;
			} else if (jesus[1].angle == 90 || jesus[0].angle == 270) {
				seen[2] = true;
				seen[1] = true;
			} else {
				seen[2] = true;
				seen[1] = true;
				seen[0] = true;
			}
		} else if (Watson.gridx == 4 && Watson.gridy == 2) {
			if (jesus[5].angle == 270)
				;
			else if (jesus[5].angle == 90 || jesus[4].angle == 270) {
				seen[5] = true;
			} else if (jesus[4].angle == 90 || jesus[3].angle == 270) {
				seen[5] = true;
				seen[4] = true;
			} else {
				seen[5] = true;
				seen[4] = true;
				seen[3] = true;
			}
		} else if (Watson.gridx == 4 && Watson.gridy == 3) {
			if (jesus[8].angle == 270)
				;
			else if (jesus[8].angle == 90 || jesus[7].angle == 270) {
				seen[8] = true;
			} else if (jesus[7].angle == 90 || jesus[6].angle == 270) {
				seen[8] = true;
				seen[7] = true;
			} else {
				seen[8] = true;
				seen[7] = true;
				seen[6] = true;
			}
		}
		//down
		else if (Watson.gridx == 1 && Watson.gridy == 4) {
			if (jesus[6].angle == 0)
				;
			else if (jesus[6].angle == 180 || jesus[3].angle == 0) {
				seen[6] = true;
			} else if (jesus[3].angle == 180 || jesus[0].angle == 0) {
				seen[6] = true;
				seen[3] = true;
			} else {
				seen[6] = true;
				seen[3] = true;
				seen[0] = true;
			}
		} else if (Watson.gridx == 2 && Watson.gridy == 4) {
			if (jesus[7].angle == 0)
				;
			else if (jesus[7].angle == 180 || jesus[4].angle == 0) {
				seen[7] = true;
			} else if (jesus[4].angle == 180 || jesus[1].angle == 0) {
				seen[7] = true;
				seen[4] = true;
			} else {
				seen[7] = true;
				seen[4] = true;
				seen[1] = true;
			}
		} else if (Watson.gridx == 3 && Watson.gridy == 4) {
			if (jesus[8].angle == 0)
				;
			else if (jesus[8].angle == 180 || jesus[5].angle == 0) {
				seen[8] = true;
			} else if (jesus[5].angle == 180 || people[2].angle == 0) {
				seen[8] = true;
				seen[5] = true;
			} else {
				seen[8] = true;
				seen[5] = true;
				seen[2] = true;
			}
		}

		boolean jack_seen = false;
		for (int i = 0; i < 9; i++) {
			if (jack == jesus[i].character) {
				System.out.printf("found jack: \njack is %d\nseen[%d]\n",jack,i);
				if (seen[i])
					jack_seen = true;
				else
					jack_seen = false;
				break;
			}
		}
		if (jack_seen) {
			for (int i = 0; i < 9; i++) {
				if (!seen[i])
					people[bible[i]].setDead();
			}
		} else if (!jack_seen) {
			for (int i = 0; i < 9; i++) {
				if (seen[i])
					people[bible[i]].setDead();
			}
		}
		exchange = rotate = steps = 0;
		System.out.printf("fuck me\n");
	}

	public int num_seen() {
		boolean seen[] = new boolean[9];
		for (int i = 0; i < 9; i++)
			seen[i] = false;
		//for dog
		//left
		if (dog.gridx == 0 && dog.gridy == 1) {
			if (people[0].angle == 90)
				;
			else if (people[0].angle == 270 || people[1].angle == 90) {
				seen[0] = true;
			} else if (people[1].angle == 270 || people[2].angle == 90) {
				seen[0] = true;
				seen[1] = true;
			} else {
				seen[0] = true;
				seen[1] = true;
				seen[2] = true;
			}
		} else if (dog.gridx == 0 && dog.gridy == 2) {
			if (people[3].angle == 90)
				;
			else if (people[3].angle == 270 || people[4].angle == 90) {
				seen[3] = true;
			} else if (people[4].angle == 270 || people[5].angle == 90) {
				seen[3] = true;
				seen[4] = true;
			} else {
				seen[3] = true;
				seen[4] = true;
				seen[5] = true;
			}
		} else if (dog.gridx == 0 && dog.gridy == 3) {
			if (people[6].angle == 90)
				;
			else if (people[6].angle == 270 || people[7].angle == 90) {
				seen[6] = true;
			} else if (people[7].angle == 270 || people[8].angle == 90) {
				seen[7] = true;
				seen[8] = true;
			} else {
				seen[6] = true;
				seen[7] = true;
				seen[8] = true;
			}
		}
		//up
		else if (dog.gridx == 1 && dog.gridy == 0) {
			if (people[0].angle == 180)
				;
			else if (people[0].angle == 0 || people[3].angle == 180) {
				seen[0] = true;
			} else if (people[3].angle == 0 || people[6].angle == 180) {
				seen[0] = true;
				seen[3] = true;
			} else {
				seen[0] = true;
				seen[3] = true;
				seen[6] = true;
			}
		} else if (dog.gridx == 2 && dog.gridy == 0) {
			if (people[1].angle == 180)
				;
			else if (people[1].angle == 0 || people[4].angle == 180) {
				seen[1] = true;
			} else if (people[4].angle == 0 || people[7].angle == 180) {
				seen[1] = true;
				seen[4] = true;
			} else {
				seen[1] = true;
				seen[4] = true;
				seen[7] = true;
			}
		} else if (dog.gridx == 3 && dog.gridy == 0) {
			if (people[2].angle == 180)
				;
			else if (people[2].angle == 0 || people[5].angle == 180) {
				seen[2] = true;
			} else if (people[5].angle == 0 || people[8].angle == 180) {
				seen[2] = true;
				seen[5] = true;
			} else {
				seen[2] = true;
				seen[5] = true;
				seen[8] = true;
			}
		}
		//right
		else if (dog.gridx == 4 && dog.gridy == 1) {
			if (people[2].angle == 270)
				;
			else if (people[2].angle == 90 || people[1].angle == 270) {
				seen[2] = true;
			} else if (people[1].angle == 90 || people[0].angle == 270) {
				seen[2] = true;
				seen[1] = true;
			} else {
				seen[2] = true;
				seen[1] = true;
				seen[0] = true;
			}
		} else if (dog.gridx == 4 && dog.gridy == 2) {
			if (people[5].angle == 270)
				;
			else if (people[5].angle == 90 || people[4].angle == 270) {
				seen[5] = true;
			} else if (people[4].angle == 90 || people[3].angle == 270) {
				seen[5] = true;
				seen[4] = true;
			} else {
				seen[5] = true;
				seen[4] = true;
				seen[3] = true;
			}
		} else if (dog.gridx == 4 && dog.gridy == 3) {
			if (people[8].angle == 270)
				;
			else if (people[8].angle == 90 || people[7].angle == 270) {
				seen[8] = true;
			} else if (people[7].angle == 90 || people[6].angle == 270) {
				seen[8] = true;
				seen[7] = true;
			} else {
				seen[8] = true;
				seen[7] = true;
				seen[6] = true;
			}
		}
		//down
		else if (dog.gridx == 1 && dog.gridy == 4) {
			if (people[6].angle == 0)
				;
			else if (people[6].angle == 180 || people[3].angle == 0) {
				seen[6] = true;
			} else if (people[3].angle == 180 || people[0].angle == 0) {
				seen[6] = true;
				seen[3] = true;
			} else {
				seen[6] = true;
				seen[3] = true;
				seen[0] = true;
			}
		} else if (dog.gridx == 2 && dog.gridy == 4) {
			if (people[7].angle == 0)
				;
			else if (people[7].angle == 180 || people[4].angle == 0) {
				seen[7] = true;
			} else if (people[4].angle == 180 || people[1].angle == 0) {
				seen[7] = true;
				seen[4] = true;
			} else {
				seen[7] = true;
				seen[4] = true;
				seen[1] = true;
			}
		} else if (dog.gridx == 3 && dog.gridy == 4) {
			if (people[8].angle == 0)
				;
			else if (people[8].angle == 180 || people[5].angle == 0) {
				seen[8] = true;
			} else if (people[5].angle == 180 || people[2].angle == 0) {
				seen[8] = true;
				seen[5] = true;
			} else {
				seen[8] = true;
				seen[5] = true;
				seen[2] = true;
			}
		}
		//for Holmes
		//left
		if (Holmes.gridx == 0 && Holmes.gridy == 1) {
			if (people[0].angle == 90)
				;
			else if (people[0].angle == 270 || people[1].angle == 90) {
				seen[0] = true;
			} else if (people[1].angle == 270 || people[2].angle == 90) {
				seen[0] = true;
				seen[1] = true;
			} else {
				seen[0] = true;
				seen[1] = true;
				seen[2] = true;
			}
		} else if (Holmes.gridx == 0 && Holmes.gridy == 2) {
			if (people[3].angle == 90)
				;
			else if (people[3].angle == 270 || people[4].angle == 90) {
				seen[3] = true;
			} else if (people[4].angle == 270 || people[5].angle == 90) {
				seen[3] = true;
				seen[4] = true;
			} else {
				seen[3] = true;
				seen[4] = true;
				seen[5] = true;
			}
		} else if (Holmes.gridx == 0 && Holmes.gridy == 3) {
			if (people[6].angle == 90)
				;
			else if (people[6].angle == 270 || people[7].angle == 90) {
				seen[6] = true;
			} else if (people[7].angle == 270 || people[8].angle == 90) {
				seen[6] = true;
				seen[7] = true;
			} else {
				seen[6] = true;
				seen[7] = true;
				seen[8] = true;
			}
		}
		//up
		else if (Holmes.gridx == 1 && Holmes.gridy == 0) {
			if (people[0].angle == 180)
				;
			else if (people[0].angle == 0 || people[3].angle == 180) {
				seen[0] = true;
			} else if (people[3].angle == 0 || people[6].angle == 180) {
				seen[0] = true;
				seen[3] = true;
			} else {
				seen[0] = true;
				seen[3] = true;
				seen[6] = true;
			}
		} else if (Holmes.gridx == 2 && Holmes.gridy == 0) {
			if (people[1].angle == 180)
				;
			else if (people[1].angle == 0 || people[4].angle == 180) {
				seen[1] = true;
			} else if (people[4].angle == 0 || people[7].angle == 180) {
				seen[1] = true;
				seen[4] = true;
			} else {
				seen[1] = true;
				seen[4] = true;
				seen[7] = true;
			}
		} else if (Holmes.gridx == 3 && Holmes.gridy == 0) {
			if (people[2].angle == 180)
				;
			else if (people[2].angle == 0 || people[5].angle == 180) {
				seen[2] = true;
			} else if (people[5].angle == 0 || people[8].angle == 180) {
				seen[2] = true;
				seen[5] = true;
			} else {
				seen[2] = true;
				seen[5] = true;
				seen[8] = true;
			}
		}
		//right
		else if (Holmes.gridx == 4 && Holmes.gridy == 1) {
			if (people[2].angle == 270)
				;
			else if (people[2].angle == 90 || people[1].angle == 270) {
				seen[2] = true;
			} else if (people[1].angle == 90 || people[0].angle == 270) {
				seen[2] = true;
				seen[1] = true;
			} else {
				seen[2] = true;
				seen[1] = true;
				seen[0] = true;
			}
		} else if (Holmes.gridx == 4 && Holmes.gridy == 2) {
			if (people[5].angle == 270)
				;
			else if (people[5].angle == 90 || people[4].angle == 270) {
				seen[5] = true;
			} else if (people[4].angle == 90 || people[3].angle == 270) {
				seen[5] = true;
				seen[4] = true;
			} else {
				seen[5] = true;
				seen[4] = true;
				seen[3] = true;
			}
		} else if (Holmes.gridx == 4 && Holmes.gridy == 3) {
			if (people[8].angle == 270)
				;
			else if (people[8].angle == 90 || people[7].angle == 270) {
				seen[8] = true;
			} else if (people[7].angle == 90 || people[6].angle == 270) {
				seen[8] = true;
				seen[7] = true;
			} else {
				seen[8] = true;
				seen[7] = true;
				seen[6] = true;
			}
		}
		//down
		else if (Holmes.gridx == 1 && Holmes.gridy == 4) {
			if (people[6].angle == 0)
				;
			else if (people[6].angle == 180 || people[3].angle == 0) {
				seen[6] = true;
			} else if (people[3].angle == 180 || people[0].angle == 0) {
				seen[6] = true;
				seen[3] = true;
			} else {
				seen[6] = true;
				seen[3] = true;
				seen[0] = true;
			}
		} else if (Holmes.gridx == 2 && Holmes.gridy == 4) {
			if (people[7].angle == 0)
				;
			else if (people[7].angle == 180 || people[4].angle == 0) {
				seen[7] = true;
			} else if (people[4].angle == 180 || people[1].angle == 0) {
				seen[7] = true;
				seen[4] = true;
			} else {
				seen[7] = true;
				seen[4] = true;
				seen[1] = true;
			}
		} else if (Holmes.gridx == 3 && Holmes.gridy == 4) {
			if (people[8].angle == 0)
				;
			else if (people[8].angle == 180 || people[5].angle == 0) {
				seen[8] = true;
			} else if (people[5].angle == 180 || people[2].angle == 0) {
				seen[8] = true;
				seen[5] = true;
			} else {
				seen[8] = true;
				seen[5] = true;
				seen[2] = true;
			}
		}
		//for Watson
		//left
		if (Watson.gridx == 0 && Watson.gridy == 1) {
			if (people[0].angle == 90)
				;
			else if (people[0].angle == 270 || people[1].angle == 90) {
				seen[0] = true;
			} else if (people[1].angle == 270 || people[2].angle == 90) {
				seen[0] = true;
				seen[1] = true;
			} else {
				seen[0] = true;
				seen[1] = true;
				seen[2] = true;
			}
		} else if (Watson.gridx == 0 && Watson.gridy == 2) {
			if (people[3].angle == 90)
				;
			else if (people[3].angle == 270 || people[4].angle == 90) {
				seen[3] = true;
			} else if (people[4].angle == 270 || people[5].angle == 90) {
				seen[3] = true;
				seen[4] = true;
			} else {
				seen[3] = true;
				seen[4] = true;
				seen[5] = true;
			}
		} else if (Watson.gridx == 0 && Watson.gridy == 3) {
			if (people[6].angle == 90)
				;
			else if (people[6].angle == 270 || people[7].angle == 90) {
				seen[6] = true;
			} else if (people[7].angle == 270 || people[8].angle == 90) {
				seen[6] = true;
				seen[7] = true;
			} else {
				seen[6] = true;
				seen[7] = true;
				seen[8] = true;
			}
		}
		//up
		else if (Watson.gridx == 1 && Watson.gridy == 0) {
			if (people[0].angle == 180)
				;
			else if (people[0].angle == 0 || people[3].angle == 180) {
				seen[0] = true;
			} else if (people[3].angle == 0 || people[6].angle == 180) {
				seen[0] = true;
				seen[3] = true;
			} else {
				seen[0] = true;
				seen[3] = true;
				seen[6] = true;
			}
		} else if (Watson.gridx == 2 && Watson.gridy == 0) {
			if (people[1].angle == 180)
				;
			else if (people[1].angle == 0 || people[4].angle == 180) {
				seen[1] = true;
			} else if (people[4].angle == 0 || people[7].angle == 180) {
				seen[1] = true;
				seen[4] = true;
			} else {
				seen[1] = true;
				seen[4] = true;
				seen[7] = true;
			}
		} else if (Watson.gridx == 3 && Watson.gridy == 0) {
			if (people[2].angle == 180)
				;
			else if (people[2].angle == 0 || people[5].angle == 180) {
				seen[2] = true;
			} else if (people[5].angle == 0 || people[8].angle == 180) {
				seen[2] = true;
				seen[5] = true;
			} else {
				seen[2] = true;
				seen[5] = true;
				seen[8] = true;
			}
		}
		//right
		else if (Watson.gridx == 4 && Watson.gridy == 1) {
			if (people[2].angle == 270)
				;
			else if (people[2].angle == 90 || people[1].angle == 270) {
				seen[2] = true;
			} else if (people[1].angle == 90 || people[0].angle == 270) {
				seen[2] = true;
				seen[1] = true;
			} else {
				seen[2] = true;
				seen[1] = true;
				seen[0] = true;
			}
		} else if (Watson.gridx == 4 && Watson.gridy == 2) {
			if (people[5].angle == 270)
				;
			else if (people[5].angle == 90 || people[4].angle == 270) {
				seen[5] = true;
			} else if (people[4].angle == 90 || people[3].angle == 270) {
				seen[5] = true;
				seen[4] = true;
			} else {
				seen[5] = true;
				seen[4] = true;
				seen[3] = true;
			}
		} else if (Watson.gridx == 4 && Watson.gridy == 3) {
			if (people[8].angle == 270)
				;
			else if (people[8].angle == 90 || people[7].angle == 270) {
				seen[8] = true;
			} else if (people[7].angle == 90 || people[6].angle == 270) {
				seen[8] = true;
				seen[7] = true;
			} else {
				seen[8] = true;
				seen[7] = true;
				seen[6] = true;
			}
		}
		//down
		else if (Watson.gridx == 1 && Watson.gridy == 4) {
			if (people[6].angle == 0)
				;
			else if (people[6].angle == 180 || people[3].angle == 0) {
				seen[6] = true;
			} else if (people[3].angle == 180 || people[0].angle == 0) {
				seen[6] = true;
				seen[3] = true;
			} else {
				seen[6] = true;
				seen[3] = true;
				seen[0] = true;
			}
		} else if (Watson.gridx == 2 && Watson.gridy == 4) {
			if (people[7].angle == 0)
				;
			else if (people[7].angle == 180 || people[4].angle == 0) {
				seen[7] = true;
			} else if (people[4].angle == 180 || people[1].angle == 0) {
				seen[7] = true;
				seen[4] = true;
			} else {
				seen[7] = true;
				seen[4] = true;
				seen[1] = true;
			}
		} else if (Watson.gridx == 3 && Watson.gridy == 4) {
			if (people[8].angle == 0)
				;
			else if (people[8].angle == 180 || people[5].angle == 0) {
				seen[8] = true;
			} else if (people[5].angle == 180 || people[2].angle == 0) {
				seen[8] = true;
				seen[5] = true;
			} else {
				seen[8] = true;
				seen[5] = true;
				seen[2] = true;
			}
		}
		int result = 0;
		//boolean jack_seen = false;
		for (int i = 0; i < 9; i++) {
			if (seen[i])
				result++;
			/*
			 * if (jack == people[i].character) { if (seen[i]) jack_seen = true;
			 * else jack_seen = false; }
			 */
		}
		return result;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JackGUI gui = new JackGUI();
		gui.onCreate();
		/*
		 * int round = 1,score = 0; int act_num = 1; Random rand = new Random();
		 */
		//int jack = rand.nextInt(9)+1;
		gui.jackid.setText("jack is ");
		//System.out.printf("%d\n", gui.jack);
		gui.displayJack.setDisabledIcon(new ImageIcon(("res/" + gui.jack + "_0.png")));
		/*
		 * for(int i=0;i<4;i++){ int a=rand.nextInt(2); if(a == 0){
		 * action_used[i] = true; gui.actions[i].setEnabled(true);
		 * gui.actions[i+4].setEnabled(false); } else{ action_used[i] = false;
		 * gui.actions[i].setEnabled(false); gui.actions[i+4].setEnabled(true);
		 * } }
		 */

		//System.out.printf("%d\n", gui.people[2].character);
		//gui.people[2].setDead();

		// System.out.printf("%d\n", d.get_deg());
	}
}