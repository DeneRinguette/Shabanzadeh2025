package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public final class Polar extends Complex implements ComGrpUndMult<Polar>
{
	public double r;
	public double t;
	
	private static final double TWO_PI = 2*Math.PI;	
	private static final double iTheta = Math.PI/2;
	
	public static Polar i()
	{
		return new Polar(1.0, Polar.iTheta);
	}
	
	public Polar(Complex that)
	{
		this.r = that.mod();
		this.t = that.ang();
	}
	
	public Polar(double real)
	{
		if(real < 0)
			this.set(-real, Math.PI);
		else
			this.set(real, 0.0);
	}
	
	public Polar(double mod, double arg)
	{
		this.r = mod;
		this.t = arg;
	}
	
	public double ang()
	{
		if(this.r == 0)
			return Double.NaN;
		double ang = this.t;
		if(this.r < 0)
			ang += Math.PI;
		ang %= TWO_PI;
		if(ang <= -Math.PI)
			return ang + Math.PI;
		if(ang > Math.PI)
			return ang - Math.PI;
		return ang;
	}
	
	public void conj()
	{
		this.t = -this.t;
	}
	
	public void div(Polar that)
	{
		this.set(this.r / that.r, this.t - that.t);
	}
	
	public void div(double r, double t)
	{
		this.set(this.r / r, this.t - t);
	}
	
	public void div(double re)
	{
		this.r /= re;
	}
	
	public Rect log()
	{
		return new Rect(Math.log(this.mod()), this.ang());
	}
		
	public double mod()
	{
		return Math.abs(this.r);
	}
	
	public double mod2()
	{
		return this.r * this.r;
	}
	
	public void mult(Polar that)
	{
		this.r *= that.r;
		this.t += that.t;
	}
	
	public void mult(double re)
	{
		this.r *= re;
	}
	
	public void mult(double r, double t)
	{
		this.r *= r;
		this.t += t;
	}
	
	public void neg()
	{
		if(this.t < 0)
			this.t += Math.PI;
		else
			this.t -= Math.PI;
	}
	
	public void norm()
	{
		this.set(1.0, this.ang());
	}
	
	public void power(double n)
	{
		this.set(Math.pow(this.r, n), n * this.t);
	}

	public void radiusDrop()
	{
		if(this.r < 0)
		{
			this.r = -this.r;
			this.neg();
		}
	}
	
	public void inv()
	{
		this.set(1.0/this.r, - this.t);
	}

	public Polar[] roots(int n)
	{
		if(n < 1)
			throw new IllegalArgumentException();
		Polar[] roots = new Polar[n];
		double mod = Math.pow(this.mod(), 1.0/n);
		double ang = this.ang()/n;
		double step = TWO_PI/n;
		for(int i = 0; i < n; i += 1)
			roots[i] = new Polar(mod, ang + i * step);
		return roots;
	}

	public void set(Complex arg)
	{
		this.r = arg.mod();
		this.t = arg.ang();
	}

	public void set(double arg)
	{
		this.set(Math.abs(arg), arg < 0 ? Math.PI : 0);
	}
	
	public void set(double mod, double arg)// throws IllegalArgumentException
	{
		this.r = mod;
		this.t = arg;
	}
	
	public void std()
	{
		this.set(this.mod(), this.ang());
	}
	
	public String toString()
	{
		return "(" + this.r + ")cis(" + this.t + ")";
	}

	@Override
	public void idn()
	{
		this.r = 1.0;
		this.t = 0.0;
	}

	@Override
	public void set(Polar arg)
	{
		this.r = arg.r;
		this.t = arg.t;
	}

	@Override
	public Polar copy()
	{
		return new Polar(this.r, this.t);
	}

	@Override
	public Polar get() 
	{
		return new Polar(0.0, 0.0);
	}
}
