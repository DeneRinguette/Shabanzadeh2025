package shabanzadeh2025.rend;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import shabanzadeh2025.util.Circle;
import shabanzadeh2025.util.Column;
import shabanzadeh2025.util.Err;
import shabanzadeh2025.util.Gen;
import shabanzadeh2025.util.IJTools;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.MeanData;
import shabanzadeh2025.util.MultiVarFunc;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Range;
import shabanzadeh2025.util.Significance;
import shabanzadeh2025.util.Stat;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.TreeCounter;
import shabanzadeh2025.util.Updatable;
import shabanzadeh2025.util.VarData;

import org.apache.commons.math3.stat.inference.TTest;

/**
 * Class is a generic plot for all plot types in two dimensional space.
 * 
 * @author Dene Ringuette
 */

public class Plot extends Draw
{
	protected int decimals = 3;	
	
	protected boolean scientific = false;
		
	protected int tickSize = 3;
	
	private Span data;
	
	private int tic_style = 0;
	
	public void tics_span()
	{
		this.tic_style = 0;
	}
	
	public void tics_out()
	{
		this.tic_style = -1;
	}
	
	public void tics_in()
	{
		this.tic_style = +1;
	}
	
	public Span getPlaneBox()
	{
		return this.data;
	}
	
	public Box getPlotBox()
	{
		return this.box;
	}
	
	public static Plot quick(double minX, double maxX, double minY, double maxY)
	{
		return new Plot(new Box(100, 100, 640, 480), new Span(minX, maxX, minY, maxY));
	}
	
	public static Plot quick(int xMargin, int yMargin, int xLength, int yLength, double minX, double maxX, double minY, double maxY)
	{
		return new Plot(new Box(xMargin, yMargin, xLength, yLength), new Span(minX, maxX, minY, maxY));
	}
	
	public static Plot quick(double maxX, double maxY)
	{
		return new Plot(new Box(100, 100, 640, 480), new Span(0, maxX, 0, maxY));
	}
	
	public static Plot quick()
	{
		return new Plot(new Box(100, 100, 640, 480), new Span(-2, 2, -2, 2));
	}
	
	public static void quick(double[] x, double[] y, File dst) throws IOException
	{
		final double[] xx = (x == null) ? Gen.range(0, 1, y.length-1) : x;
		final double xMin = Stat.min(xx);
		final double xMax = Stat.max(xx);
		final double yMin = Stat.min(y);
		final double yMax = Stat.max(y);
		Plot pp = new Plot(new Box(100, 100, 1280, 720), new Span(xMin, xMax, yMin, yMax));
		pp.rendWidth(2);
		pp.drawBox();
		pp.labelXCorrdinate(xMin);
		pp.labelXCorrdinate(xMax);
		pp.labelYCorrdinate(yMin);
		pp.labelYCorrdinate(yMax);
		pp.setRound();
		pp.rendColor("blue");
		pp.plotCurve(xx, y);
		SVG.writeToFile(pp.getSVG(), dst);
	}
	
	public static void quick(final File dst, double[] x, final double[]... y) throws IOException
	{
		final int n = y[0].length;
		
		x = (x == null) ? Gen.range(0, 1, n-1) : x;
		
		if(x.length != n)
			throw new IllegalArgumentException("ordinate absissa dimension mistmatch");
		
		if(!Stat.rect(y))
			throw new IllegalArgumentException("non-rectangular array in multiple curve");
		
		final double xMin = Stat.min(x);
		final double xMax = Stat.max(x);
		final double yMin = Stat.min(y);
		final double yMax = Stat.max(y);
		Plot pp = new Plot(new Box(100, 100, 1280, 720), new Span(xMin, xMax, yMin, yMax));
		pp.rendWidth(2);
		pp.drawBox();
		pp.labelXCorrdinate(xMin);
		pp.labelXCorrdinate(xMax);
		pp.labelYCorrdinate(yMin);
		pp.labelYCorrdinate(yMax);
		pp.setRound();
		String[] colors = SvgColor.getDefaultHSVDistinct(y.length);
		for(int i = 0; i < y.length; i++)
		{
			pp.rendColor(colors[i]);
			pp.plotCurve(x, y[i]);
		}
		SVG.writeToFile(pp.getSVG(), dst);
	}
	
	public Plot(Box plotBox, Span planeBox)
	{
		super(plotBox);
		this.data = planeBox;
	}
	
	public Plot(Box plotBox, Span planeBox, int numberOfGraphs)
	{
		super(plotBox, numberOfGraphs);
		this.data = planeBox;
	}
	
	public void newPlanePlot(Box plotBox, Span planeBox)
	{
		this.box = plotBox;
		this.data = planeBox;
	}
	
	public double dataX(double graphX)
	{
		double c = (graphX - box.left())/box.width();
		return c * (this.data.xMax - this.data.xMin) + this.data.xMin;
		
	}
	
	public double dataY(double graphY)
	{
		double c = (- graphY + box.bottom())/box.height();
		return c * (this.data.yMax - this.data.yMin) + this.data.yMin;
	}
	
	public void plotCircle(double cx, double cy, double r)
	{
		this.plotEllipse(cx,cy,r,r);
	}
	
	public void plotCircle(Circle c)
	{
		this.plotCircle(c.centerX(), c.centerY(), c.radius());
	}
	
	public void plotCurve(double[] x, double[] y)
	{
		this.drawCurve(this.graphX(x), this.graphY(y));
	}
	
	public void plotArea(double[] args, double[] val0, double[] val1)
	{
		this.drawCurve(
				this.graphX(Op.append(args, Op.flip(args))), 
				this.graphY(Op.append(val0, Op.flip(val1)))
			);
	}
	
	public void plotCurve(double[] x, double[][] y)
	{
		VarData[] z = VarData.columns(y);
		final int n = z.length;
		double[] y_mean = new double[n];
		double[] y_std = new double[n];
		for(int i = 0; i < n; i++)
		{
			y_mean[i] = z[i].mean();
			y_std[i] = z[i].stdDev();
		}
		this.plotCurve(x, y_mean, y_std);
	}
	
	public void plotCurves(double[] x, double[][] y)
	{
		for(int i = 0; i < y.length; i++)
			this.plotCurve(x, y[i]);
	}
	
