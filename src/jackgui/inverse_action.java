package jackgui;

public class inverse_action {
	int origin_x,origin_y,angle,t1,t2;
	myButton target,target2;
	private int ins;
	inverse_action(){
		ins = 0;
		origin_x = 0;
		origin_y = 0;
		angle = 0;
		target =null;
	}
	public void setMove(myButton t){
		ins = 0;
		target = t;
		origin_x = t.gridx;
		origin_y = t.gridy;
	}
	public void setSpin(myButton p){
		ins = 1;
		target = p;
		angle = p.angle;
	}
	public void setSwap(myButton t,myButton t2){
		if(t == null || t2 == null) return;
		ins = 2;
		target = t;
		target2 = t2;
	}
	public void exec(){
		switch(ins){
		case 0:resetMove();break;
		case 1:resetSpin();break;
		case 2:resetSwap();break;
		}
	}
	private void resetMove(){
		target.gridx = origin_x;
		target.gridy = origin_y;
	}
	private void resetSpin(){
		target.angle = angle;
	}
	private void resetSwap(){
		// v = x;
		int t = target.gridx, v = target.gridy;
		//x = y;
		target.gridx = target2.gridx;
		target.gridy = target2.gridy;
		// y = v;
		target2.gridx = t;
		target2.gridy = v;
	}
}
