package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public class Label<T extends Comparable<T>> implements Comparable<Label<T>>
{
	public final T value;
	
	public final String label;
	
	public Label(T value, String name)
	{
		this.value = value;
		this.label = name;
	}

	@Override
	public int compareTo(Label<T> other) 
	{
		return this.value.compareTo(other.value);
	}
	
	public String toString()
	{
		return this.value.toString() + " <- " + this.label;
	}
}