	public void plotCurve(double[] x, double[] y, double[] e)
	{
		this.plotCurve(x, Op.sum(y, e));
		this.plotCurve(x, Op.diff(y, e));
	}
	
	public void plotSignificance(double[] x, VarData[]... y)
	{
		plotSignificance(x, false, 0, y);
	}
	
	public void plotSignificance(double[] x, boolean bonferroni, int baseline_neglect, VarData[]... y)
	{
		double[][] sizes = new double[y.length][];
		for(int i = 0; i < y.length; i++)
			sizes[i] = VarData.weights(y[i]);
		double[][] means = new double[y.length][];
		for(int i = 0; i < y.length; i++)
			means[i] = VarData.means(y[i]);
		double[][] stds_of_mean = new double[y.length][];
		for(int i = 0; i < y.length; i++)
			stds_of_mean[i] = VarData.stds_of_mean(y[i]);
		
		Ip.add(means, stds_of_mean);
		
		double[] max = Column.max(means);
		
		for(int i = baseline_neglect; i < x.length; i++)
		{
			double p = VarData.anova(i, y);
			if(bonferroni)
				p *= x.length - baseline_neglect;
			
			if(p < 0.001)
				this.plotStar(x[i], 0, max[i], 0, TextAlignment.TOP, "***");
			else if(p < 0.01)
				this.plotStar(x[i], 0, max[i], 0, TextAlignment.TOP, "**");
			else if(p < 0.05)
				this.plotStar(x[i], 0, max[i], 0, TextAlignment.TOP, "*");
			else
				;
		}
	}
	
	public void plotError(double[] x, VarData[] y)
	{
		this.plotCurve3(x, VarData.means(y), VarData.stds_of_mean(y));
	}
	
	public void plotCurve2(double[] x, double[] y, double[] e)
	{
		this.plotCurve(x, y);
		this.plotPoints(x, y);
		for(int i = 0; i < x.length; i++)
		{
			this.plotLine(x[i], y[i]-e[i], x[i], y[i]+e[i]);
			this.plotLine(x[i]-0.2, y[i]-e[i], x[i]+0.2, y[i]-e[i]);
			this.plotLine(x[i]-0.2, y[i]+e[i], x[i]+0.2, y[i]+e[i]);
		}
	}
	
	public void plotCurve3(double[] x, double[] y, double[] e)
	{
		this.plotCurve(x, y);
		for(int i = 0; i < x.length; i++)
		{
			final double d = (i == 0) ? (x[i+1]-x[i])/6: (x[i]-x[i-1])/6; // changed from 1/3 to 1/6 2022-04-12
			this.plotLine(x[i], y[i]-e[i], x[i], y[i]+e[i]);
			this.plotLine(x[i]-d, y[i]-e[i], x[i]+d, y[i]-e[i]);
			this.plotLine(x[i]-d, y[i]+e[i], x[i]+d, y[i]+e[i]);
		}
	}
	
	public void plotCurves(double[][] table)
	{
		for(int i = 1; i < table.length; i++)
			this.drawCurve(this.graphX(table[0]), this.graphY(table[i]));
	}
	
	public void plotCurvesNest(double[][] table)
	{
		for(int i = 1; i < table.length; i++)
			this.drawCurve(this.graphX(table[i-1]), this.graphY(table[i]));
	}
	
	public void plotCurve(float[] x, float[] y)
	{
		this.drawCurve(this.graphX(x), this.graphY(y));
	}
	
	public void plotCurve(double[] x, float[] y)
	{
		this.drawCurve(this.graphX(x), this.graphY(y));
	}
	
	public void plotCurve(SortedMap<Double, Double> xtoy)
	{
		this.drawCurve(this.graphX(xtoy.keySet()), this.graphY(xtoy.values()));
	}
	
	public void plotCurve(double... pairs)
	{
		this.plotCurve(Op.evens(pairs), Op.odds(pairs));
	}
	
	public void plotFunc(double[] x, DoubleUnaryOperator func)
	{
		this.plotCurve(x, Op.apply(x, func));
	}
	
	public void plotInvFunc(double[] x, DoubleUnaryOperator func)
	{
		this.plotCurve(Op.apply(x, func), x);
	}
	
	public void plotEllipse(double cx, double cy, double rx, double ry)
	{
		this.drawEllipse(this.graphX(cx), this.graphY(cy),
				this.graphXD(rx), this.graphYD(ry));
	}
	
	public void plotEllipse(double cx, double cy, double rx, double ry, double theta)
	{
		this.drawEllipse(this.graphX(cx), this.graphY(cy),
				this.graphXD(rx), this.graphYD(ry), theta);
	}
	
	public void plotArc(double cx, double cy, double rx, double ry, double a0, double a1, boolean _short)
	{
		this.drawArc(
				this.graphX(cx+rx*Math.cos(a0)), 
				this.graphY(cy+ry*Math.sin(a0)), 
				this.graphXD(rx), 
				this.graphYD(ry),
				this.graphX(cx+rx*Math.cos(a1)), 
				this.graphY(cy+ry*Math.sin(a1)),
				_short
			);
	}
	
	public void plotHeat(MultiVarFunc func, int xGaps, int yGaps, double min, double max)
	{
		Stroke old = this.rend.stroke;
		this.rendWidth(1);
		double xStep = (data.xMax - data.xMin)/xGaps;
		double width = this.graphXD(xStep);
		double yStep = (data.yMax - data.yMin)/yGaps;
		double height = this.graphYD(yStep);
		double midX = xStep/2;
		double midY = yStep/2;
		
		double x = data.xMin;
		for(int i = 0; i < xGaps; i++)
		{
			double x1 = this.graphX(x);
			double argX = x + midX;
			double y = data.yMax;
			for(int j = 0; j < yGaps; j++)
			{
				double y1 = this.graphY(y);
				double argY = y - midY;
				double val = func.apply(argX, argY);
				if(val < min)
					val = min;
				else
				if(max < val)
					val = max;
				double val255 = 255 * (val - min)/(max - min);
				int int255 = (int)val255;
				this.rendFill(int255, int255, int255);
				this.rendColor(int255, int255, int255);
				this.drawRect(x1, y1, width, height);
				y -= yStep;
			}
			x += xStep;
		}
		this.rend.stroke = old;
	}
	
