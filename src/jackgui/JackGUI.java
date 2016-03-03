package jackgui;
import java.awt.GridBagLayout;
import java.awt.*;
import javax.swing.*;
public class JackGUI {
	private static JFrame mainwindow = new JFrame();
	public JackGUI(){
		
	}
	public void onCreate() {
		mainwindow.setSize(640, 480);
		mainwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainwindow.getContentPane().setLayout(new GridBagLayout());
		char name = 'a';
		for(int i =0; i<5;i++){	
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
					JButton people_a = new JButton(String.valueOf(name));
					mainwindow.add(people_a,cons);
					name++;
				}
				else {
					JButton empty = new JButton("empty");
					mainwindow.add(empty,cons);
				}
			}
		}
		mainwindow.setVisible(true);
	}
}
