/**
 * Filter that amplifies edges
 */
public class Laplacian implements Filter 
{
	public void filter(PixelImage pi) 
	{
		//filter's factor matrix
		int[][] laplacianFilter = {{-1,-1,-1},
								   {-1, 8,-1},
								   {-1,-1,-1}};
		
		//addition calculation to adjust the result when calculating new RGB
		boolean scaleDown16 = false;
		boolean checkBounds = true;
		
		//apply filter
		Pixel[][] data = pi.applyFilter(laplacianFilter, scaleDown16, checkBounds);
		
		//generate filtered image
		pi.setData(data);
	}
}
