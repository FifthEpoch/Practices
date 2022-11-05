/**
 * embed msg in image
 *
 * @author Wun Ting Chan
 */
public class AddMessage implements Filter {
	
	// importing SnapShop to get message String
	
	public void filter(PixelImage pi, SnapShop s) {
		
		// adding "/end/" to indicate end of String
		
		String msg = s.getMsg();
		
		Pixel [][] data = pi.applyMsg(msg);
		
		/** [test block]
		* 
		*for (int row = 0; row <= pi.getHeight() / 10; row++) {
		*	for(int col = 0; col <= pi.getWidth() / 10; col++) {
		*		System.out.println(data[row][col].red);
		*		System.out.println(data[row][col].green);
		*		System.out.println(data[row][col].blue);
		*	}
		*}
		*/
		
		pi.setData(data);
	}	
}