	public void plotHistgram(double[] values, double start, double end)
	{
		double step = (end - start)/values.length;
		double x = start;
		for(int i = 0; i < values.length; i++)
			this.plotRect(x, 0, x += step, values[i]);
	}
	
	public void plotHorizontalLine(double y)
	{
		if(data.yMin < y || y < data.yMax)
			this.plotLine(data.xMin, y, data.xMax, y);
	}
	
	public void plotHorizontalLines(double point, double step)
	{
		this.plotHorizontalLine(point);
		double y;
		y = point;
		while((y += step) <= data.yMax)
			this.plotHorizontalLine(y);
		y = point;
		while(data.yMin <= (y -= step))
			this.plotHorizontalLine(y);
	}
	
	public void plotImplicit(MultiVarFunc func, int xGaps, int yGaps, double zero)
	{
		double xStep = (data.xMax - data.xMin)/xGaps;
		double yStep = (data.yMax - data.yMin)/yGaps;
		for(int i = 0; i <= xGaps; i++)
		{
			double xArg = data.xMin + i * xStep;
			for(int j = 0; j <= yGaps; j++)
			{	
				double yArg = data.yMin + j * yStep;
				if(Math.abs(func.apply(xArg, yArg)) < zero)
					this.plotPoint(xArg, yArg);
			}
		}
	}
	
	public void plotInnerAxis()
	{
		this.plotVerticalLine(0.0);
		this.plotHorizontalLine(0.0);
	}
	
	public void plotLine(double x1, double y1, double x2, double y2)
	{
		this.drawLine(this.graphX(x1), this.graphY(y1), 
			this.graphX(x2), this.graphY(y2));
	}	
	
	public void plotPoint(double x, double y)
	{
		this.drawCircle(this.graphX(x), this.graphY(y), this.rend.point);
	}
	
	public void plotPoint(Point2D.Double point)
	{
		this.plotPoint(point.x, point.y);
	}
	
	public void plotPoints(double[] x, double[] y)
	{
		for(int i = 0; i < Math.min(x.length, y.length); i++)
			this.plotPoint(x[i], y[i]);
	}
	
	public void plotCurve(double dx, double[] y)
	{
		final int n = y.length;
		
		final double[] x = new double[n];
		for(int i = 0; i < n; i++)
			x[i] = i * dx;
		
		this.plotCurve(x, y);
	}
	
	public void plotPoints(int bx, double[] x, int by, double[] y, String imageName)
	{
		FloatProcessor powerPro = new FloatProcessor(bx, by);
		for(int i = 0; i < Math.min(x.length, y.length); i++)
		{
			int xi = (int)(bx*this.data.normX(x[i]));
			int yi = by-(int)(by*this.data.normY(y[i]));
			powerPro.setf(xi, yi, powerPro.getf(xi, yi) + 1.0f);
		}
		double[] values = IJTools.toDoubles(powerPro);
		double std = Stat.std(values);
		double mean = Stat.mean(values);
		ByteProcessor powerProByte = IJTools.toByte(0, (float)(mean+std), powerPro);
		
		IJ.save(new ImagePlus("asdf", powerProByte), imageName);
		this.linkImage(imageName);
		
	}
	
	public void plotPoints(int bx, double[] x, int by, double[] y, String imageName, int n)
	{
		FloatProcessor powerPro = new FloatProcessor(bx, by);
		for(int i = 0; i < Math.min(x.length, y.length); i++)
		{
			int xi = (int)(bx*this.data.normX(x[i]));
			int yi = by-(int)(by*this.data.normY(y[i]));
			powerPro.setf(xi, yi, powerPro.getf(xi, yi) + 1.0f);
		}
		double[] values = IJTools.toDoubles(powerPro);
		double std = Stat.std(values);
		double mean = Stat.mean(values);
		ByteProcessor powerProByte = IJTools.toByte(0, (float)(mean+n*std), powerPro);
		
		IJ.save(new ImagePlus("asdf", powerProByte), imageName);
		this.linkImage(imageName);
		
	}
	
	public void plotPoints(int bx, double[] x, int by, double[] y, String imageName, boolean why)
	{
		final int n = x.length;
		if(y.length != n)
			throw new IllegalArgumentException("x-y mismatch: Length of 1st " + x.length + " != Length of 2nd " + y.length);
		FloatProcessor channel0 = new FloatProcessor(bx, by);
		FloatProcessor channel1 = new FloatProcessor(bx, by);
		
		for(int i = 200; i < n; i++)
		{
			int xi = (int)((bx-1)*this.data.normX(x[i]));
			int yi = (by-1)-(int)((by-1)*this.data.normY(y[i]));
			xi = Math.max(0, xi);
			xi = Math.min(xi, bx-1);
			yi = Math.max(0, yi);
			yi = Math.min(yi, by-1);
			
			if(x[i-200] < x[i])
				channel0.setf(xi, yi, channel0.getf(xi, yi) + 1.0f);
			else
				channel1.setf(xi, yi, channel1.getf(xi, yi) + 1.0f);
		}
		double[] values = IJTools.toDoubles(channel0);
		double std = Stat.std(values);
		double mean = Stat.mean(values);
		ByteProcessor red = IJTools.toByte(0, (float)(mean+std), channel0);
		
		values = IJTools.toDoubles(channel1);
		std = Stat.std(values);
		mean = Stat.mean(values);
		ByteProcessor green = IJTools.toByte(0, (float)(mean+std), channel1);
		
		ByteProcessor blue = new ByteProcessor(bx, by);
		
		ColorProcessor powerProColor = new ColorProcessor(bx, by);
		
		
		powerProColor.setRGB((byte[])red.getPixels(), (byte[])green.getPixels(), (byte[])blue.getPixels());
		
		IJ.save(new ImagePlus("asdf", powerProColor), imageName);
		this.linkImage(imageName);
		
	}
	
	public void plotPoints(double[] x, double[] y, double[] err)
	{
		double width = Math.abs(x[0]-x[1])/4;
		for(int i = 0; i < Math.min(x.length, y.length); i++)
			this.plotPoint(x[i], y[i]);
		for(int i = 0; i < Math.min(x.length, y.length); i++)
			this.plotLine(x[i], y[i]-err[i], x[i], y[i]+err[i]);
		for(int i = 0; i < Math.min(x.length, y.length); i++)
			this.plotLine(x[i]-width, y[i]+err[i], x[i]+width, y[i]+err[i]);
		for(int i = 0; i < Math.min(x.length, y.length); i++)
			this.plotLine(x[i]-width, y[i]-err[i], x[i]+width, y[i]-err[i]);		
	}
	
