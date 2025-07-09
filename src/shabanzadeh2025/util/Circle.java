package shabanzadeh2025.util;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import ij.gui.OvalRoi;
import ij.gui.Roi;

/**
 * @author Dene Ringuette
 */

public class Circle implements MultiVarFunc
{
	private final double radius, centerX, centerY;
	
	public Circle(double r)
	{
		this.radius = r;
		this.centerX = 0;
		this.centerY = 0;
	}
	
	public Circle(double r, double x, double y)
	{
		this.radius = r;
		this.centerX = x;
		this.centerY = y;
	}
	
	public Circle(Tensor params)
	{
		this.radius = params.rGet(0);
		this.centerX = params.rGet(1);
		this.centerY = params.rGet(2);
	}
	
	public Circle(Roi roi)
	{
		Rectangle rect = roi.getBounds();

		final int diameter = rect.width;
		
		if(diameter != rect.height)
			throw new IllegalArgumentException("Not a circle but an ellipse.");

		this.radius = diameter/2;
		this.centerX = rect.x+radius;
		this.centerY = rect.y+radius;
	}
	
	public double radius()
	{
		return this.radius;
	}
	
	public double centerX()
	{
		return this.centerX;
	}
	
	public double centerY()
	{
		return this.centerY;
	}

	@Override
	public double apply(double... args)
	{
		return Math.hypot(args[0]-this.centerX, args[1]-this.centerY) - this.radius;
	}
	
	public double error(double x, double y)
	{
		return Math.hypot(x-this.centerX, y-this.centerY) - this.radius;
	}
	
	public boolean in(Point2D p)
	{
		return this.error(p.getX(), p.getY()) < 0;
	}
	
	public boolean in(int x, int y)
	{
		return this.error(x, y) < 0;
	}
	
	public boolean out(int x, int y)
	{
		return 0 < this.error(x, y);
	}
	
	public Circle scale(double factor)
	{
		return new Circle(factor * this.radius, this.centerX, this.centerY);
	}
	
	public OvalRoi toRoi()
	{
		final int cornerX = (int)Math.round(this.centerX-this.radius);
		final int cornerY = (int)Math.round(this.centerY-this.radius);
		final int diameter = (int)Math.round(2*this.radius);
		
		return new OvalRoi(cornerX, cornerY, diameter, diameter);
	}
	
	private static byte[][] RING_STEPS;
	
	public GaplessPath toPath()
	{
		final int x = (int)(this.centerX());
		final int y = (int)(this.centerY());
		final int r = (int)(this.radius());
		return new GaplessPath(x+r, y, Circle.RING_STEPS[r]);
	}
	
	public Tensor toTensor()
	{
		return Tensor.col(this.radius(), this.centerX(), this.centerY());
	}
	
	public double[] toArray()
	{
		return new double[]{this.radius(), this.centerX(), this.centerY()};
	}
	
	public String toString()
	{
		
		return 
				"(x"+
				(this.centerX < 0 ? "+" : "-")+
				Math.abs(this.centerX)+
				")^2+(y"+
				(this.centerY < 0 ? "+" : "-")+
				Math.abs(this.centerY)+
				")^2=("+
				this.radius+
				")^2";
		
	}
}
