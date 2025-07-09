package shabanzadeh2025.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Supplement to <code>java.util.Arrays</code>.
 * @author Dene Ringuette
 */

public class Arrayz 
{
	
	public static int MAX_SIZE = Integer.MAX_VALUE-2;
	
	public static <T> void addToRandomPosition(T[] that, T arg)
	{
		that[(int)(that.length * Math.random())] = arg;
	}
	
	public static <T> int size(T[] a)
	{
		return a.length;
	}
	
	public static <T> int size(T[][] a)
	{
		int size = 0;
		for(T[] i : a)
			size += Arrayz.size(i);
		return size;
	}
	
	public static <T> int size(T[][][] a)
	{
		int size = 0;
		for(T[][] i : a)
			size += Arrayz.size(i);
		return size;
	}
	
	public static <T> boolean rectangular(T[][] a)
	{
		for(T[] row : a)
			if(a[0].length != row.length)
				return false;
		return true;
	}
	
	public static <T> boolean square(T[][] a)
	{
		for(T[] row : a)
			if(a.length != row.length)
				return false;
		return true;
	}

	public static <T> T getRandom(T[] that)
	{
		return that[(int)(that.length * Math.random())];
	}
	
	public static <T> String toString(T[] array)
	{
		if(array.length == 0)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ", ");
		sb.append(array[array.length-1] + "]");
		return new String(sb);
	}
	
	public static <T> String toString(T[][] array)
	{
		if(array.length == 0)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(Arrayz.toString(array[i]) + ", ");
		sb.append(Arrayz.toString(array[array.length-1]) + "]");
		return new String(sb);
	}
	
	public static String toString(double[][] array)
	{
		if(array.length == 0)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(Arrayz.toString(array[i]) + ", ");
		sb.append(Arrayz.toString(array[array.length-1]) + "]");
		return new String(sb);
	}
	
	public static String toString(float[][] array)
	{
		if(array.length == 0)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(Arrayz.toString(array[i]) + ", ");
		sb.append(Arrayz.toString(array[array.length-1]) + "]");
		return new String(sb);
	}
	
	public static String toString(long[][] array)
	{
		if(array.length == 0)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(Arrayz.toString(array[i]) + ", ");
		sb.append(Arrayz.toString(array[array.length-1]) + "]");
		return new String(sb);
	}
	
	public static String toString(short[][] array)
	{
		if(array.length == 0)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(Arrayz.toString(array[i]) + ", ");
		sb.append(Arrayz.toString(array[array.length-1]) + "]");
		return new String(sb);
	}
	
	public static String toString(int[][] array)
	{
		if(array.length == 0)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(Arrayz.toString(array[i]) + ", ");
		sb.append(Arrayz.toString(array[array.length-1]) + "]");
		return new String(sb);
	}
	
	public static String toString(byte[][] array)
	{
		if(array.length == 0)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(Arrayz.toString(array[i]) + ", ");
		sb.append(Arrayz.toString(array[array.length-1]) + "]");
		return new String(sb);
	}
	
	public static String toString(char[][] array)
	{
		if(array.length == 0)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(Arrayz.toString(array[i]) + ", ");
		sb.append(Arrayz.toString(array[array.length-1]) + "]");
		return new String(sb);
	}
	
	public static String toString(boolean[][] array)
	{
		if(array.length == 0)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(Arrayz.toString(array[i]) + ", ");
		sb.append(Arrayz.toString(array[array.length-1]) + "]");
		return new String(sb);
	}
	
	public static String toString(double... array)
	{
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ", ");
		sb.append(array[array.length-1] + "]");
		return new String(sb);
	}
	
	public static String toString(float... array)
	{
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ", ");
		sb.append(array[array.length-1] + "]");
		return new String(sb);
	}
	
	public static String toString(long... array)
	{
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ", ");
		sb.append(array[array.length-1] + "]");
		return new String(sb);
	}
	
	public static String toString(int... array)
	{
		if(array == null)
			return "[]";
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ", ");
		sb.append(array[array.length-1] + "]");
		return new String(sb);
	}
	
	public static String con(int[] array)
	{
		if(array == null)
			return "null";
		StringBuffer sb = new StringBuffer("new int[]{"); 
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ", ");
		sb.append(array[array.length-1] + "}");
		return new String(sb);
	}
	
	public static String con(byte[] array)
	{
		if(array == null)
			return "null";
		StringBuffer sb = new StringBuffer("new byte[]{");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ",");
		sb.append(array[array.length-1] + "}");
		return new String(sb);
	}
	
	public static String toString(short... array)
	{
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ", ");
		sb.append(array[array.length-1] + "]");
		return new String(sb);
	}
	
	public static String toString(byte... array)
	{
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ", ");
		sb.append(array[array.length-1] + "]");
		return new String(sb);
	}
	
	public static String toString(boolean... array)
	{
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ", ");
		sb.append(array[array.length-1] + "]");
		return new String(sb);
	}
	
	public static String toString(char... array)
	{
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < array.length - 1; i++)
			sb.append(array[i] + ", ");
		sb.append(array[array.length-1] + "]");
		return new String(sb);
	}
	
	public static double[][] copiesOfRanges(double[] values, int... indices)
	{
		final int n = indices.length;
		final double[][] split = new double[n/2+n%2][];
		int i = 0;
		int j = 0;
		while(i < n - n % 2)
			split[j++] = Arrays.copyOfRange(values, indices[i++], indices[i++]);
		if(n % 2 == 1)
			split[j++] = Arrays.copyOfRange(values, indices[i++], values.length);
		return split;
	}
	
	public static int[][] copiesOfRanges(int[] values, int... indices)
	{
		final int n = indices.length;
		final int[][] split = new int[n/2+n%2][];
		int i = 0;
		int j = 0;
		while(i < n - n % 2)
			split[j++] = Arrays.copyOfRange(values, indices[i++], indices[i++]);
		if(n % 2 == 1)
			split[j++] = Arrays.copyOfRange(values, indices[i++], values.length);
		return split;
	}
	
	public static long[][] copiesOfRanges(long[] values, int... indices)
	{
		final int n = indices.length;
		final long[][] split = new long[n/2+n%2][];
		int i = 0;
		int j = 0;
		while(i < n - n % 2)
			split[j++] = Arrays.copyOfRange(values, indices[i++], indices[i++]);
		if(n % 2 == 1)
			split[j++] = Arrays.copyOfRange(values, indices[i++], values.length);
		return split;
	}
	
	public static float[][] copiesOfRanges(float[] values, int... indices)
	{
		final int n = indices.length;
		final float[][] split = new float[n/2+n%2][];
		int i = 0;
		int j = 0;
		while(i < n - n % 2)
			split[j++] = Arrays.copyOfRange(values, indices[i++], indices[i++]);
		if(n % 2 == 1)
			split[j++] = Arrays.copyOfRange(values, indices[i++], values.length);
		return split;
	}
	
	public static short[][] copiesOfRanges(short[] values, int... indices)
	{
		final int n = indices.length;
		final short[][] split = new short[n/2+n%2][];
		int i = 0;
		int j = 0;
		while(i < n - n % 2)
			split[j++] = Arrays.copyOfRange(values, indices[i++], indices[i++]);
		if(n % 2 == 1)
			split[j++] = Arrays.copyOfRange(values, indices[i++], values.length);
		return split;
	}
	
	public static byte[][] copiesOfRanges(byte[] values, int... indices)
	{
		final int n = indices.length;
		final byte[][] split = new byte[n/2+n%2][];
		int i = 0;
		int j = 0;
		while(i < n - n % 2)
			split[j++] = Arrays.copyOfRange(values, indices[i++], indices[i++]);
		if(n % 2 == 1)
			split[j++] = Arrays.copyOfRange(values, indices[i++], values.length);
		return split;
	}
	
	public static char[][] copiesOfRanges(char[] values, int... indices)
	{
		final int n = indices.length;
		final char[][] split = new char[n/2+n%2][];
		int i = 0;
		int j = 0;
		while(i < n - n % 2)
			split[j++] = Arrays.copyOfRange(values, indices[i++], indices[i++]);
		if(n % 2 == 1)
			split[j++] = Arrays.copyOfRange(values, indices[i++], values.length);
		return split;
	}
	
	public static boolean[][] copiesOfRanges(boolean[] values, int... indices)
	{
		final int n = indices.length;
		final boolean[][] split = new boolean[n/2+n%2][];
		int i = 0;
		int j = 0;
		while(i < n - n % 2)
			split[j++] = Arrays.copyOfRange(values, indices[i++], indices[i++]);
		if(n % 2 == 1)
			split[j++] = Arrays.copyOfRange(values, indices[i++], values.length);
		return split;
	}
	
	public static int[] shrinkIncludedAfterDelta(final int[] pairs)
	{
		List<Integer> shrunk = new ArrayList<Integer>(); 
		
		for(int i = 0; i < pairs.length-1; i += 2)
		{
			if(pairs[i] != pairs[i+1]-1)
			{
				shrunk.add(pairs[i]);
				shrunk.add(pairs[i+1]-1);
			}
		}
		
		if(pairs.length%2 != 0)
			shrunk.add(pairs[pairs.length-1]);
		
		return Arrayz.toIntegerArray(shrunk);
	}
	
	public static int[] toIntegerArray(List<Integer> list)
	{
		final int[] thus = new int[list.size()];
		
		int i = 0;
		Iterator<Integer> iter = list.iterator();
		
		while(iter.hasNext())
			thus[i++] = iter.next();
		
		return thus;
	}
	
	public static double[] toDoubleArray(List<Double> list)
	{
		final double[] thus = new double[list.size()];
		
		int i = 0;
		Iterator<Double> iter = list.iterator();
		
		while(iter.hasNext())
			thus[i++] = iter.next();
		
		return thus;
	}
	
	public static String[] flip(String[] that)
	{
		final int n = that.length;
		final int e = n-1;
		String[] thus = new String[n];
		for(int i = 0; i < n; i++)
			thus[i] = that[e-i];
		return thus;
	}
	
	public static String[] subsample(String[] that, int... indices)
	{
		final int size = indices.length;
		final String[] thus = new String[size];
		for(int i = 0; i < size; i++)
			thus[i] = that[indices[i]];
		return thus;
	}
	
	public static <T> void flip(T[] that)
	{
		final int n = that.length;
		final int e = n-1;
		
		for(int i = 0; i < n; i++)
		{
			T temp = that[i]; 
			that[i] = that[e-i];
			that[e-i] = temp;
		}
	}
}
