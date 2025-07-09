package shabanzadeh2025.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Dene Ringuette
 */

public class Xlsx 
{
	final Workbook workbook;
	Sheet sheet;
	
	public Xlsx(File file) throws FileNotFoundException, IOException
	{
		this.workbook = new XSSFWorkbook(new FileInputStream(file));
	}
	
	public void setSheet(int index)
	{
		this.sheet = this.workbook.getSheetAt(index);
	}
	
	public void setSheet(String name)
	{
		this.sheet = this.workbook.getSheet(name);
	}
	
	public int noColums()
	{
		Row top_row = this.sheet.getRow(0);
		return top_row.getLastCellNum();
	}
	
	public List<String> getHeaders()
	{
		return this.getHeaders(0);
	}
	
	public List<String> getHeaders(int row)
	{
		Row top_row = this.sheet.getRow(row);
		ArrayList<String> headers = new ArrayList<String>();
		
		for (Cell cell : top_row) 
	    {
			switch(cell.getCellType())
	        {
	        	case STRING: headers.add(cell.getStringCellValue()); break;
	        	case NUMERIC: headers.add("" + cell.getNumericCellValue()); break;
	        	default: new IllegalArgumentException("Header must be of STRING type.");
	        }
	    }
		
		return headers;
	}
	
	public List<List<Double>> getColumns(int offset) throws IOException
	{
		List<List<Double>> columns = new ArrayList<List<Double>>();  
		final int noColumns = this.noColums();
		for(int c = 0; c < noColumns; c++)
			columns.add(new ArrayList<Double>());
		
		for(Row row : this.sheet)
		{
			final int row_index = row.getRowNum();
			
			if(offset <= row_index)
			{	
				for(Cell cell : row) 
			    {
			    	final int col_index = cell.getColumnIndex();
			    	
			    	if(col_index < noColumns)
			    	{
			    		List<Double> column = columns.get(col_index);
			    		column.add(
			    				cell.getCellType() == CellType.NUMERIC ? 
			    						cell.getNumericCellValue() : null
			    			);
			    	}
			    }
			}
		}
		
		return columns;
	}
	
	public Cell find(String name)
	{
		Iterator<Row> row_iter = this.sheet.rowIterator();
		while(row_iter.hasNext())
		{
			Row row = row_iter.next();
			Iterator<Cell> cell_iter = row.cellIterator();
			while(cell_iter.hasNext())
			{
				Cell cell = cell_iter.next();
				if(cell.getCellType() == CellType.STRING)
					if(name.equals(cell.getStringCellValue()))
						return cell;
			}
		}
		return null;
	}

	public double[] getNumericColumn(String name) throws IOException
	{
		List<Double> column = new ArrayList<Double>();  
		
		Cell header = null;
		
		Iterator<Row> row_iter = this.sheet.rowIterator();
		while(row_iter.hasNext() && header == null)
		{
			Row row = row_iter.next();
			Iterator<Cell> cell_iter = row.cellIterator();
			while(cell_iter.hasNext())
			{
				Cell cell = cell_iter.next();
				if(cell.getCellType() == CellType.STRING && header == null)
					if(name.equals(cell.getStringCellValue()))
						header = cell;
			}
		}
		
		final int j = header.getColumnIndex();
		boolean empty = false;
		while(row_iter.hasNext() && !empty)
		{
			Row row = row_iter.next();
			Cell cell = row.getCell(j);
			if(cell == null)
				empty = true;
			else if(cell.getCellType() == CellType.NUMERIC) 
				column.add(cell.getNumericCellValue());
			else
				empty = true;
		}
		
		return Numbers.toDouble(column);
	}
	
	public List<VarData> getNumericColumns(String... name) throws IOException
	{
		final int no_columns = name.length;
		double[][] columns = new double[no_columns][];
		for(int column_index = 0; column_index < no_columns; column_index++)
			columns[column_index] = getNumericColumn(name[column_index]);
		
		final int no_rows = columns[0].length;
		List<VarData> list = new ArrayList<VarData>(no_rows);
		for(int row_index = 0; row_index < no_rows; row_index++)
		{
			VarData row = new VarData();
			for(int column_index = 0; column_index < no_columns; column_index++)
				row.add(columns[column_index][row_index]);
			list.add(row);
		}
		return list;
	}
	
	public int headerIndex(String name)
	{
		Row row = this.sheet.getRow(0);
		Iterator<Cell> cell_iter = row.cellIterator();
		while(cell_iter.hasNext())
		{
			Cell cell = cell_iter.next();
			if(cell.getCellType() == CellType.STRING)
				if(name.equals(cell.getStringCellValue()))
					return cell.getColumnIndex();					
		}
		return -1;
	}
	
	public ArrayList<String> getColumn(String name) throws IOException
	{
		ArrayList<String> column = new ArrayList<String>();  
		
		Cell header = null;
		
		Iterator<Row> row_iter = this.sheet.rowIterator();
		
		while(row_iter.hasNext() && header == null)
		{
			Row row = row_iter.next();
			Iterator<Cell> cell_iter = row.cellIterator();
			while(cell_iter.hasNext() && header == null)
			{
				Cell cell = cell_iter.next();
				if(cell.getCellType() == CellType.STRING)
					if(name.equals(cell.getStringCellValue()))
						header = cell;
			}
		}
		
		final int j = header.getColumnIndex();
		
		boolean empty = false;
		while(row_iter.hasNext() && !empty)
		{
			Row row = row_iter.next();
			Cell cell = row.getCell(j);
			if(cell == null)
				empty = true;
			else
			{
				if(cell.getCellType() == CellType.STRING)
					column.add(cell.getStringCellValue());
				else if(cell.getCellType() == CellType.NUMERIC)
				{
					String asString = "" + cell.getNumericCellValue();
					column.add(asString);
				}
			}
		}
		
		return column;
	}
	
	public double[] getNumericColumn(int column_index, int row_offset) throws IOException
	{
		List<Double> column = new ArrayList<Double>(); 
		
		for(Row row : this.sheet)	
			if(row_offset <= row.getRowNum())
			{	
				Cell cell = row.getCell(column_index);
				if(cell != null && cell.getCellType() == CellType.NUMERIC)
					column.add(cell.getNumericCellValue());
			}
		
		return Numbers.toDouble(column);
	}	
}
