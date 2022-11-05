import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class ControlPanel extends JPanel implements MouseMotionListener, MouseListener {
	
	private static final long serialVersionUID = 5L;
	
	protected Shape shape;
	
	protected String fullStr;
	protected String colorStr;
	protected String shapeStr;
	protected String sizeStr;
	
	private boolean isInBound;
	private boolean cache_isInBound;
	private boolean isDragged;
	private boolean dx_dy_stored;
	
	private int dx;
	private int dy;
	
	//****************************************************
	//*	Sets up this Panel and Listener for mouse events.
	//* Panel listens for mouse events
	//****************************************************
	public ControlPanel() {
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	//****************************************************
	//*	Create new circle at the location, whenever the
	//* mouse button is pressed and repaints.
	//****************************************************
	public void mouseDragged(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		
		Graphics pen = getGraphics();
		//update your information here
		pen.setColor(Color.orange);
		pen.fillOval(x, y, 12, 4);
		
		if (isInBound) {
			isDragged = true;
			if (!dx_dy_stored) {
				dx = x - shape.getX();
				dy = y - shape.getY();
				dx_dy_stored = true;
			}
			shape.setX(x - dx);
			shape.setY(y - dy);
			repaint();
		}
	}
	
	//****************************************************
	//*	Provides empty definitions for the unused
	//* mouse methods of the Listener interface.
	//****************************************************
	public void mouseMoved(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		
		Graphics pen = getGraphics();
		
		//check to see if there is a shape present, if so we need to send it over to paintComponent to see if a hover state is needed
		if(shape != null) {
			
			dx_dy_stored = false;
			
			if (shape.isPointInBound(x, y) != isInBound) {
				isInBound = !isInBound;
			} 
			if (cache_isInBound != isInBound || isDragged == true) {
				repaint();
			}
		}
	}
	
	//********************************************************
	//*	Create new shape based on user input,
	//* fill/draw a shape when curses goes in and out of bound
	//********************************************************
	public void paintComponent(Graphics pen) {
		
		super.paintComponent(pen);
		
		if (shape == null) {
			if (sizeStr != null && shapeStr != null && colorStr != null) {
				if (shapeStr.equalsIgnoreCase("rectangle")) {
					shape = new Rectangle(colorStr, sizeStr);
	    			shape.paint(pen);
				} else if (shapeStr.equalsIgnoreCase("circle")) {
					shape = new Circle(colorStr, sizeStr);
    				shape.paint(pen);
				}
			}
		} else { //shape != null
			if (isInBound) {
				shape.fill(pen);
				shape.paintCoordinates(pen);
				if (isDragged) {
					if (shapeStr.equalsIgnoreCase("rectangle")) {
		    			shape.fill(pen, shape.getX(), shape.getY());
					} else if (shapeStr.equalsIgnoreCase("circle")) {
	    				shape.fill(pen, shape.getX(), shape.getY());
					}
					isDragged = false;
				}
				cache_isInBound = true;
			} else { // curse is not in bound
				if (isInBound != cache_isInBound) { 	//only repaint if isInBound has changed state to avoid repainting at every call
					shape.paint(pen);
				}
				cache_isInBound = false;
			}
		}
	}	
	
	public void setFullStr(String fullStr) {
		this.fullStr = fullStr;
	}
	
	public void parseFullStr() {
		this.sizeStr = fullStr.toLowerCase().substring(0, fullStr.indexOf(" "));
		this.colorStr = fullStr.toLowerCase().substring(fullStr.indexOf(" ") + 1, fullStr.lastIndexOf(" "));
		this.shapeStr = fullStr.toLowerCase().substring(fullStr.lastIndexOf(" ") + 1);
	}
	
	// draws a star when mouse click is detected
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		Graphics pen = getGraphics();
		int[] starX = {x,x+7, x+21,x+10,x+14,x,x-14,x-10,x-21,x-7};
		int[] starY = {y-25,y-10,y-10,y,y+16,y+5,y+16,y,y-10,y-10};
		pen.fillPolygon(starX, starY, 10);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