	public void plotPoints(double[] x, double[] y, double[] ex, double[] ey)
	{
		Fill old = this.rend.fill;
		this.rend.fill = this.rend.fill.changeColor("none");
		for(int i = 0; i < Math.min(x.length, y.length); i++)
		{
			this.plotEllipse(x[i], y[i], ex[i], ey[i]);
		}
		this.rend.fill = old;
	}
	
	public void plotRect(double x1, double y1, double x2, double y2)
	{
		double x = this.graphX(Math.min(x1, x2));
		double y = this.graphY(Math.max(y1, y2));
		double width = this.graphXD(Math.abs(x2-x1));
		double height = this.graphYD(Math.abs(y2-y1));
		this.drawRect(x, y, width, height);
	}
	
	public void highlightDomain(double x1, double x2)
	{
		double x = this.graphX(Math.min(x1, x2));
		double y = this.graphY(this.data.yMax);
		double width = this.graphXD(Math.abs(x2-x1));
		double height = this.graphYD(Math.abs(this.data.yMax-this.data.yMin));
		this.drawRect(x, y, width, height);
	}
	
	public void plotImage(double x1, double x2, double y1, double y2, String name)
	{
		double x = this.graphX(Math.min(x1, x2));
		double y = this.graphY(Math.max(y1, y2));
		double width = this.graphXD(Math.abs(x2-x1));
		double height = this.graphYD(Math.abs(y2-y1));
		this.linkImage(x, y, width, height, name);
	}
	
	public void plotImage(String name)
	{
		this.plotImage(this.data.xMin, this.data.xMax, this.data.yMin, this.data.yMax, name);
	}

	public void plotText(Point2D.Double point, TextAlignment align, String text)
	{
		this.plotText(point.x, point.y, align, text);
	}
		
	public void plotText(double dataX, double dataY, TextAlignment align, String text)
	{
		this.drawText(this.graphX(dataX), this.graphY(dataY), align, text);
	}
	
	public void plotText(double dataX, double dataY, String text)
	{
		this.drawText(this.graphX(dataX), this.graphY(dataY), text);
	}
	
	public void plotStar(double dataX, double dataY, TextAlignment align, String text)
	{
		this.drawStar(this.graphX(dataX), this.graphY(dataY), align, text);
	}
	
	public void plotStar(double dataX, int shiftX, double dataY, int shiftY, TextAlignment align, String text)
	{
		this.drawStar(this.graphX(dataX)+shiftX, this.graphY(dataY)-shiftY, align, text);
	}
	
	public void plotText(double dataX, int shiftX, double dataY, int shiftY, TextAlignment align, String text)
	{
		this.drawText(this.graphX(dataX)+shiftX, this.graphY(dataY)-shiftY, align, text);
	}
	
	public void plotVector(Function<double[], double[]> func, int xGaps, int yGaps, double min, double max)
	{
		String oldColor = this.rend.stroke.color;
		double xStep = (data.xMax - data.xMin)/xGaps;
		double yStep = (data.yMax - data.yMin)/yGaps;
		double midX = xStep/2;
		double midY = yStep/2;
		
		double fact;
		double width = data.normDiffX(xStep) * box.width();
		double height = data.normDiffY(yStep) * box.height();
		if(width < height)
			fact = xStep/3;
		else
			fact = yStep/3;
		
		
		double x = data.xMin;
		for(int i = 0; i < xGaps; i++)
		{
			double argX = x + midX;
			double y = data.yMax;
			for(int j = 0; j < yGaps; j++)
			{
				double argY = y - midY;
				double[] vec = func.apply(new double[]{argX, argY});
				double mag = Math.hypot(vec[0], vec[1]);
				if(mag < min)
					mag = min;
				else
				if(max < mag)
					mag = max;
				double val255 = 255 * (mag - min)/(max - min);
				int int255 = 255 - (int)val255;
				this.rendColor(int255, int255, int255);
				double dx = 0;
				double dy = 0;
				if(0 < mag)
				{
					dx = (vec[0]/mag)*fact;
					dy = (vec[1]/mag)*fact;
				}
				this.plotLine(argX - dx, argY - dy, argX + dx, argY + dy);
				y -= yStep;
			}
			x += xStep;
		}
		this.rendColor(oldColor);
	}
	
	public void plotVerticalLine(double x)
	{
		if(data.xMin < x && x < data.xMax)
			this.plotLine(x, data.yMin, x, data.yMax);
	}
	
	public void plotVerticalLines(double[] args)
	{
		for(double arg : args)
			this.plotVerticalLine(arg);
	}
	
	public void plotUpArrow(double x, double y)
	{
		if(data.xMin < x && x < data.xMax)
		{
			double gx = this.graphX(x);
			double gy = this.graphY(y)+10;
			this.drawLine(gx, gy, gx, gy+20);
			this.drawCurve(new double[]{gx-5, gx, gx+5}, new double[]{gy+10, gy, gy+10});
			
		}
	}
	
	public void plotDownArrow(double x, double y)
	{
		if(data.xMin < x && x < data.xMax)
		{
			double gx = this.graphX(x);
			double gy = this.graphY(y)-10;
			this.drawLine(gx, gy, gx, gy-20);
			this.drawCurve(new double[]{gx-5, gx, gx+5}, new double[]{gy-10, gy, gy-10});
		}
	}

	public void plotVerticalScaleBar(double range)
	{
		double half = range/2.0;
		double mean = (data.yMax + data.yMin)/2.0;
		this.drawLine(this.graphX(data.xMax) + this.tickSize, this.graphY(mean-half), 
				this.graphX(data.xMax) + this.tickSize, this.graphY(mean+half));
	}

	
	public void plotVerticalLines(double point, double step)
	{
		double x;
		x = point;
		this.plotVerticalLine(x);
		while((x += step) <= data.xMax)
			this.plotVerticalLine(x);
		x = point;
		while(data.xMin <= (x -= step))
			this.plotVerticalLine(x);
	}
	
