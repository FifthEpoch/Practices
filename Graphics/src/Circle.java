import java.awt.Graphics;

public class Circle extends Shape {

	int radius;
	int length;
	
	public Circle() {
		super();
		this.radius = 50;
	}
	
	public Circle(String color) {
		super(color);
		this.radius = 50;
	}
	
	public Circle(String color, String size) {
		super(color, size);
		setX(250);
		setY(140);
		
		switch (size) {
		
		case "small":	this.radius = 25;
						break;
		case "medium":	this.radius = 100;
						break;
		case "big":		this.radius = 300;
						break;
		default:		this.radius = 150;
						break;
		}
		length = radius * 2;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(getColor());
		g.drawOval(x, y, length, length);
	}
	
	@Override
	public void paint(Graphics g, int x, int y) {
		g.setColor(getColor());
		g.drawOval(x, y, length, length);
	}
	
	@Override
	public void fill(Graphics g) {
		g.setColor(getColor());
		g.fillOval(x, y, length, length);
	}
	
	@Override
	public void fill(Graphics g, int x, int y) {
		g.setColor(getColor());
		g.fillOval(x, y, length, length);
	}
	
	@Override
	public void paintCoordinates(Graphics g) {
		String str = size + " " + color + " circle" + "\n" +
					"Center x: " + (x + radius) + "\n" +
					"Center y: " + (y + radius) + "\n" +
					"Radius: " + radius;
		g.setColor(stringToColor("BLACK"));
		g.drawString(str, 30, 550);
	}
	
	public boolean isPointInBound(int x, int y) {
		int dx = (int) Math.abs((getX() + radius) - x);
		int dy = (int) Math.abs((getY() + radius) - y);
		
		if(((dx * dx) + (dy * dy)) < (radius * radius)) {
			return true;
		}
		return false;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public int getRadius() {
		return radius;
	}
	
}
