package shabanzadeh2025.rend;

import shabanzadeh2025.util.Setable;

/**
 * Style for graph elements.
 * 
 * @author Dene Ringuette
 */

public class GraphRender implements Setable<GraphRender>
{
	public enum Boundary
	{
		OPEN,
		MIXED,
		CLOSED;
	};
	
	public Boundary bounds;
	
	public Stroke stroke;
	
	public Fill fill;
	
	public Text text;
	
	public Dash dash;
	
	public double point;
	
	public Text star;
	
	public GraphRender()
	{
		this.point = 3;
		
		this.text = new Text(8);
		this.fill = new Fill();
		this.stroke = new Stroke();
		this.dash = null;
		
		this.star = new Text(14);
	}
	
	@Override
	public GraphRender copy() 
	{
		GraphRender thus = new GraphRender();
		thus.set(this);
		return thus;
	}

	@Override
	public void set(GraphRender thus) 
	{
		this.stroke = thus.stroke;
		this.fill = thus.fill;
		this.text = thus.text;
		this.dash = thus.dash;
		this.point = thus.point;
		this.star = thus.star;
		this.bounds = thus.bounds;
	}

	@Override
	public GraphRender get() 
	{
		return new GraphRender();
	}
}
