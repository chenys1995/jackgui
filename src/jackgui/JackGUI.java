package jackgui;
import java.util.Random;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class JackGUI implements ActionListener{
	class myButton extends JButton {

		boolean IsPeople = false, IsDead = false;
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
			//System.out.printf("%d is dead.\n", character);
		}
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
			actions[7].setEnabled(false);
			ok.setEnabled(true);
			//plus score or people dead
			if(round % 2 == 0){
				if(move == 1 || move == 4){
					switch(p.character){
					case 1:score += 2;break;
					case 2:score += 0;break;
					case 3:score += 1;break;
					case 4:score += 0;break;
					case 5:score += 1;break;
					case 6:score += 1;break;
					case 7:score += 1;break;
					case 8:score += 1;break;
					case 9:score += 1;break;
					}
				}
				else {
					if(!p.IsDead)p.setDead();
				}
			}
			else {
				if(move == 2 || move == 3){
					switch(p.character){
					case 1:score += 2;break;
					case 2:score += 0;break;
					case 3:score += 1;break;
					case 4:score += 0;break;
					case 5:score += 1;break;
					case 6:score += 1;break;
					case 7:score += 1;break;
					case 8:score += 1;break;
					case 9:score += 1;break;
					}
				}
				else{
					if(!p.IsDead)p.setDead();
				}
			}
			refresh_score();
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
				WinRate Inv=new WinRate(),Jack=new WinRate();
				round_done(Inv,Jack);
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
	public void invisible_Swap(myButton x, myButton y) {
		// v = x;
		int t = x.gridx, v = x.gridy;
		//x = y;
		x.gridx = y.gridx;
		x.gridy = y.gridy;
		// y = v;
		y.gridx = t;
		y.gridy = v;
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
	public void invisible_Spin(myButton action, myButton p, int angle) {
		// if(action.isEnabled()) // //comment for test
		{
			p.setAngle(angle);
		}
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
	public void invisible_Move(myButton action, myButton sel, int _steps) {
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
	public void refresh_round(){
		Font f = new Font("SansSerif", Font.BOLD, 18);
		rounds.setEditable(false);
		rounds.setFont(f);
		String lol = "Round " + round;
		rounds.setText(lol);
		mainwindow.revalidate();
		mainwindow.repaint();
	}
	public void refresh_score(){
		Font f = new Font("SansSerif", Font.BOLD, 18);
		scores.setEditable(false);
		scores.setFont(f);
		String lol = "Jack score " + score;
		scores.setText(lol);
		mainwindow.revalidate();
		mainwindow.repaint();
	}
	public int priority_of(myButton act) {
		if (act == actions[0] || act == actions[3] || act == actions[4])
			return 1;
		else if (act == actions[1] || act == actions[2])
			return 2;
		else if (act == actions[5])
			return 3;
		else if (act == actions[7])
			return 2;
		else
			return 0;
	}
	public void random_agent() {
		List<Integer> available_list = new ArrayList<Integer>();
		int H = 0;
		for(int i=0;i<8;i++){
			if (actions[i].isEnabled()){
				available_list.add(i);
			}
		}
		Random r = new Random();
		int n = r.nextInt(available_list.size());
		H = available_list.get(n);
		actions[H].setEnabled(false);
		switch (H) {
		case 0:
			Move(actions[0], null, r.nextInt(2));
			break;
		case 1:case 2:
			Spin(actions[1],people[r.nextInt(9)],r.nextInt(4)*90);
			break;
		case 3:
			Move(actions[3], null, r.nextInt(2));
			break;
		case 4:
			Move(actions[4], null, r.nextInt(2));
			break;
		case 5:
			myButton p =null;
			switch(r.nextInt(3)){
			case 0: p = Holmes;break;
			case 1: p = Watson;break;
			case 2: p = dog;break;
			}
			Move(actions[5],p,r.nextInt(1));
			break;
		case 6:
			int s=r.nextInt(9),t=r.nextInt(9);
			while(s==t){
				s=r.nextInt(9);
			}
			Swap(people[s],people[t]);
			break;
		case 7:
			myButton b = card.pop();
			actions[7].setEnabled(false);
			if(round % 2 == 0){
				if(move == 1 || move == 4){
					switch(b.character){
					case 1:score += 2;break;
					case 2:score += 0;break;
					case 3:score += 1;break;
					case 4:score += 0;break;
					case 5:score += 1;break;
					case 6:score += 1;break;
					case 7:score += 1;break;
					case 8:score += 1;break;
					case 9:score += 1;break;
					}
				}
				else {
					if(!b.IsDead)b.setDead();
				}
			}
			else {
				if(move == 2 || move == 3){
					switch(b.character){
					case 1:score += 2;break;
					case 2:score += 0;break;
					case 3:score += 1;break;
					case 4:score += 0;break;
					case 5:score += 1;break;
					case 6:score += 1;break;
					case 7:score += 1;break;
					case 8:score += 1;break;
					case 9:score += 1;break;
					}
				}
				else{
					if(!b.IsDead)b.setDead();
				}
			}
			refresh_score();
			break;
		}
	}

	public void inv_agent(Double avg) {
		int H = -1;
		//select the highest priority action.
		for (int j = 0; j < 8; j++) {
			if (actions[j].isEnabled()) {
				if (H != -1)
					H = priority_of(actions[H]) < priority_of(actions[j]) ? j : H;
				else
					H = j;
			}
		}
		actions[H].setEnabled(false);
		//judge the most suitable result
		Double dead = 0.0;
		int ori_x,ori_y;
		switch (H) {
		case 0:
			ori_x = Watson.gridx;
			ori_y = Watson.gridy;
			invisible_Move(actions[0], null, 1);
			dead = (double)num_seen();
			invisible_Move(actions[0], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5. 
			// Other cases > 0.5.
			//current is better
			if (Math.abs(dead - avg) < Math.abs(num_seen() - avg)){
				Watson.setxy(ori_x, ori_y);
				Move(actions[0], null, 1);
				//System.out.printf("Watson move 1 steps\n");
			}
			else{
				Holmes.setxy(ori_x, ori_y);
				Move(actions[0], null, 2);
				//System.out.printf("Watson move 2 steps\n");
			}
			
			break;
		case 1:case 2:
			int s = 0,a=0;
			int[] origin_angle = new int[9];
			//save origin state.
			for(int i=0;i<9;i++){
				origin_angle[i] = people[i].angle;
			}
			//decide the best situation.
			dead = 10.0;//the worst
			for (int p = 0; p < 9; p++) {
				for(int pi = 0 ; pi < 360;pi += 90){
					if(origin_angle[p] == pi) continue;
					invisible_Spin(actions[1], people[p], pi);
					//current is worse.
					int t = num_seen();
					if(Math.abs(dead - avg) > Math.abs(t - avg)) {
						dead = (double)t ;
						s = p;
						a = pi;
					}
				}
			}
			//reverse origin state;
			for(int i=0;i<9;i++){
				people[i].setAngle(origin_angle[i]); 
			}
			Spin(actions[1],people[s],a);
			//System.out.printf("people[%d] rotate %d\n",s,a);
			break;
		case 3:
			ori_x = Holmes.gridx;
			ori_y = Holmes.gridy;
			invisible_Move(actions[3], null, 1);
			dead = (double)num_seen();
			invisible_Move(actions[3], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5. 
			// Other cases > 0.5.
			if (Math.abs(dead - avg) < Math.abs(num_seen() - avg)){
				Holmes.setxy(ori_x, ori_y);
				Move(actions[3], null, 1);
				//System.out.printf("Holmes move 1 steps\n");
			}
			else{
				Holmes.setxy(ori_x, ori_y);
				Move(actions[3], null, 2);
				//System.out.printf("Holmes move 2 steps\n");
			}
			break;
		case 4:
			ori_x = dog.gridx;
			ori_y = dog.gridy;
			invisible_Move(actions[4], null, 1);
			dead = (double)num_seen();
			invisible_Move(actions[4], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5. 
			// Other cases > 0.5.
			if (Math.abs(dead - avg) < Math.abs(num_seen() - avg)){
				dog.setxy(ori_x, ori_y);
				Move(actions[4], null, 1);
				//System.out.printf("dog move 1 steps\n");
			}
			else{
				dog.setxy(ori_x, ori_y);
				Move(actions[4], null, 2);
				//System.out.printf("dog move 2 steps\n");
			}
			break;
		case 5:
			myButton p =null;
			int sp=0;
			double t;
			//zero step
			dead = (double) num_seen();
			//Holmes's phase
			ori_x = Holmes.gridx;
			ori_y = Holmes.gridy;
			invisible_Move(actions[5], Holmes, 1);
			t = num_seen();
			if (Math.abs(dead - avg) < Math.abs(t - avg)){
				dead = (double)t;
				p = Holmes;
				sp = 1;
			}
			//reset
			Holmes.setxy(ori_x, ori_y);
			//Watson's phase
			ori_x = Watson.gridx;
			ori_y = Watson.gridy;
			invisible_Move(actions[5], Watson, 1);
			t = num_seen();
			if (Math.abs(dead - avg) < Math.abs(t - avg)){
				dead = (double)t;
				p = Watson;
				sp = 1;
			}
			//reset
			Watson.setxy(ori_x, ori_y);
			//Dog's phase
			ori_x = dog.gridx;
			ori_y = dog.gridy;
			invisible_Move(actions[5], dog, 1);
			t = num_seen();
			if (Math.abs(dead - avg) < Math.abs(t - avg)){
				dead = (double)t;
				p = dog;
				sp = 1;
			}
			//reset
			dog.setxy(ori_x, ori_y);
			//actually do action. 
			if(sp > 0)
				Move(actions[5],p,sp);
			//System.out.printf("act 5 move %d steps\n",sp);
			break;
		case 6:
			dead =0.0;
			int t1 = 0,t2 = 1;
			for(int y=0;y<9;y++){
				for(int x =y+1;x<9;x++){
					invisible_Swap(people[x],people[y]);
					t = num_seen();
					if (Math.abs(dead - avg) < Math.abs(t - avg)){
						dead = (double)t;
						t1 =x;
						t2 =y;
					}
					//reset 
					invisible_Swap(people[x],people[y]);		
				}
			}
			Swap(people[t1],people[t2]);
			//System.out.printf("%d <-> %d\n",t1,t2);
			break;
		case 7:
			myButton b = card.pop();
			actions[7].setEnabled(false);
			if(round % 2 == 0){
				if(move == 1 || move == 4){
					switch(b.character){
					case 1:score += 2;break;
					case 2:score += 0;break;
					case 3:score += 1;break;
					case 4:score += 0;break;
					case 5:score += 1;break;
					case 6:score += 1;break;
					case 7:score += 1;break;
					case 8:score += 1;break;
					case 9:score += 1;break;
					}
				}
				else {
					if(!b.IsDead)b.setDead();
				}
			}
			else {
				if(move == 2 || move == 3){
					switch(b.character){
					case 1:score += 2;break;
					case 2:score += 0;break;
					case 3:score += 1;break;
					case 4:score += 0;break;
					case 5:score += 1;break;
					case 6:score += 1;break;
					case 7:score += 1;break;
					case 8:score += 1;break;
					case 9:score += 1;break;
					}
				}
				else{
					if(!b.IsDead)b.setDead();
				}
			}
			refresh_score();
			break;
		}
		//System.out.printf("dead: %f\n", dead);
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
		mainwindow.setSize(800, 900);
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
	public void Delay(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void test_agent(int millis){
		Delay(millis);
		inv_agent(4.5);
		Delay(millis);
		inv_agent(4.5);
		Delay(millis);
		inv_agent(4.5);
		Delay(millis);
		inv_agent(4.5);
		Delay(millis);
	}
	
	public void game_start(int millis,WinRate Inv,WinRate Jack){
		int jack_agent =0,investigator_agent = 1;
		Double avg = 4.0,min = 1.0;
		//0 for random agent ;
		//1 for base agent;
		while(round!=9){
			//System.out.printf("Round:%d\n",round);
			refresh_round();
			if(round % 2 == 0){
				Delay(millis);
				switch(jack_agent){
				case 0:random_agent();break;
				case 1:inv_agent(min);break;
				}
				Delay(millis);
				switch(investigator_agent){

				case 0:random_agent();break;

				case 1:
					inv_agent(avg);
					inv_agent(avg);
					break;
				}
				Delay(millis);
				switch(jack_agent){
				case 0:random_agent();break;
				case 1:inv_agent(min);break;
				}
			}
			else {
				Delay(millis);
				switch(investigator_agent){
				case 0:random_agent();break;
				case 1:inv_agent(avg);break;
				}
				Delay(millis);
				switch(jack_agent){
				case 0:
					random_agent();
					random_agent();
					break;
				case 1:
					inv_agent(min);
					inv_agent(min);break;
				}
				Delay(millis);
				switch(investigator_agent){
				case 0:random_agent();break;
				case 1:inv_agent(avg);break;
				}
			}
			Delay(millis);
			if(round_done(Inv,Jack)){return;}
		}
	}
	public boolean round_done(WinRate Inv,WinRate Jack) {
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
		//System.out.printf("in round_done\n");
		//for(int i = 0; i < 9;i++)System.out.printf("%d 's angle :%d\n",jesus[i].character,jesus[i].angle);
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
				//System.out.printf("found jack: \njack is %d\nseen[%d]\n",jack,i);
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
			score++;
			for (int i = 0; i < 9; i++) {
				if (seen[i])
					people[bible[i]].setDead();
			}
		}
		int gameover = 0;
		for(int i = 0; i < 9 ;i++){
			if(!people[i].IsDead){
				gameover++;
				if(gameover==2)break;
			}
		}
		//determine who wins
		refresh_round();
		refresh_score();
		exchange = rotate = steps = 0;
		if(gameover == 1 && score >= 6&& !jack_seen&& round==9){
			Jack.Win();
			Inv.lose();
			System.out.printf("Jack has endured, Jack wins\n");
			return true;
		}
		else if(gameover == 1 && score >= 6&& jack_seen){
			Inv.Win();
			Jack.lose();
			System.out.printf("Though the score, Jack's seen\nHolmes wins\n");
			return true;
		}
		else if(gameover == 1 ){
			Inv.Win();
			Jack.lose();
			System.out.printf("all are dead\nHolmes wins\n");
			return true;
		}
		else if(score == 6){
			Jack.Win();
			Inv.lose();
			System.out.printf("Jack's got 6 hourglasses\nJack wins\n");
			return true;
		}
		else if(round == 9){
			Jack.Win();
			Inv.lose();
			System.out.printf("Jack wins\n");
			return true;
		}
		//System.out.printf("fuck me\n");
		return false; //not finish
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
		WinRate Inv = new WinRate(),Jack = new WinRate();
		JackGUI gui = new JackGUI();
		
		for(int i=0;i<100;i++){
			gui.onCreate();
			gui.jackid.setText("jack is ");
			gui.game_start(0,Inv,Jack);
			//gui.test_agent(1000);
		}
		Integer percent = ((Double)(Jack.get_winrate() * 100)).intValue();
		System.out.printf("Jack's winrate : %d%%\n",percent);
		percent =((Double)(Inv.get_winrate() * 100)).intValue();
		System.out.printf("Investigator's winrate : %d%%\n",percent);
		//gui.test_agent();
	}
}