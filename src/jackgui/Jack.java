package jackgui;
import java.util.Random;
import java.util.*;
/*
class District{
	//class member
	public char id;
	private int deadroad;//only one road is dead
    private boolean dead = false;
	//class constructor
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
		//System.console().printf("%c dies", id);
        this.dead = true;
		}
    }; // turn over the card
    public boolean isDie() {
        return dead;
    };
}
class Holmes_team{
	private int pos_hol;
	private int pos_dog;
	private int pos_wat;
	private static final int Holmes =0;
	private static final int  Watson= 1;
	private static final int  Dog=2;
    Holmes_team(){
    	pos_hol = 4;
    	pos_dog = 8;
    	pos_wat = 12;
    }
    public void set_character_pos(int partner,int pos)//set posistion to the character
    {
    	switch(partner){
	    	case Holmes: pos_hol =pos;break;
	    	case Watson :pos_wat = pos;break;
	    	case Dog : pos_dog=pos;break;
    	}
    }
    void get_all_pos(){
    	// unused function 
    }
    int get_hol() {
        return pos_hol;
    }
    int get_wat() {
        return pos_wat;
    }
    int get_dog() {
        return pos_dog;
    }


	
}

class Desktop{
	class pair<T1,T2>{
		public T1 first;
		public T2 second;
	}
	public Desktop(){
		
	}
	public void print_map(){
		
	}
	public void print_status(){
		
	}
	public void print_act(Desktop desk, boolean act_lock, boolean used)//not sure pointer or reference stuff
	{
		
	}
	public char get_jack(){
		return 'c';
	}
	public void suspect_check(int round){
		
	}
	public boolean inOver(){
		return true;
	}
	
	private District[] people = new District[9];
	private char jack_id;
	private Holmes_team hol_tm;
    private Stack<char[]> Card;//not sure
    private int score;
    private pair<String,String> ActCard[];//need to construct a class later
	private boolean gameover;
	
	
}*/
public class Jack {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JackGUI gui = new JackGUI();
		gui.onCreate();
		//System.out.printf("%d\n", d.get_deg());
	}

}