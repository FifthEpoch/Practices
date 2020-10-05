/**
 * Added: 
 * 3 filters
 * AddMessage() hides a string in the image in ImagePane;
 * DecodeMessage() reverse the calculation in AddMessage();
 * ExportImage() writes a new image file "applied_image.png" from current PixelImage,
 * 		saves it to the current directory on user's computer; 
 *
 * A class to configure the SnapShop application
 *
 * @author Wun Ting Chan
 */
public class SnapShopConfiguration
{
  /**
   * Method to configure the SnapShop.  Call methods like addFilter
   * and setDefaultFilename here.
   * @param theShop A pointer to the application
   */
  public static void configure(SnapShop theShop)
  {
	String dir = System.getProperty("user.dir");  
    theShop.setDefaultFilename(dir + "/src/sample.png");
    

    // add your other filters below
    theShop.addFilter(new AddMessage(), "Hide Message");
    theShop.addFilter(new DecodeMessage(), "Decode Message");
    theShop.addFilter(new ExportImage(), "Export Image");

  }
}