	public void distance(double x1, double y1 , double x2, double y2, double width)
	{
		double nx = y1-y2;
		double ny = x2-x1;
		double mag = Math.hypot(nx, ny);
		nx /= mag;
		ny /= mag;
		nx *= width;
		ny *= width;
		this.plotLine(x1, y1, x2, y2);
		this.plotLine(x1 + nx, y1 + ny, x1 - nx, y1 - ny);
		this.plotLine(x2 + nx, y2 + ny, x2 - nx, y2 - ny);
	}
	
	public void labelXCorrdinate(double dataX)
	{
		double low = box.bottom() + this.rend.text.size() + this.tickSize;
		double neg = (dataX < 0) ? this.rend.text.size()*.2 : 0;
		if(scientific)
			this.drawText(this.graphX(dataX)-neg, low, TextAlignment.BOTTOM, "" + Format.scientific(dataX, this.decimals));
		else
			this.drawText(this.graphX(dataX)-neg, low, TextAlignment.BOTTOM, "" + Format.decimals(dataX, this.decimals));
	}
	
	public void labelXCorrdinateTop(double dataX)
	{
		double low = box.top() - this.tickSize;
		if(scientific)
			this.drawText(this.graphX(dataX), low, TextAlignment.BOTTOM, "" + Format.scientific(dataX, this.decimals));
		else
			this.drawText(this.graphX(dataX), low, TextAlignment.BOTTOM, "" + Format.decimals(dataX, this.decimals));
	}
	
	public void labelXCorrdinateTop(double dataX, String datalabel)
	{
		double low = box.top() - this.rend.text.size() - this.tickSize;
		this.drawText(this.graphX(dataX), low, TextAlignment.BOTTOM, datalabel);
	}
	
	public void labelXCorrdinate(double dataX, String datalabel)
	{
		double low = box.bottom() + this.rend.text.size() + this.tickSize;
		this.drawText(this.graphX(dataX), low, TextAlignment.BOTTOM, datalabel);
	}
	
	public void labelYCorrdinate(double dataY)
	{
		double low = box.left() - this.rend.text.size()/4 - this.tickSize;
		if(scientific)
			this.drawText(low, this.graphY(dataY)+this.rend.text.size()*.35, TextAlignment.Y_AXIS, "" + Format.scientific(dataY, this.decimals));
		else
			this.drawText(low, this.graphY(dataY)+this.rend.text.size()*.35, TextAlignment.Y_AXIS, "" + Format.decimals(dataY, this.decimals));
	}
	
	public void labelYCorrdinateRight(double dataY)
	{
		double low = box.right() + this.rend.text.size()/2 + this.tickSize;
		if(scientific)
			this.drawText(low, this.graphY(dataY), TextAlignment.RIGHT, "" + Format.scientific(dataY, this.decimals));
		else
			this.drawText(low, this.graphY(dataY), TextAlignment.RIGHT, "" + Format.decimals(dataY, this.decimals));
	}
	
	public void labelYCorrdinateRight(double dataY, String param)
	{
		double low = box.right() + this.rend.text.size()/2 + this.tickSize;
		this.drawText(low, this.graphY(dataY), TextAlignment.RIGHT, "" + param);
	}
	
	public void ledgend(int step, String name)
	{
		this.drawCircle(box.right() + 4 * this.tickSize, box.top() + step * this.rend.text.size(), this.rend.text.size()/4.0);
		this.drawText(box.right() + 5 * this.tickSize, box.top() + step * this.rend.text.size(), TextAlignment.RIGHT, name);
	}
	
	public void ledgend2(int step, String name)
	{
		this.drawLine(
				box.right() + 3 * this.tickSize, 
				box.top() + step * this.rend.text.size(), 
				box.right() + 3 * this.tickSize + this.rend.text.size(), 
				box.top() + step * this.rend.text.size()
			);
		this.drawText(
				box.right() + 6 * this.tickSize, 
				box.top() + step * this.rend.text.size(), 
				TextAlignment.RIGHT, 
				name
			);
	}
	
	public void ledgendText(int step, String name)
	{
		this.drawText(
				box.right() + 5 * this.tickSize, 
				box.top() + step * this.rend.text.size(), 
				TextAlignment.RIGHT, 
				name
			);
	}
	
	public void labelVertScale(String name)
	{
		
		double low = box.right() + this.rend.text.size()/2 + this.tickSize;
		this.drawText(low, box.middleY(), TextAlignment.RIGHT, name);
	}
	
	public void labelXCorrdinates(double point, double step)
	{
		double x;
		x = point;
		this.labelXCorrdinate(x);
		while((x += step) <= data.xMax)
			this.labelXCorrdinate(x);
		x = point;
		while(data.xMin <= (x -= step))
			this.labelXCorrdinate(x);
	}
	
	public void labelYCorrdinates(double point, double step)
	{
		double y;
		y = point;
		this.labelYCorrdinate(y);
		while((y += step) <= data.yMax)
			this.labelYCorrdinate(y);
		y = point;
		while(data.yMin <= (y -= step))
			this.labelYCorrdinate(y);
	}
	
	public void labelXCorrdinates(double[] points)
	{
		for(double point : points)
			this.labelXCorrdinate(point);
	}
	
	public void labelYCorrdinates(double[] points)
	{
		for(double point : points)
			this.labelYCorrdinate(point);
	}
		
	public void labelCorner(String label)
	{
		this.drawText(box.left() - 2 * this.tickSize, box.top(), TextAlignment.LEFT, label);
	}
	
	public void labelYCorrdinatesRight(double point, double step)
	{
		double y;
		y = point;
		this.labelYCorrdinateRight(y);
		while((y += step) <= data.yMax)
			this.labelYCorrdinateRight(y);
		y = point;
		while(data.yMin <= (y -= step))
			this.labelYCorrdinateRight(y);
	}
	
	public void xTic(double dataX)
	{
		double graphX = this.graphX(dataX);
		double high = box.bottom() - ((this.tic_style < 0) ? 0 : this.tickSize);
		double low = box.bottom() + ((this.tic_style > 0) ? 0 : this.tickSize);
		this.drawLine(graphX, low , graphX, high);
	}
	
