
public class Unsharp implements Filter
{
	public void filter(PixelImage pi) 
	{
		//filter's factor matrix
		int[][] UnsharpMask = {{-1,-2,-1},
							   {-2,28,-2},
							   {-1,-2,-1}};
		
		//addition calculation to adjust the result when calculating new RGB
		boolean scaleDown16 = true;
		boolean checkBounds = true;
		
		//apply filter
		Pixel[][] data = pi.applyFilter(UnsharpMask, scaleDown16, checkBounds);
		
		//generate filtered image
		pi.setData(data);
	}
}
