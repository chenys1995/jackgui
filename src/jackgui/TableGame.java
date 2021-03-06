package jackgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TableGame extends JackGUI {
	private final Boolean Debug = false;

	public void invisible_Spin(myButton action, myButton p, int angle) {
		// if(action.isEnabled()) // //comment for test
		{
			if (p.character != 6 || !p.IsDead)
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
		// x = y;
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
			return 1;
		else if (act == actions[6])
			return 3;
		else if (act == actions[7])
			return 1;
		else
			return 0;
	}

	public Action_pack random_agent() {
		List<Integer> available_list = new ArrayList<Integer>();
		int H = 0;
		Action_pack sel = new Action_pack();
		for (int i = 0; i < 8; i++) {
			if (actions[i].isEnabled()) {
				available_list.add(i);
			}
		}
		Random r = new Random();
		int n = r.nextInt(available_list.size());
		H = available_list.get(n);
		actions[H].setEnabled(false);
		switch (H) {
		case 0:
			sel.setInvMemMove(Action_pack.Moves, Action_pack.Watson, r.nextInt(2) + 1);
			// Move(actions[0], null, r.nextInt(2));
			break;
		case 1:
			sel.setSpin(1, r.nextInt(9), r.nextInt(4) * 90);
			break;
		case 2:
			int a = r.nextInt(4) * 90, pp = r.nextInt(9);
			while (people[pp].angle == a)
				a = r.nextInt(4) * 90;
			sel.setSpin(2, pp, a);
			// Spin(actions[1],people[r.nextInt(9)],r.nextInt(4)*90);
			break;
		case 3:
			sel.setInvMemMove(Action_pack.Moves, Action_pack.Holmes, r.nextInt(2) + 1);
			break;
		case 4:
			sel.setInvMemMove(Action_pack.Moves, Action_pack.Dog, r.nextInt(2) + 1);
			break;
		case 5:
			// myButton p =null;
			int p = 0;
			switch (r.nextInt(3)) {
			case 0:
				p = Action_pack.Holmes;
				break;
			case 1:
				p = Action_pack.Watson;
				break;
			case 2:
				p = Action_pack.Dog;
				break;
			}
			sel.setInvMemMove(Action_pack.Moves, p, r.nextInt(2));
			// Move(actions[5],p,r.nextInt(1));
			break;
		case 6:
			int s = r.nextInt(9), t = r.nextInt(9);
			while (s == t) {
				s = r.nextInt(9);
			}
			sel.setSwapCharacter(s, t);
			// Swap(people[s],people[t]);
			break;
		case 7:
			sel.setDraw();
			break;
		}
		return sel;
	}

	public Action_pack jack_agent() {
		Action_pack sel = new Action_pack();
		int H = -1;
		// select the highest priority action.
		for (int j = 0; j < 8; j++) {
			if (actions[j].isEnabled()) {
				if (H != -1)
					H = priority_of(actions[H]) < priority_of(actions[j]) ? j : H;
				else
					H = j;
			}
		}
		actions[H].setEnabled(false);
		// criteria 1
		int survival = 0;
		int ori_x, ori_y, cs = 0;
		switch (H) {
		case 0:
			ori_x = Watson.gridx;
			ori_y = Watson.gridy;
			invisible_Move(actions[0], null, 1);
			survival = Math.max(num_notseen_living(), num_seen_living());
			invisible_Move(actions[0], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5.
			// Other cases > 0.5.
			// current is better
			if (num_notseen_living() > survival && !is_Jack_be_Seen()) {
				Watson.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Watson, 2);
				// Move(actions[0], null, 1);
				// System.out.printf("Watson move 1 steps\n");
			} else if (num_seen_living() > survival && is_Jack_be_Seen()) {
				Watson.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Watson, 2);
				// System.out.printf("Watson move 2 steps\n");
			} else {
				Watson.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Watson, 1);
				// System.out.printf("Watson move 2 steps\n");
			}
			break;
		case 1:
			cs = 1;
		case 2:

			int s = 0, a = 0;
			int[] origin_angle = new int[9];
			// save origin state.
			for (int i = 0; i < 9; i++) {
				origin_angle[i] = people[i].angle;
			}
			// decide the best situation.
			survival = 0;// the worst
			for (int p = 0; p < 9; p++) {
				for (int pi = 0; pi < 360; pi += 90) {
					if (origin_angle[p] == pi)
						continue;
					invisible_Spin(actions[1], people[p], pi);
					// current is worse.
					int t = Math.max(num_notseen_living(), num_seen_living());
					if (num_notseen_living() > survival && !is_Jack_be_Seen()) {
						survival = num_notseen_living();
						s = p;
						a = pi;
					} else if (num_seen_living() > survival && is_Jack_be_Seen()) {
						survival = num_seen_living();
						s = p;
						a = pi;
					} else if (t > survival) {
						survival = t;
						s = p;
						a = pi;
					}
				}
			}
			// reverse origin state;
			for (int i = 0; i < 9; i++) {
				people[i].setAngle(origin_angle[i]);
			}
			if (cs != 1)
				cs = 2;
			sel.setSpin(cs, s, a);
			// Spin(actions[1],people[s],a);
			// System.out.printf("people[%d] rotate %d\n",s,a);
			break;
		case 3:
			ori_x = Holmes.gridx;
			ori_y = Holmes.gridy;
			invisible_Move(actions[3], null, 1);
			survival = Math.max(num_notseen_living(), num_seen_living());
			invisible_Move(actions[3], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5.
			// Other cases > 0.5.
			if (num_notseen_living() > survival && !is_Jack_be_Seen()) {
				Holmes.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Holmes, 2);
				// Move(actions[3], null, 1);
				// System.out.printf("Holmes move 1 steps\n");
			} else if (num_seen_living() > survival && is_Jack_be_Seen()) {
				Holmes.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Holmes, 2);
				// Move(actions[3], null, 2);
				// System.out.printf("Holmes move 2 steps\n");
			} else {
				Holmes.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Holmes, 1);
				// Move(actions[3], null, 2);
				// System.out.printf("Holmes move 2 steps\n");
			}
			break;
		case 4:
			ori_x = dog.gridx;
			ori_y = dog.gridy;
			invisible_Move(actions[4], null, 1);
			survival = Math.max(num_notseen_living(), num_seen_living());
			invisible_Move(actions[4], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5.
			// Other cases > 0.5.
			if (num_notseen_living() > survival && !is_Jack_be_Seen()) {
				dog.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Dog, 2);
				// Move(actions[4], null, 1);
				// System.out.printf("dog move 1 steps\n");
			} else if (num_seen_living() > survival && is_Jack_be_Seen()) {
				dog.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Dog, 2);
				// Move(actions[4], null, 2);
				// System.out.printf("dog move 2 steps\n");
			} else {
				dog.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Dog, 1);
			}
			break;
		case 5:
			myButton p = null;
			int sp = 0;
			int t;
			// zero step
			survival = Math.max(num_notseen_living(), num_seen_living());
			// Holmes's phase
			ori_x = Holmes.gridx;
			ori_y = Holmes.gridy;
			invisible_Move(actions[5], Holmes, 1);
			t = Math.max(num_notseen_living(), num_seen_living());
			if (t > survival) {
				survival = t;
				p = Holmes;
				sp = 1;
			}
			// reset
			Holmes.setxy(ori_x, ori_y);
			// Watson's phase
			ori_x = Watson.gridx;
			ori_y = Watson.gridy;
			invisible_Move(actions[5], Watson, 1);
			t = Math.max(num_notseen_living(), num_seen_living());
			if (t > survival) {
				survival = t;
				p = Watson;
				sp = 1;
			}
			// reset
			Watson.setxy(ori_x, ori_y);
			// Dog's phase
			ori_x = dog.gridx;
			ori_y = dog.gridy;
			invisible_Move(actions[5], dog, 1);
			t = Math.max(num_notseen_living(), num_seen_living());
			if (t > survival) {
				survival = t;
				p = dog;
				sp = 1;
			}
			// reset
			dog.setxy(ori_x, ori_y);
			// actually do action.
			if (sp > 0) {
				int tmp = 0;
				if (p == Watson)
					tmp = Action_pack.Watson;
				else if (p == dog)
					tmp = Action_pack.Dog;
				sel.setInvMemMove(Action_pack.Tri, tmp, sp);
				// Move(actions[5],p,sp);
			}
			// System.out.printf("act 5 move %d steps\n",sp);
			break;
		case 6:
			survival = 0;
			int t1 = 0, t2 = 1;
			for (int y = 0; y < 9; y++) {
				for (int x = y + 1; x < 9; x++) {
					invisible_Swap(people[x], people[y]);
					t = Math.max(num_notseen_living(), num_seen_living());
					if (num_notseen_living() > survival && !is_Jack_be_Seen()) {
						survival = num_notseen_living();
						t1 = x;
						t2 = y;
					} else if (num_seen_living() > survival && is_Jack_be_Seen()) {
						survival = num_seen_living();
						t1 = x;
						t2 = y;
					} else if (t > survival) {
						survival = t;
						t1 = x;
						t2 = y;
					}
					// reset
					invisible_Swap(people[x], people[y]);
				}
			}
			sel.setSwapCharacter(t1, t2);
			// Swap(people[t1],people[t2]);
			// System.out.printf("%d <-> %d\n",t1,t2);
			break;
		case 7:
			sel.setDraw();
			break;
		}
		return sel;
		// System.out.printf("dead: %f\n", dead);
	}

	public Action_pack ori_jack_agent() {
		Action_pack sel = new Action_pack();
		int H = -1;
		// select the highest priority action.
		for (int j = 0; j < 8; j++) {
			if (actions[j].isEnabled()) {
				if (H != -1)
					H = priority_of(actions[H]) < priority_of(actions[j]) ? j : H;
				else
					H = j;
			}
		}
		actions[H].setEnabled(false);
		// criteria 1
		int survival = 0;
		int ori_x, ori_y, cs = 0;
		switch (H) {
		case 0:
			ori_x = Watson.gridx;
			ori_y = Watson.gridy;
			invisible_Move(actions[0], null, 1);
			survival = Math.max(num_notseen_living(), num_seen_living());
			invisible_Move(actions[0], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5.
			// Other cases > 0.5.
			// current is better
			if (num_notseen_living() > survival && !is_Jack_be_Seen()) {
				Watson.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Watson, 2);
				// Move(actions[0], null, 1);
				// System.out.printf("Watson move 1 steps\n");
			} else if (num_notseen_living() > survival) {
				Watson.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Watson, 1);
				// System.out.printf("Watson move 2 steps\n");
			}
			break;
		case 1:
			cs = 1;
		case 2:

			int s = 0, a = 0;
			int[] origin_angle = new int[9];
			// save origin state.
			for (int i = 0; i < 9; i++) {
				origin_angle[i] = people[i].angle;
			}
			// decide the best situation.
			survival = 0;// the worst
			for (int p = 0; p < 9; p++) {
				for (int pi = 0; pi < 360; pi += 90) {
					if (origin_angle[p] == pi)
						continue;
					invisible_Spin(actions[1], people[p], pi);
					// current is worse.
					int t = Math.max(num_notseen_living(), num_seen_living());
					if (num_notseen_living() > survival && !is_Jack_be_Seen()) {
						survival = num_notseen_living();
						s = p;
						a = pi;
					} else if (t > survival) {
						survival = t;
						s = p;
						a = pi;
					}
				}
			}
			// reverse origin state;
			for (int i = 0; i < 9; i++) {
				people[i].setAngle(origin_angle[i]);
			}
			if (cs != 1)
				cs = 2;
			sel.setSpin(cs, s, a);
			// Spin(actions[1],people[s],a);
			// System.out.printf("people[%d] rotate %d\n",s,a);
			break;
		case 3:
			ori_x = Holmes.gridx;
			ori_y = Holmes.gridy;
			invisible_Move(actions[3], null, 1);
			survival = Math.max(num_notseen_living(), num_seen_living());
			invisible_Move(actions[3], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5.
			// Other cases > 0.5.
			if (num_notseen_living() > survival && !is_Jack_be_Seen()) {
				Holmes.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Holmes, 2);
				// Move(actions[3], null, 1);
				// System.out.printf("Holmes move 1 steps\n");
			} else if (num_notseen_living() > survival) {
				Holmes.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Holmes, 1);
				// Move(actions[3], null, 2);
				// System.out.printf("Holmes move 2 steps\n");
			}
			break;
		case 4:
			ori_x = dog.gridx;
			ori_y = dog.gridy;
			invisible_Move(actions[4], null, 1);
			survival = Math.max(num_notseen_living(), num_seen_living());
			invisible_Move(actions[4], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5.
			// Other cases > 0.5.
			if (num_notseen_living() > survival && !is_Jack_be_Seen()) {
				dog.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Dog, 2);
				// Move(actions[4], null, 1);
				// System.out.printf("dog move 1 steps\n");
			} else if (num_notseen_living() > survival) {
				dog.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Dog, 1);
			}
			break;
		case 5:
			myButton p = null;
			int sp = 0;
			int t;
			// zero step
			survival = Math.max(num_notseen_living(), num_seen_living());
			// Holmes's phase
			ori_x = Holmes.gridx;
			ori_y = Holmes.gridy;
			invisible_Move(actions[5], Holmes, 1);
			t = Math.max(num_notseen_living(), num_seen_living());
			if (t > survival) {
				survival = t;
				p = Holmes;
				sp = 1;
			}
			// reset
			Holmes.setxy(ori_x, ori_y);
			// Watson's phase
			ori_x = Watson.gridx;
			ori_y = Watson.gridy;
			invisible_Move(actions[5], Watson, 1);
			t = Math.max(num_notseen_living(), num_seen_living());
			if (t > survival) {
				survival = t;
				p = Watson;
				sp = 1;
			}
			// reset
			Watson.setxy(ori_x, ori_y);
			// Dog's phase
			ori_x = dog.gridx;
			ori_y = dog.gridy;
			invisible_Move(actions[5], dog, 1);
			t = Math.max(num_notseen_living(), num_seen_living());
			if (t > survival) {
				survival = t;
				p = dog;
				sp = 1;
			}
			// reset
			dog.setxy(ori_x, ori_y);
			// actually do action.
			if (sp > 0) {
				int tmp = 0;
				if (p == Watson)
					tmp = Action_pack.Watson;
				else if (p == dog)
					tmp = Action_pack.Dog;
				sel.setInvMemMove(Action_pack.Tri, tmp, sp);
				// Move(actions[5],p,sp);
			}
			// System.out.printf("act 5 move %d steps\n",sp);
			break;
		case 6:
			survival = 0;
			int t1 = 0, t2 = 1;
			for (int y = 0; y < 9; y++) {
				for (int x = y + 1; x < 9; x++) {
					invisible_Swap(people[x], people[y]);
					t = Math.max(num_notseen_living(), num_seen_living());
					if (num_notseen_living() > survival && !is_Jack_be_Seen()) {
						survival = num_notseen_living();
						t1 = x;
						t2 = y;
					} else if (t > survival) {
						survival = t;
						t1 = x;
						t2 = y;
					}
					// reset
					invisible_Swap(people[x], people[y]);
				}
			}
			sel.setSwapCharacter(t1, t2);
			// Swap(people[t1],people[t2]);
			// System.out.printf("%d <-> %d\n",t1,t2);
			break;
		case 7:
			sel.setDraw();
			break;
		}
		return sel;
		// System.out.printf("dead: %f\n", dead);
	}

	public Action_pack inv_agent() {
		int H = -1;
		// select the highest priority action.
		for (int j = 0; j < 8; j++) {
			if (actions[j].isEnabled()) {
				if (H != -1)
					H = priority_of(actions[H]) < priority_of(actions[j]) ? j : H;
				else
					H = j;
			}
		}
		actions[H].setEnabled(false);
		// judge the most suitable result
		Double dead = 0.0, comp = 0.0;
		int ori_x, ori_y, cs = 0;
		Action_pack sel = new Action_pack();
		switch (H) {
		case 0:
			ori_x = Watson.gridx;
			ori_y = Watson.gridy;
			invisible_Move(actions[0], null, 1);
			dead = Math.abs((double) num_seen() - (double) num_notseen_living());
			invisible_Move(actions[0], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5.
			// Other cases > 0.5.
			// current is better
			comp = Math.abs((double) num_seen_living() - (double) num_notseen_living());
			// System.out.printf("living:
			// %d\nseen:%d\n,comp:%f\n",num_living(),num_seen_living(),comp);
			if (dead < comp) {
				Watson.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Watson, 1);
				// System.out.printf("Watson move 1 steps\n");
			} else {
				Watson.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Watson, 2);
				// System.out.printf("Watson move 2 steps\n");
			}

			break;
		case 1:
			cs = 1;
		case 2:
			int s = 0, a = 0;
			int[] origin_angle = new int[9];
			// save origin state.
			for (int i = 0; i < 9; i++) {
				origin_angle[i] = people[i].angle;
			}
			// decide the best situation.
			dead = 10.0;// the worst
			for (int p = 0; p < 9; p++) {
				for (int pi = 0; pi < 360; pi += 90) {
					if (origin_angle[p] == pi)
						continue;
					invisible_Spin(actions[1], people[p], pi);
					// current is worse.
					double t = Math.abs((double) num_seen_living() - (double) num_notseen_living());
					// System.out.printf("living:
					// %d\nseen:%d\n,comp:%f\n",num_living(),num_seen_living(),t);
					if (dead > t) {
						dead = (double) t;
						s = p;
						a = pi;
					}
				}
			}
			// reverse origin state;
			for (int i = 0; i < 9; i++) {
				people[i].setAngle(origin_angle[i]);
			}
			if (cs != 1)
				cs = 2;
			sel.setSpin(cs, s, a);
			// Spin(actions[cs],people[s],a);
			// System.out.printf("people[%d] rotate %d\n",s,a);
			break;
		case 3:
			ori_x = Holmes.gridx;
			ori_y = Holmes.gridy;
			invisible_Move(actions[3], null, 1);
			dead = Math.abs((double) num_seen() - (double) num_notseen_living());
			invisible_Move(actions[3], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5.
			// Other cases > 0.5.
			comp = Math.abs((double) num_seen_living() - (double) num_living() / 2);
			// System.out.printf("living:
			// %d\nseen:%d\n,comp:%f\n",num_living(),num_seen_living(),comp);
			if (dead < comp) {
				Holmes.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Holmes, 1);
				// System.out.printf("Holmes move 1 steps\n");
			} else {
				Holmes.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Holmes, 2);
				// System.out.printf("Holmes move 2 steps\n");
			}
			break;
		case 4:
			ori_x = dog.gridx;
			ori_y = dog.gridy;
			invisible_Move(actions[4], null, 1);
			dead = Math.abs((double) num_seen() - (double) num_notseen_living());
			invisible_Move(actions[4], null, 2);
			// Select the most average cases |(4 or 5)-avg| = 0.5.
			// Other cases > 0.5.
			comp = Math.abs((double) num_seen_living() - (double) num_living() / 2);
			// System.out.printf("living:
			// %d\nseen:%d\n,comp:%f\n",num_living(),num_seen_living(),comp);
			if (dead < comp) {
				dog.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Dog, 1);
				// System.out.printf("dog move 1 steps\n");
			} else {
				dog.setxy(ori_x, ori_y);
				sel.setInvMemMove(Action_pack.Moves, Action_pack.Dog, 2);
				// System.out.printf("dog move 2 steps\n");
			}
			break;
		case 5:
			myButton p = null;
			int sp = 0;
			// zero step
			dead = Math.abs((double) num_seen() - (double) num_notseen_living());
			// Holmes's phase
			ori_x = Holmes.gridx;
			ori_y = Holmes.gridy;
			invisible_Move(actions[5], Holmes, 1);
			comp = Math.abs((double) num_seen_living() - (double) num_living() / 2);
			// System.out.printf("3in1\n");
			// System.out.printf("living:
			// %d\nseen:%d\n,comp:%f\n",num_living(),num_seen_living(),comp);
			if (dead > comp) {
				dead = comp;
				p = Holmes;
				sp = 1;
			}
			// reset
			Holmes.setxy(ori_x, ori_y);
			// Watson's phase
			ori_x = Watson.gridx;
			ori_y = Watson.gridy;
			invisible_Move(actions[5], Watson, 1);
			comp = Math.abs((double) num_seen_living() - (double) num_living() / 2);
			// .out.printf("living:
			// %d\nseen:%d\n,comp:%f\n",num_living(),num_seen_living(),comp);
			if (dead > comp) {
				dead = comp;
				p = Watson;
				sp = 1;
			}
			// reset
			Watson.setxy(ori_x, ori_y);
			// Dog's phase
			ori_x = dog.gridx;
			ori_y = dog.gridy;
			invisible_Move(actions[5], dog, 1);
			comp = Math.abs((double) num_seen_living() - (double) num_living() / 2);
			// System.out.printf("living:
			// %d\nseen:%d\n,comp:%f\n",num_living(),num_seen_living(),comp);
			// System.out.printf("3in1\n");
			if (dead > comp) {
				dead = comp;
				p = dog;
				sp = 1;
			}
			// reset
			dog.setxy(ori_x, ori_y);
			// actually do action.
			if (sp > 0) {
				int tmp = 0;
				if (p == Watson)
					tmp = Action_pack.Watson;
				else if (p == dog)
					tmp = Action_pack.Dog;
				sel.setInvMemMove(Action_pack.Tri, tmp, sp);
				// Move(actions[5],p,sp);
			} else {
				sel.setInvMemMove(Action_pack.Tri, 0, 0);
			}
			// System.out.printf("act 5 move %d steps\n",sp);
			break;
		case 6:
			dead = 10.0;
			int t1 = 0, t2 = 1;
			for (int y = 0; y < 9; y++) {
				for (int x = y + 1; x < 9; x++) {
					invisible_Swap(people[x], people[y]);
					comp = Math.abs((double) num_seen_living() - (double) num_living() / 2);
					// System.out.printf("living:
					// %d\nseen:%d\n,comp:%f\n",num_living(),num_seen_living(),comp);
					if (dead > comp) {
						dead = comp;
						t1 = x;
						t2 = y;
					}
					// reset
					invisible_Swap(people[x], people[y]);
				}
			}
			sel.setSwapCharacter(t1, t2);
			// Swap(people[t1],people[t2]);
			// System.out.printf("%d <-> %d\n",t1,t2);
			break;
		case 7:
			sel.setDraw();
			break;
		}
		return sel;
		// System.out.printf("dead: %f\n", dead);
	}

	public Action_pack brute_inv_agent(int Remaining_action) {
		Action_pack best = new Action_pack();
		List<Integer> available_list = new ArrayList<Integer>();
		Action_pack[][] sel = new Action_pack[Remaining_action][36];
		int[] sz = new int[Remaining_action];
		inverse_action iv = new inverse_action();
		if(Remaining_action == 1 && actions[7].isEnabled()){
			best.setDraw();
			return best;
		}
		for (int j = 0; j < 8; j++) {
			if (actions[j].isEnabled()) {
				available_list.add(j);
			}
		}
		// Find all action in remaining action
		for (int i = 0; i < Remaining_action; i++) {
			sel[i] = new Action_pack[36];
			for (int j = 0; j < available_list.size(); j++) {
				for (int k = 0; k < 36; k++) {
					sel[i][k] = new Action_pack();
					sel[i][k].transform(available_list.get(i));// save
																// initial
																// action
				}
				// actions[j].setEnabled(false);
			}
		}
		for (int i = 0; i < Remaining_action; i++) {
			switch (sel[i][0].cur_type) {
			case Action_pack.Moves:
				sz[i] = 2;
				sel[i][0].setInvMemMove(sel[i][0].cur_type, sel[i][0].inv_team_member, 1);
				sel[i][1].setInvMemMove(sel[i][1].cur_type, sel[i][1].inv_team_member, 2);
				for (int iter = 2; iter < 36; iter++) {
					sel[i][iter].setInvMemMove(sel[i][iter].cur_type, sel[i][iter].inv_team_member, 1);
				}
				break;
			case Action_pack.Spin:
				sz[i] = 27;
				int ix = 0;
				for (int j = 0; j < 9; j++) {
					int ag = people[j].angle + 90 % 360;
					for (int k = 0; k < 3; k++)
						sel[i][ix++].setSpin(sel[i][ix].numOfAct, j, (ag + (k * 90)) % 360);
				}
				break;
			case Action_pack.Swap:
				sz[i] = 36;
				int index = 0;
				for (int j = 0; j < 8; j++) {
					for (int k = j + 1; k < 9; k++) {
						sel[i][index++].setSwapCharacter(j, k);
					}
				}
				break;
			case Action_pack.Draw:
				sz[i] = 0;
				break;
			case Action_pack.Tri:
				sz[i] = 4;
				sel[i][0].setInvMemMove(sel[i][0].cur_type, 0, 0);
				sel[i][1].setInvMemMove(sel[i][1].cur_type, 0, 1);
				sel[i][2].setInvMemMove(sel[i][2].cur_type, 1, 1);
				sel[i][3].setInvMemMove(sel[i][2].cur_type, 2, 1);
				break;
			}
		}
		// Combine two action and select the best combination.
		int endp = 10;// effective number of dead people
		for (int i = 0; i < Remaining_action; i++) {// action i or j
			for (int j = 0; j < sz[i]; j++) {
				if (sel[i][0].cur_type == Action_pack.Draw)
					continue;
				// System.out.printf("The iv (%d,%d): ",i,j);
				// sel[i][j].status();
				iv = exec(sel[i][j], false);
				int res = Math.abs(num_seen_living() - num_notseen_living());
				if (endp > res) {
					endp = res;
					best = sel[i][j];
				}
				iv.exec();
			}
		}
		return best;
	}

	public Action_pack brute_jack_agent(int Remaining_action) {
		Action_pack best = new Action_pack();
		List<Integer> available_list = new ArrayList<Integer>();
		Action_pack[][] sel = new Action_pack[Remaining_action][36];
		int[] sz = new int[Remaining_action];
		if(Remaining_action == 1 && actions[7].isEnabled()){
			best.setDraw();
			return best;
		}
		inverse_action iv = new inverse_action();
		for (int j = 0; j < 8; j++) {
			if (actions[j].isEnabled()) {
				available_list.add(j);
			}
		}
		// Find all action in remaining action
		for (int i = 0; i < Remaining_action; i++) {
			sel[i] = new Action_pack[36];
			for (int j = 0; j < available_list.size(); j++) {
				for (int k = 0; k < 36; k++) {
					sel[i][k] = new Action_pack();
					sel[i][k].transform(available_list.get(i));// save
																// initial
																// action
				}
				// actions[j].setEnabled(false);
			}
		}
		for (int i = 0; i < Remaining_action; i++) {
			switch (sel[i][0].cur_type) {
			case Action_pack.Moves:
				sz[i] = 2;
				sel[i][0].setInvMemMove(sel[i][0].cur_type, sel[i][0].inv_team_member, 1);
				sel[i][1].setInvMemMove(sel[i][1].cur_type, sel[i][1].inv_team_member, 2);
				for (int iter = 2; iter < 36; iter++) {
					sel[i][iter].setInvMemMove(sel[i][iter].cur_type, sel[i][iter].inv_team_member, 1);
				}
				break;
			case Action_pack.Spin:
				sz[i] = 27;
				int ix = 0;
				for (int j = 0; j < 9; j++) {
					int ag = people[j].angle + 90 % 360;
					for (int k = 0; k < 3; k++)
						sel[i][ix++].setSpin(sel[i][ix].numOfAct, j, (ag + (k * 90)) % 360);
				}
				break;
			case Action_pack.Swap:
				sz[i] = 36;
				int index = 0;
				for (int j = 0; j < 8; j++) {
					for (int k = j + 1; k < 9; k++) {
						sel[i][index++].setSwapCharacter(j, k);
					}
				}
				break;
			case Action_pack.Draw:
				sz[i] = 0;
				break;
			case Action_pack.Tri:
				sz[i] = 4;
				sel[i][0].setInvMemMove(sel[i][0].cur_type, 0, 0);
				sel[i][1].setInvMemMove(sel[i][1].cur_type, 0, 1);
				sel[i][2].setInvMemMove(sel[i][2].cur_type, 1, 1);
				sel[i][3].setInvMemMove(sel[i][2].cur_type, 2, 1);
				break;
			}
		}
		// Combine two action and select the best combination.
		int ensp = 0;// effective number of dead people
		for (int i = 0; i < Remaining_action; i++) {// action i or j
			for (int j = 0; j < sz[i]; j++) {
				if (sel[i][0].cur_type == Action_pack.Draw)
					continue;
				// System.out.printf("The iv (%d,%d): ",i,j);
				iv = exec(sel[i][j], false);
				if (num_notseen_living() > ensp && !is_Jack_be_Seen()) {
					ensp = num_notseen_living();
					best = sel[i][j];
				} else if (num_seen_living() > ensp && is_Jack_be_Seen()) {
					ensp = num_seen_living();
					best = sel[i][j];
				} else if (num_notseen_living() > ensp) {
					ensp = num_notseen_living();
					best = sel[i][j];
				}
				iv.exec();
			}
		}
		return best;
	}

	public Action_pack[] inv_agent_predicate(int Remaining_action) {
		Action_pack[] best = new Action_pack[2];
		List<Integer> available_list = new ArrayList<Integer>();
		for (int i = 0; i < 2; i++)
			best[i] = new Action_pack();
		if (Remaining_action == 4 || Remaining_action == 1) {
			//best[0] = inv_agent();
			///*
			best[0] = brute_inv_agent(Remaining_action);
			actions[best[0].numOfAct].setEnabled(false);
			//*/
			/*
			if(Remaining_action==4){
			best[0] = first_move_inv_agent();
			actions[best[0].numOfAct].setEnabled(false);
			}
			else {
				best[0] = brute_inv_agent(1); 
				actions[best[0].numOfAct].setEnabled(false);
			}
			//*/
			return best;
		} else {
			Action_pack[][] sel = new Action_pack[Remaining_action][36];
			int[] sz = new int[Remaining_action];
			inverse_action iv = new inverse_action(), iv2 = new inverse_action();
			for (int j = 0; j < 8; j++) {
				if (actions[j].isEnabled()) {
					available_list.add(j);
				}
			}
			// Find all action in remaining action
			for (int i = 0; i < Remaining_action; i++) {
				sel[i] = new Action_pack[36];
				for (int j = 0; j < available_list.size(); j++) {
					for (int k = 0; k < 36; k++) {
						sel[i][k] = new Action_pack();
						sel[i][k].transform(available_list.get(i));// save
																	// initial
																	// action
					}
					// actions[j].setEnabled(false);
				}
			}
			// Save all variety of action to table
			for (int i = 0; i < Remaining_action; i++) {
				switch (sel[i][0].cur_type) {
				case Action_pack.Moves:
					sz[i] = 2;
					sel[i][0].setInvMemMove(sel[i][0].cur_type, sel[i][0].inv_team_member, 1);
					sel[i][1].setInvMemMove(sel[i][1].cur_type, sel[i][1].inv_team_member, 2);
					for (int iter = 2; iter < 36; iter++) {
						sel[i][iter].setInvMemMove(sel[i][iter].cur_type, sel[i][iter].inv_team_member, 1);
					}
					break;
				case Action_pack.Spin:
					sz[i] = 27;
					int ix = 0;
					for (int j = 0; j < 9; j++) {
						int ag = people[j].angle + 90 % 360;
						for (int k = 0; k < 3; k++)
							sel[i][ix++].setSpin(sel[i][ix].numOfAct, j, (ag + (k * 90)) % 360);
					}
					break;
				case Action_pack.Swap:
					sz[i] = 36;
					int index = 0;
					for (int j = 0; j < 8; j++) {
						for (int k = j + 1; k < 9; k++) {
							sel[i][index++].setSwapCharacter(j, k);
						}
					}
					break;
				case Action_pack.Draw:
					sz[i] = 0;
					break;
				case Action_pack.Tri:
					sz[i] = 4;
					sel[i][0].setInvMemMove(sel[i][0].cur_type, 0, 0);
					sel[i][1].setInvMemMove(sel[i][1].cur_type, 0, 1);
					sel[i][2].setInvMemMove(sel[i][2].cur_type, 1, 1);
					sel[i][3].setInvMemMove(sel[i][2].cur_type, 2, 1);
					break;
				}
			}
			// Combine two action and select the best combination.
			int endp = 10;// effective number of dead people
			for (int i = 0; i < Remaining_action -1; i++) {// action i or j
				for (int j = 0; j < sz[i]; j++) {
					if (sel[i][0].cur_type == Action_pack.Draw)
						continue;
					// System.out.printf("The iv (%d,%d): ",i,j);
					// sel[i][j].status();
					iv = exec(sel[i][j], false);
					for (int k = i + 1; k < Remaining_action; k++) {
						for (int l = 0; l < sz[k]; l++) {
							// System.out.printf("The iv2 (%d,%d) ",k,l);
							// sel[k][l].status();
							iv2 = exec(sel[k][l], false);
							int res = Math.abs(num_seen_living() - num_notseen_living());
							if (endp > res) {
								endp = res;
								best[0] = sel[i][j];
								best[1] = sel[k][l];
							}
							iv2.exec();
						}
					}
					iv.exec();
				}
			}
			return best;
		}
	}
	public Action_pack[] middle_opt_inv_agent(int Remaining_action) {
		Action_pack[] best = new Action_pack[2];
		List<Integer> available_list = new ArrayList<Integer>();
		for (int i = 0; i < 2; i++)
			best[i] = new Action_pack();
		if (Remaining_action == 4 || Remaining_action == 1) {
			//best[0] = inv_agent();
			/*
			best[0] = brute_inv_agent(Remaining_action);
			actions[best[0].numOfAct].setEnabled(false);
			//*/
			///*
			if(Remaining_action==4){
				best[0] = first_move_inv_agent();
				actions[best[0].numOfAct].setEnabled(false);
			}
			else {
				best[0] = brute_inv_agent(1); 
				actions[best[0].numOfAct].setEnabled(false);
			}
			//*/
			return best;
		} else {
			Action_pack[][] sel = new Action_pack[Remaining_action][36];
			int[] sz = new int[Remaining_action];
			inverse_action iv = new inverse_action(), iv2 = new inverse_action();
			for (int j = 0; j < 8; j++) {
				if (actions[j].isEnabled()) {
					available_list.add(j);
				}
			}
			// Find all action in remaining action
			for (int i = 0; i < Remaining_action; i++) {
				sel[i] = new Action_pack[36];
				for (int j = 0; j < available_list.size(); j++) {
					for (int k = 0; k < 36; k++) {
						sel[i][k] = new Action_pack();
						sel[i][k].transform(available_list.get(i));// save
																	// initial
																	// action
					}
					// actions[j].setEnabled(false);
				}
			}
			// Save all variety of action to table
			for (int i = 0; i < Remaining_action; i++) {
				switch (sel[i][0].cur_type) {
				case Action_pack.Moves:
					sz[i] = 2;
					sel[i][0].setInvMemMove(sel[i][0].cur_type, sel[i][0].inv_team_member, 1);
					sel[i][1].setInvMemMove(sel[i][1].cur_type, sel[i][1].inv_team_member, 2);
					for (int iter = 2; iter < 36; iter++) {
						sel[i][iter].setInvMemMove(sel[i][iter].cur_type, sel[i][iter].inv_team_member, 1);
					}
					break;
				case Action_pack.Spin:
					sz[i] = 27;
					int ix = 0;
					for (int j = 0; j < 9; j++) {
						int ag = people[j].angle + 90 % 360;
						for (int k = 0; k < 3; k++)
							sel[i][ix++].setSpin(sel[i][ix].numOfAct, j, (ag + (k * 90)) % 360);
					}
					break;
				case Action_pack.Swap:
					sz[i] = 36;
					int index = 0;
					for (int j = 0; j < 8; j++) {
						for (int k = j + 1; k < 9; k++) {
							sel[i][index++].setSwapCharacter(j, k);
						}
					}
					break;
				case Action_pack.Draw:
					sz[i] = 0;
					break;
				case Action_pack.Tri:
					sz[i] = 4;
					sel[i][0].setInvMemMove(sel[i][0].cur_type, 0, 0);
					sel[i][1].setInvMemMove(sel[i][1].cur_type, 0, 1);
					sel[i][2].setInvMemMove(sel[i][2].cur_type, 1, 1);
					sel[i][3].setInvMemMove(sel[i][2].cur_type, 2, 1);
					break;
				}
			}
			// Combine two action and select the best combination.
			int endp = 10;// effective number of dead people
			for (int i = 0; i < Remaining_action -1; i++) {// action i or j
				for (int j = 0; j < sz[i]; j++) {
					if (sel[i][0].cur_type == Action_pack.Draw)
						continue;
					// System.out.printf("The iv (%d,%d): ",i,j);
					// sel[i][j].status();
					iv = exec(sel[i][j], false);
					for (int k = i + 1; k < Remaining_action; k++) {
						for (int l = 0; l < sz[k]; l++) {
							// System.out.printf("The iv2 (%d,%d) ",k,l);
							// sel[k][l].status();
							iv2 = exec(sel[k][l], false);
							int res = Math.abs(num_seen_living() - num_notseen_living());
							if (endp > res) {
								endp = res;
								best[0] = sel[i][j];
								best[1] = sel[k][l];
							}
							iv2.exec();
						}
					}
					iv.exec();
				}
			}
			return best;
		}
	}

	public Action_pack[] middle_opt_jack_agent(int Remaining_action) {
		Action_pack[] best = new Action_pack[2];
		List<Integer> available_list = new ArrayList<Integer>();
		for (int i = 0; i < 2; i++)
			best[i] = new Action_pack();
		if (Remaining_action == 4 || Remaining_action == 1) {
			//best[0] = ori_jack_agent();
			//best[0] = jack_agent();
			/*
			best[0] = brute_jack_agent(Remaining_action);
			actions[best[0].numOfAct].setEnabled(false);
			//*/
			//*
			if(Remaining_action==4){
				best[0] = first_move_jack_agent();
				actions[best[0].numOfAct].setEnabled(false);
			}
			else {
				best[0] = brute_jack_agent(1); 
				actions[best[0].numOfAct].setEnabled(false);
			}
			//*/
		} 
		else {
			Action_pack[][] sel = new Action_pack[Remaining_action][36];
			int[] sz = new int[Remaining_action];
			inverse_action iv = new inverse_action(), iv2 = new inverse_action();
			for (int j = 0; j < 8; j++) {
				if (actions[j].isEnabled()) {
					available_list.add(j);
				}
			}
			// Find all action in remaining action
			for (int i = 0; i < Remaining_action; i++) {
				sel[i] = new Action_pack[36];
				for (int j = 0; j < available_list.size(); j++) {
					for (int k = 0; k < 36; k++) {
						sel[i][k] = new Action_pack();
						sel[i][k].transform(available_list.get(i));// save
																	// initial
																	// action
					}
					// actions[j].setEnabled(false);
				}
			}
			// Save all variety of action to table
			for (int i = 0; i < Remaining_action; i++) {
				switch (sel[i][0].cur_type) {
				case Action_pack.Moves:
					sz[i] = 2;
					sel[i][0].setInvMemMove(sel[i][0].cur_type, sel[i][0].inv_team_member, 1);
					sel[i][1].setInvMemMove(sel[i][1].cur_type, sel[i][1].inv_team_member, 2);
					for (int iter = 2; iter < 36; iter++) {
						sel[i][iter].setInvMemMove(sel[i][iter].cur_type, sel[i][iter].inv_team_member, 1);
					}
					break;
				case Action_pack.Spin:
					sz[i] = 27;
					int ix = 0;
					for (int j = 0; j < 9; j++) {
						int ag = people[j].angle + 90 % 360;
						for (int k = 0; k < 3; k++)
							sel[i][ix++].setSpin(sel[i][ix].numOfAct, j, (ag + (k * 90)) % 360);
					}
					break;
				case Action_pack.Swap:
					sz[i] = 36;
					int index = 0;
					for (int j = 0; j < 8; j++) {
						for (int k = j + 1; k < 9; k++) {
							sel[i][index++].setSwapCharacter(j, k);
						}
					}
					break;
				case Action_pack.Draw:
					sz[i] = 0;
					break;
				case Action_pack.Tri:
					sz[i] = 4;
					sel[i][0].setInvMemMove(sel[i][0].cur_type, 0, 0);
					sel[i][1].setInvMemMove(sel[i][1].cur_type, 0, 1);
					sel[i][2].setInvMemMove(sel[i][2].cur_type, 1, 1);
					sel[i][3].setInvMemMove(sel[i][2].cur_type, 2, 1);
					break;
				}
			}
			// Combine two action and select the best combination.
			int ensp = 0;// effective number of survival people
			for (int i = 0; i < Remaining_action -1; i++) {// action i or j
				for (int j = 0; j < sz[i]; j++) {
					if (sel[i][0].cur_type == Action_pack.Draw)
						continue;
					// System.out.printf("The iv (%d,%d): ",i,j);
					// sel[i][j].status();
					iv = exec(sel[i][j], false);
					for (int k = i + 1; k < Remaining_action; k++) {
						for (int l = 0; l < sz[k]; l++) {
							// System.out.printf("The iv2 (%d,%d) ",k,l);
							// sel[k][l].status();
							iv2 = exec(sel[k][l], false);
							int res = Math.max(num_seen_living(), num_notseen_living());
							if (num_notseen_living() > ensp && !is_Jack_be_Seen()) {
								ensp = num_notseen_living();
								best[0] = sel[i][j];
								best[1] = sel[k][l];
							} else if (num_seen_living() > ensp && is_Jack_be_Seen()) {
								ensp = num_seen_living();
								best[0] = sel[i][j];
								best[1] = sel[k][l];
							} else if (res > ensp) {
								ensp = res;
								best[0] = sel[i][j];
								best[1] = sel[k][l];
							}
							iv2.exec();

						}
					}
					iv.exec();
				}
			}
			
		}
		return best;
	}
	public Action_pack[] jack_agent_predicate(int Remaining_action) {
		Action_pack[] best = new Action_pack[2];
		List<Integer> available_list = new ArrayList<Integer>();
		for (int i = 0; i < 2; i++)
			best[i] = new Action_pack();
		if (Remaining_action == 4 || Remaining_action == 1) {
			//best[0] = ori_jack_agent();
			//best[0] = jack_agent();
			///*
			best[0] = brute_jack_agent(Remaining_action);
			actions[best[0].numOfAct].setEnabled(false);
			//*/
			///*
			if(Remaining_action==4){
				best[0] = first_move_jack_agent();
				actions[best[0].numOfAct].setEnabled(false);
			}
			else {
				best[0] = brute_jack_agent(1); 
				actions[best[0].numOfAct].setEnabled(false);
			}
			//*/
		} 
		else {
			Action_pack[][] sel = new Action_pack[Remaining_action][36];
			int[] sz = new int[Remaining_action];
			inverse_action iv = new inverse_action(), iv2 = new inverse_action();
			for (int j = 0; j < 8; j++) {
				if (actions[j].isEnabled()) {
					available_list.add(j);
				}
			}
			// Find all action in remaining action
			for (int i = 0; i < Remaining_action; i++) {
				sel[i] = new Action_pack[36];
				for (int j = 0; j < available_list.size(); j++) {
					for (int k = 0; k < 36; k++) {
						sel[i][k] = new Action_pack();
						sel[i][k].transform(available_list.get(i));// save
																	// initial
																	// action
					}
					// actions[j].setEnabled(false);
				}
			}
			// Save all variety of action to table
			for (int i = 0; i < Remaining_action; i++) {
				switch (sel[i][0].cur_type) {
				case Action_pack.Moves:
					sz[i] = 2;
					sel[i][0].setInvMemMove(sel[i][0].cur_type, sel[i][0].inv_team_member, 1);
					sel[i][1].setInvMemMove(sel[i][1].cur_type, sel[i][1].inv_team_member, 2);
					for (int iter = 2; iter < 36; iter++) {
						sel[i][iter].setInvMemMove(sel[i][iter].cur_type, sel[i][iter].inv_team_member, 1);
					}
					break;
				case Action_pack.Spin:
					sz[i] = 27;
					int ix = 0;
					for (int j = 0; j < 9; j++) {
						int ag = people[j].angle + 90 % 360;
						for (int k = 0; k < 3; k++)
							sel[i][ix++].setSpin(sel[i][ix].numOfAct, j, (ag + (k * 90)) % 360);
					}
					break;
				case Action_pack.Swap:
					sz[i] = 36;
					int index = 0;
					for (int j = 0; j < 8; j++) {
						for (int k = j + 1; k < 9; k++) {
							sel[i][index++].setSwapCharacter(j, k);
						}
					}
					break;
				case Action_pack.Draw:
					sz[i] = 0;
					break;
				case Action_pack.Tri:
					sz[i] = 4;
					sel[i][0].setInvMemMove(sel[i][0].cur_type, 0, 0);
					sel[i][1].setInvMemMove(sel[i][1].cur_type, 0, 1);
					sel[i][2].setInvMemMove(sel[i][2].cur_type, 1, 1);
					sel[i][3].setInvMemMove(sel[i][2].cur_type, 2, 1);
					break;
				}
			}
			// Combine two action and select the best combination.
			int ensp = 0;// effective number of survival people
			for (int i = 0; i < Remaining_action -1; i++) {// action i or j
				for (int j = 0; j < sz[i]; j++) {
					if (sel[i][0].cur_type == Action_pack.Draw)
						continue;
					// System.out.printf("The iv (%d,%d): ",i,j);
					// sel[i][j].status();
					iv = exec(sel[i][j], false);
					for (int k = i + 1; k < Remaining_action; k++) {
						for (int l = 0; l < sz[k]; l++) {
							// System.out.printf("The iv2 (%d,%d) ",k,l);
							// sel[k][l].status();
							iv2 = exec(sel[k][l], false);
							int res = Math.max(num_seen_living(), num_notseen_living());
							if (res > ensp) {
								ensp = res;
								best[0] = sel[i][j];
								best[1] = sel[k][l];
							}
							iv2.exec();
						}
					}
					iv.exec();
				}
			}
			
		}
		return best;
	}

	public Action_pack first_move_jack_agent() {
		Action_pack best = new Action_pack();
		Action_pack[] predicate = new Action_pack[2];
		List<Integer> available_list = new ArrayList<Integer>();
		Action_pack[][] sel = new Action_pack[4][36];
		int[] sz = new int[4];
		inverse_action iv = new inverse_action();
		inverse_action[] rev = new inverse_action[2];
		for (int i = 0; i < 2; i++) {
			predicate[i] = new Action_pack();
			rev[i] = new inverse_action();
		}
		for (int j = 0; j < 8; j++) {
			if (actions[j].isEnabled()) {
				available_list.add(j);
			}
		}
		// Find all action in remaining action
		for (int i = 0; i < 4; i++) {
			sel[i] = new Action_pack[36];
			for (int j = 0; j < available_list.size(); j++) {
				for (int k = 0; k < 36; k++) {
					sel[i][k] = new Action_pack();
					sel[i][k].transform(available_list.get(i));// save
																// initial
																// action
				}
				// actions[j].setEnabled(false);
			}
		}
		// Save all variety of action to table
		for (int i = 0; i < 4; i++) {
			switch (sel[i][0].cur_type) {
			case Action_pack.Moves:
				sz[i] = 2;
				sel[i][0].setInvMemMove(sel[i][0].cur_type, sel[i][0].inv_team_member, 1);
				sel[i][1].setInvMemMove(sel[i][1].cur_type, sel[i][1].inv_team_member, 2);
				for (int iter = 2; iter < 36; iter++) {
					sel[i][iter].setInvMemMove(sel[i][iter].cur_type, sel[i][iter].inv_team_member, 1);
				}
				break;
			case Action_pack.Spin:
				sz[i] = 27;
				int ix = 0;
				for (int j = 0; j < 9; j++) {
					int ag = people[j].angle + 90 % 360;
					for (int k = 0; k < 3; k++)
						sel[i][ix++].setSpin(sel[i][ix].numOfAct, j, (ag + (k * 90)) % 360);
				}
				break;
			case Action_pack.Swap:
				sz[i] = 36;
				int index = 0;
				for (int j = 0; j < 8; j++) {
					for (int k = j + 1; k < 9; k++) {
						sel[i][index++].setSwapCharacter(j, k);
					}
				}
				break;
			case Action_pack.Draw:
				sz[i] = 0;
				break;
			case Action_pack.Tri:
				sz[i] = 4;
				sel[i][0].setInvMemMove(sel[i][0].cur_type, 0, 0);
				sel[i][1].setInvMemMove(sel[i][1].cur_type, 0, 1);
				sel[i][2].setInvMemMove(sel[i][2].cur_type, 1, 1);
				sel[i][3].setInvMemMove(sel[i][2].cur_type, 2, 1);
				break;
			}
		}
		// Combine two action and select the best combination.
		int ensp = 0;// effective number of survival people
		for (int i = 0; i < 4; i++) {// action i or j
			for (int j = 0; j < sz[i]; j++) {
				if (sel[i][0].cur_type == Action_pack.Draw)
					continue;
				iv = exec(sel[i][j], false);
				actions[sel[i][j].numOfAct].setEnabled(false);
				predicate = inv_agent_predicate(3);
				rev[0] = exec(predicate[0], false);
				rev[1] = exec(predicate[1], false);
				int res = Math.max(num_seen_living(), num_notseen_living());
				if (num_notseen_living() > ensp && !is_Jack_be_Seen()) {
					ensp = num_notseen_living();
					best = sel[i][j];
				} else if (num_seen_living() > ensp && is_Jack_be_Seen()) {
					ensp = num_seen_living();
					best = sel[i][j];
					
				} else if (res > ensp) {
					ensp = res;
					best = sel[i][j];
				}
				rev[1].exec();
				rev[0].exec();
				iv.exec();
				actions[sel[i][j].numOfAct].setEnabled(true);
			}
		}
		return best;
	}
	public Action_pack first_move_inv_agent() {
		Action_pack best = new Action_pack();
		Action_pack[] predicate = new Action_pack[2];
		List<Integer> available_list = new ArrayList<Integer>();
		Action_pack[][] sel = new Action_pack[4][36];
		int[] sz = new int[4];
		inverse_action iv = new inverse_action();
		inverse_action[] rev = new inverse_action[2];
		for (int i = 0; i < 2; i++) {
			predicate[i] = new Action_pack();
			rev[i] = new inverse_action();
		}
		for (int j = 0; j < 8; j++) {
			if (actions[j].isEnabled()) {
				available_list.add(j);
			}
		}
		// Find all action in remaining action
		for (int i = 0; i < 4; i++) {
			sel[i] = new Action_pack[36];
			for (int j = 0; j < available_list.size(); j++) {
				for (int k = 0; k < 36; k++) {
					sel[i][k] = new Action_pack();
					sel[i][k].transform(available_list.get(i));// save
																// initial
																// action
				}
				// actions[j].setEnabled(false);
			}
		}
		// Save all variety of action to table
		for (int i = 0; i < 4; i++) {
			switch (sel[i][0].cur_type) {
			case Action_pack.Moves:
				sz[i] = 2;
				sel[i][0].setInvMemMove(sel[i][0].cur_type, sel[i][0].inv_team_member, 1);
				sel[i][1].setInvMemMove(sel[i][1].cur_type, sel[i][1].inv_team_member, 2);
				for (int iter = 2; iter < 36; iter++) {
					sel[i][iter].setInvMemMove(sel[i][iter].cur_type, sel[i][iter].inv_team_member, 1);
				}
				break;
			case Action_pack.Spin:
				sz[i] = 27;
				int ix = 0;
				for (int j = 0; j < 9; j++) {
					int ag = people[j].angle + 90 % 360;
					for (int k = 0; k < 3; k++)
						sel[i][ix++].setSpin(sel[i][ix].numOfAct, j, (ag + (k * 90)) % 360);
				}
				break;
			case Action_pack.Swap:
				sz[i] = 36;
				int index = 0;
				for (int j = 0; j < 8; j++) {
					for (int k = j + 1; k < 9; k++) {
						sel[i][index++].setSwapCharacter(j, k);
					}
				}
				break;
			case Action_pack.Draw:
				sz[i] = 0;
				break;
			case Action_pack.Tri:
				sz[i] = 4;
				sel[i][0].setInvMemMove(sel[i][0].cur_type, 0, 0);
				sel[i][1].setInvMemMove(sel[i][1].cur_type, 0, 1);
				sel[i][2].setInvMemMove(sel[i][2].cur_type, 1, 1);
				sel[i][3].setInvMemMove(sel[i][2].cur_type, 2, 1);
				break;
			}
		}
		// Combine two action and select the best combination.
		int endp = 0;// effective number of dead people
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < sz[i]; j++) {
				if (sel[i][0].cur_type == Action_pack.Draw)
					continue;
				iv = exec(sel[i][j], false);
				actions[sel[i][j].numOfAct].setEnabled(false);
				predicate = jack_agent_predicate(3);
				rev[0] = exec(predicate[0], false);
				rev[1] = exec(predicate[1], false);
				int res = Math.abs(num_seen_living() - num_notseen_living());
				if (endp > res) {
					endp = res;
					best = sel[i][j];
				}
				rev[1].exec();
				rev[0].exec();
				iv.exec();
				actions[sel[i][j].numOfAct].setEnabled(true);
			}
		}
		return best;
	}

	public void test_agent(int millis) {
		/*
		 * Delay(millis); Action_pack jk=jack_agent(); jk.status();
		 * exec(jk,true); Delay(millis); Action_pack[]
		 * bst=middle_opt_inv_agent(3); bst[0].status(); exec(bst[0],true);
		 * Delay(millis); bst[1].status(); exec(bst[1],true); Delay(millis);
		 * jk=jack_agent(); jk.status(); exec(jk,true); Delay(millis);
		 */
	}

	public void wait_act_used(int num) {
		int used = 0;
		System.out.printf("round:%d\n", round);
		while (used != num) {
			used = 0;
			for (int i = 0; i < 8; i++) {
				if (actions[i].isUsed) {
					used++;
				}
			}
			Delay(10);
		}
	}

	public void game_start(int millis, WinRate Inv, WinRate Jack) {
		int jack_agent =2, investigator_agent = 2;
 		final Boolean isVisible = true;
		Action_pack tmp = null;
		// 0 for random agent ;
		// 1 for base' agent;
		// 2 for advanced agent;
		// 3 for jack base agent;inv brute-force agent
		// 4 for jack brute force agent.
		// other for manual;
		if (jack_agent == -1) {
			displayJack.setEnabled(true);
		}
		while (round != 9) {
			// System.out.printf("Round:%d\n",round);
			refresh_round();
			if (round % 2 == 0) {
				Delay(millis);
				// Jack move 1
				switch (jack_agent) {
				case -1:
					wait_act_used(1);
					break;
				case 0:
					tmp = random_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 1:
					tmp = jack_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 2:
					Action_pack[] best = middle_opt_jack_agent(4);
					exec(best[0], isVisible);
					actions[best[0].numOfAct].setUsed(true);
					break;
				case 3:
					tmp = ori_jack_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 4:
					tmp = brute_jack_agent(4);
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setEnabled(false);
					actions[tmp.numOfAct].setUsed(true);
					break;
				}
				
				Delay(millis);
				// Inv move 2
				switch (investigator_agent) {
				case -1:
					wait_act_used(3);
					break;
				case 0:
					tmp = random_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					Delay(millis);
					tmp = random_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 1:
					tmp = inv_agent();
					if (Debug)
						tmp.status();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					Delay(millis);
					tmp = inv_agent();
					if (Debug)
						tmp.status();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 2:
					Action_pack[] best = middle_opt_inv_agent(3);
					exec(best[0], isVisible);
					actions[best[0].numOfAct].setUsed(true);
					Delay(millis);
					exec(best[1], isVisible);
					actions[best[1].numOfAct].setUsed(true);
					actions[best[0].numOfAct].setEnabled(false);
					actions[best[1].numOfAct].setEnabled(false);
					break;
				case 3:
					tmp = brute_inv_agent(3);
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setEnabled(false);
					actions[tmp.numOfAct].setUsed(true);
					Delay(millis);
					tmp = brute_inv_agent(2);
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setEnabled(false);
					actions[tmp.numOfAct].setUsed(true);
					break;
				}
				Delay(millis);
				// Jack move 1
				switch (jack_agent) {
				case -1:
					wait_act_used(4);
					break;
				case 0:
					tmp = random_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 1:
					tmp = jack_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 2:
					Action_pack[] best = middle_opt_jack_agent(1);
					exec(best[0], isVisible);
					actions[best[0].numOfAct].setUsed(true);
					break;
				case 3:
					tmp = ori_jack_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 4:
					tmp = brute_jack_agent(1);
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setEnabled(false);
					actions[tmp.numOfAct].setUsed(true);
					break;
				}
			} else {
				Delay(millis);
				// Inv move 1
				switch (investigator_agent) {
				case -1:
					wait_act_used(1);
					break;
				case 0:
					tmp = random_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 1:
					tmp = inv_agent();
					if (Debug) {
						tmp.status();
						if (tmp.cur_type == Action_pack.Spin)
							System.out.printf("origin angle: %d\n", people[tmp.c1].angle);
					}
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 2:
					Action_pack[] best = middle_opt_inv_agent(4);
					if (Debug)
						best[0].status();
					exec(best[0], isVisible);
					actions[best[0].numOfAct].setUsed(true);
					break;
				case 3:
					tmp = brute_inv_agent(4);
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setEnabled(false);
					actions[tmp.numOfAct].setUsed(true);
					break;
				}
				Delay(millis);
				// Jack move 2
				switch (jack_agent) {
				case -1:
					wait_act_used(3);
					break;
				case 0:
					tmp = random_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					Delay(millis);
					tmp = random_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 1:
					tmp = jack_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					Delay(millis);
					tmp = jack_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 2:
					Action_pack[] best = middle_opt_jack_agent(3);
					exec(best[0], isVisible);
					actions[best[0].numOfAct].setUsed(true);
					Delay(millis);
					exec(best[1], isVisible);
					actions[best[1].numOfAct].setUsed(true);
					actions[best[0].numOfAct].setEnabled(false);
					actions[best[1].numOfAct].setEnabled(false);
					break;
				case 3:
					tmp = ori_jack_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					Delay(millis);
					tmp = ori_jack_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 4:
					tmp = brute_jack_agent(3);
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setEnabled(false);
					actions[tmp.numOfAct].setUsed(true);
					Delay(millis);
					tmp = brute_jack_agent(2);
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setEnabled(false);
					actions[tmp.numOfAct].setUsed(true);
					break;
				}
				Delay(millis);
				switch (investigator_agent) {
				case -1:
					wait_act_used(4);
					break;
				case 0:
					tmp = random_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 1:
					tmp = inv_agent();
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setUsed(true);
					break;
				case 2:
					Action_pack[] best = middle_opt_inv_agent(1);
					exec(best[0], isVisible);
					actions[best[0].numOfAct].setUsed(true);
					break;
				case 3:
					tmp = brute_inv_agent(1);
					exec(tmp, isVisible);
					actions[tmp.numOfAct].setEnabled(false);
					actions[tmp.numOfAct].setUsed(true);
					break;
				}
			}
			for (int i = 0; i < 8; i++) {
				actions[i].setUsed(false);
			}
			Delay(millis);
			if (round_done(Inv, Jack)) {
				return;
			}
		}
	}

	public static void main(String[] args) {
		WinRate Inv = new WinRate(), Jack = new WinRate();
		int times = 10000;
		// *
		for (int count = 0; count < times; count++) {
			TableGame gui = new TableGame();
			//gui.onCreate(TableGame.visible);
			gui.onCreate(TableGame.invisible);
			gui.jackid.setText("jack is ");
			gui.game_start(TableGame.no_delay, Inv, Jack);
			//gui.game_start(TableGame.interval, Inv, Jack);
			// gui.test_agent(1000);
		}
		// */

		Double percent = (Jack.get_winrate() * 100);
		System.out.printf("Jack's winrate : %2.2f%%\n", percent);
		percent = (Inv.get_winrate() * 100);
		System.out.printf("Investigator's winrate : %2.2f%%\n", percent);
		// gui.test_agent();
		return;
	}

	public boolean[] seen_after_move(int act, int step, int which) {
		// try to implement which will be seen after one of the investigators
		// moves
		boolean[] result;
		result = new boolean[9];
		for (int i = 0; i < 9; i++)
			result[i] = false;
		if (act == 0) {// watson

		} else if (act == 3) {// holmes

		} else if (act == 4) {// dog

		} else if (act == 5) {// joker
			if (which == 1) {// watson

			} else if (which == 2) {// holmes

			} else if (which == 3) {// dog

			} else
				System.out.printf("in seen_after_move, parameter which is wrong : %d\n", which);
		} else
			System.out.printf("in seen_after_move, parameter act is wrong : %d\n", act);
		return result;
	}

	public int num_notseen_living() {
		return num_living() - num_seen_living();
	}

	private boolean Go_Up(int i, int j) {
		while (i > 0 && i < 4 && j > 0 && j < 4) {
			for (int s = 0; s < 9; s++) {
				if (people[s].gridx == i && people[s].gridy == j) {
					if (people[s].angle == 180)
						return false;
					else {
						j--;
					}
				}
			}
		}
		if ((Holmes.gridx == i && Holmes.gridy == j) || (Watson.gridx == i && Watson.gridy == j)
				|| (dog.gridx == i && dog.gridy == j))
			return true;
		else
			return false;
	}

	private boolean Go_Down(int i, int j) {
		while (i > 0 && i < 4 && j > 0 && j < 4) {
			for (int s = 0; s < 9; s++) {
				if (people[s].gridx == i && people[s].gridy == j) {
					if (people[s].angle == 0)
						return false;
					else {
						j++;
					}
				}
			}
		}
		if ((Holmes.gridx == i && Holmes.gridy == j) || (Watson.gridx == i && Watson.gridy == j)
				|| (dog.gridx == i && dog.gridy == j))
			return true;
		else
			return false;
	}

	private boolean Go_Left(int i, int j) {
		while (i > 0 && i < 4 && j > 0 && j < 4) {
			for (int s = 0; s < 9; s++) {
				if (people[s].gridx == i && people[s].gridy == j) {
					if (people[s].angle == 90)
						return false;
					else {
						i--;
					}
				}
			}
		}
		if ((Holmes.gridx == i && Holmes.gridy == j) || (Watson.gridx == i && Watson.gridy == j)
				|| (dog.gridx == i && dog.gridy == j))
			return true;
		else
			return false;
	}

	private boolean Go_Right(int i, int j) {
		while (i > 0 && i < 4 && j > 0 && j < 4) {
			for (int s = 0; s < 9; s++) {
				if (people[s].gridx == i && people[s].gridy == j) {
					if (people[s].angle == 270)
						return false;
					else {
						i++;
					}
				}
			}
		}
		if ((Holmes.gridx == i && Holmes.gridy == j) || (Watson.gridx == i && Watson.gridy == j)
				|| (dog.gridx == i && dog.gridy == j))
			return true;
		else
			return false;
	}

	public boolean is_Jack_be_Seen() {
		boolean isSeen = false;
		int i = 0, j = 0, k = 0;
		for (int s = 0; s < 9; s++) {
			if (people[s].character == jack) {
				k = s;
				i = people[s].gridx;
				j = people[s].gridy;
			}
		}
		switch (people[k].angle) {
		case 0:
			isSeen |= Go_Up(i, j);
			isSeen |= Go_Left(i, j);
			isSeen |= Go_Right(i, j);
			break;
		case 90:
			isSeen |= Go_Up(i, j);
			isSeen |= Go_Right(i, j);
			isSeen |= Go_Down(i, j);
			break;
		case 180:
			isSeen |= Go_Left(i, j);
			isSeen |= Go_Right(i, j);
			isSeen |= Go_Down(i, j);
			break;
		case 270:
			isSeen |= Go_Up(i, j);
			isSeen |= Go_Left(i, j);
			isSeen |= Go_Down(i, j);
			break;
		}
		return isSeen;
	}

	public boolean[] seen_if_such(int[] inv_pos) {
		// who will be seen if investigators are arranged with these positions
		/*
		 * inv_pos: 0 1 2 11 x x x 3 10 x x x 4 9 x x x 5 8 7 6
		 */
		if (inv_pos.length > 3)
			System.out.printf("in seen_if_such, parameter inv_pos length is wrong : %d\n", inv_pos.length);
		myButton[] jesus = new myButton[9];
		int[] bible = new int[9];
		for (int i = 0; i < 9; i++) {
			if (people[i].gridx == 1 && people[i].gridy == 1) {
				jesus[0] = people[i];
				bible[0] = i;
				continue;
			} else if (people[i].gridx == 2 && people[i].gridy == 1) {
				jesus[1] = people[i];
				bible[1] = i;
				continue;
			} else if (people[i].gridx == 3 && people[i].gridy == 1) {
				jesus[2] = people[i];
				bible[2] = i;
				continue;
			} else if (people[i].gridx == 1 && people[i].gridy == 2) {
				jesus[3] = people[i];
				bible[3] = i;
				continue;
			} else if (people[i].gridx == 2 && people[i].gridy == 2) {
				jesus[4] = people[i];
				bible[4] = i;
				continue;
			} else if (people[i].gridx == 3 && people[i].gridy == 2) {
				jesus[5] = people[i];
				bible[5] = i;
				continue;
			} else if (people[i].gridx == 1 && people[i].gridy == 3) {
				jesus[6] = people[i];
				bible[6] = i;
				continue;
			} else if (people[i].gridx == 2 && people[i].gridy == 3) {
				jesus[7] = people[i];
				bible[7] = i;
				continue;
			} else if (people[i].gridx == 3 && people[i].gridy == 3) {
				jesus[8] = people[i];
				bible[8] = i;
				continue;
			}
		}
		boolean seen[] = new boolean[9];
		for (int i = 0; i < 9; i++)
			seen[i] = false;

		for (int i = 0; i < inv_pos.length; i++) {
			switch (inv_pos[i]) {
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
			case (1):
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
			case (2):
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
			case (3):
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
			case (4):
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
			case (5):
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
			case (6):
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
			case (7):
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
			case (8):
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
			case (9):
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
			case (10):
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
			case (11):
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
			default:
				System.out.printf("in seen_if_such, parameter inv_pos[%d] is wrong : %d\n", i, inv_pos[i]);
			}
		}
		boolean[] result = new boolean[9];

		for (int i = 0; i < 9; i++) {
			if (seen[i])
				result[bible[i]] = true;
			else
				result[bible[i]] = false;
		}
		return result;
	}

	public inverse_action exec(Action_pack act, Boolean isVisible) {
		myButton p = null, ins = actions[act.numOfAct];
		inverse_action iv = new inverse_action();
		switch (act.inv_team_member) {
		case Action_pack.Holmes:
			p = this.Holmes;
			break;
		case Action_pack.Watson:
			p = this.Watson;
			break;
		case Action_pack.Dog:
			p = this.dog;
			break;
		}
		iv.setMove(p);
		switch (act.cur_type) {
		case Action_pack.Moves:
			if (isVisible)
				Move(ins, p, act.steps);
			else
				invisible_Move(ins, p, act.steps);
			break;
		case Action_pack.Swap:
			iv.setSwap(people[act.c1], people[act.c2]);
			if (isVisible)
				Swap(people[act.c1], people[act.c2]);
			else
				invisible_Swap(people[act.c1], people[act.c2]);
			break;
		case Action_pack.Spin:
			iv.setSpin(people[act.c1]);
			if (isVisible)
				Spin(ins, people[act.c1], act.angle);
			else
				invisible_Spin(ins, people[act.c1], act.angle);
			break;
		case Action_pack.Draw:
			Draw();
			break;
		case Action_pack.Tri:
			if (isVisible)
				Move(ins, p, act.steps);
			else
				invisible_Move(ins, p, act.steps);
			break;
		}
		return iv;
	}
}