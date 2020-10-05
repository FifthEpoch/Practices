import java.text.DecimalFormat;

public class Eulers_Method {
	
	static DecimalFormat df = new DecimalFormat("0.0000000");
	
	// Initial x and y
	static double x = 1.0;
	static double y = 0.0;
	
	// stepSize is Delta x, FinalX is the desired y value at this x
	static double stepSize = 0.3;
	static double FinalX = 1.6;
	
	
	public static void main (String[] args) {
		
		for (int i = 0; i <= ((int)(FinalX - x) / stepSize) + 2; i++) {
			
			System.out.println("x = " + df.format(x) + ", y = " + df.format(y) + " , dydx =  " + df.format((- 2 + (2 * x) + (4 * y))));
			
			y = (- 2 + (2 * x) + (4 * y)) * stepSize + y;
			x += stepSize;
		}
	}	
}
