import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
/**
 * Filter that saves a .png to user's computer in the current dir;
 *
 * @author Wun Ting Chan
 */
public class ExportImage implements Filter {
	
	public void filter(PixelImage pi, SnapShop s) {
		
		BufferedImage img = new BufferedImage(pi.getWidth(), pi.getHeight(), BufferedImage.TYPE_INT_RGB);
		Raster r = pi.getRaster();
		img.setData(r);

		File outputfile = new File("applied_image.png");
	
		try {
			ImageIO.write(img, "png", outputfile);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(outputfile.getPath());
		
		s.hideWaitDialog();
		JOptionPane.showMessageDialog(s, "<html><body><p style='width: 200px;'>" + outputfile.getPath() + "</p></body></html>",
				"Saved as", JOptionPane.PLAIN_MESSAGE);
		
	}
}
