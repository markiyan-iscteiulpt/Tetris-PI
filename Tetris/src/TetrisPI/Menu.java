package TetrisPI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Menu extends JFrame{
	
	private static final long serialVersionUID = 1136823974110127369L;
	private final File main_screen = new File("images/main_screen.png");
	private final File control = new File("images/controls.png");
	private Tetris tetris;
	private String version = "v 0.01 Beta";
	
	private JPanel menu;

	
	//Main_screen
	private JLabel main_menu_label;
	private JLabel currentUser;
	private JLabel vers;
	private JButton quick_game;
	private JButton select_user;
	private JButton high_scores;
	private JButton controls;

	//Controls_screen
	private JLabel control_label;
	
	
	public Menu() throws IOException {
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		menu = new JPanel();
		menu.setLayout(null);
		menu.setPreferredSize(new Dimension(400, 450));
		
		
		BufferedImage myPicture = ImageIO.read(main_screen);
		main_menu_label = new JLabel(new ImageIcon(myPicture));
		main_menu_label.setBounds(0, 0, 400, 450);
		
//		BufferedImage myPicture1 = ImageIO.read(main_screen);
//		main_menu_label = new JLabel(new ImageIcon(myPicture));
//		main_menu_label.setBounds(0, 0, 400, 450);
//		
//		BufferedImage myPicture2 = ImageIO.read(main_screen);
//		main_menu_label = new JLabel(new ImageIcon(myPicture));
//		main_menu_label.setBounds(0, 0, 400, 450);
		
		BufferedImage myPicture3 = ImageIO.read(control);
		control_label = new JLabel(new ImageIcon(myPicture3));
		control_label.setBounds(0, 0, 400, 450);
		
		currentUser = new JLabel("Markiyan");
		currentUser.setFont(new Font("Dialog", Font.PLAIN, 30));
		currentUser.setForeground (Color.WHITE);
		currentUser.setBounds(140, 100, 200, 100);
		
		vers = new JLabel(version);
		vers.setForeground (Color.WHITE);
		vers.setBounds(320, 380, 100, 100);
		
		quick_game = new JButton();
		quick_game.setOpaque(false);
		quick_game.setContentAreaFilled(false);
		quick_game.setBorderPainted(false);
		quick_game.setBounds(80, 195, 240, 40);
		
		select_user = new JButton();
		select_user.setOpaque(false);
		select_user.setContentAreaFilled(false);
		select_user.setBorderPainted(false);
		select_user.setBounds(80, 250, 240, 40);
		
		high_scores = new JButton();
		high_scores.setOpaque(false);
		high_scores.setContentAreaFilled(false);
		high_scores.setBorderPainted(false);
		high_scores.setBounds(80, 307, 240, 40);
		
		controls = new JButton();
		controls.setOpaque(false);
		controls.setContentAreaFilled(false);
		controls.setBorderPainted(false);
		controls.setBounds(80, 363, 240, 40);
		
		
		
				
				
		ActionListener button_listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == quick_game){
					quick_game();
				}else if(e.getSource() == select_user){
					select_user();
				}else if(e.getSource() == high_scores){
					high_scores();
				}else if(e.getSource() == controls){
					controls();
				}
				
			}
		};
		
		quick_game.addActionListener(button_listener);
		select_user.addActionListener(button_listener);
		high_scores.addActionListener(button_listener);
		controls.addActionListener(button_listener);
		
		
		menu.add(vers);
		menu.add(currentUser);
		menu.add(quick_game);
		menu.add(select_user);
		menu.add(high_scores);
		menu.add(controls);
		menu.add(main_menu_label);
		
		add(menu, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	
	
	private void quick_game(){
		FrameRunnable t = new FrameRunnable(this);
		new Thread(t).start();
		this.tetris = t.getTetris();
	}
	
	private void select_user(){
		System.out.println("select_user");
	}
	
	private void high_scores(){
		System.out.println("high_scores");
	}
	
	private void controls(){
		menu.removeAll();
		menu.add(control_label);
		this.repaint();
	}
}
