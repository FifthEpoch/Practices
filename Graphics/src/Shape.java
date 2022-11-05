import java.awt.Graphics;
import java.awt.Color;
import java.lang.reflect.Field;

public abstract class Shape {
	
	protected String color;
	protected Color c;
	
	protected int centerX;
	protected int centerY;
	
	protected int x;
	protected int y;
	
	protected String size;
	
	public Shape(String color, String size) {
		this.color = color;
		this.size = size;
		this.c = stringToColor(color);
		this.x = 100;
		this.y = 100;
	}
	
	public Shape(String color) {
		
		this.color = color;
		this.c = stringToColor(color);
		this.x = 250;
		this.y = 200;
	}
	
	public Shape() {
		this.color = "BLACK";
		this.c = stringToColor(color);
		this.x = 250;
		this.y = 200;
	}
	
	abstract void paint(Graphics g);
	abstract void paint(Graphics g, int x, int y);
	
	abstract void fill(Graphics g);
	abstract void fill(Graphics g, int x, int y);
	
	abstract boolean isPointInBound(int x, int y);
	
	abstract void paintCoordinates(Graphics g);
	
	public Color stringToColor(String color) {
		if(color == null) {
			return Color.BLACK;
		}
		try {
		    Field field = Color.class.getField(color);
		    return (Color)field.get(null);
		    
		} catch (Exception e) {
		    switch (color) {
		    case "purple":	return Color.getHSBColor(0.77f, 0.87f, 1f);
		    case "violet":	return Color.getHSBColor(0.69f, 0.91f, 0.91f);
		    case "teal":	return Color.getHSBColor(0.53f, 1f, 0.55f); 
		    case "brown":	return Color.getHSBColor(0.068f, 1f, 0.50f); 
		    case "tan":		return Color.getHSBColor(0.09f, 0.27f, 0.73f);
		    case "navy":	return Color.getHSBColor(0.62f, 0.84f, 0.49f);
		    case "mint":	return Color.getHSBColor(0.51f, 0.39f, 0.80f);
		    case "forest":	return Color.getHSBColor(0.5f, 0.94f, 0.24f);
		    case "coral":	return Color.getHSBColor(1f, 0.8f, 0.9f);
		    case "maroon":	return Color.getHSBColor(0.94f, 1f, 0.5f);
		    default:		return Color.black;
		    }
		}
	}
	
	public Color getColor() {
		return c;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getCenterX() {
		return centerX;
	}
	
	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}
	
	public int getCenterY() {
		return centerY;
	}
	
	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}
	
}
