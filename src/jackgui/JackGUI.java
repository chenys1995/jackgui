package jackgui;
import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.List;;
public class JackGUI {
	private static JFrame mainwindow = new JFrame();
	public JackGUI(){
		
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
		JButton[] people = new JButton[9];
		JButton[] actions = new JButton[8];
		JButton[] empty = new JButton[13];
		int c=0,act=0,n=0;
		for(int i =0; i<7;i++){	
			for(int j=0;j<5;j++){
				GridBagConstraints cons = new GridBagConstraints();
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
					people[c] = new JButton(new ImageIcon(path));
					mainwindow.add(people[c],cons);
					c++;
				}
				else if(i==1 &&  j==0){
					JButton Holmes = new JButton(new ImageIcon("res/Holmes.png"));
					mainwindow.add(Holmes,cons);
				}
				else if(i==2 &&  j==4){
					JButton Watson = new JButton(new ImageIcon("res/Watson.png"));
					mainwindow.add(Watson,cons);
				}
				else if(i==3 &&  j==0){
					JButton dog = new JButton(new ImageIcon("res/dog.png"));
					mainwindow.add(dog,cons);
				}
				else if(i>4 && j<4){
					actions[act] = new JButton(new ImageIcon("res/act"+act+".png"));
					mainwindow.add(actions[act],cons);
					act++;
				}
				else if(i<5 && j<5){
					empty[n]= new JButton(new ImageIcon("res/empty.png"));
					empty[n].setEnabled(false);
					mainwindow.add(empty[n],cons);
					n++;
				}
			}
		}
		people[0].setEnabled(false);
		people[1].setEnabled(false);
		people[2].setEnabled(false);
		actions[0].setEnabled(false);
		actions[3].setEnabled(false);
		actions[6].setEnabled(false);
		actions[5].setEnabled(false);
		actions[1].setEnabled(false);
		actions[2].setEnabled(false);
		actions[7].setEnabled(false);
		actions[4].setEnabled(false);
		mainwindow.setVisible(true);
	}
}
