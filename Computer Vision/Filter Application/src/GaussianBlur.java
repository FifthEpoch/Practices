/**
 * Filter that blurs the image by averaging each pixel's values 
 */
public class GaussianBlur implements Filter
{
	public void filter(PixelImage pi) 
	{
		//filter's factor matrix
		int[][] gaussianBlurFilter = {{1,2,1},
									  {2,4,2},
									  {1,2,1}};
		
		//addition calculation to adjust the result when calculating new RGB
		boolean scaleDown16 = true;
		boolean checkBounds = false;
		
		//apply filter
		Pixel[][] data = pi.applyFilter(gaussianBlurFilter, scaleDown16, checkBounds);
		
		//generate filtered image
		pi.setData(data);
	}
}
