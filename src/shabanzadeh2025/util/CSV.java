package shabanzadeh2025.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Comma separated values reader.
 * @author Dene Ringuette
 */

public class CSV 
{
	public static Map<Integer, List<String>> columns(
			final File file,
			final int[] cols,
			Map<Integer, List<String>> map
		) 
			throws FileNotFoundException, IOException
	{	
		CSVParser parser = 
				new CSVParser(
						new FileReader(file), 
						CSVFormat.DEFAULT
					);
		
		Iterator<CSVRecord> iterator = 
				parser.iterator();
		
		while(iterator.hasNext())
		{
			CSVRecord record = iterator.next();
			
			for(int col : cols)
			{
				List<String> list = map.get(col);
				if(list == null)
				{
					list = new ArrayList<String>();
					map.put(col, list);
				}
				list.add(record.get(col));
			}
		}
		
		parser.close();
		
		return map;
	}
}
