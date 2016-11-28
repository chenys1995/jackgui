package jackgui;

public class WinRate {
	int win,lose;
	Double winrate;
	public WinRate(){
		win = 0;
		lose = 0;
		winrate = 0.0;
	}
	public void Win(){
		win++;
		winrate =(double)win/(win+lose);
	}
	public void lose(){
		lose++;
		winrate =(double)win/(win+lose);
	}
	public Double get_winrate(){
		return winrate;
	}
}
