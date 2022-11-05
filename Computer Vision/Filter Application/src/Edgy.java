
public class Edgy implements Filter
{

	
	public void filter(PixelImage pi) 
	{
		//filter's factor matrix
		int[][] EdgyFilter = {{-1,-1,-1},
							  {-1, 9,-1},
							  {-1,-1,-1}};
		
		//addition calculation to adjust the result when calculating new RGB
		boolean scaleDown16 = false;
		boolean checkBounds = true;
		
		//apply filter
		Pixel[][] data = pi.applyFilter(EdgyFilter, scaleDown16, checkBounds);
		
		//generate filtered image
		pi.setData(data);
	}	
}
