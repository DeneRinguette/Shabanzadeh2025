package shabanzadeh2025.rend;

import java.awt.Point;
import java.awt.Rectangle;

/**
 *  ---N---
 * | | T   |
 * W L   R E
 * | O-B-- |
 *  ---S--- 
 * 
 * @author Dene Ringuette
 */

public class Box 
{		
	public Rectangle graph;
	
	public Rectangle bound;
	
	public Box(int xMargin, int yMargin, int xLength, int yLength)
	{
		this.graph = new Rectangle(xMargin, yMargin, xLength, yLength);
		this.bound = new Rectangle(0, 0, xLength+2*xMargin, yLength+2*yMargin);
	}
	
	public Box(Rectangle graph, Rectangle bound)
	{
		if(!bound.contains(graph))
			throw new IllegalArgumentException("Graph box outside of bounding box.");
		this.graph = graph.getBounds();
		this.bound = bound.getBounds();
	}
	
	public Box(Rectangle graph, int boundary)
	{
		this(graph, boundary, boundary);
	}
	
	public Box(Rectangle graph, int dx, int dy)
	{
		if(dx < 0 || dy < 0)
			throw new IllegalArgumentException("Boundary must be positive.");
		this.graph = graph.getBounds();
		this.bound = new Rectangle(
				graph.x-dx, 
				graph.y-dy, 
				graph.width+2*dx, 
				graph.height+2*dy
			);
	}
	
	public void graphStep(int gap_x, int gap_y)
	{
		this.graph.translate(this.graph.width + gap_x, this.graph.height + gap_y);
	}
	
	public Box(Rectangle graph)
	{
		this(graph, graph.width/4, graph.height/4);
	}
	
	public int left()
	{
		return this.graph.x;
	}
		
	public int top()
	{
		return this.graph.y;
	}
	
	public int width()
	{
		return this.graph.width;
	}
		
	public int height()
	{
		return this.graph.height;
	}
	
	public int right()
	{
		return this.left() + this.width();
	}
	
	public int bottom()
	{
		return this.top() + this.height();
	}
	
	public int middleX()
	{
		return this.left() + this.width()/2;
	}
	
	public int middleY()
	{
		return this.top() + this.height()/2;
	}
	
	public Point title()
	{
		return new Point(this.middleX(), (this.top()+this.north())/2);
	}
	
	public Point xLabel()
	{
		return new Point(this.middleX(), (this.bottom()+2*this.south())/3);
	}
	
	public Point xxLabel()
	{
		return new Point(this.middleX(), (this.top()+2*this.north())/3);
	}
	
	public Point yLabel()
	{
		return new Point((this.left()+2*this.west())/3, this.middleY());
	}
	
	public Point topLabel()
	{
		return new Point(this.middleX(), (2*this.top()+this.north())/3);
	}
	
	public Point yyLabel()
	{
		return new Point((this.right()+2*this.east())/3, this.middleY());
	}
	
	public Point middle()
	{
		return new Point(this.middleX(), this.middleY());
	}
	
	public int west()
	{
		return this.bound.x;
	}
	
	public int north()
	{
		return this.bound.y;
	}
	
	public int totalWidth()
	{
		return this.bound.width;
	}
	
	public int totalHeight()
	{
		return this.bound.height;
	}
	
	public int south()
	{
		return this.north() + this.totalHeight();
	}
	
	public int east()
	{
		return this.west() + this.totalWidth();
	}
	
	public void translate(int dx, int dy)
	{
		this.graph.translate(dx, dy);
		this.bound.translate(dx, dy);
	}
	
	public void moveEast()
	{
		this.translate(this.totalWidth(), 0);
	}
	
	public void moveSouth()
	{
		this.translate(0, this.totalHeight());
	}
	
	public void moveNorth()
	{
		this.translate(0, -this.totalHeight());
	}
	
	public void moveWest()
	{
		this.translate(-this.totalWidth(), 0);
	}
	
	public double barWidth(int total)
	{
		return this.width()/(double)total;
	}
	
	public double barHeight(int total)
	{
		return this.height()/(double)total;
	}
	
	public double barCenter(int index, int total)
	{ 
		return this.left() + (index+0.5) * this.barWidth(total);
	}
	
	public Point topRight()
	{
		return new Point(this.right(), this.top());
	}
	
	public Point bottomRight()
	{
		return new Point(this.right(), this.bottom());
	}
	
	public Point topLeft()
	{
		return new Point(this.left(), this.top());
	}
	
	public Point bottomLeft()
	{
		return new Point(this.left(), this.bottom());
	}
	
	public Point northWest()
	{
		return new Point(this.west(), this.north());
	}
	
	public Point northEast()
	{
		return new Point(this.east(), this.north());
	}
	
	public Point southWest()
	{
		return new Point(this.west(), this.south());
	}
	
	public Point southEast()
	{
		return new Point(this.east(), this.south());
	}
}
