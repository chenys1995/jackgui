package jackgui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;;
public class JackGUI implements ActionListener{
	class myButton extends JButton{
		public myButton(ImageIcon imageIcon) {
			// TODO Auto-generated constructor stub
			super(imageIcon);
		}
		public void setxy(int x,int y){
			gridx=x;
			gridy=y;
		}
		
		int gridx,gridy;
	}
	private static JFrame mainwindow = new JFrame();
	myButton[] people = new myButton[9];
	myButton[] actions = new myButton[8];
	myButton[] empty = new myButton[13];
	myButton Holmes = new myButton(new ImageIcon("res/Holmes.png"));
	myButton Watson = new myButton(new ImageIcon("res/Watson.png"));
	myButton dog = new myButton(new ImageIcon("res/dog.png"));
	GridBagConstraints cons = new GridBagConstraints();
	boolean[] Duplicate = new boolean[3];//[Holmes, Watson, Dog]
	public JackGUI(){
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == Holmes || e.getSource() == Watson || e.getSource() == dog){
			int gridx = ((myButton)e.getSource()).gridx,gridy = ((myButton)e.getSource()).gridy;
			this.movepos((myButton)e.getSource());
			cons.gridx = ((myButton)e.getSource()).gridx;
			cons.gridy = ((myButton)e.getSource()).gridy;
			if((myButton)e.getSource() == Holmes){
					if(dog.gridx == cons.gridx && dog.gridy == cons.gridy){
						Duplicate[0]= true;
						Duplicate[2]= true;
					}
					else if(Watson.gridx == cons.gridx && Watson.gridy == cons.gridy){
						Duplicate[0]= true;
						Duplicate[1]= true;
					}
			}
			mainwindow.remove((myButton)e.getSource());
			mainwindow.add((myButton)e.getSource(),cons);
			mainwindow.revalidate();
			mainwindow.repaint();
		}
    }
	public void movepos(myButton b){
		//1st row :right
		if(b.gridy == 0 && b.gridx >= 0 && b.gridx < 4) {
			b.gridx = b.gridx + 1;
			//b.gridy=b.gridy;
		}
		//4th row :left
		else if(b.gridy == 4 && b.gridx > 0 && b.gridx <= 4) {
			b.gridx=b.gridx - 1;
			//b.gridy=b.gridy - 1;
		}
		//1st column:up
		else if(b.gridx == 0 && b.gridy <= 4 && b.gridy > 0){
			//b.gridx=b.gridx;
			b.gridy=b.gridy - 1;
		}
		//4th column:down
		else if(b.gridx == 4 && b.gridy < 4 && b.gridy >= 0){
			//b.gridx=b.gridx;
			b.gridy=b.gridy + 1;
		}
	}
	public void onCreate() {
		mainwindow.setSize(800, 800);
		mainwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainwindow.getContentPane().setLayout(new GridBagLayout());
		List<Integer> order=new ArrayList<Integer>();
		for(int i =0; i<9;i++){	
			order.add(i);
		}
		java.util.Collections.shuffle(order);
		
		int c=0,act=0,n=0;
		for(int i =0; i<7;i++){	
			for(int j=0;j<5;j++){
	            cons.gridx = j;
				cons.gridy = i;
	            cons.gridwidth = 1;
	            cons.gridheight = 1;
	            cons.weightx = 0;
	            cons.weighty = 0;
	            cons.fill = GridBagConstraints.BOTH;
	            cons.anchor = GridBagConstraints.CENTER;
				if(i>0 && i<4 && j>0 && j<4){
					String path = "res/"+((Integer) order.toArray()[c]+1)+".png";
					people[c] = new myButton(new ImageIcon(path));
					people[c].setxy(j,i);
					mainwindow.add(people[c],cons);
					c++;
				}
				else if(i==1 &&  j==0){
					Holmes.setxy(j,i);
					Holmes.addActionListener(this);
					mainwindow.add(Holmes,cons);
				}
				else if(i==2 &&  j==4){
					Watson.setxy(j,i);
					Watson.addActionListener(this);
					mainwindow.add(Watson,cons);
				}
				else if(i==3 &&  j==0){
					dog.setxy(j,i);
					dog.addActionListener(this);
					mainwindow.add(dog,cons);
				}
				else if(i>5 && j<4){
					actions[act] = new myButton(new ImageIcon("res/act"+act+".png"));
					actions[act].setxy(j,i);
					mainwindow.add(actions[act],cons);
					act++;
				}
			}
		}
		
		mainwindow.setVisible(true);
	}
}
