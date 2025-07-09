package shabanzadeh2025.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

/**
 * Dynamically complex tensor (vector or matrix).
 * 
 * @author Dene Ringuette
 */

public class Tensor extends AbstractTensor<Tensor> implements Iterable<Tensor.Entry>, Serializable 		
{	
	private static final long serialVersionUID = -1025432115592776726L;

	public static class Entry extends Complex
	{
		private final Tensor src;
		private final int row;
		private final int col;
		private final int index;
		
		public Entry(int row, int col, Tensor src)
		{
			this.index = src.index(row, col);
			this.row = row;
			this.col = col;
			this.src = src;
		}
		
		public Entry(int index, Tensor src)
		{
			this.index = index;
			this.row = src.rowIdx(index);
			this.col = src.colIdx(index);
			this.src = src;
		}
		
		public Tensor src()
		{
			return this.src;
		}
		
		public int row()
		{
			return this.row;
		}
		
		public int col()
		{
			return this.col;
		}
		
		public int idx()
		{
			return this.index;
		}
		
		@Override
		public double im()
		{
			return this.src.iGet(this.row, this.col);
		}
		
		@Override
		public double re()
		{
			return this.src.rGet(this.row, this.col);
		}
	}
 	
	private boolean locked = false;
	
	public void lock()
	{
		this.locked = true;
	}
	
	private static Random random = new Random();
	
	public static Tensor col(double... args)
	{
		return new Tensor(args, true);
	}
	
	public static Tensor col(int arg)
	{
		return new Tensor(arg, 1);
	}
	
	public static Tensor crx(Tensor a, Tensor b)
	{
		if(!a.isSpc() || !b.isSpc())
			throw new IllegalArgumentException();
		Tensor cp = Tensor.col(3);
		cp.x(a.y() * b.z() - a.z() * b.y());
		cp.y(a.z() * b.x() - a.x() * b.z());
		cp.z(a.x() * b.y() - a.y() * b.x());
		return cp;
	}
	
	public static double det(Tensor that) throws IllegalArgumentException
	{
		// destroys matrix in calculation
		if(!that.isSqr())
			throw new IllegalArgumentException("Determinates only defined for square matrices.");
		if(!that.isReal())
			throw new IllegalArgumentException("This determinate is  only defined for real matrices.");
		if(!that.finite())
			throw new IllegalArgumentException("Matrix must be finite."); 
		
		return Ip.det(that.cols(), that.re());
	}
	
	public static Tensor fourVec()
	{
		return Tensor.col(4);
	}
	
	public static Tensor idn(int arg)
	{
		Tensor thus = new Tensor(arg, arg);
		for(int i = 0; i < arg; i++)
			thus.rSet(i, i, 1.0);
		return thus;
	}
	
	public static Tensor leastSquares(Tensor b, Tensor a)
	{
		Tensor at = a.tran();
		Tensor ata = TensorTools.mult(at, a);
		Tensor atb = TensorTools.mult(at, b);
		return Tensor.soln(ata, atb);
	}
	
	public static Tensor plane()
	{
		return Tensor.col(2);
	}
	
	public static Tensor rot(double ang, Tensor dir)
	{
		double[] vec = dir.re();
		double c = Math.cos(ang);
		double s = Math.sin(ang);
		double t = 1 - c;
		double xt = vec[0] * t;
		double yt = vec[1] * t;
		double zt = vec[2] * t;
		double xxt = vec[0] * xt;
		double yyt = vec[1] * yt;
		double zzt = vec[2] * zt;
		double xyt = vec[0] * yt;
		double yzt = vec[1] * zt;
		double zxt = vec[2] * xt;
		double xs = vec[0] * s;
		double ys = vec[1] * s;
		double zs = vec[2] * s;
		return 
			new Tensor
			(
				3, 3,
				xxt + c, xyt - zs, zxt + ys,
				xyt + zs, yyt + c, yzt - xs,
				zxt - ys, yzt + xs, zzt + c
			);
	}
	
	public static Tensor rot(double[] quat) 
	{
		double aa = quat[0] * quat[0];
		double bb = quat[1] * quat[1];
		double cc = quat[2] * quat[2];
		double dd = quat[3] * quat[3];
		double ab2 = 2 * quat[0] * quat[1];
		double ac2 = 2 * quat[0] * quat[2];
		double ad2 = 2 * quat[0] * quat[3];
		double bc2 = 2 * quat[1] * quat[2];
		double bd2 = 2 * quat[1] * quat[3];
		double cd2 = 2 * quat[2] * quat[3];
		return 
			new Tensor
			(
				3, 3,
				aa + bb - cc - dd, bc2 - ad2, bd2 + ac2,
				bc2 + ad2, aa - bb + cc - dd, cd2 - ab2,
				bd2 - ac2, cd2 + ab2, aa - bb - cc + dd
			);
	}
	
	public static Tensor rot(Tensor quat)
	{
		return Tensor.rot(quat.re());
	}
	
	public static Tensor rot(double a, double b) 
	{
		return new Tensor(2, 2, a, -b, b, a);
	}
	
	public static Tensor rot(double ang) 
	{
		return Tensor.rot(Math.cos(ang), Math.sin(ang));
	}
	
	public static Tensor row(double[] args)
	{
		return new Tensor(args, false);
	}
	
	public static Tensor row(int arg)
	{
		return new Tensor(1, arg);
	}
	
	public static Tensor soln(Tensor mat0, Tensor mat1) throws IllegalArgumentException
	{
		if(!mat0.isReal() || !mat1.isReal())
			throw new IllegalArgumentException();
		if(mat0.rows() != mat1.rows())
			throw new IllegalArgumentException("Dimension Inconsistent.  Input and output rows must match.");
		if(mat0.rows() < mat0.cols())
			throw new IllegalArgumentException("Under-Determined Linear System has no solution.");
		if(mat0.cols() < mat0.rows())
			throw new IllegalArgumentException("Over-Determined Linear System should use Least-Squares.");
		
		Op.soln(mat0.cols(), mat0.re(), mat1.cols(), mat1.re());
		return mat1;
	}
	
	public static Tensor spc()
	{
		return Tensor.col(3);
	}
	
	public static Tensor sqr(int arg)
	{
		return new Tensor(arg, arg);
	}
	
	public static Tensor sqr(int arg, double... vals)
	{
		return new Tensor(arg, arg, vals);
	}
	
	private double[] re;
	private double[] im;
	
	private Tensor(double[] args, boolean column)
	{
		super(column ? args.length : 1, column ? 1 : args.length);
		this.re = args;
		this.im = null;
	}
	
	public Tensor(PlnMesh mesh)
	{
		this(mesh.ny(), mesh.nx());
	}
	
	public void set(PlnMesh mesh, PlaneFunc func)
	{
		for(int i = 0; i < mesh.ny(); i++)
			for(int j = 0; j < mesh.nx(); j++)
				this.rSet(i, j, func.apply(mesh.x(j), mesh.y(i)));
		if(this.isComplex())
			Ip.zero(this.im());
	}
	
	public Tensor(double[][] mat) throws IllegalArgumentException
	{
		super(mat.length, mat[0].length);
		int n = mat[0].length;
		for(double[] i : mat)
			if(i.length != n)
				throw new IllegalArgumentException();
		this.re = this.block();
		this.im = null;
		for(int i = 0; i < this.size(); i++)
			this.re[i] = mat[rowIdx(i)][colIdx(i)];
	}
	
