package shabanzadeh2025.util;

public class TensorTools 
{
	public static <T extends TensorModel<T>> T mult(T mat0, T mat1)
	{
		if(!TensorTools.canMult(mat0, mat1))
			throw new IllegalArgumentException("Incongruent multiplication:" + mat0.rows() +","+mat0.cols()+ " x "+ "" + mat1.rows() +","+mat1.cols());
		boolean isComplex = mat0.isComplex() || mat1.isComplex();
		T thus = mat0.make(mat0.rows(), mat1.cols());
		final int r = mat0.cols(); 
		if(isComplex)
		{
			for(int i = 0; i < thus.rows(); i++)
				for(int j = 0; j < thus.cols(); j++)
				{
					Rect sum = new Rect(0.0);
					for(int k = 0; k < r; k++)
					{
						Rect term = mat0.cGet(i,k);
						term.mult(mat1.cGet(k,j));
						sum.add(term);
					}
					thus.cSet(i, j, sum);
				}
		}
		else
		{
			for(int i = 0; i < thus.rows(); i++)
				for(int j = 0; j < thus.cols(); j++)
				{
					double reSum = 0.0;
					for(int k = 0; k < r; k++)
					{
						double re0 = mat0.rGet(i,k);
						double re1 = mat1.rGet(k,j);
						reSum += re0 * re1;
					}
					thus.rSet(i, j, reSum);
				}
		}
		return thus;
	}
	
	public static boolean canMult(TensorModel<?> mat0, TensorModel<?> mat1)
	{
		return mat0.cols() == mat1.rows();
	}

	public static <T extends TensorModel<T>> T mult(T arg0, T arg1, T arg2)
	{
		return mult(arg0,mult(arg1,arg2));
	}

	public static <T extends TensorModel<T>> T mult(T arg0, T arg1, T arg2, T arg3)
	{
		return mult(mult(arg0,arg1),mult(arg2,arg3));
	}

	public static <T extends TensorModel<T>> T quadProd(T vec0, T mat0, T vec1)
	{
		return mult(vec0.tran(), mat0, vec1);
	}
	
	public static Rect cDet(TensorModel<?> that) throws IllegalArgumentException
	{
		// destroys matrix in calculation
		if(!that.isSqr())
			throw new IllegalArgumentException("Determinates only defined for square matrices.");
		
		final int n = that.cols();
		
		Rect det = new Rect(1.0);
		
		for(int d = 0; d < n; d++)
		{
			double abs;
			double maxAbs = that.mod(d, d);
			int maxIdx = d;
			
			for(int i = d+1; i < n; i += 1)
				if(maxAbs < (abs = that.mod(i,d)))
				{
					maxAbs = abs;
					maxIdx = i;
				}
			
			if(maxAbs == 0.0)
				return new Rect(0.0); 
			
			if(maxIdx != d)
			{
				that.diagSwap(d, maxIdx);
				det.neg();
			}
			
			det.mult(that.cGet(d, d));
			
			for(int i = d+1; i < n; i += 1)
			{
				Rect fact = that.cGet(i, d);
				fact.div(that.cGet(d, d));
				for(int j = d+1; j < n; j++)
				{
					Rect sub = that.cGet(d, j);
					sub.mult(fact);
					Rect val = that.cGet(i, j);
					val.sub(sub);
					that.cSet(i, j, val);
				}
			}
		}
		return det;
	}
	
	public static <T extends TensorModel<T>> T outPro(T arg0, T arg1)
	{
		if(!arg0.isVec() || !arg1.isVec())
			throw new IllegalArgumentException();
		if(!arg0.isReal() || !arg1.isReal())
			throw new IllegalArgumentException();
		T thus = arg0.make(arg0.size(), arg1.size());
		final int n = thus.cols();
		for(int i = 0; i < thus.size(); i++)
			thus.rAdd(i, arg0.rGet(i/n) * arg1.rGet(i%n));
		return thus;
	}

	public static void reducable(TensorModel<?> rev, TensorModel<?> inv)
	{
		if(inv != null)
			if(rev.rows() != inv.rows())
				throw new IllegalArgumentException(
						"Same number of rows required.");
	}
	
	public static boolean reduceDiag(TensorModel<?> rev, TensorModel<?> inv, double epsilon)
	{
		reducable(rev, inv);
		int d = 0;
		while(d < rev.rows() && d < rev.cols())
		{
			if(rev.mod(d,d) < epsilon)
				return false;
			for(int i = d+1; i < rev.rows(); i++)
			{
				if(rev.isReal())
				{
					double fact = -rev.rGet(i,d) / rev.rGet(d,d);
					rev.addRows(d, fact, i, d+1);
					if(inv != null)
						inv.addRows(d, fact, i, 0);
				}
				else
				{
					Rect fact = rev.cGet(i,d);
					fact.div(rev.cGet(d,d));
					fact.neg();
					rev.addRows(d, fact, i, d+1);
					if(inv != null)
						inv.addRows(d, fact, i, 0);
				}
			}
			d++;
		}
		return true;
	}
	
	public static boolean backSubDiag(TensorModel<?> rev, TensorModel<?> inv)
	{
		int d = Math.min(rev.rows(), rev.cols())-1;
		while(0 <= d)
		{
			for(int i = d-1; 0 <= i; i--)
			{
				if(rev.isReal())
				{
					double fact = -rev.rGet(i,d) / rev.rGet(d,d);
					inv.addRows(d, fact, i, 0);
				}
				else
				{
					Rect fact = rev.cGet(i,d);
					fact.div(rev.cGet(d,d));
					fact.neg();
					inv.addRows(d, fact, i, 0);
				}
			}
			if(rev.isReal())
				inv.divRow(d, rev.rGet(d,d));
			else
				inv.divRow(d, rev.cGet(d,d));
			d--;
		}
		return true;
	}
}