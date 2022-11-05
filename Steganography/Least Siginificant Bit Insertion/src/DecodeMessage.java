import javax.swing.JOptionPane;
/**
 * decode and display message found in image;
 *
 * @author Wun Ting Chan
 */
public class DecodeMessage implements Filter {	
	
	public void filter(PixelImage pi, SnapShop s) {
		
		String msg = pi.decode();
		
		// subtracting the message ending marker "-end-"
		msg = msg.substring(0, msg.length() - 5);
		
		System.out.println(msg);
		
		s.hideWaitDialog();
		s.setMsg(msg);
		
		
		JOptionPane.showMessageDialog(s, "<html><body><p style='width: 200px;'>" + msg + "</p></body></html>",
				"Your Message: ", JOptionPane.PLAIN_MESSAGE);
		
	}
}
