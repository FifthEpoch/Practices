import java.awt.Graphics2D;
import java.awt.image.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * Provides an interface to a picture as an array of Pixels;
 * 
 * Added:
 * [method] applyMsg(String msg);
 * [method] alterRGB(char c, int rgb);
 * [method] decode();
 * [method] return0or1(int rgb);
 * [method] getRaster(Pixel[][] data);
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
  
public int getColor(int row, int col) {
	  
	return this.myImage.getRGB(row, col);
	  
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
   * Takes Pixel[][] by getData();
   * Embedded msg 1 char at a time, using 3 pixels to hide 1 char;
   * calculate each pixel's new RGB calue with alterRGB();
   *
   * @param msg The string user entered in MessageLoader's text area;
   *
   * @return Pixel[][] with altered RGB value based on value derived from msg;
   */
  public Pixel[][] applyMsg(String msg) {
	  
	  Pixel[][] data = this.getData();
	  
	  // how many batch of 3 pixels per row?
	  int batchPerRow = (int)this.width / 3;
	  System.out.println("Batches per row: " + batchPerRow);
	  
	  // how many characters are we embedding?
	  for (int i = 0; i <= msg.length() - 1; i++) {
		  
		  // find current row
		  int row = (int) (Math.ceil( (i + 1) / batchPerRow ));
		  System.out.println("Current row: " + row + " i: " + i);

		  Pixel[] batch = new Pixel[3];
		  int startingPixel = 0; 
		  
		  // store the 3 pixels next-in-line
		  for (int j = 0; j <= 2; j++) {
			 
			 batch[j] = data[row][(((i+1) - (batchPerRow * row)) * 3) + j];
			  
		  }
		  
		  // get binary string for current character
		  String charInBinary = Integer.toBinaryString(msg.charAt(i));
		  System.out.println("char: " + msg.charAt(i) + " binary: " + charInBinary);
		  
		  // upper and lower case letters returns as a 7 digit value;
		  // special characters and numbers return as a 6 digit value;
		  // unifying all Binary Strings to have the length of 7 characters;
		  if(charInBinary.length() < 7) {
			  for (int a = charInBinary.length(); a <= 7; a++) {
				  charInBinary = "0" + charInBinary;
			  }
		  } 
		  
		  // deal with return key "U+E006"
		  
		  
		  // altering RGB values of the 3 pixels in batch
		  // process value of Red & Blue in first and third pixel;
		  // process value of Red, Green, & Blue in second pixel;
		  // see [method] alterRGB;
		  for(int k = 0; k <= 2; k++) {
			  
			  if(k % 2 == 0) {
				  
				  batch[k].red = alterRGB(charInBinary.charAt((int)(k * 2.5)), batch[k].red);
				  batch[k].blue = alterRGB(charInBinary.charAt((int)((k * 2.5) + 1)), batch[k].blue);
				  
			  } else {
				  
				  batch[k].red = alterRGB(charInBinary.charAt(2), batch[k].red);
				  batch[k].green = alterRGB(charInBinary.charAt(3), batch[k].green);
				  batch[k].blue = alterRGB(charInBinary.charAt(4), batch[k].blue);
			  }
			  
			  // setting the new Pixel values to data[][]
			  batch[k] = data[row][(((i+1) - (batchPerRow * row)) * 3) + k];
		  } 
	  }
	  return data;
  }
  
  /**
   * returns a new rbg value whose rightmost digit in binary conforms to the char 0/1;
   *
   * @param char c the digit from charInBinary we are currently trying to embed in image;
   * 
   * @param int rgb the Red, Green, or Blue value we are trying to alter;
   * 
   * @return rgb a new int value for either the Red, Green, or Blue value of a pixel;
   */
  public int alterRGB(char c, int rgb) {
	
	  if (c % 2 != rgb % 2) {
		  rgb += 1;
	  }
	  
	  return rgb;
  }
  
  /**
   * Construct a string by reading the LSB of certain pixels in the image;
   * 
   * @return string Hidden message in the image;
   */
  public String decode() {
	  
	  Pixel[][] data = this.getData();
	  String msg = "";
	  
	  for(int row = 0; row <= this.getHeight() - 1; row++) {
		  
		  // reading 3 pixels at a time since we used 3 pixels to embed a char in applyMsg()
		  int batchInRow = (int)this.getWidth() / 3;
		  for(int batchCol = 0; batchCol <= batchInRow - 1; batchCol ++) {
			  
			  String binary = "";
			  
			  // construct a string containing a 7 digit binary from parsing values in the 3 pixels;
			  // see return0or1();
			  binary = binary + return0or1(data[row][batchCol * 3].red)
			  				  + return0or1(data[row][batchCol * 3].blue)
			  
			  				  + return0or1(data[row][batchCol * 3 + 1].red)
			  				  + return0or1(data[row][batchCol * 3 + 1].green)
			  				  + return0or1(data[row][batchCol * 3 + 1].blue)
			  
			  				  + return0or1(data[row][batchCol * 3 + 2].red)
			  				  + return0or1(data[row][batchCol * 3 + 2].blue);
			  
			  // convert binary string into its decimal representation
			  int i = Integer.parseInt(binary, 2);
			  
			  // convert int into its char representation in ASCII 
			  char s = (char)i;
			  msg = msg + s;
			  
			  // break out of loop if message ending marker is detected
			  if(msg.endsWith("-end-")) {
				  break;
			  }
		  }
		  if(msg.endsWith("-end-")) {
			  break;
		  }
	  }
	  return msg;
  }

  /**
   * parse rgb value to determine if it;s even or odd;
   * if even, that value's LSB is 0;
   * if odd, that value's LSB is 1;
   *
   * @param int rgb the Red, Green, or Green value of a certain pixel;
   * 
   * @return string 0 or 1;
   */
  public String return0or1(int rgb) {
	
	if(rgb % 2 == 0) {
		return "0";
	} else {
		return "1";
	}
  }

  
  /**
   * returns a raster of the current image;
   *
   * @return Raster;
   */
  public Raster getRaster(){
	  
	  Pixel[][] data = this.getData();
	  int[] pixelValues = new int[3]; 
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
	  return wr;
  	}
}  
