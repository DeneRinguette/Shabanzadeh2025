package shabanzadeh2025.util;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.List;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.Roi;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public final class CovData extends Scatter implements VecSpc<CovData>, DataSet, Dim, Serializable
{
	private static final long serialVersionUID = 1L;

	public static final int NO_OF_TERMS = 6;
	
	public static final int W = 0;
	
	public static final int WX = 1;
	public static final int WY = 2;

	public static final int WX2 = 3;
	public static final int WXY = 4;
	public static final int WY2 = 5;
	
	private final double[] c = new double[NO_OF_TERMS];
	
	public CovData()
	{
		
	}
	
	public CovData(double x, double y)
	{
		this.add(x, y);
	}
	
	public CovData(ImageProcessor a, ImageProcessor b)
	{
		IJTools.checkDim(a, b);
		final int n = a.getPixelCount();
		for(int i = 0 ; i < n; i++)
			this.add(a.getf(i), b.getf(i));
	}

	public CovData(ColorProcessor cpro, int channelA, int channelB)
	{
		// 00000000000000000000000011111111
		final int BLUE_BITS = 255; 
		// 00000000000000001111111100000000
		final int GREEN_BITS = BLUE_BITS << 8;
		// 00000000111111110000000000000000
		final int RED_BITS = GREEN_BITS << 8; 
		
		final int[] color_bits = new int[]{0, RED_BITS, GREEN_BITS, BLUE_BITS};
		final int ch_a = color_bits[channelA];
		final int ch_b = color_bits[channelB];
		
		final int pixels = cpro.getPixelCount();
		for(int pixel = 0 ; pixel < pixels; pixel++)
		{
			final int rgb = cpro.get(pixel);
			this.add(rgb & ch_a, rgb & ch_b);
		}
	}
	
	public CovData(ImageProcessor a)
	{
		this.add(a);
	}
	
	public CovData(ImageProcessor a, ImageProcessor b, Roi roi)
	{
		IJTools.checkDim(a, b);
		final int w = a.getWidth();
		final int h = a.getHeight();
		for(int y = 0 ; y < h; y++)
			for(int x = 0 ; x < w; x++)
				if(roi.contains(x, y))
					this.add(a.getf(x,y), b.getf(x,y));
	}
	
	public CovData(ImageStack a, ImageStack b)
	{
		IJTools.checkDim(a, b);
		final int size = a.getSize();
		final int n = a.getWidth() * b.getHeight();
		for(int z = 1; z <= size; z++)
		{
			ImageProcessor c = a.getProcessor(z);
			ImageProcessor d = b.getProcessor(z);
			for(int i = 0 ; i < n; i++)
				this.add(c.getf(i), d.getf(i));
		}
	}
	
	public CovData(ImagePlus hyper, int channelA, int channelB)
	{
		final int frames = hyper.getNFrames();
		final int slices = hyper.getNSlices();
		final int channels = hyper.getNChannels();
		final int pixels = hyper.getWidth() * hyper.getHeight();
		
		if(channels < channelA || channels < channelB)
			throw new IllegalArgumentException("Channel indicies out of range");
		
		for(int frame = 1; frame < frames; frame++)
			for(int slice = 1; slice < slices; slice++)
			{
				hyper.setPosition(channelA, slice, frame);
				ImageProcessor proA = hyper.getProcessor();
				
				hyper.setPosition(channelB, slice, frame);
				ImageProcessor proB = hyper.getProcessor();
				
				for(int pixel = 0 ; pixel < pixels; pixel++)
					this.add(proA.getf(pixel), proB.getf(pixel));
			}		
	}
	
	public CovData(CovData data)
	{
		this.set(data);
	}
	
	public CovData(double[] x, double[] y)
	{
		if(x.length != y.length)
			throw new IllegalArgumentException();
		for(int i = 0; i < x.length; i++)
			this.add(x[i], y[i]);
	}
	
	public CovData(ImageProcessor img, Roi roi)
	{
		this.add(img, roi);
	}
	
	public CovData(List<? extends Number> x, List<? extends Number> y)
	{
		if(x.size() != y.size())
			throw new IllegalArgumentException();
		//this.c = new double[6];
		for(int i = 0; i < x.size(); i++)
			this.add(x.get(i).doubleValue(), y.get(i).doubleValue());
	}
	
	public void add(double x, double y)
	{
		this.c[W] += 1;
		this.c[WX] += x;
		this.c[WY] += y;
		this.c[WX2] += x * x;
		this.c[WXY] += x * y;
		this.c[WY2] += y * y;
	}
	
	public void add(double w, double x, double y)
	{
		if(w == 0)
			return;
		this.c[W] += w;
		final double wx = w * x;
		final double wy = w * y;
		this.c[WX] += wx;
		this.c[WY] += wy;
		this.c[WX2] += wx * x;
		this.c[WXY] += wx * y;
		this.c[WY2] += wy * y;
	}
	
	public static double[] slopeFilter(int k, double[] x, double[] y)
	{
		final int n = y.length;
		if(n != x.length)
			throw new IllegalArgumentException("Dimension mismatch");
		double[] slopes = new double[n-k+1];
		CovData covData = new CovData();
		for(int i = 0; i < k-1; i++)
			covData.add(x[i], y[i]);
		for(int i = k-1; i < n; i++)
		{
			covData.add(x[i], y[i]);
			final int j = i - k + 1;
			slopes[j] = covData.slopeYatX();
			covData.sub(x[j], y[j]);
		}
		return slopes;
	}
	
	public static double[] slopeFilter(int k, double[] y)
	{
		final int n = y.length;
		double[] slopes = new double[n-k+1];
		CovData covData = new CovData();
		for(int i = 0; i < k-1; i++)
			covData.add(i, y[i]);
		for(int i = k-1; i < n; i++)
		{
			covData.add(i, y[i]);
			final int j = i - k + 1;
			slopes[j] = covData.slopeYatX();
			covData.sub(j, y[j]);
		}
		return slopes;
	}
	
	public static double[][] slopeFilter(int k, double[][] y)
	{
		final int n = y.length;
		double[][] thus = new double[n][];
		for(int i = 0; i < n; i++)
			thus[i] = CovData.slopeFilter(k, y[i]);
		return thus;
	}
	
	public static float[] slopeFilter(int k, float[] y)
	{
		final int n = y.length;
		float[] slopes = new float[n-k+1];
		CovData covData = new CovData();
		for(int i = 0; i < k-1; i++)
			covData.add(i, y[i]);
		for(int i = k-1; i < n; i++)
		{
			covData.add(i, y[i]);
			final int j = i - k + 1;
			slopes[j] = (float)covData.slopeYatX();
			covData.sub(j, y[j]);
		}
		return slopes;
	}
	
	public void sub(double x, double y)
	{
		// non-sense if point is never added before parameter evaluation
		this.c[W] -= 1;
		this.c[WX] -= x;
		this.c[WY] -= y;
		this.c[WX2] -= x * x;
		this.c[WXY] -= x * y;
		this.c[WY2] -= y * y;
	}
	
	public void sub(double w, double x, double y)
	{
		// non-sense if point is never added before parameter evaluation
		if(w == 0)
			return;
		this.c[W] -= w;
		final double wx = w * x;
		final double wy = w * y;
		this.c[WX] -= wx;
		this.c[WY] -= wy;
		this.c[WX2] -= wx * x;
		this.c[WXY] -= wx * y;
		this.c[WY2] -= wy * y;
	}
	
	public static double[] r2_filter(int k, double[] y)
	{
		final int n = y.length;
		double[] r2 = new double[n-k+1];
		CovData covData = new CovData();
		for(int i = 0; i < k-1; i++)
			covData.add(i, y[i]);
		for(int i = k-1; i < n; i++)
		{
			covData.add(i, y[i]);
			final int j = i - k + 1;
			r2[j] = covData.r2();
			covData.sub(j, y[j]);
		}
		return r2;
	}
	
	public static double[] r_cross(int k, double[] y0, double[] y1)
	{
		final int n = y0.length;
		if(n != y1.length)
			throw new IllegalArgumentException("Fish");
		final double[] r = new double[2*k+1];
		for(int i = 0; i <= k; i++)
		{
			r[k+i] = new CovData(
					Op.removeHead(i, y0), 
					Op.removeTail(i, y1)
				).r();
		}
		for(int i = 1; i <= k; i++)
		{
			r[k-i] = new CovData(
					Op.removeHead(i, y1), 
					Op.removeTail(i, y0)
				).r();
		}
		return r;
	}
	
	public static double[][] r_err_cross(int k, double[] y0, double[] y1)
	{
		final int n = y0.length;
		if(n != y1.length)
			throw new IllegalArgumentException("Fish");
		final double[][] err = new double[2*k+1][];
		for(int i = 0; i <= k; i++)
		{
			err[k+i] = new CovData(
					Op.removeHead(i, y0), 
					Op.removeTail(i, y1)
				).r_t95();
			 
		}
		for(int i = 1; i <= k; i++)
		{
			err[k-i] = new CovData(
					Op.removeTail(i, y0), 
					Op.removeHead(i, y1)
				).r_t95();
		}
		return err;
	}
	
	public double r2()
	{
		double spErr = this.spErr(); 
		return (spErr / this.ssErrorX()) * (spErr / this.ssErrorY());
	}
	
	public double r2Adj()
	{
		if(this.c[W] < 3)
			return Double.NaN;
		return 1 - ((1-this.r2())*(this.c[0]-1)/(this.c[0]-2));			
	}
	
	public double varX()
	{
		return this.ssErrorX()/this.df();
	}
	
	public double sigmaXX()
	{
		return this.ssErrorX()/this.weight();
	}
	
	public double sigmaYY()
	{
		return this.ssErrorY()/this.weight();
	}
	
	public double sigmaXY()
	{
		return this.spErr()/this.weight();
	}
	
	public double varY()
	{
		return this.ssErrorY()/this.df();
	}
	
	public double cov()
	{
		return this.spErr()/this.df();
	}
	
	public double s2b()
	{
		final double s2yx = this.ssResidualYatX() / (this.size()-2);
		return s2yx/this.varX()/this.df();
	}
	
	public double slopeYatX_ste()
	{
		return Math.sqrt(this.s2b());
	}
	
	public double ssResidualYatX()
	{
		return this.ssErrorY() * (1.0 - this.r2());
	}
	
	public double ssResidualXatY()
	{
		return this.ssErrorX() * (1.0 - this.r2());
	}
	
	public double sumX()
	{
		return this.c[WX];
	}
	
	public double sumY()
	{
		return this.c[WY];
	}
	
	public int size()
	{
		return (int)this.c[W];
	}
	
	public double meanX()
	{
		return this.sumX()/this.weight();
	}
	
	public double meanY()
	{
		return this.sumY()/this.weight();
	}
	
	public Tensor meanVec()
	{
		return Tensor.col(this.meanX(), this.meanY());
	}
	
	public Tensor covMat()
	{
		final double xx = this.varX();
		final double xy = this.cov();
		final double yy = this.varY();
		return Tensor.sqr(2, xx, xy, xy, yy);
	}
	
	public double df()
	{
		return this.weight()-1.0;
	}
	
	public double df2()
	{
		return this.weight()-2.0;
	}
	
	public double slopeYatX()
	{
		return this.spErr()/this.ssErrorX();		
	}
	
	public double slopeXatY()
	{
		return this.spErr()/this.ssErrorY();
	}
	
	public double interceptYatX()
	{
		return this.meanY() - this.slopeYatX()*this.meanX();
	}
	
	public double interceptXatY()
	{
		return this.meanX() - this.slopeXatY()*this.meanY();
	}
	
	public double sumXX()
	{
		return this.c[WX2];
	}
	
	public double sumXY()
	{
		return this.c[WXY];
	}
	
	public double sumYY()
	{
		return this.c[WY2];
	}
	
	public double ssErrorX()
	{
		return this.sumXX()-this.sumX()*this.meanX();
	}
	
	public double ssErrorY()
	{
		return this.sumYY()-this.sumY()*this.meanY();
	}
		
	public double spErr()
	{
		return this.sumXY() - this.meanX()*this.meanY()*this.weight();
	}
	
	public void add(CovData that) 
	{
		Ip.add(this.c, that.c);
	}
	
	public void neg() 
	{
		Ip.neg(this.c);
	}
	
	public void sub(CovData that) 
	{
		Ip.sub(this.c, that.c);
	}
	
	public void zero() 
	{
		Ip.zero(this.c);
	}
	
	public void set(CovData that) 
	{
		Ip.set(this.c, that.c);
	}
	
	public CovData copy() 
	{
		CovData thus = new CovData();
		thus.set(this);
		return thus;
	}
	
	public int dim()
	{
		return 2;
	}
	
	public void clear()
	{
		this.zero();
	}
	
	public double weight()
	{
		return this.c[W];
	}
	
	public double sum1()
	{
		return this.c[W];
	}
	
	public double[] moments()
	{
		final double weight = weight();
		final double invW = 1.0/weight;
		
		final double meanX = sumX()*invW;
		final double meanY = sumY()*invW;
		
		final double sigmaXX = sumXX()*invW - meanX*meanX;
		final double sigmaXY = sumXY()*invW - meanX*meanY;
		final double sigmaYY = sumYY()*invW - meanY*meanY;
		
		return new double[]{
				weight, 
				meanX, meanY, 
				sigmaXX, sigmaXY, sigmaYY
			};
	}
	
	public double[] moments(double x0, double y0)
	{
		final double weight = weight();
		final double invW = 1.0/weight;
		
		final double meanX = sumX()*invW;
		final double meanY = sumY()*invW;
		
		final double sigmaXX = sumXX()*invW - 2*meanX*x0 + x0*x0;
		final double sigmaXY = sumXY()*invW - meanX*y0 - meanY*x0 + x0*y0;
		final double sigmaYY = sumYY()*invW - 2*meanY*y0 + y0*y0;
		
		return new double[]{
				weight, 
				meanX-x0, meanY-y0,
				sigmaXX, sigmaXY, sigmaYY
			};
	}
	
	public double[] normalizedMoments()
	{
		final double[] mu = this.moments();
		
		final double sigmaX = Math.sqrt(mu[WX2]);
		final double sigmaY = Math.sqrt(mu[WY2]);
		
		final double snrX = mu[WX] / sigmaX;
		final double snrY = mu[WY] / sigmaY;
		
		final double corr = mu[WXY] / (sigmaX * sigmaY);
		
		return new double[]{0.0, snrX, snrY, 1.0, corr, 1.0};
	}
	
	public static double lineness(double[] lambda)
	{
		return lambda[0]/lambda[1]; 
	}
	
	public static double roundness(double[] lambda)
	{
		return lambda[1]/lambda[0]; 
	}
	
	public static double pointness(double[] lambda)
	{
		return 1.0 / Math.hypot(lambda[0], lambda[1]); 
	}
	
	public static double spotness(double[] lambda)
	{
		return roundnessBeta(lambda)* pointness(lambda);
	}
	
	public double lineness()
	{
		return CovData.lineness(this.lambda());
	}
	
	public static double roundnessBeta(double[] lambda)
	{
		return 1.0 / Math.abs(lambda[0]-lambda[1]); 
	}
	
	public static MultiVarFunc moment_lineness()
	{
		return new MultiVarFunc(){

			@Override
			public double apply(double... mu) 
			{
				return CovData.lineness(CovData.lambda(mu));
			}
		};
	}
	
	public static MultiVarFunc moment_spotness()
	{
		return new MultiVarFunc(){

			@Override
			public double apply(double... mu) 
			{
				return CovData.spotness(CovData.lambda(mu));
			}
		};
	}

	
	public static MultiVarFunc moment_roundness()
	{
		return new MultiVarFunc(){

			@Override
			public double apply(double... mu) 
			{
				return CovData.roundness(CovData.lambda(mu));
			}
		};
	}
	
	public double[] lambda()
	{
		return CovData.lambda(this.moments());
	}
	
	public static double[] lambda(double[] moments)
	{
		final double sigmaXX = moments[WX2];
		final double sigmaXY = moments[WXY];
		final double sigmaYY = moments[WY2];
		
		final double summation = sigmaXX + sigmaYY;
		final double discriminant = Math.sqrt( Pow.two(sigmaXX-sigmaYY) + 4.0 * Pow.two(sigmaXY));
		
		final double lambdaMin = 0.5 * (summation - discriminant);
	    final double lambdaMax = 0.5 * (summation + discriminant);
	    
	    return new double[]{lambdaMax, lambdaMin};
	}
	
	public static MultiVarFunc minVar()
	{
		return new MultiVarFunc()
		{
			@Override
			public double apply(double... moments) 
			{
				double[] lambda = CovData.lambda(moments);
				return Math.min(lambda[0], lambda[1]);
			}
			
		};
	}
	
	public static MultiVarFunc maxVar()
	{
		return new MultiVarFunc()
		{
			@Override
			public double apply(double... moments) 
			{
				double[] lambda = CovData.lambda(moments);
				return Math.max(lambda[0], lambda[1]);
			}
			
		};
	}
	
	public double projAngle()
	{
		final double[] moments = this.moments();
		final double[] lambda = CovData.lambda(moments);
		final double sigmaXX = moments[WX2];
		final double sigmaXY = moments[WXY];
		final double lambdaMax = lambda[0];
		return Math.atan2( -sigmaXY, sigmaXX-lambdaMax );
	}
	
	public double[][] eigenvectors()
	{
		return CovData.eigenvectors(this.moments());
	}
		
	private static double[][] eigenvectors(double[] moments)
	{
		final double[] lambda = CovData.lambda(moments);
		
		return new double[][] {
				CovData.eigenvector(moments, lambda[0]),
				CovData.eigenvector(moments, lambda[1]) 
			};
	}
	
	private static double[] eigenvector(double[] moments, double lambda)
	{
		final double differ = moments[WXY] + lambda;
		return new double[]{moments[WY2] - differ, moments[WX2] - differ};
	}
	
	public double r()
	{
		return this.spErr() / (Math.sqrt(this.ssErrorX()) * Math.sqrt(this.ssErrorY()));
	}
	
	public double fisher()
	{
		return Hyperbolic.artanh(this.r());
	}
	
	public double[] r_CI95()
	{
		final double d = 1.96*this.standardErrorOfFisher();
		final double p = this.fisher();
		return new double[]{Cov.toPeasons(p-d), Cov.toPeasons(p+d)};
	}
	
	public double[] r_t95()
	{
		final double d = 1.96*this.standardErrorOfFisher();
		final double r = this.r();
		final double p = Cov.toFisher(r);
		return new double[]{Cov.toPeasons(p-d), r, Cov.toPeasons(p+d)};
	}
	
	public double[] fisher_t95()
	{
		final double d = 1.96*this.standardErrorOfFisher();
		final double r = this.r();
		final double p = Cov.toFisher(r);
		return new double[]{p-d, p, p+d};
	}
	
	public double[] fisher_pms()
	{
		final double d = this.standardErrorOfFisher();
		final double r = this.r();
		final double p = Cov.toFisher(r);
		return new double[]{Cov.toPeasons(p-d), r, Cov.toPeasons(p+d)};
	}
	
	public double probabilityOfPearsons()
	{
		return ZTest.twoTailedProbability(this.zFisher());
	}
	
	public double probabilityOfPearsons(double r0)
	{
		return ZTest.twoTailedProbability(this.zFisher(r0));
	}
	
	public double zFisher()
	{
		return this.fisher() / this.standardErrorOfFisher();
	}
	
	public double zFisher(double r0)
	{
		final double fisher0 = Hyperbolic.artanh(r0);
		return (this.fisher() - fisher0) / this.standardErrorOfFisher();
	}
	
	public static double zFisher(CovData src, CovData ref)
	{
		return (src.fisher() - ref.fisher()) / Math.sqrt(src.varianceOfFisher() + ref.varianceOfFisher());
	}
	
	public double tSlopeYatX(double slope0)
	{
		return (this.slopeYatX()-slope0) / this.standardErrorOfSlopeYatX();
	}
	
	public double tSlopeXatY(double slope0)
	{
		return (this.slopeXatY()-slope0) / this.standardErrorOfSlopeXatY();
	}
	
	public static double tSlope(CovData src, CovData ref)
	{
		final double varYatX_pooled = (src.ssResidualYatX() + ref.ssResidualYatX()) / (src.df2() + ref.df2());
		final double varSlope_pooled = varYatX_pooled * (1.0 / src.varX() / src.df() + 1.0 / ref.varX() / ref.df());
		return (src.slopeYatX() - ref.slopeYatX()) / Math.sqrt(varSlope_pooled);
	}
	
	public static double studentsTTestSlope(CovData src, CovData ref)
	{
		return Ccdf.t(CovData.tSlope(src, ref), (int)(src.df2() + ref.df2()));
	}
	
	public double standardErrorOfFisher()
	{
		return Math.sqrt(this.varianceOfFisher());
	}
	
	public double varianceOfFisher()
	{
		return 1.0 / (this.weight() - 3.0);
	}
	
	public double standardErrorOfSlopeYatX()
	{
		return Math.sqrt(this.varianceOfSlopeYatX());
	}
	
	public double standardErrorOfSlopeXatY()
	{
		return Math.sqrt(this.varianceOfSlopeXatY());
	}
	
	public double varianceOfSlopeYatX()
	{
		return this.varianceYdotX() / this.varX() / this.df();
	}
	
	public double varianceOfSlopeXatY()
	{
		return this.varianceXdotY() / this.varY() / this.df();
	}
	
	public double varianceYdotX()
	{
		return this.ssResidualYatX() / this.df2();
	}

	public double varianceXdotY()
	{
		return this.ssResidualXatY() / this.df2();
	}

	
	public double[] r2_t95()
	{
		return Op.sq(this.r_t95());
	}
	
	public double[] r2_pms()
	{
		return Op.sq(this.fisher_pms());
	}
	
	public double[] r2_CI95()
	{
		return Op.sq(this.r_CI95());
	}
	
	public double[] r2_CIA95()
	{
		double[] a = this.r2_CI95();
		return new double[]{(a[0]+a[1])/2, Math.abs(a[0]-a[1])/2};
	}
	
	@Override
	public void mult(double arg) 
	{
		Ip.mult(this.c, arg);	
	}

	@Override
	public void div(double arg) 
	{
		Ip.div(this.c, arg);
	}
	
	public static double[] directionCoefficients(double a, double b)
	{
		final double a2 = a * a;
		final double ab = a * b;
		final double b2 = b * b;
		return new double[]{
				1, 
				a, b, 
				a2, 2*ab, b2};
	}
	
	public static double[][] directionCoefficients(int period)
	{
		double[][] dirCoeffs = new double[period][];
		double theta = 2*Math.PI/period;
		Rect root = Rect.expi(theta);
		Rect phasor = new Rect(1.0, 0.0);
		for(int i = 0; i < period; i++)
		{
			dirCoeffs[i] = directionCoefficients(phasor.re(), phasor.im());
			phasor.mult(root);
		}
		return dirCoeffs;
	}
	
	@Override
	public int order() 
	{
		return 2;
	}
	
	public Line getLineSegmentYatX(Rectangle rect)
	{
		final double x0 = rect.getMinX();
		final double y0 = rect.getMinY();
		final double x1 = rect.getMaxX();
		final double y1 = rect.getMaxY();
		
		final double m = this.slopeYatX();
		final double b = this.interceptYatX();
		final double yB = m * x0 + b;
		final double yD = m * x1 + b;
		
		final double n = this.slopeXatY();
		final double a = this.interceptXatY();
		final double xA = n * y0 + a;
		final double xC = n * y1 + a;
		
		final boolean A = x0 < xA && xA < x1;
		final boolean C = x0 < xC && xC < x1;
		final boolean B = y0 <= yB && yB <= y1;
		final boolean D = y0 <= yD && yD <= y1;
		
		if(B)
		{
			if(D)
				return new Line(x0, yB, x1, yD);
			if(A)
				return new Line(x0, yB, xA, y0);
			if(C)
				return new Line(x0, yB, xC, y1);
		}
		if(D)
		{
			if(A)
				return new Line(xA, y0, x1, yD);
			if(C)
				return new Line(xC, y1, x1, yD);
		}
		if(A && C)
		{
			if(xA < xC)
				return new Line(xA, y0, xC, y1);
			else
				return new Line(xC, y1, xA, y0);
		}
		return null;
	}
	
	public EllipseRoi roi(double sd)
	{
		return roi(this.moments(), sd);
	}
	
	public static EllipseRoi roi(double[] moments, double sd)
	{
		final double[] lambda = CovData.lambda(moments);
		final double[] vector = CovData.eigenvector(moments, lambda[0]);
		final double[] scale = Op.apply(lambda, Math::sqrt);
		final double hypot = Math.hypot(vector[0], vector[1]);
		
		if(hypot < 0.5)
		{
			return new EllipseRoi(
					moments[WX] - sd * scale[0] + 0.5, 
					moments[WY] + 0.5, 
					moments[WX] + sd * scale[0] + 0.5, 
					moments[WY] + 0.5, 
					scale[1] / scale[0]
				);
		}
		else
		{
			Ip.mult(vector, sd * scale[0] / hypot);
		
			return new EllipseRoi(
					moments[WX]-vector[0] + 0.5, 
					moments[WY]-vector[1] + 0.5, 
					moments[WX]+vector[0] + 0.5, 
					moments[WY]+vector[1] + 0.5, 
					scale[1] / scale[0]
				);
		}
	}
	
	public SimpleLine getLineYatX()
	{
		return new SimpleLine(this.meanX(), this.meanY(), this.slopeYatX());
	}
	
	public Circle circleOfBestFit(double p, double q)
	{		
		final double fact = 1.0/weight();
		
		final double x = fact * sumX();
		final double y = fact * sumY();
		
		final double x2 = fact * sumXX();
		final double y2 = fact * sumYY();
		
		// u = x - p and v = y - q;
		final double u2 = x2 - 2*x*p + p*p;
		final double v2 = y2 - 2*y*q + q*q;
		
		final double r2 = u2 + v2;
		final double r = Math.sqrt(r2);
		
		return new Circle(r, p, q);
	}
	
	public double ssim()
	{
		final double c1 = Pow.two(0.01 * 65535);
		final double c2 = Pow.two(0.03 * 65535);
		return this.ssim(c1, c2);
	}
	
	public double ssim(final double c1, final double c2)
	{
		final double meanX = this.meanX();
		final double meanY = this.meanY();
		final double meanX2 = Pow.two(meanX);
		final double meanY2 = Pow.two(meanY);
		final double covXY = this.cov();
		final double varX = this.varX();
		final double varY = this.varY();
		return ((2*meanX*meanY+c1)/(meanX2+meanY2+c1))*((2*covXY+c2)/(varX+varY+c2));
	}

	@Override
	public CovData get() 
	{
		return new CovData();
	}
	
	public double density()
	{
		return this.weight() / Math.sqrt(this.sigmaXX() + this.sigmaYY());
	}
	
	public double manders()
	{
		return this.sumXY() / Math.sqrt(this.sumXX()) / Math.sqrt(this.sumYY());
	}

	@Override
	public boolean isFinite() 
	{
		return Stat.isFinite(this.c);
	}
	
	public double hotellingsT2(Tensor mu)
	{
		Tensor diff = Tensor.col(this.meanX(), this.meanY());
		diff.sub(mu);
		double sigmaXY = this.sigmaXY();
		Tensor inv_cov = Tensor.sqr(2, this.sigmaXX(), sigmaXY, sigmaXY, this.sigmaYY());
		inv_cov.inv2x2();
		return TensorTools.quadProd(diff, inv_cov, diff).rGet(0);
	}
	
	public double hotellingP()
	{
		return this.hotellingsP(Tensor.col(0.0, 0.0));
	}
	
	public double hotellingsP(Tensor mu)
	{
		final int p = 2;
		final int n = this.size();
		return t2toP(this.hotellingsT2(mu), n-1, n-p, p);
	}
	
	public double hotellingsT2()
	{
		return this.hotellingsT2(Tensor.col(0.0, 0.0));
	}
	
	public double hotellingsT2(CovData that)
	{
		Tensor diff = this.meanVec();
		diff.sub(that.meanVec());
		
		Tensor cov = this.covMat();
		Tensor cow = that.covMat();
		cov.mult(this.df());
		cow.mult(that.df());
		cov.add(cow);
		cov.div(this.df() + that.df());
		cov.div((double)(this.size() * that.size()) / (this.size() + that.size()));
		cov.inv2x2();
		return TensorTools.quadProd(diff, cov, diff).rGet(0);
	}
	
	public double hotellingsP(CovData that)
	{
		final int n = this.size() + that.size();
		return t2toP(this.hotellingsT2(that), n-2, n-3, 2);
	}
	
	public static double t2toP(double t2, int total_df, int within_df, int p)
	{
		return Ccdf.f(((double)within_df / total_df / p) * t2, p, within_df);
	}
	
	public double restrictedRegressionAngle(double x0, double y0)
	{
		double[] moments = moments(x0, y0);
		return 0.5*Math.atan(2*moments[WXY]/(moments[WX2]-moments[WY2]));
	}
	
	public double restrictedRegressionError(double x0, double y0)
	{
		double[] moments = moments(x0, y0);
		double theta = 0.5*Math.atan(2*moments[WXY]/(moments[WX2]-moments[WY2]));
		double sin = Math.sin(theta);
		double cos = Math.cos(theta);
		return sin*sin*moments[WX2]-2*sin*cos*moments[WXY]+cos*cos*moments[WXY]; 
	}
}
