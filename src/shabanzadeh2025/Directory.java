package shabanzadeh2025;

import java.io.File;

/**
 * Directory References.
 * 
 * Change location of main folder to run code in local  
 * 
 * @author Dene Ringuette
 */

public class Directory 
{
	/**
	 * File should point to main folder containing method subfolders.
	 */
	
	private static final File MAIN = new File("C:\\Users\\Dene\\Dropbox\\Shabanzadeh2025");
	
	public static final File BWC = new File(MAIN, "Brain Water Content");
	public static final File CTRD = new File(MAIN, "Confocal TR-Dextran");
	public static final File EBOD = new File(MAIN, "Evans Blue Optical Density");
	public static final File ZYMO = new File(MAIN, "Gel Zymography");
	public static final File HUMAN = new File(MAIN, "Human RGMa");
	public static final File IHC = new File(MAIN, "Immunohistochemistry");
	public static final File LDF = new File(MAIN, "Laser Doppler Flowmetry");
	public static final File LSM = new File(MAIN, "Light Sheet Microscopy");
	public static final File MRI = new File(MAIN, "Magnetic Resonance Imaging");
	public static final File BEHAVIOR = new File(MAIN, "Mice Behavior");
	public static final File PHYSIO = new File(MAIN, "Mice Physiology Assessment");
	public static final File ELISA = new File(MAIN, "Mice RGMa Concentrations");
	public static final File SCRNASEQ = new File(MAIN, "Single-cell RNA Sequencing");
	public static final File PFCONC = new File(MAIN, "PF-429242 Concentrations");
	public static final File NEOAP = new File(MAIN, "Relative Neo-AP Binding");
	public static final File TTC = new File(MAIN, "TTC Staining");
	public static final File WESTERN = new File(MAIN, "Western blots");
}
