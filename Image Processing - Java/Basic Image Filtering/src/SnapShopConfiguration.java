/** Filtered added: 
 * Flip Vertical 
 * Negative 
 * Gaussian Blur
 * Laplacian
 * Unsharp
 * Edgy
 * 
 * Weighted pixel calcultion in PixelImage.java
 * All filters imeplements Filter interface

 * A class to configure the SnapShop application
 *
 * @author Wun Ting Chan
 * @version v1 06/01/2020
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

    theShop.setDefaultFilename("/Users/tingting/eclipse-workspace/SnapShop_Chan/src/billg.jpg");
    theShop.addFilter(new FlipHorizontalFilter(), "Flip Horizontal");
    

    // add your other filters below
    theShop.addFilter(new FlipVerticalFilter(), "Flip Vertical");
    theShop.addFilter(new NegativeFilter(), "Negative");
    theShop.addFilter(new GaussianBlur(), "Gaussian Blur");
    theShop.addFilter(new Laplacian(),  "Laplacian");
    theShop.addFilter(new Unsharp(), "Unsharp Mask");
    theShop.addFilter(new Edgy(), "Edgy");
  }
}
