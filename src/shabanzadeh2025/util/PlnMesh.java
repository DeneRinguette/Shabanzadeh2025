package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class PlnMesh
{
	final int nx, ny;
	final double x0, x1;
	final double y0, y1;
	final double dx, dy;
	
	public PlnMesh(int nx, double x0, double x1, int ny, double y0, double y1)
	{
		this.nx = nx;
		this.ny = ny;
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
		this.dx = (x1-x0)/(nx-1);
		this.dy = (y1-y0)/(ny-1);
	}
	
	public PlnMesh scale(double factX, double factY)
	{
		return new PlnMesh(this.nx(), this.x0/factX, this.x1/factX, this.ny(), this.y0/factY, this.y1/factY);
	}
	
	public PlnMesh scale(double factX)
	{
		return this.scale(factX, factX);
	}
	
	public PlnMesh(int nx, double x0, double x1)
	{
		this(nx, x0, x1, nx, x0, x1);
	}
	
	PlnMesh(int nx, int ny)
	{
		this(nx, 0.0, nx-1.0, ny, 0.0, ny-1.0);
	}
	
	public double dx()
	{
		return this.dx;
	}
	
	public double dy()
	{
		return this.dy;
	}
	
	public double x(int j)
	{
		return this.x0 + this.dx * j;
	}
	
	public double y(int i)
	{
		return this.y1 - this.dy * i;
	}
	
	public int j(double x)
	{
		return (int)((x - this.x0) / this.dx + 0.5);
	}
	
	public int i(double y)
	{
		return (int)((this.y1 - y) / this.dy + 0.5);
	}
	
	public int nx()
	{
		return this.nx;
	}
	
	public int ny()
	{
		return this.ny;
	}
	
	public int ni()
	{
		return this.ny;
	}
	
	public int nj()
	{
		return this.nx;
	}
	
	public double[] imgX()
	{
		double[] x = new double[this.nx];
		for(int j = 0; j < this.nx; j++)
			x[j] = this.x(j);
		return x;
	}
	
	public double[] imgY()
	{
		double[] y = new double[this.ny];
		for(int i = 0; i < this.ny; i++)
			y[i] = this.y(i);
		return y;
	}
	
	public int size()
	{
		return this.nx() * this.ny();
	}
}
