package jackgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TableGame extends JackGUI {
	public void invisible_Spin(myButton action, myButton p, int angle) {
		// if(action.isEnabled()) // //comment for test
		{
			p.setAngle(angle);
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
	
	public int priority_of(myButton act) {
		if (act == actions[0] || act == actions[3] || act == actions[4])
			return 1;
		else if (act == actions[1] || act == actions[2])
			return 2;
		else if (act == actions[5])
			return 3;
		else if (act == actions[7])
			return 1;
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
					if(!b.IsDead){
						b.setDead();
						mainwindow.revalidate();
						mainwindow.repaint();
					}
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
					if(!b.IsDead){
						b.setDead();
						mainwindow.revalidate();
						mainwindow.repaint();
					}
				}
			}
			refresh_score();
			break;
		}
	}
	public void jack_agent(Double avg) {
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
			if (Math.abs(dead - avg) > Math.abs(num_seen() - avg)){
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
					if(Math.abs(dead - avg) < Math.abs(t - avg)) {
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
			if (Math.abs(dead - avg) > Math.abs(num_seen() - avg)){
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
			if (Math.abs(dead - avg) > Math.abs(num_seen() - avg)){
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
			if (Math.abs(dead - avg) > Math.abs(t - avg)){
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
			if (Math.abs(dead - avg) > Math.abs(t - avg)){
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
			if (Math.abs(dead - avg) > Math.abs(t - avg)){
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
					if (Math.abs(dead - avg) > Math.abs(t - avg)){
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
					if(!b.IsDead){
						b.setDead();
						mainwindow.revalidate();
						mainwindow.repaint();
					}
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
					if(!b.IsDead){
						b.setDead();
						mainwindow.revalidate();
						mainwindow.repaint();
					}
				}
			}
			refresh_score();
			break;
		}
		//System.out.printf("dead: %f\n", dead);
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
				Watson.setxy(ori_x, ori_y);
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
					if(!b.IsDead){
						b.setDead();
						mainwindow.revalidate();
						mainwindow.repaint();
					}
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
					if(!b.IsDead){
						b.setDead();
						mainwindow.revalidate();
						mainwindow.repaint();
					}
				}
			}
			refresh_score();
			break;
		}
		//System.out.printf("dead: %f\n", dead);
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
		int jack_agent =1,investigator_agent = 1;
		Double avg = 4.0;
		//0 for random agent ;
		//1 for base agent;
		while(round!=9){
			//System.out.printf("Round:%d\n",round);
			refresh_round();
			if(round % 2 == 0){
				Delay(millis);
				switch(jack_agent){
				case 0:random_agent();break;
				case 1:jack_agent(avg);break;
				}
				Delay(millis);
				switch(investigator_agent){
				case 0:random_agent();break;
				case 1:
					inv_agent(avg);
					Delay(millis);
					inv_agent(avg);
					break;
				}
				Delay(millis);
				switch(jack_agent){
				case 0:random_agent();break;
				case 1:jack_agent(avg);break;
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
					Delay(millis);
					random_agent();
					break;
				case 1:
					jack_agent(avg);
					Delay(millis);
					jack_agent(avg);
					break;
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
	

	public static void main(String[] args) {
		WinRate Inv = new WinRate(),Jack = new WinRate();
		int times = 10000;
		//*
		for(int count=0;count<times;count++){
			TableGame gui = new TableGame();
			gui.onCreate(TableGame.invisible);
			gui.jackid.setText("jack is ");
			gui.game_start(TableGame.no_delay,Inv,Jack);//delay control: 0 ms
			//gui.test_agent(1000);
			
		}//*/

		Integer percent = ((Double)(Jack.get_winrate() * 100)).intValue();
		System.out.printf("Jack's winrate : %d%%\n",percent);
		percent =((Double)(Inv.get_winrate() * 100)).intValue();
		System.out.printf("Investigator's winrate : %d%%\n",percent);
		//gui.test_agent();
		return;
	}
	public boolean [] seen_after_move(int act, int step,int which){
		//try to implement which will be seen after one of the investigators moves
		boolean[] result;
		result = new boolean[9];
		for(int i = 0; i < 9; i++)result[i]=false;
		if(act == 0){//watson
			
		}
		else if(act == 3){//holmes
			
		}
		else if(act == 4){//dog
			
		}
		else if(act == 5){//joker
			if(which==1){//watson
				
			}
			else if(which == 2){//holmes
				
			}
			else if(which == 3){//dog
				
			}
			else  System.out.printf("in seen_after_move, parameter which is wrong : %d\n", which);
		}
		else System.out.printf("in seen_after_move, parameter act is wrong : %d\n", act);
		return result;
	}
	public boolean[] seen_if_such(int[] inv_pos){
		//who will be seen if investigators are arranged with these positions
		/*
		inv_pos:
		    0 1 2
		11x  x x 3    
		10x  x x 4
		 9 x  x x 5
		    8 7 6
		 */
		if(inv_pos.length>3)
			System.out.printf("in seen_if_such, parameter inv_pos length is wrong : %d\n", inv_pos.length);
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
		boolean seen[] = new boolean[9];
		for (int i = 0; i < 9; i++)
			seen[i] = false;
		
		for(int i = 0; i < inv_pos.length; i++){
			switch(inv_pos[i]){
				case (0):
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
					break;
				case(1):
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
					break;
				case(2):
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
					break;
				case(3):
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
					break;
				case(4):
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
					break;
				case(5):
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
					break;
				case(6):
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
					break;
				case(7):
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
					break;
				case(8):
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
					break;
				case(9):
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
					break;
				case(10):
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
					break;
				case(11):
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
					break;
				default:System.out.printf("in seen_if_such, parameter inv_pos[%d] is wrong : %d\n", i,inv_pos[i]);
			}
		}
		boolean[] result = new boolean[9];
		
		for(int i = 0; i < 9; i++){
			if(seen[i]) result[bible[i]]=true;
			else result[bible[i]]=false;
		}
		return result;
	}
	
	

}
