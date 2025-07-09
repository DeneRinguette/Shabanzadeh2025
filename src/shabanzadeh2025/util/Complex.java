package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public abstract class Complex
{
	public double re()
	{
		return this.mod() * Math.cos(this.ang());
	}
	
	public double im()
	{
		return this.mod() * Math.sin(this.ang());
	}
	
	public double mod()
	{
		return Math.hypot(this.re(), this.im());
	}
	
	public double mod2()
	{
		return this.re() * this.re() + this.im() * this.im();
	}
	
	public double ang()
	{
		return Math.atan2(this.im(), this.re());
	}
	
	public double eta()
	{
		return this.im() / ( this.mod2() + 1 );
	}
	
	public double xi()
	{
		return this.re() / (this.mod2() + 1);
	}
	
	public double zeta()
	{
		double r2 = this.mod2();
		return r2 / ( r2 + 1 );
	}
}
