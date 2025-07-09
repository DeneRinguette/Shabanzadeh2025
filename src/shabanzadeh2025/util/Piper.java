package shabanzadeh2025.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Dene Ringuette
 */

public class Piper 
{
	public static void ser(Object object, File out) 
			throws IOException
	{
		FileOutputStream fos = new FileOutputStream(out);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
	    oos.writeObject(object);
	    oos.close();
	    fos.close();
	}
	
	public static void out(Map<?,?> stuff, File file) 
			throws IOException
	{
		PrintWriter out = Piper.printer(file);
		for(Entry<?,?> i : stuff.entrySet())
			out.println(i.getKey().toString() + "\t" + i.getValue().toString());
		out.close();
	}
	
	public static void out(String key_name, String value_name, Map<?,?> stuff, File file) 
			throws IOException
	{
		PrintWriter out = Piper.printer(file);
		out.println(key_name + "\t" + value_name);
		for(Entry<?,?> i : stuff.entrySet())
			out.println(i.getKey().toString() + "\t" + i.getValue().toString());
		out.close();
	}
	
	public static PrintWriter printer(File file) 
			throws IOException
	{
		return new PrintWriter(new FileWriter(file), true);
	}
	
	public static void table(File file, String title, String[] headers, double[][] data) throws IOException
	{
		PrintWriter writer = Piper.printer(file);
		
		if(title == null)
			writer.println("Title");
		else
			writer.println(title);
		
		final int cols = data.length;
		
		if(headers == null)
		{
			headers = new String[cols];
			for(int col = 0; col < cols; col++)
				headers[col] = "Col_" + col;
		}
		else if(headers.length != data.length)
			throw new IllegalArgumentException("Number of headers must match number of columns.");
		
		int width = 0;
		for(double[] row : data)
			if(width < row.length)
				width = row.length;
		final int rows = width;
		
		final int last = cols-1;
		for(int col = 0; col < last; col++)
			writer.print(headers[col] + "\t");
		writer.println(headers[last]);
		
		for(int row = 0; row < rows; row++)
		{
			for(int col = 0 ; col < last; col++)
			{
				final double[] column = data[col];
				if(row < column.length)
					writer.print(column[row]);
				writer.print("\t");
			}
			final double[] column = data[last];
			if(row < column.length)
				writer.print(column[row]);
			writer.println();
		}
		writer.close();
	}
}
