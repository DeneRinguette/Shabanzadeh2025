package shabanzadeh2025.util;

import java.util.Collection;

/**
 * @author Dene Ringuette
 */

public class Primitives 
{	
	public static int[] toInt(Collection<? extends Number> col)
	{
		if(col.isEmpty())
			return null;
		int[] thus = new int[col.size()];
		int i = 0;
		for(Number val : col)
			thus[i++] = val.intValue();
		return thus;
	}
}
