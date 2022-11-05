import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class Viewer {

	private static final int WIDTH  = 850;
	private static final int HEIGHT = 600;
	private static final int LEFT_X = 200;
	private static final int TOP_Y  = 100;
	
	private static final Integer BOTTOM = 0;
	private static final Integer TOP = 1;
	
	public static void main(String[] args) {
		
		JFrame frame  = new JFrame("Viewer");
		
		UserInput input = new UserInput();
		ControlPanel panel = new ControlPanel();
		
		frame.add(panel);
		frame.add(input);
		
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocation(LEFT_X, TOP_Y);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}	
}
