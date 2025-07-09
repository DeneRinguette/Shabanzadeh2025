package shabanzadeh2025.util;

import java.io.Serializable;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public abstract class AbstractTensor<T extends TensorModel<T>> implements TensorModel<T>, Serializable 
{
	private static final long serialVersionUID = 6774067636764611694L;
	
	final int rows;
	final int cols;
	final int size;
	
	AbstractTensor(int rows, int cols)
	{
		this.rows = rows;
		this.cols = cols;
		this.size = rows * cols;
	}
	
	public Rect cDot(TensorModel<?> that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		Rect dot = new Rect(0.0);
		for(int i = 0; i < this.size(); i++)
		{
			Rect a = this.cGet(i);
			Rect b = that.cGet(i);
			b.conj();
			a.mult(b);
			dot.add(a);
		}
		return dot;	
	}
	
	public double max()
	{
		double max = 0.0;
		for(int i = 0; i < this.size(); i++)
			max = Math.max(max, this.mod(i));
		return max;
	}
	
	public void addRows(int a, double fact, int b)
	{
		this.addRows(a, fact, b, 0);
	}
	
	public void addRows(int a, Complex fact, int b, int offset)
	{
		this.imm();
		for(int j = offset; j < this.cols(); j++)
		{
			Rect add = this.cGet(a, j);
			add.mult(fact);
			this.cAdd(b, j, add);
		}
	}
	
	public void multRow(int row, Complex fact)
	{
		this.imm();
		for(int col = 0; col < this.cols(); col++)
		{
			Rect value = this.cGet(row, col);
			value.mult(fact);
			this.cSet(row, col, value);
		}
	}
	
	public void cMult(int i, int j, double re, double im)
	{
		this.cMult(this.index(i, j), re, im);
	}
	
	public void cMult(int i, int j, Complex arg)
	{
		this.cMult(this.index(i, j), arg);
	}
	
	public void cMult(int i, Complex arg)
	{
		this.cMult(i, arg.re(), arg.im());
	}
	
	public void multCol(int col, Complex fact)
	{
		this.imm();
		for(int row = 0; row < this.rows(); row++)
		{
			Rect value = this.cGet(row, col);
			value.mult(fact);
			this.cSet(row, col, value);
		}
	}

	
	public void rowSwap(int i, int j)
	{
		this.rowSwap(i, j, 0);
	}
	
	public void addRows(int a, Complex fact, int b)
	{
		addRows(a, fact, b, 0);
	}
	
	public void add1()
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		int d = this.cols() + 1;
		for(int i = 0; i < this.size(); i += d)
			this.rAdd(i, 1.0);
	}
	
	public void plot(double x0, double x1, double y0, double y1, PlaneFunc func)
	{
		double dx = (x1-x0)/(this.cols()-1);
		double dy = (y1-y0)/(this.rows()-1);
		for(int i = 0; i < this.cols(); i += 1)
		{
			double x = i * dx + x0;
			for(int j = 0; j < this.rows(); j += 1)
			{
				double y = y1 - j * dy;
				this.rSet(i, j, func.apply(x, y));
			}
		}
	}
	
	public String toString()
	{
		StringBuilder name = new StringBuilder();
		
		if(this.isReal())		
			for(int i = 0; i < this.rows(); i++)
			{
				name.append("[ " + this.rGet(i, 0));
				for(int j = 1; j < this.cols(); j++)
					name.append(", " + this.rGet(i, j));
				name.append(" ]");
			}
		else
			for(int i = 0; i < this.rows(); i++)
			{
				name.append("[ " + this.cGet(i, 0));
				for(int j = 1; j < this.cols(); j++)
					name.append(", " + this.cGet(i, j));
				name.append(" ]");
			}		
		return new String(name);
	}
	
	public String toTab()
	{
		StringBuilder name = new StringBuilder();
		
		if(this.isReal())		
			for(int i = 0; i < this.rows(); i++)
			{
				name.append("" + this.rGet(i, 0));
				for(int j = 1; j < this.cols(); j++)
					name.append("\t" + this.rGet(i, j));
				name.append("\n");
			}
		else
			for(int i = 0; i < this.rows(); i++)
			{
				name.append("" + this.cGet(i, 0));
				for(int j = 1; j < this.cols(); j++)
					name.append("\t" + this.cGet(i, j));
				name.append("\n");
			}		
		return new String(name);
	}
	
	public static Tensor power(int rows, int cols, Rect argRow, Rect argCol)
	{
		Tensor power = new Tensor(rows, cols);
		Rect last = new Rect(1.0, 0.0);
		power.cSet(0, 0, last);
		for(int j = 1; j < cols; j++)
		{
			last.mult(argCol);
			power.cSet(0, j, last);
			
		}
		last.set(1.0, 0.0);
		for(int i = 1; i < rows; i++)
		{
			last.mult(argRow);
			power.cSet(i, 0, last);
		}
		if(!power.isVec())
			for(int i = 1; i < rows; i++)
			{
				last.set(power.cGet(i,0));
				for(int j = 1; j < cols; j++)
				{
					Rect top = power.cGet(i-1,j);
					last.mult(top);
					power.cSet(i, j, last);
				}
			}
		return power;
	}
	
	public boolean sym()
	{
		if(!this.isSqr())
			return false;
		for(int i = 0; i < this.rows(); i++)
			for(int j = 0; j < this.cols(); j++)
				if(this.rGet(i,j) != this.rGet(j,i))
					return false;
		if(this.isComplex())
			for(int i = 0; i < this.rows(); i++)
				for(int j = 0; j < this.cols(); j++)
					if(this.iGet(i,j) != this.iGet(j,i))
						return false;	
		return true;
	}
	
	public void divRow(int a, Complex fact)
	{
		this.imm();
		for(int j = 0; j < this.cols(); j++)
		{
			Rect value = this.cGet(a, j);
			value.div(fact);
			this.cSet(a, j, value);
		}
	}
	
	public void addRows(int a, double fact, int b, int offset)
	{
		for(int j = offset; j < this.cols(); j++)
			this.rAdd(b, j, fact * this.rGet(a, j));
	}
	
	public void set(PlnMesh mesh, ComplexField func)
	{
		for(int i = 0; i < mesh.ny(); i++)
			for(int j = 0; j < mesh.nx(); j++)
				this.cSet(i, j, func.apply(mesh.x(j), mesh.y(i)));
	}
	
	public void set(PlnMesh mesh, PlaneFunc func)
	{
		for(int i = 0; i < mesh.ny(); i++)
			for(int j = 0; j < mesh.nx(); j++)
				this.cSet(i, j, func.apply(mesh.x(j), mesh.y(i)), 0.0);
	}
	
	public T make(PlnMesh mesh)
	{
		return this.make(mesh.ny(), mesh.nx());
	}
	
	static double pow2(double arg)
	{
		return arg * arg;
	}
	
	public void sub1()
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		int d = this.cols() + 1;
		for(int i = 0; i < this.size(); i += d)
			this.rAdd(i, -1.0);
	}
	
	public void mult(PlnMesh mesh, ComplexField func)
	{
		if(!this.compatible(mesh))
			throw new IllegalArgumentException();
		for(int i = 0; i < this.rows(); i++)
			for(int j = 0; j < this.cols(); j++)
			{
				Rect value = this.cGet(i, j);
				value.mult(func.apply(mesh.x(j), mesh.y(i)));
				this.cSet(i, j, value); 
			}
	}
	
	public int rows() 
	{
		return this.rows;
	}

	
	public int cols() 
	{
		return this.cols;
	}

	
	public int size() 
	{
		return this.size;
	}
	
	public double[][] toRows()
	{
		double[][] rows = new double[this.rows()][this.cols()];
		final int n = this.cols();
		for(int i = 0; i < this.size(); i++)
			rows[i/n][i%n] = this.rGet(i);
		return rows;
	}
	
	public double[][] toCols()
	{
		double[][] rows = new double[this.cols()][this.rows()];
		final int n = this.rows();
		for(int i = 0; i < this.size(); i++)
			rows[i%n][i/n] = this.rGet(i);
		return rows;
	}
	
	public boolean compatible(PlnMesh mesh)
	{
		return mesh.ny() == this.rows() && mesh.nx() == this.cols();
	}
	
	public void compDiv(TensorModel<?> that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		for(int i = 0; i < this.size(); i++)
		{
			Rect thisI = this.cGet(i);
			Rect thatI = that.cGet(i);
			thisI.div(thatI);
			this.cSet(i, thisI);
		}
	}
	
	public void compMult(TensorModel<?> that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		for(int i = 0; i < this.size(); i++)
		{
			Rect thisI = this.cGet(i);
			Rect thatI = that.cGet(i);
			thisI.mult(thatI);
			this.cSet(i, thisI);
		}
	}
	
	public void circshift(int di, int dj)
	{
		for(int i = 0; i < this.rows(); i++)
			for(int j = 0; j < this.cols(); j++)
			{
				final int pi = (i+di)%this.rows();
				final int pj = (j+dj)%this.cols();
				final Rect temp = this.cGet(i, j);
				this.cSet(i, j, this.cGet(pi, pj));
				this.cSet(pi, pj, temp);
			}
	}
	
	public void dimSure(TensorModel<?> arg)
	{
		if(!this.sameDims(arg))
			throw new IllegalArgumentException("Inconsistent Dimenssions");
	}
	
	public void compOper(ComplexOper func, Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException("Inconsistent Dimenions for element-wise operation.");
		for(int k = 0; k < this.size(); k++)
			this.cSet(k, func.apply(this.cGet(k), that.cGet(k)));
	}
	
	public boolean isRow()
	{
		return this.rows() == 1;
	}
	
	public boolean isCol()
	{
		return this.cols() == 1;
	}
	
	public int rowIdx(int idx)
	{
		return idx / this.cols();
	}
	
	public boolean inDomain(int i)
	{
		return 0 <= i && i < this.size();
	}
	
	public boolean inDomain(int i, int j)
	{
		return 0 <= i && i < this.rows() && 0 <= j && j < this.cols();
	}
	
	public boolean isSpc()
	{
		return this.size() == 3 && this.isReal();
	}
	
	public double iTr() throws IllegalStateException
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		if(this.isReal())
			return 0.0;
		double trace = 0.0;
		for(int i = 0; i < this.rows(); i++)
			trace += this.iGet(i, i);
		return trace;
	}
	
	public double rTr() throws IllegalStateException
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		double trace = 0.0;
		for(int i = 0; i < this.rows(); i++)
			trace += this.rGet(i, i);
		return trace;
	}
		
	public boolean sameDims(TensorModel<?> that)
	{
		return this.rows() == that.rows() && this.cols() == that.cols();
	}
	
	public boolean isSqr()
	{
		return this.rows() == this.cols();
	}
	
	public Indexed maxUnder(int row, int col)
	{
		double max = this.mod(row, col);
		int mix = row;
		for(int i = row + 1; i < this.rows(); i++)
		{
			double val = this.mod(i, col);
			if(max < val)
			{
				max = val;
				mix = i;
			}
		}
		return new Indexed(mix, max);
	}
	
	public int colIdx(int idx)
	{
		return idx % this.cols();
	}
	
	public boolean isVec()
	{
		return this.isRow() || this.isCol();
	}	
}
