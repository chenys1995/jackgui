package jackgui;

public class Action_pack {
	public static final int Holmes = 0,Watson = 1,Dog = 2; 
	public static final int Moves = 0;
	public static final int Swap = 1;
	public static final int Spin = 2;
	public static final int Draw = 3;
	public static final int Tri = 4; 
	int cur_type,numOfAct,steps,c1,c2,angle,inv_team_member;
	
	Action_pack(){
		cur_type =0;
		numOfAct = 0;
		c1 = 0;
		c2 = 0;
		inv_team_member = 0;
		steps = 1;
		angle = 0;
	}
	public void status(){
		switch(numOfAct){
		case 0:
			System.out.printf("This action is move Watson %d steps\n",steps);
			break;
		case 1:
		case 2:
			System.out.printf("This action is spin %d,%d deg\n", 
					c1,angle);
			break;
		case 3:
			System.out.printf("This action is move Holmes %d steps\n",steps);
			break;
		case 4:
			System.out.printf("This action is move Dog %d steps\n",steps);
			break;
		case 5:
			System.out.printf("This action is Tri :%d steps\n",inv_team_member,
					steps);
			break;
		case 6:
			System.out.printf("This action is swap %d %d\n",c1, c2);
			break;
		case 7:
			System.out.printf("This action is draw\n");
			break;
		}
	}
	public void transform(final int index){
		numOfAct = index;
		switch(index){
		case 0:
			cur_type = Moves;
			inv_team_member = Watson;
			break;
		case 1:cur_type = Spin;break;
		case 2:
			cur_type = Spin;
			break;
		case 3:
			cur_type = Moves;
			inv_team_member = Holmes;
			break;
		case 4:
			cur_type = Moves;
			inv_team_member = Dog;
			break;
		case 5:
			cur_type = Tri;
			break;
		case 6:
			cur_type = Swap;
			break;
		case 7:
			cur_type = Draw;
			break;
		}
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
	public void setSpin(int num,int ch1,int angles){
		cur_type = Spin;
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
