package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Rect extends Complex implements Field<Rect>, NormVecSpc<Rect>, ComGrpUndMult<Rect>
{
	public static Rect i()
	{
		return new Rect(0.0, 1.0);
	}
	
	public double re;
	public double im;
	
	public static Rect expi(double phi)
	{
		return new Rect(Math.cos(phi), Math.sin(phi));
	}

	public static Rect sum(Complex a, Complex b)
	{
		Rect thus = new Rect(a);
		thus.add(b);
		return thus;
	}
	
	public static Rect diff(Complex a, Complex b)
	{
		Rect thus = new Rect(a);
		thus.sub(b);
		return thus;
	}
	
	public static Rect mult(Complex a, Complex b)
	{
		Rect thus = new Rect(a);
		thus.mult(b);
		return thus;
	}
	
	public static Rect div(Complex a, Complex b)
	{
		Rect thus = new Rect(a);
		thus.div(b);
		return thus;
	}
	
	public Rect(Complex that)
	{
		this.re = that.re();
		this.im = that.im();
	}
	
	public Rect(double re)
	{
		this.re = re;
		this.im = 0;
	}
	
	public Rect(double re, double im)
	{
		this.set(re, im);
	}
	
	public void add(Complex that) 
	{
		this.re += that.re();
		this.im += that.im();
	}
	
	public void add(double re, double im) 
	{
		this.re += re;
		this.im += im;
	}
	
	public void conj()
	{
		this.im = -this.im;
	}
	
	public void div(Complex that)
	{
		this.mult(that.re(), -that.im());
		this.div(that.mod2());
	}
		
	public void div(Rect that)
	{
		this.div(that.re, that.im);
	}
	
	public void div(double that)
	{
		this.re /= that;
		this.im /= that;
	}
	
	public void div(double re, double im)
	{
		this.mult(re, -im);
		this.div(re*re + im*im);
	}
	
	public Polar exp()
	{
		return new Polar(Math.exp(this.re()), this.im());
	}
	
	public double im()
	{
		return this.im;
	}
	
	public void mult(Complex that)
	{
		this.mult(that.re(), that.im());
	}
	
	public void mult(Rect that)
	{
		this.mult(that.re, that.im);
	}
	
	public void mult(double that)
	{
		this.re *= that;
		this.im *= that;
	}
	
	public void mult(double re, double im)
	{
		this.set(this.re * re - this.im * im, this.im * re + this.re * im);
	}
	
	public static double reMult(double re0, double im0, double re1, double im1)
	{
		return re0 * re1 - im0 * im1;
	}
	
	public static double imMult(double re0, double im0, double re1, double im1)
	{
		return re0 * im1 + re1 * im0;
	}
	
	public void neg()
	{
		this.mult(-1.0);
	}

	public void normalize()
	{
		this.div(this.mod());
	}

	public double re()
	{
		return this.re;
	}

	public void rec()
	{
		this.conj();
		this.div(this.mod2());
	}

	public void set(Complex that)
	{
		this.re = that.re();
		this.im = that.im();
	}
	
	public void set(Rect that)
	{
		this.re = that.re;
		this.im = that.im;
	}

	public void set(double re, double im)
	{
		this.re = re;
		this.im = im;
	}
	
	public void sub(Complex that)
	{
		this.re -= that.re();
		this.im -= that.im();
	}
	
	public void sub(double re, double im)
	{
		this.re -= re;
		this.im -= im;
	}

	public String toString()
	{
		return "(" + this.re + ")+i(" + this.im + ")";
	}
	
	@Override
	public Rect copy()
	{
		return new Rect(this.re, this.im);
	}

	@Override
	public void add(Rect that)
	{
		this.re += that.re;
		this.im += that.im;
	}

	@Override
	public void sub(Rect that)
	{
		this.re -= that.re;
		this.im -= that.im;
	}

	@Override
	public void zero()
	{
		this.re = 0.0;
		this.im = 0.0;
	}

	@Override
	public void add1()
	{
		this.re += 1.0;
	}

	@Override
	public void set1()
	{
		this.re = 1.0;
		this.im = 0.0;
	}

	@Override
	public void sqr()
	{
		this.mult(this);
	}

	@Override
	public void sub1()
	{
		this.re -= 1.0;
	}

	@Override
	public double norm()
	{
		return this.mod();
	}

	@Override
	public double norm2()
	{
		return this.mod2();
	}

	@Override
	public void idn() 
	{
		this.set(1.0, 0.0);
	}

	@Override
	public void inv() 
	{
		this.rec();
	}

	@Override
	public Rect get() 
	{
		return new Rect(0.0, 0.0);
	}
}