	public Tensor(int rows, int cols)
	{
		super(rows, cols);
		this.re = this.block();
		this.im = null;
	}
	
	public Tensor(int rows, int cols, boolean imaginary)
	{
		this(rows, cols);
		if(imaginary)
			this.imm();
	}
	
	public Tensor(int rows, int cols, double... vals)
	{
		super(rows, cols);
		if(vals.length != this.size())
			throw new IllegalArgumentException();
		this.re = this.block();
		for(int i = 0; i < this.size; i++)
			this.re[i] = vals[i];
		this.im = null;
	}
	
	public void add(double arg)
	{
		for(int i = 0; i < this.size(); i++)
			this.re[i] += arg;
	}
	
	public void add(double re0, double im0)
	{
		Ip.add(this.re, re0);
		this.imm();
		Ip.add(this.im, im0);		
	}
	
	public void add(double re0, double im0, Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		this.imm();
		for(int i = 0; i < this.size(); i++)
		{
			double re1 = that.re[i];
			double im1 = that.im[i];
			this.re[i] += re0 * re1 - im0 * im1;
			this.im[i] += re0 * im1 + re1 * im0;
		}
	}
	
	public void add(double arg, Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		for(int i = 0; i < this.size(); i++)
			this.re[i] += arg * that.re[i];
		if(that.isReal())
			return;
		this.imm();
		for(int i = 0; i < this.size(); i++)
			this.im[i] += arg * that.im[i];
	}
	
	public void add(Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		for(int i = 0; i < this.size(); i++)
			this.re[i] += that.re[i];
		if(that.isReal())
			return;
		this.imm();
		for(int i = 0; i < this.size(); i++)
			this.im[i] += that.im[i];
	}
	
	public void compMult(TensorModel<?> that)
	{
		if(that.isReal() && that instanceof Tensor)
		{
			double[] other = ((Tensor)that).re();
			Ip.compMult(this.re(), other);
			if(this.isComplex())
				Ip.compMult(this.im(), other);
		}
		else
			super.compMult(that);
	}
	
	public void compDiv(TensorModel<?> that)
	{
		if(that.isReal() && that instanceof Tensor)
		{
			double[] other = ((Tensor)that).re();
			Ip.compDiv(this.re(), other);
			if(this.isComplex())
				Ip.compDiv(this.im(), other);;
		}
		else
			super.compDiv(that);
	}
	
