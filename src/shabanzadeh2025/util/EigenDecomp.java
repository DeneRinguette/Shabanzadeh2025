package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class EigenDecomp
{
	public Tensor eigenValues;
	public Tensor eigenVectors;
	public Tensor sourceMatrix;
	
	public EigenDecomp(Tensor sourceMatrix, Tensor eigenValues, Tensor eigenVectors) 
	{
		super();
		this.sourceMatrix = sourceMatrix;
		this.eigenValues = eigenValues;
		this.eigenVectors = eigenVectors;
	}
	
	public double error()
	{
		Tensor product = TensorTools.mult(this.sourceMatrix, this.eigenVectors);
		Tensor scaled = this.eigenVectors.copy();
		scaled.multCols(this.eigenValues);
		product.sub(scaled);
		return product.mag2();
	}
}