	public void xMarkup(double point, double step, int precision)
	{
		final int temp = this.decimals;
		this.xTics(point, step);
		this.setAxisPrecision(precision);
		this.labelXCorrdinates(point, step);
		this.setAxisPrecision(temp);
	}
	
	public void yMarkup(double point, double step, int precision)
	{
		this.yTics(point, step);
		if(precision > -1)
		{
			final int temp = this.decimals;
			this.setAxisPrecision(precision);
			this.labelYCorrdinates(point, step);
			this.setAxisPrecision(temp);
		}
	}
	
	public void xTics(double point, double step)
	{
		double x;
		x = point;
		this.xTic(x);
		while((x += step) <= data.xMax)
			this.xTic(x);
		x = point;
		while(data.xMin <= (x -= step))
			this.xTic(x);
	}
	
	public void yTic(double dataY)
	{
		double graphY = this.graphY(dataY);
		double left = box.left() - ((this.tic_style > 0) ? 0 : this.tickSize);
		double right = box.left() + ((this.tic_style < 0) ? 0 : this.tickSize);
		this.drawLine(left, graphY, right, graphY);
	}
	
	public void yTics(double point, double step)
	{
		double y;
		y = point;
		this.yTic(y);
		while((y += step) <= data.yMax)
			this.yTic(y);
		y = point;
		while(data.yMin <= (y -= step))
			this.yTic(y);
	}
	
	public void yTics(double[] points)
	{
		for(double point : points)
			this.yTic(point);
	}
	
	
	public void yTicR(double dataY)
	{
		double graphY = this.graphY(dataY);
		double left = box.right() - this.tickSize;
		double right = box.right() + this.tickSize;
		this.drawLine(left, graphY, right, graphY);
	}
	
	public void yTicsR(double point, double step)
	{
		double y;
		y = point;
		this.yTicR(y);
		while((y += step) <= data.yMax)
			this.yTicR(y);
		y = point;
		while(data.yMin <= (y -= step))
			this.yTicR(y);
	}
	
	public void autoTicsY(boolean label)
	{
		double max = data.yMax;
		double min = data.yMin;
		double range = max-min;
		double step = Math.pow(10, (int)(Math.log10(range)));
		if(Math.abs(range/step) < 5)
			step /= 2.0;
		if(Math.abs(range/step) < 2)
			step /= 5.0;
		if(Math.abs(range/step) < 5)
			step /= 2.0;
		double start = (step-min%step) + min;
		this.yTics(start, step);
		if(label)
			this.labelYCorrdinates(start, step);
	}
	
	public void autoTicsX(boolean label)
	{
		this.autoTicsX(label, 1.0);
	}
	
	public void autoTicsX(boolean label, double factor)
	{
		double max = data.xMax;
		double min = data.xMin;
		double range = max-min;
		double step = Math.pow(10, (int)(Math.log10(range)));
		if(Math.abs(range/step) < 5)
			step /= 2.0;
		if(Math.abs(range/step) < 2)
			step /= 5.0;
		if(Math.abs(range/step) < 5)
			step /= 2.0;
		double start = (step-min%step) + min;
		this.xTics(start, step);
		if(label)
			this.labelXCorrdinates(start, step);
	}
		
	private double graphYD(double arg)
	{
		return data.normDiffY(arg) * box.height();
	}
	
	private double graphXD(double arg)
	{
		return data.normDiffX(arg) * box.width();
	}
	
	public double graphX(double arg)
	{
		return box.left() + data.normX(arg) * box.width();
	}
	
	public double graphDx(double arg)
	{
		return data.normX(arg) * box.width();
	}
		
	private double[] graphX(double[] x)
	{
		double[] data = new double[x.length];
		for(int i = 0; i < x.length; i++)
			data[i] = this.graphX(x[i]);
		return data;
	}
	
	private double[] graphX(float[] x)
	{
		double[] data = new double[x.length];
		for(int i = 0; i < x.length; i++)
			data[i] = this.graphX(x[i]);
		return data;
	}
	
	private double[] graphX(Collection<Double> x)
	{
		double[] data = new double[x.size()];
		int i = 0;
		for(Double value : x)
			data[i++] = this.graphX(value);
		return data;
	}
	
	public double graphY(double arg)
	{
		return box.bottom() - data.normY(arg) * box.height();
	}
		
	private double[] graphY(double[] y)
	{
		double[] data = new double[y.length];
		for(int i = 0; i < y.length; i++)
			data[i] = this.graphY(y[i]);
		return data;
	}
	
	private double[] graphY(float[] y)
	{
		double[] data = new double[y.length];
		for(int i = 0; i < y.length; i++)
			data[i] = this.graphY(y[i]);
		return data;
	}
	
	private double[] graphY(Collection<Double> y)
	{
		double[] data = new double[y.size()];
		int i = 0;
		for(Double value : y)
			data[i++] = this.graphY(value);
		return data;
	}
		
	public void setTickSize(int size)
	{
		this.tickSize = size;
	}
	
	public void setAxisPrecision(int dec)
	{
		this.decimals = dec;
	}
	
	public void scientific()
	{
		this.scientific = true;
	}

	public void decimal()
	{
		this.scientific = false;
	}
	
	public void plotColumnScatters(double[] x, double[][] value)
	{
		final int n = x.length;
		if(n != value.length)
			throw new IllegalStateException("Not applicable if sub categories used.");

		for(int i = 0; i < n; i++)
			this.plotColumnScatter(x[i], value[i]);	
	}
	
	public void plotColumnScatter(double point, double[] data)
	{
		final int n = data.length;
		final double rad = this.rend.point;
		
		double[] values = new double[n];
		double[] offset = new double[n];
		for(int i = 0 ; i < n; i++)
			values[i] = this.graphY(data[i]);
		Arrays.sort(values);
		boolean overlap_found;
		do
		{
			overlap_found = false;
			for(int i = 0 ; i < n-1; i++)
				if(Math.hypot(values[i] - values[i+1], offset[i] - offset[i+1]) < 1.5 * rad)
				{
					offset[i] += i%2 == 0 ? 1.0*rad : - 1.0*rad;
					overlap_found = true;
				}
		}
		while(overlap_found);
		
		final String radius = Double.toString(rad);
		for(int i = 0 ; i < n; i++)
			this.drawCircle(str(this.graphX(point)+offset[i]), str(values[i]), radius);
	}
	
