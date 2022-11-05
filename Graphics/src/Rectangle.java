import java.awt.Graphics;

public class Rectangle extends Shape{
	
	int width;
	int height;
	
	public Rectangle() {
		super();
		this.width = 200;
		this.height = 300;
	}
	
	public Rectangle(String color) {
		super(color);
		this.width = 200;
		this.height = 300;
	}
	
	public Rectangle(String color, String size) {
		super(color, size);
		setX(300);
		setY(200);
		
		switch (size) {
		
		case "small":	this.width = 50;
						this.height = 60;
						break;
		case "medium":	this.width = 200;
						this.height = 130;
						break;
		case "big":		this.width = 400;
						this.height = 300;
						break;
		default:		this.width = 350;
						this.height = 300;
						break;
		}
		
	}

	@Override
	void paint(Graphics g) {
		g.setColor(getColor());
		g.drawRect(getX(), getY(), width, height);
	}
	
	@Override
	void paint(Graphics g, int x, int y) {
		g.setColor(getColor());
		g.drawRect(x, y, width, height);
	}
	
	@Override
	void fill(Graphics g) {
		g.setColor(getColor());
		g.fillRect(getX(), getY(), width, height);
	}
	
	@Override
	void fill(Graphics g, int x, int y) {
		g.setColor(getColor());
		g.fillRect(x, y, width, height);
	}
	
	@Override
	void paintCoordinates(Graphics g) {
		String str = size + " " + color + " rectangle" + "\n" +
				"Center x: " + (x + width/2) + "\n" +
				"Center y: " + (y + height/2) + "\n" +
				"Height: " + height + "\n" +
				"Width: " + width;
		g.setColor(stringToColor("BLACK"));
		g.drawString(str, 30, 550);
	}
	
	public boolean isPointInBound(int x, int y) {
		if(x >= this.x && x <= this.x + width &&
				y >= this.y && y <= this.y + height) {
			return true;
		} else {
			return false;
		}
	}

}
