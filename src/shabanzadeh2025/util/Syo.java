package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Syo 
{
	public static void pl()
	{
		System.out.println();
	}
	
	public static void pl(Object arg)
	{
		System.out.println(arg);
	}
	
	public static void p(String arg)
	{
		System.out.print(arg);
	}
	
	public static void pl(String arg)
	{
		System.out.println(arg);
	}
	
	public static void pl(int arg)
	{
		System.out.println(arg);
	}
	
	public static void p(double arg)
	{
		System.out.print(arg);
	}
	
	public static void pl(double arg)
	{
		System.out.println(arg);
	}
	
	public static void pl(double... arg)
	{
		System.out.println(Arrayz.toString(arg));
	}
	
	public static void p(long... arg)
	{
		System.out.print(Arrayz.toString(arg));
	}
	
	public static void pl(long... arg)
	{
		System.out.println(Arrayz.toString(arg));
	}
	
	public static void pl(double[][] arg)
	{
		System.out.println(Arrayz.toString(arg));
	}
	
	public static void table(String title, String[] headers, double[][] data)
	{
		if(title == null)
			System.out.println("Title");
		else
			System.out.println(title);
		
		final int cols = data.length;
		
		if(headers == null)
		{
			headers = new String[cols];
			for(int col = 0; col < cols; col++)
				headers[col] = "Col_" + col;
		}
		else if(headers.length != data.length)
			throw new IllegalArgumentException("Number of headers must match number of columns.");
		
		final int rows = Stat.width(data);
		final int last = cols-1;
		
		for(int col = 0; col < last; col++)
			System.out.print(headers[col] + "\t");
		System.out.println(headers[last]);
		
		for(int row = 0; row < rows; row++)
		{
			for(int col = 0 ; col < last; col++)
			{
				final double[] column = data[col];
				if(row < column.length)
					System.out.print(column[row]);
				System.out.print("\t");
			}
			final double[] column = data[last];
			if(row < column.length)
				System.out.print(column[row]);
			System.out.println();
		}
	}
}
