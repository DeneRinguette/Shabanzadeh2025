package shabanzadeh2025.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

/**
 * @author Dene Ringuette
 */

public class Parser 
{
	public static int[] getIntegers(File file) 
			throws IOException
	{
		final int[] array = new int[Parser.lines(file)];
		final BufferedReader in = Parser.buffer(file);
		int index = 0;
		String line;
		while((line = in.readLine()) != null)
			array[index++] = Integer.parseInt(line);
		in.close();
		return array;
	}
	
	public static int lines(File file) 
			throws IOException
	{
		final BufferedReader in = 
			Parser.buffer(file);
		int length = 0;
		while(in.readLine() != null)
			length += 1;
		in.close();
		return length;
	}
	
	public static BufferedReader buffer(File file) 
			throws FileNotFoundException
	{
		return new BufferedReader(new FileReader(file));
	}
	
	public static double[][] getDoubles(File file, int preamble, int width) 
			throws IOException
	{
		final int lines = Parser.lines(file);
		final int length = lines - preamble;
		if(length < 1)
			throw new IllegalStateException();
		
		final double[][] array = new double[width][length];
		final BufferedReader reader = Parser.buffer(file);
		for(int i = 0; i < preamble; i++)
			reader.readLine();
		int row = 0;
		String line;
		while((line = reader.readLine()) != null)
		{
			final String[] split = line.split("\t");
			for(int col = 0; col < width; col++)
				array[col][row] = (col < split.length) ? Double.parseDouble(split[col]) : Double.NaN;
			row++;
		}
		reader.close();
		return array;
	}
	
	public static double[][] getDoubles(File file, int preamble, int[] columns) 
			throws IOException
	{
		final int lines = Parser.lines(file);
		final int length = lines - preamble;
		if(length < 1)
			throw new IllegalStateException();
		
		final double[][] array = new double[columns.length][length];
		final BufferedReader reader = Parser.buffer(file);
		for(int i = 0; i < preamble; i++)
			reader.readLine();
		int row = 0;
		String line;
		while((line = reader.readLine()) != null)
		{
			final String[] split = line.split("\t");
			for(int col = 0; col < columns.length; col++)
				array[col][row] = (col < split.length) ? Double.parseDouble(split[columns[col]]) : Double.NaN;
			row++;
		}
		reader.close();
		return array;
	}
	
	public static ArrayList<String> toList(File file) 
			throws IOException
	{
		return Parser.add(file, new ArrayList<String>());
	}
	
	public static <T extends Collection<String>> T add(File file, T col) 
			throws IOException
	{
		return Parser.add(Parser.autoBuffer(file), col);	
	}
	
	public static BufferedReader autoBuffer(File file) throws IOException
	{
		if(file.getName().endsWith(".gz"))
			return gzBuffer(file);
		return buffer(file);
	}
	
	public static BufferedReader gzBuffer(File file) 
			throws IOException
	{
		return new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream(file))));
	}
	
	public static <T extends Collection<String>> T add(BufferedReader reader, T collection) 
			throws IOException
	{
		String input;
		while((input = reader.readLine()) != null)
			collection.add(input);
		reader.close();	
		return collection;
	}
}
