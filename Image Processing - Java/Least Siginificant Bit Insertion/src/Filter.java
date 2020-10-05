/**
 * Defines a filter method to modify images
 * This simple interface is COMPLETE. Don't change it.
 * 
 * Added:
 * SnapShop as a parameter to gain access to method getMsg(); 
 */

public interface Filter
{
  /**
   * Modify the image according to your algorithm
   * @param  theImage The image to modify
   * 
   * @param  s The SnapShop allows filter to access getMsg() in the SnapShop class;
   * 
 * @throws Exception 
   */
  void filter(PixelImage theImage, SnapShop s)  ;
}
