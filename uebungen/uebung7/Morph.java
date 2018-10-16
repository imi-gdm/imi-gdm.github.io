import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Morph implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = { 
		"Weiche Blende", 
		"Rot skalieren",
		"Rot skalieren & schieben", 
		"Gruen skalieren",
		"Gruen skalieren & schieben", 
		"Rot und Gruen skalieren & schieben",
		"Morphing" };

	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB + STACK_REQUIRED;
	}

	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ();
		IJ.open("/Users/barthel/Applications/ImageJ/plugins/Mete1/RedGreen.jpg");
		ij.exitWhenQuitting(true);
		Morph md = new Morph();

		md.imp = IJ.getImage();
		ImageProcessor B_ip = md.imp.getProcessor();
		md.run(B_ip);
	}

	
	public void run(ImageProcessor B_ip) {
		ImageStack stack_B = imp.getStack();

		int length = 25; // Uebergang mit 25 Bildern
		int width = B_ip.getWidth();
		int height = B_ip.getHeight();

		IJ.open("/Users/barthel/Applications/ImageJ/plugins/Mete1/RedApple.jpg");
		ImagePlus imp2 = IJ.getImage();

		ImageStack stack_A = imp2.getStack();

		ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height,
				length, NewImage.FILL_BLACK);
		ImageStack stack_Erg = Erg.getStack();

		
		// Dialog fuer Auswahl des Ueberlagerungsmodus
		GenericDialog gd = new GenericDialog("Ueberlagerung");
		gd.addChoice("Methode", choices, "");
		gd.showDialog();

		
		int methode = 0;
		String s = gd.getNextChoice();
		if (s.equals("Weiche Blende"))
			methode = 0;
		if (s.equals("Rot skalieren"))
			methode = 1;
		if (s.equals("Rot skalieren & schieben"))
			methode = 2;

		
		// Arrays fuer die einzelnen Bilder
		int[] pixels_B;
		int[] pixels_A;
		int[] pixels_Erg;

		// Schleife ueber alle Bilder
		for (int z = 1; z <= length; z++) {
			pixels_B = (int[]) stack_B.getPixels(1);
			pixels_A = (int[]) stack_A.getPixels(1);
			pixels_Erg = (int[]) stack_Erg.getPixels(z);

			double a = (z - 1.) / (length - 1.);

			// Verschiebungsvektor
			int dx = 258;
			int dy = 69;

			// Skalierungsfaktor
			double fx = 1 / 0.76;
			double fy = 1 / 0.66;

			int pos = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++, pos++) {
					// weiche Blende
					if (methode == 0) {
						int cA = pixels_A[pos];
						int rA = (cA & 0xff0000) >> 16;
						int gA = (cA & 0x00ff00) >> 8;
						int bA = (cA & 0x0000ff);

						int cB = pixels_B[pos];
						int rB = (cB & 0xff0000) >> 16;
						int gB = (cB & 0x00ff00) >> 8;
						int bB = (cB & 0x0000ff);

						int r = (int) (a * rA + (1 - a) * rB);
						int g = (int) (a * gA + (1 - a) * gB);
						int b = (int) (a * bA + (1 - a) * bB);
						pixels_Erg[pos] = 0xFF000000 + (r << 16) + (g << 8) + b;
					}

					// rot skalieren
					if (methode == 1) {

						double sx = fx * a + (1 - a) * 1;
						double sy = fy * a + (1 - a) * 1;

						int rA = 255, gA = 255, bA = 255;

						int xa = (int) (x * sx);
						int ya = (int) (y * sy);

						if (xa >= 0 && ya >= 0 && xa < width && ya < height) {
							int posa = ya * width + xa;
							int cA = pixels_A[posa];
							rA = (cA & 0xff0000) >> 16;
							gA = (cA & 0x00ff00) >> 8;
							bA = (cA & 0x0000ff);
						}

						int cB = pixels_B[pos];
						int rB = (cB & 0xff0000) >> 16;
						int gB = (cB & 0x00ff00) >> 8;
						int bB = (cB & 0x0000ff);

						int r = (int) (0.5 * rA + 0.5 * rB);
						int g = (int) (0.5 * gA + 0.5 * gB);
						int b = (int) (0.5 * bA + 0.5 * bB);
						pixels_Erg[pos] = 0xFF000000 + (r << 16) + (g << 8) + b;
					}

					// rot skalieren und schieben
					if (methode == 2) {

						
					}
				}
			}
		}

		// neues Bild anzeigen
		Erg.show();
		Erg.updateAndDraw();

	}

}
