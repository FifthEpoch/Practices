import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class UserInput extends ControlPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	JTextArea textArea;
	JButton button;

	public UserInput() {
		textArea = new JTextArea(1,50);
		add(textArea);
		
		button = new JButton("Enter");
		button.addActionListener(this);
		add(button);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			shape = null;
			setFullStr(textArea.getText().trim());
			parseFullStr();
			
		} catch (Exception incorrectFormat) {
			JOptionPane.showMessageDialog(this, "Incorrect format: \ntry something like \"medium pink circle\" or \"big blue rectangle\".",
				"Error", JOptionPane.ERROR_MESSAGE);
		}
		Graphics pen = getGraphics();
		paintComponent(pen);
	}
	
}
