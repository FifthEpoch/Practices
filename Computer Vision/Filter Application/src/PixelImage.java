import java.awt.image.*;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Provides an interface to a picture as an array of Pixels
 */
public class PixelImage
{
  private BufferedImage myImage;
  private int width;
  private int height;

  /**
   * Map this PixelImage to a real image
   * @param bi The image
   */
  public PixelImage(BufferedImage bi)
  {
    // initialise instance variables
    this.myImage = bi;
    this.width = bi.getWidth();
    this.height = bi.getHeight();
  }

  /**
   * Return the width of the image
   */
  public int getWidth()
  {
    return this.width;
  }

  /**
   * Return the height of the image
   */
  public int getHeight()
  {
    return this.height;
  }

  /**
   * Return the BufferedImage of this PixelImage
   */
  public BufferedImage getImage()
  {
    return this.myImage;
  }

  /**
   * Return the image's pixel data as an array of Pixels.  The
   * first coordinate is the x-coordinate, so the size of the
   * array is [width][height], where width and height are the
   * dimensions of the array
   * @return The array of pixels
   */
  public Pixel[][] getData()
  {
    Raster r = this.myImage.getRaster();
    Pixel[][] data = new Pixel[r.getHeight()][r.getWidth()];
    int[] samples = new int[3];

    for (int row = 0; row < r.getHeight(); row++)
    {
      for (int col = 0; col < r.getWidth(); col++)
      {
        samples = r.getPixel(col, row, samples);
        Pixel newPixel = new Pixel(samples[0], samples[1], samples[2]);
        data[row][col] = newPixel;
      }
    }

    return data;
  }

  /**
   * Set the image's pixel data from an array.  This array matches
   * that returned by getData().  It is an error to pass in an
   * array that does not match the image's dimensions or that
   * has pixels with invalid values (not 0-255)
   * @param data The array to pull from
   */
  public void setData(Pixel[][] data)
  {
    int[] pixelValues = new int[3];     // a temporary array to hold r,g,b values
    WritableRaster wr = this.myImage.getRaster();

    if (data.length != wr.getHeight())
    {
      throw new IllegalArgumentException("Array size does not match");
    }
    else if (data[0].length != wr.getWidth())
    {
      throw new IllegalArgumentException("Array size does not match");
    }

    for (int row = 0; row < wr.getHeight(); row++)
    {
      for (int col = 0; col < wr.getWidth(); col++)
      {
    	pixelValues[0] = data[row][col].red;
        pixelValues[1] = data[row][col].green;
        pixelValues[2] = data[row][col].blue;
        wr.setPixel(col, row, pixelValues);
      }
    }
  }
  
  /**
   * Performs weighted calculation on image based on the filter matrix passed,
   * weighted calculation is perform on each pixel using its 8 neighbouring pixels, ignores pixels on edges,
   * checks for 2 booleans for further calculation on the resulting pixel values,
   * returns a Pixel array with image's new pixel data 
   * 
   * @param filter A 3x3 matrix used in weighted calculation on each pixel of the image
   * @param scaleDown16 If result pixel values need to be scale down by a factor of 16
   * @param checkBounds If result pixel values need to be adjusted to fit with the 0-255 bounds
   */
  public Pixel[][] applyFilter(int[][] filter, boolean scaleDown16, boolean checkBounds)
  {
	  Pixel[][] data = this.getData();
	  Pixel[][] temp = this.getData();
			
	  for(int row = 1; row < this.getHeight() - 2; row++ )
	  {
		  for(int col = 1; col < this.getWidth() - 2; col++ )
		  {
			  //reset rgb array at every new pixel
			  int[] rgb = new int[3];
					
			  for (int i = -1; i <= 1; i++) 
			  {
				  for (int j = -1; j <= 1; j++) 
				  {
					  rgb[0] += (temp[row + i][col + j].red) * (filter[i+1][j+1]);
					  rgb[1] += (temp[row + i][col + j].green) * (filter[i+1][j+1]);
					  rgb[2] += (temp[row + i][col + j].blue) * (filter[i+1][j+1]);
				  }
			  }
			  
			  //scale value down by factor of 16
			  if (scaleDown16) 
			  {
				  for(int k = 0; k <= 2; k++) 
				  {
					  rgb[k] = rgb[k] / 16;
				  }
			  }
			  
			//catching for out of bound values
			  if (checkBounds) 
			  {
				  for (int k = 0; k <= 2; k++) 
				  {
					  if(rgb[k] > 255) 
					  {
						  rgb[k] = 255;
					  } else if(rgb[k] < 0) 
					  {
						  rgb[k] = 0;
					  }
				  }
			  }
			  //store final value to int array "data"
			  data[row][col].red = rgb[0];
			  data[row][col].green = rgb[1];
			  data[row][col].blue = rgb[2];
			}
	  }
	  return data;
  }
  
}
