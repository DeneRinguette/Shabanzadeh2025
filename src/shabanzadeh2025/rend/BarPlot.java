package shabanzadeh2025.rend;

import java.util.Arrays;

/**
 * Plot for bar graphs.
 * 
 * @author Dene
 * @deprecated methods for bar graphs added to <code>Plot</code>
 *
 */

public class BarPlot extends Draw
{
	
	protected int decimals = 3;	
	
	protected boolean scientific = false;
	
	protected int starSize;
		
	protected int tickSize = 6;
	
	protected BarBox bar;
	
	public BarPlot(Box box, BarBox bar)
	{
		super(box);
		this.bar = bar;
	}
	
	public void draw_err(double point, double rad, double mean, double sem)
	{
		String centre = Double.toString(point);
		String bottom = str(this.graph(mean-sem));
		String top = str(this.graph(mean+sem));
		String left = Double.toString(point - rad);
		String right = Double.toString(point + rad);
		
		this.drawLine(centre, top, centre, bottom);
		this.drawLine(left, bottom, right, bottom);
		this.drawLine(left, top, right, top);	
	}
	
	public void draw_dots(double point, double rad, double[] data)
	{
		final int n = data.length;
		String radius = Double.toString(rad);
		double[] values = new double[n];
		double[] offset = new double[n];
		for(int i = 0 ; i < n; i++)
			values[i] = this.graph(data[i]);
		Arrays.sort(values);
		boolean overlap_found;
		do
		{
			overlap_found = false;
			for(int i = 0 ; i < n-1; i++)
				if(Math.hypot(values[i] - values[i+1], offset[i] - offset[i+1]) < 2*rad)
				{
					offset[i] += i%2 == 0 ? 1.5*rad : - 1.5*rad;
					overlap_found = true;
				}
		}
		while(overlap_found);
		
		for(int i = 0 ; i < n; i++)
			this.drawCircle(str(point+offset[i]), str(values[i]), radius);
	}
	
	private double graph(double data)
	{
		double ratio = (data - this.bar.minValue)/(this.bar.maxValue - this.bar.minValue);
		return this.box.bottom() - ratio * this.box.height();
	}
	
	public void labelOrdinate(double value)
	{
		double low = box.left() - this.rend.text.size()/2 - this.tickSize;
		if(scientific)
			this.drawText(low, this.graph(value), TextAlignment.LEFT, "" + Format.scientific(value, this.decimals));
		else
			this.drawText(low, this.graph(value), TextAlignment.LEFT, "" + Format.decimals(value, this.decimals));
	}
	
	public void labelOrdinate(double value, String name)
	{
		double low = box.left() - this.rend.text.size()/2 - this.tickSize;
		this.drawText(low, this.graph(value), TextAlignment.LEFT, name);
	}

	public void labelOrdinates(double point, double step)
	{
		double y;
		y = point;
		this.labelOrdinate(y);
		while((y += step) <= this.bar.maxValue)
			this.labelOrdinate(y);
		y = point;
		while(this.bar.minValue <= (y -= step))
			this.labelOrdinate(y);
	}
	
	public void markupOrdinates(double point, double step, int precision)
	{
		this.ticOrdinates(point, step);
		if(precision > -1)
		{
			final int temp = this.decimals;
			this.setAxisPrecision(precision);
			this.labelOrdinates(point, step);
			this.setAxisPrecision(temp);
		}
	}
	
	public void ticOrdinate(double dataY)
	{
		double graphY = this.graph(dataY);
		double left = box.left() - this.tickSize;
		double right = box.left() + this.tickSize;
		this.drawLine(left, graphY, right, graphY);
	}
	
	public void ticOrdinates(double point, double step)
	{
		double y;
		y = point;
		this.ticOrdinate(y);
		while((y += step) <= this.bar.maxValue)
			this.ticOrdinate(y);
		y = point;
		while(this.bar.minValue <= (y -= step))
			this.ticOrdinate(y);
	}
	
	public void setTickSize(int size)
	{
		this.tickSize = size;
	}
	
	public void setAxisPrecision(int dec)
	{
		this.decimals = dec;
	}
	

	
}
