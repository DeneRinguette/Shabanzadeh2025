package shabanzadeh2025.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.function.Supplier;

import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class GaplessPath implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final byte[] s;
	
	private int x, y;
	
	private final int a, b;
	
	private int i;
	
	public GaplessPath(final int xo, final int yo, final byte[] steps)
	{
		this.i = 0;
		this.a = xo;
		this.b = yo;
		this.x = xo;
		this.y = yo;
		this.s = steps;
	}
	
	public GaplessPath translate(int dx, int dy)
	{
		return new GaplessPath(this.a + dx, this.b + dy, this.s);
	}
	
	public void reset()
	{
		this.i = 0;
		this.x = this.a;
		this.y = this.b;
	}
	
	public boolean next()
	{
		this.step();
		return this.hasNext();
	}
	
	public boolean hasNext()
	{
		return this.i < this.s.length;
	}
	
	public int size()
	{
		if(this.s == null)
			return 1;
		return this.s.length + 1;
	}
	
	public boolean last()
	{
		if(this.s == null)
			return true;
		return this.s.length <= this.i;
	}
	
	public void step()
	{
		if(this.s == null || this.s.length == 0)
			return;
		switch(this.s[i++])
		{
			case 0: this.x++; /*zero*/; break;
			case 1: this.x++; this.y--; break;
			case 2: /*zero*/; this.y--; break;
			case 3: this.x--; this.y--; break;
			case 4: this.x--; /*zero*/; break;
			case 5: this.x--; this.y++; break;
			case 6: /*zero*/; this.y++; break;
			case 7: this.x++; this.y++; break;
		}	
	}
	
	public static TreeMap<Integer, Point> directions()
	{
		TreeMap<Integer, Point> map = new TreeMap<Integer, Point>();
		map.put(0, new Point( 1, 0));
		map.put(1, new Point( 1,-1));
		map.put(2, new Point( 0,-1));
		map.put(3, new Point(-1,-1));
		map.put(4, new Point(-1, 0));
		map.put(5, new Point(-1, 1));
		map.put(6, new Point( 0, 1));
		map.put(7, new Point( 1, 1));
		return map;
	}
	
	public static byte opposite(byte step)
	{
		return (byte)((step+4)%8);
	}
	
	public int i()
	{
		return this.i;
	}
	
	public int x()
	{
		return this.x;
	}

	public int y()
	{
		return this.y;
	}
	
	public int[] xy()
	{
		return new int[]{this.x, this.y};
	}
	
	public int s()
	{
		return this.s[this.i];
	}
	
	public Point point()
	{
		return new Point(this.x, this.y);
	}
	
	public String local()
	{
		return "[" + this.x + " " + this.y + "]"; 
	}
	
	public static byte[] generate_annulus_path_steps(int diameter, boolean close)
	{
		if(diameter < 1)
			throw new IllegalArgumentException("Must be positive dimensions.");
		final boolean even = diameter%2 == 0;
		final int a = diameter/2;
		final double o = a - (even ? 0.5 : 0.0);
		final double r = a + (even ? 0.0 : 0.5);
		final double s = r - 1.0;
		final double r2 = r * r;
		final double s2 = s * s;
		
		int count = 0;
		for(int yi = 0; yi < diameter; yi++)
			for(int xi = 0; xi < diameter; xi++)
			{
				final double ri2 = Pow.two(xi-o) + Pow.two(yi-o);
				if(s2 < ri2 && ri2 <= r2)
					count++;
			}
		
		final Point[] coordinates = new Point[count];
		int index = 0;
		for(int yi = 0; yi < diameter; yi++)
			for(int xi = 0; xi < diameter; xi++)
			{
				final double ri2 = Pow.two(xi-o) + Pow.two(yi-o);
				if(s2 < ri2 && ri2 <= r2)
					coordinates[index++] = new Point(xi-a, a-yi);
			}
		
		Arrays.sort(coordinates, 
				new Comparator<Point>() 
				{
					final double b = even ? 0.5 : 0.0;
					@Override
					public int compare(Point arg0, Point arg1) 
					{
						Double theta0 = Math.atan2(arg0.y-b, arg0.x-b);
						Double theta1 = Math.atan2(arg1.y-b, arg1.x-b);
						if(theta0 < 0)
							theta0 += 2*Math.PI;
						if(theta1 < 0)
							theta1 += 2*Math.PI;
						return theta0.compareTo(theta1);
					}
				}
			);
		
		byte[] steps = new byte[count - (close ? 0 : 1)];
		
		for(int i = 0; i < count-1; i++)
			steps[i] = (byte)local_step(coordinates[i+1].x-coordinates[i].x, -coordinates[i+1].y+coordinates[i].y);
		if(close)
			steps[count-1] = (byte)local_step(coordinates[0].x-coordinates[count-1].x, -coordinates[0].y+coordinates[count-1].y); 
		
		return steps;
	}

	public static double sum(ImageProcessor pro, int xo, int yo, byte[] steps)
	{
		double sum = 0.0;
		GaplessPath path = new GaplessPath(xo, yo, steps);
		do
		{
			sum += pro.getf(path.x(), path.y());
		}
		while(path.next());
		return sum;
	}
	
	public static double mse(ImageProcessor pro, int xo, int yo, byte[] steps, double ref)
	{
		double sos = 0.0;
		GaplessPath path = new GaplessPath(xo, yo, steps);
		do
		{
			double val = pro.getf(path.x(), path.y());
			sos += Pow.two(val-ref);
		}
		while(path.next());
		return sos/steps.length;
	}
	
	public static double[] mse(ImageProcessor pro, int xo, int yo, byte[][] steps, double[] ref)
	{
		double[] mse = new double[steps.length];
		for(int i = 0; i < steps.length; i++)
			mse[i]  = mse(pro, xo+i, yo, steps[i], ref[i]);
		return mse;
	}
	
	public static <T extends Spitter> ArrayList<T> sum(ImageProcessor pro, int xo, int yo, byte[][] steps, Supplier<T> zeroFactory)
	{
		ArrayList<T> list = new ArrayList<T>(steps.length);
		for(int i = 0; i < steps.length; i++)
			list.add(sum(pro, xo+i, yo, steps[i], zeroFactory.get()));
		return list;
	}
	
	public static <T extends Spitter> T sum(ImageProcessor pro, int xo, int yo, byte[] steps, T sum)
	{
		GaplessPath path = new GaplessPath(xo, yo, steps);
		do
		{
			sum.add(pro.getf(path.x(), path.y()));
		}
		while(path.next());
		return sum;
	}
	
	public static void set(ImageProcessor pro, int xo, int yo, byte[] steps, float v)
	{
		GaplessPath path = new GaplessPath(xo, yo, steps);
		do
		{
			pro.setf(path.x(), path.y(), v);
		}
		while(path.next());
	}
	
	public static void set(ImageProcessor pro, int xo, int yo, byte[] steps, float[] v)
	{
		if(v.length < steps.length || steps.length + 1 < v.length)
			throw new IllegalArgumentException();
		GaplessPath path = new GaplessPath(xo, yo, steps);
		do
		{
			pro.setf(path.x(), path.y(), v[path.i()]);
		}
		while(path.next());
	}
	
	public static double mean(ImageProcessor pro, int xo, int yo, byte[] steps)
	{
		return GaplessPath.sum(pro, xo, yo, steps) / steps.length;
	}
	
	public static float[] get(ImageProcessor pro, int xo, int yo, byte[] steps, boolean close)
	{
		float[] pix = new float[steps.length + (close ? 1 : 0)];
		GaplessPath path = new GaplessPath(xo, yo, steps);
		do
		{
			pix[path.i()] = pro.getf(path.x(), path.y());
		}
		while(path.next());
		if(close)
			pix[path.i()] = pro.getf(path.x(), path.y());
		return pix;
	}
	
	public static float[][] get(ImageProcessor pro, int xo, int yo, byte[][] steps, boolean close)
	{
		float[][] paths = new float[steps.length][];
		for(int i = 0; i < steps.length; i ++)
			paths[i] = get(pro, xo + i, yo, steps[i], close);
		return paths;
	}
		
	public static float[][] angles(byte[][] steps)
	{
		final int paths = steps.length;
		float[][] ang = new float[paths][];
		ang[0] = new float[]{0}; 
		for(int k = 1; k < paths; k++)
		{	
			ang[k] = new float[steps[k].length+1];
			GaplessPath path = new GaplessPath(k,0,steps[k]);
			do
			{
				ang[k][path.i()] = (float)Math.atan2(-path.y(), path.x());
			}
			while(path.next());
			ang[k][path.i()] = (float)Math.atan2(-path.y(), path.x());
		}
		
		for(float[] row : ang)
			for(int j = 0; j < row.length; j++)
				if(row[j] < 0)
					row[j] += 2*Math.PI;
		return ang;
	}
	
	public static float[][] apply(byte[][] steps, MultiVarFunc func)
	{
		final int paths = steps.length;
		float[][] values = new float[paths][];
		 
		for(int k = 0; k < paths; k++)
		{	
			values[k] = new float[steps[k].length+1];
			GaplessPath path = new GaplessPath(k, 0, steps[k]);
			do
			{
				values[k][path.i()] = (float)func.apply(path.x(), path.y());
			}
			while(path.next());
			values[k][path.i()] = (float)func.apply(path.x(), path.y());
		}
		
		return values;
	}
		
	public static double[] sums(ImageProcessor pro, int xo, int yo, byte[][] steps)
	{
		final int paths = steps.length;
		final double[] sums = new double[paths];
		if(steps[0].length == 1 && steps[0][0] == 8)
			sums[0] = pro.getf(xo, yo);
		for(int i = 1; i < paths; i++)
			sums[i] = sum(pro, xo + i, yo, steps[i]);
		return sums;
	}
	
	public static void set(ImageProcessor pro, int xo, int yo, byte[][] steps, float v)
	{
		final int paths = steps.length;
		if(steps[0].length == 1 && steps[0][0] == 8)
			pro.setf(xo, yo, v);
		for(int i = 1; i < paths; i++)
			set(pro, xo + i, yo, steps[i], v);
	}
	
	public static void set(ImageProcessor pro, int xo, int yo, byte[][] paths, float[] values)
	{
		final int number_of_paths = paths.length;
		if(paths[0].length == 1 && paths[0][0] == 8)
			pro.setf(xo, yo, values[0]);
		for(int i = 1; i < number_of_paths; i++)
			set(pro, xo + i, yo, paths[i], values[i]);
	}
	
	public static void set(ImageProcessor pro, int xo, int yo, byte[][] steps, float[][] v)
	{
		final int paths = steps.length;
		if(steps[0].length == 1 && steps[0][0] == 8)
			pro.setf(xo, yo, v[0][0]);
		for(int i = 1; i < paths; i++)
			set(pro, xo + i, yo, steps[i], v[i]);
	}
	
	public static double[] sums(ImageProcessor pro, int[] xo, int[] yo, byte[][] steps)
	{
		final int paths = steps.length;
		final double[] sums = new double[paths];
		for(int i = 0; i < paths; i++)
			sums[i] = sum(pro, xo[i], yo[i], steps[i]);
		return sums;
	}
	
	public static double[] means(ImageProcessor pro, int[] xo, int[] yo, byte[][] steps)
	{
		final int paths = steps.length;
		final double[] means = new double[paths];
		for(int i = 0; i < paths; i++)
			means[i] = mean(pro, xo[i], yo[i], steps[i]);
		return means;
	}
	
	public static double[] means(ImageProcessor pro, int xo, int yo, byte[][] steps)
	{
		final int paths = steps.length;
		final double[] means = new double[paths];
		if(steps[0].length == 1 && steps[0][0] == 8)
			means[0] = pro.getf(xo, yo);
		for(int i = 1; i < paths; i++)
			means[i] = mean(pro, xo + i, yo, steps[i]);
		return means;
	}
	
	public static int local_step(int x, int y)
	{		
		if(x == +1 && y == +0)
			return 0;
		if(x == +1 && y == -1)
			return 1;
		if(x == +0 && y == -1)
			return 2;
		if(x == -1 && y == -1)
			return 3;
		if(x == -1 && y == +0)
			return 4;
		if(x == -1 && y == +1)
			return 5;
		if(x == +0 && y == +1)
			return 6;
		if(x == +1 && y == +1)
			return 7;
		return -1;
	}
	
	public ArrayList<GaplessPath> within(Roi pro)
	{
		int j = this.i;
		int p = this.x;
		int q = this.y;
		this.reset();
		ArrayList<GaplessPath> paths = new ArrayList<GaplessPath>(); 
		int start_i = -1;
		int start_x = -1;
		int start_y = -1;
		boolean was_in = pro.contains(this.x(), this.y());
		if(was_in)
		{
			start_i = this.i();
			start_x = this.x();
			start_y = this.y();
		}
		while(this.hasNext())
		{
			this.next();
			boolean is_in = pro.contains(this.x(), this.y()); 
			if(!was_in && is_in)
			{	
				start_i = this.i();
				start_x = this.x();
				start_y = this.y();
			}
			else if(was_in && !is_in)
				paths.add(new GaplessPath(start_x, start_y, Arrays.copyOfRange(this.s, start_i, this.i()-1)));
			was_in = is_in;
		}		
		if(was_in)
			paths.add(new GaplessPath(start_x, start_y, Arrays.copyOfRange(this.s, start_i, this.i()-1)));
		this.i = j;
		this.x = p;
		this.y = q;
		return paths;
	}
	
	public int[][] toCoordinates()
	{
		final int n = this.size();		
		this.reset();
		int[] x = new int[n];
		int[] y = new int[n];
		do
		{
			int i = this.i();
			x[i] = this.x();
			y[i] = this.y();
		}
		while(this.next());
		return new int[][]{x, y};
	}
	
	public PolygonRoi toRoi()
	{
		int[][] xy = this.toCoordinates();
		return new PolygonRoi(xy[0], xy[1], xy[0].length-1, Roi.FREELINE);
	}
		
	public void drawOn(ColorProcessor pro, int rgb)
	{
		this.reset();
		
		do
		{
			pro.set(this.x(), this.y(), rgb);
		}
		while(this.next());
		
		pro.set(this.x(), this.y(), rgb);
		
		this.reset();
	}
	
	public void drawOn(ImageProcessor pro, float value)
	{
		this.reset();
		
		do
		{
			pro.setf(this.x(), this.y(), value);
		}
		while(this.next());
		
		pro.setf(this.x(), this.y(), value);
		
		this.reset();
	}
	
	public float[] pullValues(ImageProcessor pro)
	{
		this.reset();
		
		float[] values = new float[this.size()];
		do
		{
			values[this.i()] = pro.getf(this.x(), this.y());
		}
		while(this.next());
		
		values[this.i()] = pro.getf(this.x(), this.y());
		
		this.reset();
		
		return values;
	}
	
	public void putValues(ImageProcessor pro, float[] values)
	{
		if(values.length != this.size())
			throw new IllegalArgumentException("Array length inconsistent with path.");
		
		this.reset();
		
		do
		{
			pro.setf(this.x(), this.y(), values[this.i()]);
		}
		while(this.next());
		
		pro.setf(this.x(), this.y(), values[this.i()]);
		
		this.reset();
	}
	
	public void putValues(ImageProcessor pro, double[] values)
	{
		if(values.length != this.size())
			throw new IllegalArgumentException("Array length inconsistent with path.");
		
		this.reset();
		
		do
		{
			pro.setf(this.x(), this.y(), (float)values[this.i()]);
		}
		while(this.next());
		
		pro.setf(this.x(), this.y(), (float)values[this.i()]);
		
		this.reset();
	}
	
	public GaplessPath reverse()
	{
		final int i_ = this.i();
		final int x_ = this.x();
		final int y_ = this.y();		
		this.reset();
		int x1, y1;
		final byte[] rev = new byte[this.s.length];
		do
		{
			x1 = this.x();
			y1 = this.y();
			rev[this.i()] = opposite(this.s[this.i()]);
		}
		while(this.next());
		this.i = i_;
		this.x = x_;
		this.y = y_;
		return new GaplessPath(x1, y1, rev);
	}
	
	public double[] angles(Point refer)
	{
		this.reset();
		double[] angles = new double[this.size()];
		do
		{
			Point point = this.point();		
			angles[this.i()] = Math.atan2(refer.y-point.y, point.x-refer.x);
		}
		while(this.next());
		Point point = this.point();
		angles[this.i()] = Math.atan2(refer.y-point.y, point.x-refer.x);
		this.reset();
		return angles;
	}
	
	public static GaplessPath borderSinister(Rectangle rect)
	{
		final int w = rect.width-1;
		final int h = rect.height-1;
		final int ww = w+w;
		final int hh = h+h;
		final byte[] s = new byte[ww+hh];
		for(int i = 0; i < w; i++)
			s[i] = 0;
		for(int i = w; i < w+h; i++)
			s[i] = 6;
		for(int i = w+h; i < ww+h; i++)
			s[i] = 4;
		for(int i = ww+h; i < ww+hh; i++)
			s[i] = 2;
		return new GaplessPath(rect.x, rect.y, s);
	}
	
	public static GaplessPath borderRectus(Rectangle rect)
	{
		final int w = rect.width-1;
		final int h = rect.height-1;
		final int ww = w+w;
		final int wh = w+h;
		final int hh = h+h;
		final byte[] s = new byte[ww+hh];
		for(int i = 0; i < h; i++)
			s[i] = 6;
		for(int i = h; i < wh; i++)
			s[i] = 0;
		for(int i = wh; i < w+hh; i++)
			s[i] = 2;
		for(int i = w+hh; i < ww+hh; i++)
			s[i] = 4;
		return new GaplessPath(rect.x, rect.y, s);
	}
	
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		else
		{
			if(!(obj instanceof GaplessPath))
				return false;
			else
			{
				GaplessPath that = (GaplessPath)obj;
				if(that.a != this.a || that.b != this.b)
					return false;
				if(that.s == this.s)
					return true;
				else
				{
					final int n = this.s.length;
					if(n != that.s.length)
						return false;
					for(int i = 0; i < n; i++)
						if(this.s[i] != that.s[i])
							return false;
					return true;
				}
			}
		}
	}
}
