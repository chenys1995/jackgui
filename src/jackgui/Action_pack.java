package jackgui;

public class Action_pack {
	public static final int Holmes = 0,Watson = 1,Dog = 2; 
	public static final int Moves = 0;
	public static final int Swap = 1;
	public static final int Rotate = 2;
	public static final int Draw=3;
	public static final int Tri =4; 
	int cur_type,numOfAct,steps,c1,c2,angle,inv_team_member;
	
	Action_pack(){
		cur_type =0;
		numOfAct = 0;
		c1 = 0;
		c2 = 0;
		inv_team_member = 0;
		steps = 0;
		angle = 0;
	}
	public void setInvMemMove(int ActType,int inv_team_member,int steps){
		cur_type = ActType;
		if(ActType == Tri)numOfAct =5;
		else if(ActType == Moves){
			switch(inv_team_member){
			case Holmes:numOfAct = 3;break;
			case Watson:numOfAct=0;break;
			case Dog:numOfAct =4;break;
			default:break;
			}
		}
		this.inv_team_member = inv_team_member;
		this.steps = steps;
	}
	public void setRotation(int num,int ch1,int angles){
		cur_type = Rotate;
		numOfAct = num;
		c1 = ch1;
		angle = angles;
	}
	public void setSwapCharacter(int ch1,int ch2){
		cur_type = Swap;
		numOfAct = 6;
		c1 = ch1;
		c2 = ch2;
	}
	public void setDraw(){
		cur_type = Draw;
		numOfAct = 7;
	}
}