	public void addOutPro(double fact, Tensor that)
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		if(!that.isVec())
			throw new IllegalArgumentException();
		if(!that.isReal())
			throw new IllegalArgumentException();
		if(that.size() != this.rows())
			throw new IllegalArgumentException();
		final int n = that.size();
		for(int i = 0; i < this.size(); i++)
			this.re[i] += fact * that.re[i/n] * that.re[i%n];
	}
	
	public void removeEigenVector(Tensor eig)
	{
		Tensor prod = TensorTools.quadProd(eig, this, eig);
		double lambda = prod.rGet(0, 0);
		this.addOutPro(-lambda, eig, eig);
	}
	
	public void addOutPro(double fact, Tensor arg0, Tensor arg1)
	{
		if(!arg0.isVec() || !arg1.isVec())
			throw new IllegalArgumentException();
		if(!arg0.isReal() || !arg1.isReal())
			throw new IllegalArgumentException();
		if(this.rows() != arg0.size() || this.cols() != arg1.size())
			throw new IllegalArgumentException();
		final int n = this.cols();
		for(int i = 0; i < this.size(); i++)
			this.re[i] += fact * arg0.re[i/n] * arg1.re[i%n];
	}
	
	public void addOutPro(Tensor that)
	{
		this.addOutPro(1.0, that);
	}

	public Tensor adj()
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		int n = this.rows();
		Tensor thus = new Tensor(n, n, this.isComplex());
		for(int i = 0; i < this.size(); i++)
			thus.re[i] = Pow.neg(i/n+i%n) * Tensor.det(this.minor(i%n, i/n));
		return thus;
	}
		
	public void adj2x2()
	{
		double t;
		t = re[0];
		re[0] = re[3];
		re[1] = - re[1];
		re[2] = - re[2];
		re[3] = t;
		if(this.isReal())
			return;
		t = im[0];
		im[0] = im[3];
		im[1] = - im[1];
		im[2] = - im[2];
		im[3] = t;
	}
	
	public void adj3x3()
	{
		double a = re[0];
		double b = re[1];
		double c = re[2];
		double d = re[3];
		double e = re[4];
		double f = re[5];
		double g = re[6];
		double h = re[7];
		double i = re[8];
		re[0] = e*i-f*h;
		re[1] = c*h-b*i;
		re[2] = b*f-c*e;
		re[3] = f*g-d*i;
		re[4] = a*i-c*g;
		re[5] = c*d-a*f;
		re[6] = d*h-e*g;
		re[7] = b*g-a*h;
		re[8] = a*e-b*d;
	}
	
	public double ang(int i, int j)
	{
		return this.ang(this.index(i, j));
	}
	
	public double ang(int k)
	{
		return Math.atan2(this.iGet(k), this.rGet(k));
	}
	
	public double angle(Tensor that) 
	{
		return Math.acos( this.dot(that) / (this.norm() * that.norm()) );
	}
		
	public void apply(DoubleUnaryOperator func)
	{
		if(!this.isReal())
			throw new IllegalStateException();
		
		for(int i = 0; i < this.size(); i++)
			this.re[i] = func.applyAsDouble(this.re[i]);
	}
	
	private double[] block()
	{
		return new double[this.size()];
	}
	
	public void cAdd(int i, Complex arg)
	{
		this.cAdd(i, arg.re(), arg.im());
	}
	
	public void cAdd(int i, double re, double im)
	{
		this.re[i] += re;
		this.imm();
		this.im[i] += im;
	}
		
	public void cMult(int i, double re, double im)
	{
		this.imm();
		final double rei = this.re[i];
		final double imi = this.im[i];
		this.re[i] = re * rei - im * imi;
		this.im[i] = im * rei + re * imi;
	}
	
	public void cAdd(int i, int j, Complex arg)
	{
		this.cAdd(i, j, arg.re(), arg.im());
	}
	
	public void cAdd(int i, int j, double re, double im)
	{
		this.cAdd(this.index(i, j), re, im);
	}
	
	public Rect cGet(int i)
	{
		if(this.isReal())
			return new Rect(this.re[i], 0.0);
		return new Rect(this.re[i], this.im[i]);
	}
	
	public Rect cGet(int i, int j)
	{
		return this.cGet(this.index(i, j));
	}
	
	public Tensor col(int index, boolean trans)
	{
		Tensor col;
		if(trans)
			col = new Tensor(1, this.rows());
		else
			col = new Tensor(this.rows(), 1);	
		for(int i = 0; i < col.size(); i++)
			col.rSet(i, this.rGet(i, index));
		if(this.isReal())
			return col;
		for(int i = 0; i < col.size(); i++)
			col.iSet(i, this.iGet(i, index));
		return col;
	}
	
	public int cols()
	{
		return this.cols;
	}
	
	public void comb(Oper oper, Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		if(!this.isReal())
			throw new IllegalStateException();
		if(!that.isReal())
			throw new IllegalArgumentException();
		
		for(int i = 0; i < this.size(); i++)
			this.re[i] = oper.eval(this.re[i], that.re[i]);
	}
	
	public void conjugate()
	{
		if(this.isComplex())
			for(int i = 0; i < this.size(); i++)
				this.im[i] *= -1.0;
	}
	
	public Tensor copy()
	{
		Tensor thus = this.str();
		System.arraycopy(this.re, 0, thus.re, 0, this.size());
		if(this.isComplex())
			System.arraycopy(this.im, 0, thus.im, 0, this.size());
		return thus;
	}
	
	public void cSet(int i, Complex arg)
	{
		this.cSet(i, arg.re(), arg.im());
	}
	
	public void cSet(int i, double re, double im)
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		this.re[i] = re;
		this.imm();
		this.im[i] = im;
	}
	
	public void cSet(int i, int j, Complex arg)
	{
		this.cSet(i, j, arg.re(), arg.im());
	}
	
	public void cSet(int i, int j, double re, double im)
	{
		this.cSet(this.index(i, j), re, im);
	}
	
	public void cSetAll(double re, double im)
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		this.imm();
		for(int i = 0; i < this.size(); i++)
		{
			this.re[i] = re;
			this.im[i] = im;
		}
	}
	
	public boolean degDef(double pseudoZero)
	{
		
		if(!this.isSqr())
			throw new IllegalStateException();
		if(!this.isReal())
			throw new IllegalStateException();
		double zero = Math.abs(pseudoZero);
		int dim = this.rows();
		double inverseDiagonal, factor;
		Tensor matrix = this.copy();
		for(int d = 0; d < dim; d++)
		{
			if(Math.abs(matrix.rGet(d, d)) <= zero)
				return true;
			inverseDiagonal = 1.0 / matrix.rGet(d, d);
			for(int i = d+1; i < dim; i++)
			{
				factor = inverseDiagonal * matrix.rGet(i, d);
				for(int j = d+1; j < dim; j++)
					matrix.rAdd(i, j,  - factor * matrix.rGet(d, j));
			}
		}
		return false;
	}
	
	public double det()
	{
		return Tensor.det(this.copy());
	}
	
	public double det2x2()
	{
		if(this.isSqr())
		{	
			if(this.size() == 4)
				return re[0] * re[3] - re[1] * re[2];
			if(this.size() == 9)
				return re[0] * this.re[4] - re[1] * re[3];
			return this.rGet(0, 0) * this.rGet(1, 1) - this.rGet(0, 1) * this.rGet(1, 0); 
		}
		throw new IllegalArgumentException();
	}
	
	public double det3x3()
	{
		return 
			re[0] * (re[4] * re[8] - re[5] * re[7]) +
			re[1] * (re[5] * re[6] - re[3] * re[8]) +
			re[2] * (re[3] * re[7] - re[4] * re[6]);
	}
	
	public Tensor diag()
	{
		int n = Math.min(this.rows(), this.cols());
		Tensor diag = Tensor.col(n);
		for(int i = 0; i < n; i++)
			diag.rSet(i, this.rGet(i, i));
		if(this.isReal())
			return diag;
		diag.imm();
		for(int i = 0; i < n; i++)
			diag.iSet(i, this.iGet(i, i));
		return diag;
	}
	
	public void diagSwap(int d, int s)
	{
		Ip.diagSwap(this.cols(), this.re(), d, s);
		if(this.isReal())
			return;
		Ip.diagSwap(this.cols(), this.im(), d, s);
	}
	
	public int dim()
	{
		if(this.isVec())
			return this.size();
		if(this.isSqr())
			return this.rows();
		throw new IllegalStateException();
	}
	
	public double dirCon(Tensor that, int n)
	{
		int[] d = Meth.divisors(n);
		int number = d.length;
		int last = number - 1;
		int largest = d[last];
		if(this.size()-1 < largest || that.size()-1 < largest)
			return Double.NaN;
		double dirCon = 0.0;
		for(int i = 0; i < number; i++)
			dirCon += this.re[d[i]] * that.re[d[last-i]];
		return dirCon;
	}
	
	public void div(double re0)
	{
		this.mult(1.0 / re0);
	}
	
	public void div(double re0, double im0)
	{
		double mod2 = re0 * re0 + im0 * im0;
		this.mult(re0 / mod2, - im0 / mod2);
	}
	
	public double dot(Tensor that)
	{
		if(this.isComplex() || that.isComplex())
			throw new IllegalArgumentException();
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		double dot = 0.0;
		for(int i = 0; i < this.size(); i++)
			dot += this.re[i] * that.re[i];
		return dot;	
	}
	
	public void dropIm()
	{
		this.im = null;
	}
	
	public Tensor dwt()
	{
		if(this.isComplex())
			throw new IllegalStateException();
		if(!this.isVec())
			throw new IllegalStateException();
		double[] dwt = Op.dwt(this.re);
		if(this.isRow())
			return Tensor.row(dwt);
		return Tensor.col(dwt);
	}
	
	public void dwt2d()
	{
		for(int j = 0; j < this.cols(); j++)
			this.setCol(j, this.getCol(j).dwt());
		for(int i = 0; i < this.rows(); i++)
			this.setRow(i, this.getRow(i).dwt());
	}
		
	public double eigHypot() 
	{
		if(this.isComplex())
			return Double.NaN;
		double dotSum = 0.0;
		for(int i = 0; i < this.rows(); i++)
			for(int k = 0; k < this.cols(); k++)
				dotSum += this.rGet(i,k) * this.rGet(k,i);
		return Math.sqrt(dotSum);
	}
	
	public double eigVal(Tensor that)
	{
		return that.dot(TensorTools.mult(this, that))/that.dot(that);
	}
	
	public void fft() 
    {
    	if(!this.isVec())
    		throw new IllegalArgumentException("One dimensional FFT not defined for matrix.");
    	this.imm();
    	Ip.fft(this.re, this.im);
    }
	
	public void fft2d()
	{
		this.imm();
		for(int j = 0; j < this.cols(); j++)
		{
			Tensor col = this.getCol(j);
			col.fft();
			this.setCol(j, col);
		}
		for(int i = 0; i < this.rows(); i++)
		{
			Tensor row = this.getRow(i);
			row.fft();
			this.setRow(i, row);
		}
	}
	
	public class Fcc implements Runnable
	{
		private Tensor m;
		private int j;
		
		public Fcc (Tensor m, int j)
		{
			this.m = m;
			this.j = j;		
		}

		@Override
		public void run() 
		{
			Tensor col = this.m.getCol(this.j);
			col.fft();
			this.m.setCol(j, col);
		}
		
	}

	public class Fcr implements Runnable
	{
		private Tensor m;
		private int i;
		
		public Fcr (Tensor m, int i)
		{
			this.m = m;
			this.i = i;		
		}

		@Override
		public void run() 
		{
			Tensor row = this.m.getRow(i);
			row.fft();
			this.m.setRow(i, row);
		}
		
	}
	
	public void modDrop()
	{
		if(this.isReal())
		{
			for(int i = 0; i < this.size(); i++)
				this.re[i] = Math.abs(this.re[i]);
			return;
		}
		for(int i = 0; i < this.size(); i++)
			this.rSet(i, this.mod(i));
	}
	
	public void mod2Drop()
	{
		if(this.isReal())
		{
			for(int i = 0; i < this.size(); i++)
				this.re[i] *= this.re[i];
			return;
		}
		for(int i = 0; i < this.size(); i++)
			this.rSet(i, this.mod2(i));
	}
	
	public boolean finite()
	{
		boolean finite = Stat.finite(this.re);
		if(this.isComplex())
			finite &= Stat.finite(this.im);
		return finite;
	}
	
	public void forceDims(int rows, int cols)
	{
		if(this.rows() != rows || this.cols() != cols)
			throw new IllegalStateException();
	}
	
	public Tensor getCol(int j)
	{
		final int n = this.rows();
		Tensor col = Tensor.col(n);
		for(int i = 0; i < n; i++)
			col.rSet(i, this.rGet(i, j));
		if(this.isReal())
			return col;
		for(int i = 0; i < n; i++)
			col.iSet(i, this.iGet(i, j));
		return col;
	}
	
	public Tensor getRow(int i)
	{
		final int n = this.cols();
		Tensor row = Tensor.row(n);
		for(int j = 0; j < n; j++)
			row.rSet(j, this.rGet(i, j));
		if(this.isReal())
			return row;
		for(int j = 0; j < n; j++)
			row.iSet(j, this.iGet(i, j));
		return row;
	}
	
	public double[][] rGetRows()
	{
		final double[][] array2D = new double[this.rows][this.cols];
		for(int row = 0; row < this.rows; row++)
			for(int col = 0; col < this.cols; col++)
				array2D[row][col] = this.rGet(row, col);
		return array2D;
	}
	
	public double hypot()
	{
		return this.norm();
	}
	
	public void idn()
	{
		this.set1();
	}
	
	public void ifft() 
    {
    	if(!this.isVec())
    		throw new IllegalArgumentException();
    	
    	this.imm();	
    	
    	Ip.ifft(this.re, this.im);
    }
	
	public void interflip()
	{
		Ip.interflip(this.cols(), this.re());
		if(this.isReal())
			return;
		Ip.interflip(this.cols(), this.im());
	}
	
	public void ifft2d()
	{
		for(int i = 0; i < this.rows(); i++)
		{
			Tensor row = this.getRow(i);
			row.ifft();
			this.setRow(i, row);
		}
		for(int j = 0; j < this.cols(); j++)
		{
			Tensor col = this.getCol(j);
			col.ifft();
			this.setCol(j, col);
		}
	}
	
	public double iGet(int i)
	{
		if(this.isReal())
		{
			if(!this.inDomain(i))
				throw new IndexOutOfBoundsException("Virtual Out of Bounds.");	
			return 0.0;
		}
		return this.im[i];
	}
	
	public double iGet(int i, int j)
	{
		return this.iGet(this.index(i, j));
	}
	
	public boolean illConditioned()
	{
		double[][] that = this.toRows();
		for(int i = 0; i < that.length; i++)
			for(int j = i + 1; j < that.length; j++)
				if(0.5 < Stat.cos2(that[i], that[j]))
					return true;
		return false;
	}
	
	public double[] im()
	{
		this.imm();
		return this.im;
	}
	
	public void im(double[] args)
	{
		if(args.length != this.size())
			throw new IllegalArgumentException();
		this.im = args;
	}
	
	public void imm()
	{
		if(this.isReal())
			this.im = this.block();
	}

	public int index(int i, int j)
	{
		return i * this.cols() + j;
	}
	
	public void inv()
	{
		if(!this.isSqr())
			throw new IllegalArgumentException();
		Tensor inverse = Tensor.idn(this.rows());
		Tensor reverse = this;
		if(!TensorTools.reduceDiag(reverse, inverse, reverse.max() * Meth.INSIGNIFICANCE))
			throw new IllegalStateException("Irreducible matrix");
		TensorTools.backSubDiag(reverse, inverse);
		this.re = inverse.re;
		this.im = inverse.im;
	}
	
	public static int[] reduce(Tensor rev, Tensor inv)
	{
		TensorTools.reducable(rev, inv);
		final int rows = rev.rows();
		final int cols = rev.cols();
		int flips = 0;
		int row = 0;
		int col = 0;
		while(row < rows && col < cols)
		{
			Indexed swt = rev.maxUnder(row, col);
			if(swt.value() == 0.0)
				col++;
			else
			{
				if(swt.index() != row)
				{
					rev.rowSwap(row, swt.index(), col);
					if(inv != null)
						inv.rowSwap(row, swt.index(), 0);
					flips++;
				}
				for(int i = row+1; i < rows; i++)
				{
					if(rev.isReal())
					{
						double fact = -rev.rGet(i,col) / rev.rGet(row,col);
						rev.addRows(row, fact, i, col);
						if(inv != null)
							inv.addRows(row, fact, i, 0);
					}
					else
					{
						Rect fact = rev.cGet(i,col);
						fact.div(rev.cGet(row,col));
						fact.neg();
						rev.addRows(row, fact, i, col);
						if(inv != null)
							inv.addRows(row, fact, i, 0);
					}
				}
				row++;
				col++;
			}
		}
		return new int[]{flips, row};
	}
		
	public static void backsub(Tensor reverse, Tensor inverse)
	{
		if(!reverse.isSqr())
			throw new IllegalArgumentException();
		if(reverse.rows() != inverse.rows())
			throw new IllegalArgumentException();
		
		for(int d = reverse.rows() - 1; 0 <= d; d -= 1)
			for(int i = 0; i < d; i++)
			{
				double fact = - reverse.rGet(i,d)/ reverse.rGet(d,d);
				reverse.addRows(d, fact, i, d);
				inverse.addRows(d, fact, i, 0);
			}
	}
	
	public void inv2x2()
	{
		double det = this.det2x2();
		this.adj2x2();
		this.div(det);
	}
	
	public void inv3x3()
	{
		double det = this.det3x3();
		this.adj3x3();
		this.div(det);
	}
	
	public boolean isComplex()
	{
		return this.im != null;
	}
	
	public void iSet(int i, double arg)
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		this.imm();
		this.im[i] = arg;
	}

	public void iSet(int i, int j, double arg)
	{
		this.iSet(this.index(i, j), arg);
	}

	public void iSetAll(double arg)
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		for(int i = 0; i < this.size(); i++)
			this.im[i] = arg;
	}
	
	public boolean isOver(Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		if(this.isReal())
		{
			for(int i = 0; i < this.size(); i++)
				if(this.re[i] <= that.re[i])
					return false;
		}
		else
		{
			for(int i = 0; i < this.size(); i++)
				if(this.mod(i) <= that.mod(i))
					return false;
		}
		return true;
	}
	
	public boolean isReal()
	{
		return this.im == null;
	}

	public boolean isRealSymmetric()
	{
		if(this.isComplex())
			return false;
		if(!this.isSqr())
			return false;
		
		boolean symmetric = true;
		for(int row = 1; row < this.rows; row++)
			for(int col = row; col < this.cols; col++)
				symmetric &= this.rGet(col, row) == this.rGet(row, col);
		return symmetric;
	}
	
	public double asymmetry()
	{
		if(!this.isSqr())
			throw new IllegalArgumentException("Method only applies to square matricies.");
		
		double max = 0.0;
		for(int row = 0; row < this.rows; row++)
			for(int col = 0; col < this.cols; col++)
				max = Math.max(max, this.mod(row, col));
		
		double max_diff = 0.0;
		for(int row = 1; row < this.rows; row++)
			for(int col = row; col < this.cols; col++)
				max_diff = Math.max(max_diff, this.asymmetry(row, col));
		
		return max_diff/max;
	}
	
	public double asymmetry(int i, int j)
	{
		if(this.isReal())
			return Math.abs(this.rGet(i, j) - this.rGet(j, i));
		
		return Rect.diff(this.cGet(i, j), this.cGet(j, i)).mod();
	}
	
	public void forseSymmetric()
	{
		if(!this.isSqr())
			throw new IllegalStateException("Enforced symmetry is only for square matrices.");
		
		final int rows = this.rows;
		final int cols = this.cols;
		
		for(int row = 1; row < rows; row++)
			for(int col = row; col < cols; col++)
			{
				final double a = this.rGet(row, col);
				final double b = this.rGet(col, row);
				final double c = (a+b)/2;
				this.rSet(row, col, c);
				this.rSet(col, row, c);
			}
		
		if(this.isComplex())
			for(int row = 1; row < rows; row++)
				for(int col = row; col < cols; col++)
				{
					final double a = this.iGet(row, col);
					final double b = this.iGet(col, row);
					final double c = (a+b)/2;
					this.iSet(row, col, c);
					this.iSet(col, row, c);
				}
				
	}
	
	public EigenDecomp eigenDecomp() 
	{
		if(!this.isSqr())
			throw new IllegalStateException("Eigen decomposition only for square matrices.");
		
		final int dim = this.dim();
		
		Tensor eigVals = Tensor.col(dim);
		Tensor eigVecs = Tensor.sqr(dim);
		Tensor deplete = this.copy();
		
		for(int k = 0; k < dim; k++)
		{
			Tensor eig = deplete.maxEigenVector(1e-15);
			
			eigVecs.setCol(k, eig);
			
			Tensor eigVal = TensorTools.quadProd(eig, this, eig);
			
			if(eigVal.isComplex())
				eigVals.cSet(k, eigVal.cGet(0,0));
			else
				eigVals.rSet(k, eigVal.rGet(0,0));
			
			deplete.removeEigenVector(eig);
		}
		
		return new EigenDecomp(this, eigVals, eigVecs);
	}
	
	public boolean isUnder(Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		if(this.isReal())
		{
			for(int i = 0; i < this.size(); i++)
				if(this.re[i] >= that.re[i])
					return false;
		}
		else
		{
			for(int i = 0; i < this.size(); i++)
				if(this.mod(i) >= that.mod(i))
					return false;
		}
		return true;
	}
		
	public double mag2()
	{
		double mag2 = 0.0;
		for(double i : this.re)
			mag2 += i * i;
		if(this.isComplex())
			for(double i : this.im)
				mag2 += i * i;
		return mag2;
	}
	
	public void max(Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		for(int i = 0; i < this.size(); i++)
			this.re[i] = Math.max(this.re[i], that.re[i]);
		if(that.isReal())
			return;
		this.imm();
		for(int i = 0; i < this.size(); i++)
			this.im[i] += Math.max(this.im[i], that.im[i]);
	}

	public Tensor maxEigenVector(double percision)
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		Tensor ab;
		Tensor b = Tensor.col(Gen.unit(this.rows())); 
		double eo;
		double e = Double.POSITIVE_INFINITY;
		do
		{
			eo = e;
			ab = TensorTools.mult(this, b);
			e = b.dot(ab);
			b = ab;
			b.normalize();
		}	
		while(percision < Math.abs(1-e/eo));
		return b;
	}
	
	public void min(Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		for(int i = 0; i < this.size(); i++)
			this.re[i] = Math.min(this.re[i], that.re[i]);
		if(that.isReal())
			return;
		this.imm();
		for(int i = 0; i < this.size(); i++)
			this.im[i] += Math.min(this.im[i], that.im[i]);
	}

	public Tensor minor(int row, int column)
	{
		int n = this.cols();
		int m = n-1;
		Tensor minor = new Tensor(this.rows()-1, this.cols()-1, this.isComplex());
		int i, j;
		for(i = 0; i < row; i++)
		{
			for(j = 0; j < column; j++)
				minor.re[i*m+j] = this.re[i*n+j];
			for(j = column + 1; j < this.cols(); j++)
				minor.re[i*m+j-1] = this.re[i*n+j];
		}
		for(i = row+1; i < this.rows(); i++)
		{
			for(j = 0; j < column; j++)
				minor.re[(i-1)*m+j] = this.re[i*n+j];
			for(j = column + 1; j < this.cols(); j++)
				minor.re[(i-1)*m+j-1] = this.re[i*n+j];
		}
		return minor;
	}

	public Tensor subTensor(int r0, int nr, int c0, int nc)
	{
		Tensor subTensor = new Tensor(nr, nc, this.isComplex());
		for(int i = 0; i < nr; i++)
			for(int j = 0; j < nc; j++)
				subTensor.rSet(i,j, this.rGet(r0+i, c0+j));
		if(this.isComplex())
			for(int i = 0; i < nr; i++)
				for(int j = 0; j < nc; j++)
					subTensor.iSet(i,j, this.iGet(r0+i, c0+j));		
		return subTensor;
	}
	
	public double mod(int i)
	{
		if(this.isReal())
			return Math.abs(this.re[i]);
		return Math.hypot(this.re[i], this.im[i]);
	}
	
	public double mod2(int i)
	{
		if(this.isReal())
			return Pow.two(this.re[i]);
		return Meth.mod2(this.re[i], this.im[i]);
	}
	
	public double mod2(int i, int j)
	{
		return this.mod2(this.index(i, j));
	}
	
	public double mod(int i, int j)
	{
		return this.mod(this.index(i, j));
	}
	
	public void mult(double arg)
	{
		Ip.mult(this.re, arg);
		if(this.isComplex())
			Ip.mult(this.im, arg);
	}
	
	public void mult(double re0, double im0)
	{
		this.imm();
		for(int i = 0; i < this.size(); i++)
		{
			double re1 = this.re[i];
			double im1 = this.im[i];
			this.re[i] = re0 * re1 - im0 * im1;
			this.im[i] = re0 * im1 + re1 * im0;
		}
	}

	public void mult(Complex arg)
	{
		this.mult(arg.re(), arg.im());
	}

	
	public void multCols(double[] that)
	{
		for(int i = 0; i < this.size(); i++)
			this.re[i] *= that[i%this.cols()];
		if(this.isReal())
			return;
		for(int i = 0; i < this.size(); i++)
			this.im[i] *= that[i%this.cols()];
	}
	
	public void multCols(Tensor that)
	{
		if(!that.isVec())
			throw new IllegalArgumentException();
		
		if(that.isReal())
			this.multCols(that.re());
		else
		{
			final int fact = this.cols();
			for(int index = 0; index < this.size(); index++)
				this.cMult(index, that.cGet(index%fact));
		}
	}

	@Override
	public void multL(Tensor arg)
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		if(!arg.isSqr())
			throw new IllegalArgumentException();
		Tensor mat = TensorTools.mult(arg, this);
		this.re(mat.re());
		if(mat.isComplex())
			this.im(mat.im());
	}
	
	@Override
	public void multR(Tensor arg)
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		if(!arg.isSqr())
			throw new IllegalArgumentException();
		Tensor mat = TensorTools.mult(this, arg);
		this.re(mat.re());
		if(mat.isComplex())
			this.im(mat.im());
	}
	
	public void multRows(double[] that)
	{
		for(int i = 0; i < this.size(); i++)
			this.re[i] *= that[rowIdx(i)];
		if(this.isReal())
			return;
		for(int i = 0; i < this.size(); i++)
			this.im[i] *= that[rowIdx(i)];
	}

	public void neg()
	{
		Ip.neg(this.re);
		if(this.isReal())
			return;
		Ip.neg(this.im);
	}
	
	public void perp2d()
	{
		Ip.swap(this.re, 0, 1);
		this.re[0] *= -1;
	}
	
	@Override
	public double norm()
	{
		double reNorm = Stat.hypot(this.re);
		if(this.isReal())
			return reNorm;
		double imNorm = Stat.hypot(this.im);
		return Math.hypot(reNorm, imNorm);
	}
	
	@Override
	public double norm2()
	{
		double dot = 0.0;
		for(int i = 0; i < this.size(); i++)
			dot += pow2(this.re[i]);
		if(this.isReal())
			return dot;
		for(int i = 0; i < this.size(); i++)
			dot += pow2(this.im[i]);
		return dot;	
	}
	
	@Override
	public void normalize()
	{
		this.div(this.norm());
	}
	
	public List<double[]> nullSpace()
	{
		Tensor that = this.copy();
		
		double zero = Stat.absMax(that.re) * 1.0e-9;
		int dim = that.cols();
		int maxIndex;
		double maxValue, value, factor;
		double[] inversePivot = new double[dim];
		int[] p = new int[dim];
		
		int n = 0;
		int d = 0;
		while(d+n < dim)
		{
			maxValue = that.rGet(d,d+n);
			maxIndex = d;
			for(int i = d + 1; i < dim; i++)
			{
				value = Math.abs(that.rGet(i,d+n));
				if(maxValue < value)
				{
					maxValue = value;
					maxIndex = i;
				}
			}
			if(maxValue < zero) 
				n++;
			else
			{	
				p[d] = d+n;
				if(maxIndex != d)
				{
					that.rowSwap(d, maxIndex);
				}
				inversePivot[d] = 1 / that.rGet(d,p[d]);
				for(int i = d + 1; i < dim; i++)
				{
					factor = - inversePivot[d] * that.rGet(i,p[d]);
					for(int j = p[d]+1; j < dim; j++)
						that.re[i * that.cols() + j] += factor * that.rGet(d,j);
				}
				d++;
			}
		}
		p[d] = d+n;
		
		int[] nullIndex = new int[n];
		int m = 0;
		for(d = 0; p[d] < dim; d++)
			for(int i = p[d] + 1; i < p[d+1]; i++)
				nullIndex[m++] = i;
		
		int[] nullStart = new int[dim-n];
		m = 0;
		for(d = 0; p[d] < dim; d++)
		{
			while(nullIndex[m] < p[d])
				m++;
			nullStart[d] = m;
		}		
		
		for(d = dim - 1 - n; 0 <= d; d--)
			for(int i = 0; i < d; i++)
			{
				factor = - inversePivot[d] * that.rGet(i,p[d]);
				for(m = nullStart[d]; m < n; m++)
					that.re[i*that.cols() + nullIndex[m]] += factor * that.rGet(d,nullIndex[m]);
			}
				
		List<double[]> nullSpace = new ArrayList<double[]>();
		for(int i = 0; i < that.rows(); i++)
			nullSpace.add(new double[this.cols()]);
		
		for(m = 0; m < n; m++)
			nullSpace.get(m)[nullIndex[m]] = 1;
		
		for(d = 0; p[d] < dim; d++)
			for(m = nullStart[d]; m < n; m++)
				nullSpace.get(m)[p[d]] = - inversePivot[d] * that.rGet(d, nullIndex[m]);
			
		return nullSpace;
	}
	
	public Tensor outPro()
	{
		if(!this.isVec())
			throw new IllegalStateException();
		if(!this.isReal())
			throw new IllegalStateException();
		final int n = this.size();
		Tensor thus = Tensor.sqr(n);
		for(int i = 0; i < thus.size(); i++)
			thus.re[i] = this.re[i/n] * this.re[i%n];
		return thus;
	}
	
	public boolean def(final int sign)
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		
		if(this.isComplex())
			throw new IllegalStateException();
		
		Tensor test = this.copy();
		
		if(!TensorTools.reduceDiag(test, null, test.max() * Meth.INSIGNIFICANCE))
			return false;
		
		final int diag_step = test.cols() + 1;
		final int size = test.size();
		
		for(int i = 0; i < size; i += diag_step)
			if(test.re[i] * sign < 0)
				return false;
		
		return true;
	}
	
	public boolean posDef()
	{
		return this.def(1);
	}
	
	public boolean negDef()
	{
		return this.def(-1);
	}
		
	public Tensor quadPro(Tensor that)
	{
		return TensorTools.mult(that.tran(), TensorTools.mult(this, that));
	}

	public void rAdd(int i, double arg)
	{
		this.re[i] += arg;
	}
	
	public void rDiv(int i, double arg)
	{
		this.re[i] /= arg;
	}
	
    public void rAdd(int i, int j, double arg)
	{
		this.rAdd(this.index(i, j), arg);
	}
    
    public void rDiv(int i, int j, double arg)
   	{
   		this.rDiv(this.index(i, j), arg);
   	}
    
	public void random()
	{
		this.im = null;
		for(int i = 0; i < this.size(); i++)
			this.re[i] = Tensor.random.nextDouble();
	}
    
	public void randomGaussian()
	{
		this.im = null;
		for(int i = 0; i < this.size(); i++)
			this.re[i] = Tensor.random.nextGaussian();
	}
	
	public void randomGaussian(double mean, double variance)
	{
		double sd = Math.sqrt(variance);
		this.im = null;
		for(int i = 0; i < this.size(); i++)
			this.re[i] = mean + sd * Tensor.random.nextGaussian();
	}
	
	public void randomOne()
	{
		this.randomGaussian();
		Ip.normMax(this.re);
	}
	
	public void randomProbability()
	{
		this.random();
		Ip.normSum(this.re);
	}
	
	public void randomUniform(double arg0, double arg1)
	{
		double range = arg1 - arg0;
		this.im = null;
		for(int i = 0; i < this.size(); i++)
			this.re[i] = arg0 + range * Tensor.random.nextDouble();
	}
	
	public void randomUnit()
	{
		this.randomGaussian();
		Ip.normHyp(this.re);
	}
	
	public int rank()
	{
		if(this.rows() == 1 && this.cols() == 1)
			return 0;
		if(this.rows() == 1 || this.cols() == 1)
			return 1;
		return 2;
	}
	
	public double rConvolve(Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		if(!this.isVec())
			throw new IllegalArgumentException();
		int length = this.size();
		int last = length - 1;
		double convolution = 0.0;
		for(int i = 0; i < length; i++)
			convolution += this.re[i] * that.re[last-i];
		return convolution;
	}
		
	public double[] re()
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		return this.re;
	}
	
	public void re(double[] args)
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		if(args.length != this.size())
			throw new IllegalArgumentException();
		
		this.re = args;
	}
	
	public double rGet(int i)
	{
		return this.re[i];
	}
	
	public double rGet(int i, int j)
	{
    	return this.rGet(this.index(i, j));
	}

	public Tensor row(int index, boolean trans)
	{
		Tensor row;
		if(trans)
			row = new Tensor(this.cols(), 1);
		else
			row = new Tensor(1, this.cols());
			
		for(int i = 0; i < row.size(); i++)
			row.rSet(i, this.rGet(index, i));
		if(this.isReal())
			return row;
		for(int i = 0; i < row.size(); i++)
			row.iSet(i, this.iGet(index, i));
		return row;
	}
	
	public void rowEchelonForm() throws IllegalArgumentException
	{
		if(!this.isReal())
			throw new IllegalArgumentException("This only handles real matrices.");
		
		final int n = this.cols();
		
		int pi = 0;
		int pj = 0;
		while(pi <= this.rows() && pj < this.cols())
		{
			double abs;
			double maxAbs = 0.0;
			int maxIdx = -1;
			
			for(int i = pi; i < this.rows(); i++)
				if(maxAbs < (abs = Math.abs(this.re[i*n+pj])))
				{
					maxAbs = abs;
					maxIdx = i;
				}
			
			if(maxAbs == 0.0)
				pj += 1; 
			else
			{
				if(maxIdx != pi)
					this.rowSwap(pi, maxIdx);
				final int pr = pi * n;
				for(int j = pj; j < this.cols(); j++)
					this.re[pr+j] /= this.re[pr+pj];
				for(int r = pr+n; r < this.size(); r += n)
					for(int j = pj; j < this.cols(); j++)
						this.re[r+j] -= this.re[r+pj] * this.re[pr+j];
				pi += 1;
				pj += 1;
			}
		}
	}
	
	public Tensor rowSum()
	{
		Tensor thus = Tensor.col(this.rows());
		for(int i = 0; i < this.size(); i++)
			thus.re[rowIdx(i)] += this.re[i];
		if(this.isComplex())
			for(int i = 0; i < this.size(); i++)
				thus.im[rowIdx(i)] += this.im[i];
		return thus;
	}
		
	public void rSet(int i, double arg)
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		this.re[i] = arg;
	}
	
	public void sqrt(int i)
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		this.re[i] = Math.sqrt(this.re[i]);
	}
	
	public void rSet(int i, int j, double arg)
	{
		this.rSet(this.index(i, j), arg);
	}
	
	public void sqrt(int i, int j)
	{
		this.sqrt(this.index(i, j));
	}
	
	public void rSetAll(double arg)
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		for(int i = 0; i < this.size(); i++)
			this.re[i] = arg;
	}
	
	public void rowSwap(int i, int j, int offset)
	{
		Ip.rowSwap(this.cols(), this.re(), i, j, offset);
		if(this.isReal())
			return;
		Ip.rowSwap(this.cols(), this.im(), i, j, offset);
	}
	
	public void rSetCol(int index, double[] that)
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		if(that.length != this.rows())
			throw new IllegalArgumentException();
		for(int i = 0; i < that.length; i++)
			this.re[index(i, index)] = that[i];
	}
	
	public void rSetRow(int index, double[] that)
	{
		if(this.locked)
			throw new IllegalStateException("Tensor is locked.");
		
		if(that.length != this.cols())
			throw new IllegalArgumentException();
		int r = index * this.cols();
		for(int i = 0; i < that.length; i++)
			this.re[r + i] = that[i];
	}
	
	public void set(double[] re, double[] im)
	{
		this.re(re);
		this.im(im);
	}
	
	public void set(Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		System.arraycopy(that.re, 0, this.re, 0, this.size());
		if(that.isReal())
			this.im = null;
		else
		{
			this.imm();
			System.arraycopy(that.im, 0, this.im, 0, this.size());
		}
	}
	
	@Override
	public void set1()
	{
		if(!this.isSqr())
			throw new IllegalStateException();
		this.zero();
		int d = this.cols() + 1;
		for(int i = 0; i < this.size(); i += d)
			this.rSet(i, 1.0);
	}
	
	public void setCol(int j, Tensor col)
	{
		final int n = col.size();
		if(!col.isVec())
			throw new IllegalArgumentException();
		if(n != this.rows())
			throw new IllegalArgumentException();
		for(int i = 0; i < n; i++)
			this.rSet(i, j, col.rGet(i));
		if(col.isComplex())
		{
			for(int i = 0; i < n; i++)
				this.iSet(i, j, col.iGet(i));
			return;
		}
		if(this.isComplex()) // and not row
		{
			for(int i = 0; i < n; i++)
				this.iSet(i, j, 0.0);
			return;
		}
	}
	
	public void setDiag(Tensor that) throws IllegalArgumentException
	{
		if(Math.min(this.rows(), this.cols()) != that.size())
			throw new IllegalArgumentException();
		for(int i = 0; i < that.size(); i++)
			this.rSet(i, i, that.rGet(i));
		if(that.isReal())
			return;
		this.imm();
		for(int i = 0; i < that.size(); i++)
			this.iSet(i, i, that.iGet(i));
	}
	
	public void setRow(int i, Tensor row)
	{
		final int n = row.size();
		if(!row.isVec())
			throw new IllegalArgumentException();
		if(n != this.cols())
			throw new IllegalArgumentException();
		for(int j = 0; j < n; j++)
			this.rSet(i, j, row.rGet(j));
		if(row.isComplex())
		{
			for(int j = 0; j < n; j++)
				this.iSet(i, j, row.iGet(j));
			return;
		}
		if(this.isComplex()) // and not row
		{
			for(int j = 0; j < n; j++)
				this.iSet(i, j, 0.0);
			return;
		}
	}
	
	public Rect cTr()
	{
		return new Rect(this.rTr(), this.iTr());
	}
	
	public void softMax()
	{
		if(this.isComplex())
			throw new IllegalArgumentException();
		Ip.softMax(this.re);
	}
	
	@Override
	public void sqr()
	{
		this.set(TensorTools.mult(this, this));
	}
	
	public Tensor str()
	{
		return new Tensor(this.rows(), this.cols(), this.isComplex());
	}
	
	public void sub(Tensor that)
	{
		if(!this.sameDims(that))
			throw new IllegalArgumentException();
		Ip.sub(this.re(), that.re());
		if(that.isReal())
			return;
		this.imm();
		Ip.sub(this.im(), that.im());
	}
	
	public double sumOfBlocks()
	{
		return
			this.xx() * this.xy() * this.yx() * this.yy() +
			this.xy() * this.xz() * this.yy() * this.yz() +
			this.xz() * this.xx() * this.yz() * this.yx() +
			this.yx() * this.yy() * this.zx() * this.zy() +
			this.yy() * this.yz() * this.zy() * this.zz() +
			this.yz() * this.yx() * this.zz() * this.zx() +
			this.zx() * this.zy() * this.xx() * this.xy() +
			this.zy() * this.zz() * this.xy() * this.xz() +
			this.zz() * this.zx() * this.xz() * this.xx();
	}
	
	public double sumOfPow(double exp)
	{
		double sum = 0.0;
		for(double val : this.re)
			sum += Math.pow(val,exp);
		return sum;
	}
	
	public double sumOfSquaks()
	{
		double xxz = this.xx() * this.xx(); double xyz = this.xy() * this.xy(); double xzz = this.xz() * this.xz();
		double yxz = this.yx() * this.yx(); double yyz = this.yy() * this.yy(); double yzz = this.yz() * this.yz();
		double zxz = this.zx() * this.zx(); double zyz = this.zy() * this.zy(); double zzz = this.zz() * this.zz();
		return 
			xxz * (yxz + xyz - yyz - yzz) + xyz * (yyz + xzz - yzz - yxz) + xzz * (yzz + xxz - yxz - yyz) +
			yxz * (zxz + yyz - zyz - zzz) + yyz * (zyz + yzz - zzz - zxz) + yzz * (zzz + yxz - zxz - zyz) +
			zxz * (xxz + zyz - xyz - xzz) + zyz * (xyz + zzz - xzz - xxz) + zzz * (xzz + zxz - xxz - xyz);
	}
	
	public void colSwap(int a, int b)
	{
		Ip.colSwap(this.cols(), this.re(), a, b);
		if(this.isReal())
			return;
		Ip.colSwap(this.cols(), this.im(), a, b);
	}
	
	public void addRows(int a, double fact, int b, int offset)
	{
		int ra = a * this.cols();
		int rb = b * this.cols();
		for(int j = offset; j < this.cols(); j++)
			this.re[rb+j] += fact * this.re[ra+j];
		if(this.isReal())
			return;
		for(int j = offset; j < this.cols(); j++)
			this.im[rb+j] += fact * this.im[ra+j];
	}
	
	public void multRow(int row, double fact)
	{
		int index0 = this.index(row, 0);
		for(int col = 0; col < this.cols(); col++)
			this.re[index0+col] *= fact;
		if(this.isReal())
			return;
		for(int col = 0; col < this.cols(); col++)
			this.im[index0+col] *= fact;
	}
	
	public void multCol(int col, double fact)
	{
		for(int row = 0; row < this.rows(); row++)
			this.re[this.index(row, col)] *= fact;
		if(this.isReal())
			return;
		for(int row = 0; row < this.rows(); row++)
			this.im[this.index(row, col)] *= fact;
	}
	
	public void divRow(int a, double fact)
	{
		int ra = a * this.cols();
		for(int j = 0; j < this.cols(); j++)
			this.re[ra+j] /= fact;
		if(this.isReal())
			return;
		for(int j = 0; j < this.cols(); j++)
			this.im[ra+j] /= fact;
	}	
	
	public double taylor(double x)
	{
		if(!this.isVec())
			throw new IllegalArgumentException();
		return Meth.poly(x, this.re);
	}
	
	public Rect polyApply(Rect arg)
	{
		if(!this.isVec())
			throw new IllegalArgumentException();
		Tensor power = Tensor.power(this.rows(), this.cols(), arg, arg);
		power.compMult(this);
		return new Rect(Stat.sum(power.re()), Stat.sum(power.im()));
	}
	
	public Rect polyApply(Rect argRow, Rect argCol)
	{
		if(!this.isVec())
			throw new IllegalArgumentException();
		Tensor power = Tensor.power(this.rows(), this.cols(), argRow, argCol);
		power.compMult(this);
		return new Rect(Stat.sum(power.re()), Stat.sum(power.im()));
	}
	
	public Tensor tran()
	{
		Tensor thus = new Tensor(this.cols(), this.rows(), this.isComplex());
		for(int i = 0; i < this.rows(); i++)
			for(int j = 0; j < this.cols(); j++)
				thus.rSet(j, i, this.rGet(i, j));
		if(this.isComplex())
			for(int i = 0; i < this.rows(); i++)
				for(int j = 0; j < this.cols(); j++)
					thus.iSet(j, i, this.iGet(i, j));
		return thus;
	}
	
	public boolean uppTri()
	{
		if(!this.isSqr())
			return false;
		for(int i = 0; i < this.rows(); i++)
			for(int j = 0; j < i; j++)
				if(this.rGet(i,j) != 0.0)
					return false;
		if(this.isComplex())
			for(int i = 0; i < this.rows(); i++)
				for(int j = 0; j < i; j++)
					if(this.iGet(i,j) != 0.0)
						return false;	
		return true;
	}
	
	public double x()
	{
		return this.rGet(0);
	}
	
	public void x(double v)
	{
		this.rSet(0, v);
	}
	
	public double xx()
	{
		return this.rGet(0, 0);
	}
	
	public void xx(double val)
	{
		this.rSet(0, 0, val);
	}
	
	public double xy()
	{
		return this.rGet(0, 1);
	}

	public void xy(double val)
	{
		this.re[1] = val;
	}

	public double xz()
	{
		return this.re[2];
	}

	public void xz(double val)
	{
		this.re[2] = val;
	}
	
	public double y()
	{
		return this.re[1];
	}
	
	public void y(double v)
	{
		this.re[1] = v;
	}

	public double yx()
	{
		return this.re[3];
	}
	
	public void yx(double val)
	{
		this.re[3] = val;
	}

	public double yy()
	{
		return this.re[4];
	}

	public void yy(double val)
	{
		this.re[4] = val;
	}

	public double yz()
	{
		return this.re[5];
	}
	
	public void yz(double val)
	{
		this.re[5] = val;
	}
	
	public double z()
	{
		return this.re[2];
	}
	
	public void z(double v)
	{
		this.re[2] = v;
	}
	
	public void zero()
	{
		Ip.zero(this.re);
		if(this.isComplex())
			Ip.zero(this.im);
	}
	
	public double zx()
	{
		return this.re[6];
	}
	
	public void zx(double val)
	{
		this.re[6] = val;
	}
	
	public double zy()
	{
		return this.re[7];
	}
	
	public void zy(double val)
	{
		this.re[7] = val;
	}
	
	public double zz()
	{
		return this.re[8];
	}
	
	public void zz(double val)
	{
		this.re[8] = val;
	}

	@Override
	public Iterator<Entry> iterator() 
	{
		final Tensor src = this;
		
		return new Iterator<Tensor.Entry>()
		{
			int i = 0;
			
			@Override
			public boolean hasNext() 
			{
				return this.i < size();
			}

			@Override
			public Entry next() 
			{
				final int index = this.i;
				this.i += 1;
				return new Tensor.Entry(index / rows(), index % cols(), src);
			}

			@Override
			public void remove() 
			{
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	public static Tensor separable(Tensor gx, Tensor gy)
	{
		return TensorTools.mult(gy, gx);
	}

	@Override
	public Tensor make(int rows, int cols) 
	{
		return new Tensor(rows, cols);
	}

	@Override
	public void iAdd(int i, int j, double val) 
	{
		this.iAdd(this.index(i, j), val);
	}
	
	public void iAdd(int i, double arg)
	{
		this.im[i] += arg;
	}
	
	public void zeroZero()
	{
		final int mr = this.rows/2;
		final int mc = this.cols/2;
		if(this.isComplex())
			this.iSet(mr, mc, 0.0);
		this.rSet(mr, mc, 0.0);
	}

	@Override
	public Tensor get() 
	{
		return new Tensor(this.rows, this.cols);
	}
}