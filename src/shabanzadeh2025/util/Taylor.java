package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Taylor implements CalcFunc
{
	private final double about;
	private final double[] coeff;
	
	public Taylor(double about, double... coeff)
	{
		this.about = about;
		this.coeff = coeff;
	}
	
	@Override
	public Taylor deriv() 
	{
		final int n = this.coeff.length-1;
		double [] derCoeff = new double[n];
		for(int i = 0; i < n; i++)
			derCoeff[i] = (i+1) * this.coeff[i+1];

		return new Taylor(this.about, derCoeff);
	}

	@Override
	public double applyAsDouble(double arg) 
	{
		final double diff = arg - this.about;
		double val = 0.0;
		double pow = 1.0;
		for(double v : this.coeff)
		{
			val += v * pow;
			pow *= diff;
		}
		return val;
	}

	@Override
	public Taylor anti() 
	{
		final int n = this.coeff.length+1;
		double[] antiCoeff = new double[n];
		for(int i = 1; i < n; i++)
			antiCoeff[i] = this.coeff[i-1] / i;
		return new Taylor(this.about, antiCoeff);
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer("(x) -> ");
		
		String term = "(x" + (this.about != 0 ? (this.about > 0 ? "-" : "+") + Math.abs(this.about) : "") + ")";
		
		boolean started = false;
		
		if(this.coeff[0] != 0.0)
		{
			sb.append(this.coeff[0]);
			started = true;
		}
		
		if(1 <= this.coeff.length)
			if(this.coeff[1] != 0.0)
			{
				sb.append(coef(this.coeff[1], started) + "*" + term);
				started = true;
			}
		
		for(int i = 2; i < this.coeff.length; i++)
			if(this.coeff[i] != 0.0)
			{
				sb.append(coef(this.coeff[i], started) + "*" + term  + "^" + i);
				started = true;
			}
			
		return sb.toString();
	}
	
	public static String coef(double c, boolean started)
	{
		if(c >= 0 && started)
			return "+" +  c;
		else
			return "" + c;
	}
}
