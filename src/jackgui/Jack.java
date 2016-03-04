package jackgui;
import java.util.Random;
class District{
	//class member
	public char id;
	private int deadroad;//only one road is dead
    private boolean dead = false;
	//class constructer
	public District(char c){
		this.id=c;
		Random rand = new Random();
		this.deadroad = rand.nextInt(4) * 90;
	}
	// set the direction of card; degree = 0~270
    public void set_direction(int deg){
    	this.deadroad = deg;
    } 
    //observer 
    public int get_deg(){
    	return deadroad;
    } 
    public void die() {
        if(dead == false){
		System.console().printf("%c dies", id);
        this.dead = true;
		}
    }; // turn over the card
    public boolean isDie() {
        return dead;
    };
}
class Holmes_team{
	
	
}
class Desktop{
	
	
	
}
public class Jack {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//JackGUI gui = new JackGUI();
		District d = new District('d');
		d.set_direction(90);
		//System.out.printf("%d\n", d.get_deg());
	}

}