	public void plotColumnScatter(double point, double[] data, DoublePredicate cond)
	{
		final int n = data.length;
		final double rad = this.rend.point;
		
		double[] values = new double[n];
		double[] offset = new double[n];
		for(int i = 0 ; i < n; i++)
			values[i] = this.graphY(data[i]);
		Arrays.sort(values);
		boolean overlap_found;
		do
		{
			overlap_found = false;
			for(int i = 0 ; i < n-1; i++)
				if(Math.hypot(values[i] - values[i+1], offset[i] - offset[i+1]) < 2 * rad)
				{
					offset[i] += i%2 == 0 ? 1.5*rad : - 1.5*rad;
					overlap_found = true;
				}
		}
		while(overlap_found);
		
		final String radius = Double.toString(rad);
		for(int i = 0 ; i < n; i++)
			if(cond.test(values[i]))
				this.drawCircle(str(this.graphX(point)+offset[i]), str(values[i]), radius);
	}
	
	public void plotColumnScatterDegenerate(double[][] data2, String[] color)
	{
		long max = 0;
		List<TreeMap<Double, Long>> list = new ArrayList<TreeMap<Double, Long>>(5); 
		for(double[] data : data2)
		{
			TreeCounter<Double> counter = new TreeCounter<Double>();
			for(double value : data)
				counter.count(value);
			
			TreeMap<Double, Long> map = counter.toMap();
			
			list.add(map);
			
			for(Long entry : map.values())
				max = Math.max(max, entry);
		}
		
		final double gap = 0.8/(max-1);
		
		for(int c = 0; c < data2.length; c++)
		{
			TreeMap<Double, Long> map = list.get(c);
			this.rendFill(color[c]);
			
			for(Entry<Double, Long> entry : map.entrySet())
			{
				final long count = entry.getValue();
				final double offset = c - 0.4 * gap * count;
				
				for(long i = 0; i < count; i++)
					this.plotPoint(offset + i * gap, entry.getKey());
			}
		}
	}
	
	public void circleGridPlot(MeanData[][] mean, int[] sizes, int print)
	{
		int rows = mean.length;
		int cols = mean[0].length;		
		for(int group = 0; group < rows; group++)
			for(MeanData data : mean[group])
				data.setPopulation(sizes[group]);
		
		if(print > 0)
			Syo.pl("table");
		Fill temp = this.rend.fill;
		for(int x = 0; x < cols; x += 1)
		{
			MeanData[] col = Column.get(mean, x);
			Range size = Updatable.update(new Range(), col, (MeanData d) -> d.sampleFraction());
			double max_size = size.max();
			
			for(int y = 0; y < rows; y += 1)
			{
				final double zz = col[y].mean()-col[0].mean();
				final double z = 255*zz;
				final int s = Math.min(255, (int)Math.abs(z));
				this.rendFill(255 - (z >= 0 ? 0 : s), 255 - s, 255 - (z >= 0 ? s : 0));
				final double r = Math.sqrt(mean[y][x].sampleFraction()/max_size);
				this.plotCircle(x, rows-y-1.0, 0.45*r);
				if(print == 1)
					Syo.p(mean[y][x].sampleFraction()/max_size+ "\t");
				if(print == 2)
					Syo.p(zz + "\t");
			}
			if(print > 0)
				Syo.pl();
		}
		this.rend.fill = temp;
	}
		
	public void plotColumnBars(double[][] value, String[] color, double width)
	{
		final double e = width/2;
		String temp = this.rend.fill.color;
		for(int i = 0; i < value.length; i++)
		{
			VarData bar = new VarData(value[i]);
			final double mean = bar.mean();
			
			this.rendFill(color[i]);
			this.plotRect(i-e, mean, i+e, 0.0);
		}
		this.rendFill(temp);
	}
	
	public void plotColumnSems(double[][] value, double width)
	{
		final double d = width/4;
		final double e = width/2;
		for(int i = 0; i < value.length; i++)
		{
			VarData bar = new VarData(value[i]);
			final double mean = bar.mean();
			final double sem = bar.sem();
			
			final double top = mean+sem;
			final double mid = mean;
			final double bot = mean-sem;
			
			this.plotLine(i+0, top, i+0, bot);
			this.plotLine(i-d, top, i+d, top);
			this.plotLine(i-e, mid, i+e, mid);
			this.plotLine(i-d, bot, i+d, bot);
			
		}
	}
	
	public void plotColumnQuartiles(double[][] value, double width)
	{
		final double r = width/2;
		for(int i = 0; i < value.length; i++)
		{
			final double[] q = Stat.quartiles(value[i]);
			
			final double min = q[0];
			final double bot = q[1];
			final double mid = q[2];
			final double top = q[3];
			final double max = q[4];
			
			this.plotRect(i-r, bot, i+r, top);
			this.plotLine(i-r, mid, i+r, mid);
			this.plotLine(i+0, top, i+0, max);
			this.plotLine(i+0, bot, i+0, min);
		}
	}
	
	public void plotColumnStdDev(double[][] value, double width)
	{
		final double d = width/4;
		final double e = width/2;
		for(int i = 0; i < value.length; i++)
		{
			VarData bar = new VarData(value[i]);
			final double mean = bar.mean();
			final double err = bar.stdDev();
			
			final double top = mean+err;
			final double mid = mean;
			final double bot = mean-err;
			
			this.plotLine(i+0, top, i+0, bot);
			this.plotLine(i-d, top, i+d, top);
			this.plotLine(i-e, mid, i+e, mid);
			this.plotLine(i-d, bot, i+d, bot);
			
		}
	}
	
	public void plotColumnScatter(double[][] value, String[] color)
	{
		final String temp = this.rend.fill.color;
		for(int i = 0; i < value.length; i++)
		{
			this.rendFill(color[i]);
			this.plotColumnScatter(i, value[i]);	
		}
		this.rendFill(temp);
	}
	
	public void plotColumnScatter(double[][] value)
	{
		for(int i = 0; i < value.length; i++)
			this.plotColumnScatter(i, value[i]);
	}
	
	public void plotColumnScatter(List<double[]> value)
	{
		for(int i = 0; i < value.size(); i++)
			this.plotColumnScatter(i, value.get(i));
	}
	
