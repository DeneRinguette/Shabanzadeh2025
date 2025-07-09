package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public interface TensorModel<T> extends NonComRing<T>, InnProSpc<T>, NonComGrpUndMult<T>, Dim
{
	public int rows();
	
	public int cols();
	
	public int size();
	
	public boolean isVec();
	
	public boolean isReal();
	
	public T make(int rows, int cols);
	
	public T make(PlnMesh mesh);
		
	public Rect cDot(TensorModel<?> that);
	
	public Rect cGet(int k);
	
	public int index(int i, int j);
	
	public Rect cGet(int i, int j);
	
	public void cSet(int k, Complex val);
	
	public void cSet(int i, int j, Complex val);
	
	public void cSet(int i, int j, double re, double im);
	
	public void cAdd(int k, Complex val);
	
	public void cAdd(int i, int j, Complex val);
	
	public void cMult(int i, int j, Complex arg);
	
	public void cMult(int k, Complex arg);
	
	public void cMult(int k, double re, double im);
	
	public void cMult(int i, int j, double re, double im);
	
	public void compMult(TensorModel<?> that);
	
	public void compDiv(TensorModel<?> that);
		
	public double rGet(int k);
	
	public double rGet(int i, int j);
	
	public void rSet(int k, double val);
	
	public void rSet(int i, int j, double val);
	
	public void rAdd(int k, double val);
	
	public void rAdd(int i, int j, double val);
		
	public double iGet(int k);
	
	public double iGet(int i, int j);
	
	public void iSet(int k, double val);
	
	public void iSet(int i, int j, double val);
	
	public void iAdd(int k, double val);
	
	public void iAdd(int i, int j, double val);
		
	public double mod(int k);
	
	public double mod(int i, int j);
	
	public double ang(int k);
	
	public double ang(int i, int j);
	
	public double mod2(int k);
	
	public double mod2(int i, int j);
	
	public T getRow(int i);
	
	public T getCol(int j);
	
	public boolean isSqr();
	
	public void diagSwap(int d, int s);
	
	public void rowSwap(int i, int j);
	
	public void rowSwap(int i, int j, int offset);
	
	public void colSwap(int i, int j);
	
	public boolean isComplex();
	
	public T tran();
	
	public void addRows(int i, Complex fact, int j, int offset);
	
	public void addRows(int i, Complex fact, int j);
	
	public void addRows(int i, double fact, int j, int offset);
	
	public void addRows(int i, double fact, int j);
	
	public void divRow(int i, Complex fact);
	
	public void divRow(int i, double fact);
	
	public void multRow(int i, Complex fact);
	
	public void multRow(int a, double fact);
	
	public void imm();
	
	public void fft();
	
	public void ifft();
	
	public void fft2d();
	
	public void ifft2d();
	
	public void compOper(ComplexOper func, Tensor that);
}