	public void plotColumnScatter(double[][] value, DoublePredicate cond)
	{
		for(int i = 0; i < value.length; i++)
			this.plotColumnScatter(i, value[i], cond);
	}
	
	public void plotColumnStars(double[][] value, int[] pairs, boolean fix)	
	{
		if(fix)
			for(int i = 0; i < pairs.length; i += 2)
			{
				final int a = pairs[i];
				final int b = pairs[i+1];
				VarData data_a = new VarData(value[a]);
				VarData data_b = new VarData(value[b]);
				final double height = Math.max(Stat.max(value[a]), Stat.max(value[b]));
				this.plotLine(a, height, b, height);
				final double p = new VarData(data_b).studentsTtest(data_a.mean());
				String sig = Significance.toStars3(p);
				this.plotStar(0.5*(a+b), height, TextAlignment.TOP, sig);
			}
		else
			for(int i = 0; i < pairs.length; i += 2)
			{
				final int a = Math.min(pairs[i], pairs[i+1]);
				final int b = Math.max(pairs[i], pairs[i+1]);
				final double height = 1.1 * Math.max(Stat.max(value[a]), Stat.max(value[b]));
				this.plotLine(a, height, b, height);
				double p = new TTest().homoscedasticTTest(value[a], value[b]);
				String sig = Significance.toStars3(p);
				this.plotStar(0.5*(a+b), height, TextAlignment.TOP, sig);
				boolean print_ratio = true;
				if(print_ratio)
				{
					double[] ratio = Err.div(value[pairs[i+1]], value[pairs[i]]);
					ratio[0] = ratio[0] - 1.0;
					Ip.mult(ratio, 100);
					Syo.pl("v("+pairs[i+1]+")/v("+pairs[i]+")="+ratio[0]+"+-"+ratio[1] + ", p="+p);
				}
			}
	
	}
	
	public static void printRatio(double[][] value, int[] pairs, boolean fix)
	{
		
		if(fix)
			for(int i = 0; i < pairs.length; i += 2)
			{
				final int a = pairs[i];
				final int b = pairs[i+1];
				final double p = new TTest().tTest(Stat.mean(value[a]), value[b]);
				
				double[] ratio = Op.div(value[pairs[i+1]], Stat.mean(value[pairs[i]]));
				ratio[0] = ratio[0] - 1.0;
				Ip.mult(ratio, 100);
				System.out.println("v("+pairs[i+1]+")/v("+pairs[i]+")="+ratio[0]+"+-"+ratio[1] + ", p="+p);
			}
		else
			for(int i = 0; i < pairs.length; i += 2)
			{
				final int a = Math.min(pairs[i], pairs[i+1]);
				final int b = Math.max(pairs[i], pairs[i+1]);
				final double p = new TTest().homoscedasticTTest(value[a], value[b]);
				
				double[] ratio = Err.div(value[pairs[i+1]], value[pairs[i]]);
				ratio[0] = ratio[0] - 1.0;
				Ip.mult(ratio, 100);
				System.out.println("v("+pairs[i+1]+")/v("+pairs[i]+")="+ratio[0]+"+-"+ratio[1] + ", p="+p);
			}
	}
	
	public void plotColumnPValues(double[][] value, int[] pairs, boolean fix)	
	{
		if(fix)
			for(int i = 0; i < pairs.length; i += 2)
			{
				final int a = pairs[i];
				final int b = pairs[i+1];
				final double height = 1.1 * Math.max(Stat.max(value[a]), Stat.max(value[b]));
				this.plotLine(a, height, b, height);
				final double p = new TTest().tTest(Stat.mean(value[a]), value[b]);
				this.drawText(this.graphX(0.5*(a+b)), this.graphY(height)-this.rend.text.size()/3, TextAlignment.TOP, Significance.toFourDecimals(p));
			}
		else
			for(int i = 0; i < pairs.length; i += 2)
			{
				final int a = Math.min(pairs[i], pairs[i+1]);
				final int b = Math.max(pairs[i], pairs[i+1]);
				final double height = 1.1 * Math.max(Stat.max(value[a]), Stat.max(value[b]));
				this.plotLine(a, height, b, height);
				final double p = new TTest().homoscedasticTTest(value[a], value[b]);
				this.drawText(this.graphX(0.5*(a+b)), this.graphY(height)-this.rend.text.size()/3, TextAlignment.TOP, Significance.toFourDecimals(p));
			}
	}
	
	public void plotColumnPairedPValues(double[][] value, int[] pairs)	
	{
		for(int i = 0; i < pairs.length; i += 2)
		{
			final int a = Math.min(pairs[i], pairs[i+1]);
			final int b = Math.max(pairs[i], pairs[i+1]);
			final double height = 1.1 * Math.max(Stat.max(value[a]), Stat.max(value[b]));
			this.plotLine(a, height, b, height);
			final double p = new TTest().pairedTTest(value[a], value[b]);
			this.drawText(this.graphX(0.5*(a+b)), this.graphY(height)-this.rend.text.size()/3, TextAlignment.TOP, Significance.toFourDecimals(p));
		}
	}
	
	public void plotColumnPairedStars(double[][] value, int[] pairs)	
	{
		for(int i = 0; i < pairs.length; i += 2)
		{
			final int a = Math.min(pairs[i], pairs[i+1]);
			final int b = Math.max(pairs[i], pairs[i+1]);
			
			final double height = Math.max(Stat.max(value[a]), Stat.max(value[b]));
			this.plotLine(a, height, b, height);
			double p = new TTest().pairedTTest(value[a], value[b]);
			String sig = Significance.toStars3(p);
			this.plotStar(0.5*(a+b), height, TextAlignment.TOP, sig);
		}
	}

	public void labelColumns(String... names)
	{
		for(int i = 0; i < names.length; i++)
			this.labelXCorrdinate(i, names[i]);					
	}
	
	public void labelColumns(List<String> names)
	{
		for(int i = 0; i < names.size(); i++)
			this.labelXCorrdinate(i, names.get(i));					
	}
	
	public void labelVertical(String name)
	{
		Point label = this.box.yLabel();
		this.drawText(label.x, label.y, TextAlignment.OVER, name);
	}
	
	public void labelTop(String name)
	{
		Point label = this.box.topLabel();
		this.drawText(label.x, label.y, TextAlignment.OVER, name);
	}
}
