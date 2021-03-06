import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Scale_ implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about"))
		{showAbout(); return DONE;}
		return DOES_RGB+NO_CHANGES;
		// kann RGB-Bilder und veraendert das Original nicht
	}

	public void run(ImageProcessor ip) {

		String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear"};

		GenericDialog gd = new GenericDialog("scale");
		gd.addChoice("Methode",dropdownmenue,dropdownmenue[0]);
		gd.addNumericField("Hoehe:",500,0);
		gd.addNumericField("Breite:",400,0);

		gd.showDialog();
		
		int height_n = (int)gd.getNextNumber(); // _n fuer das neue skalierte Bild
		int width_n =  (int)gd.getNextNumber();
		
		int width  = ip.getWidth();  // Breite bestimmen
		int height = ip.getHeight(); // Hoehe bestimmen

		//height_n = height;
		//width_n  = width;
		
		ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
		                   width_n, height_n, 1, NewImage.FILL_BLACK);
		
		ImageProcessor ip_n = neu.getProcessor();

		
		int[] pix = (int[])ip.getPixels();
		int[] pix_n = (int[])ip_n.getPixels();

		// Schleife ueber das neue Bild
		for (int y_n=0; y_n<height_n; y_n++) {
			for (int x_n=0; x_n<width_n; x_n++) {
				int y = y_n;
				int x = x_n;
				
				if (y < height && x < width) {
					int pos_n = y_n*width_n + x_n;
					int pos  =  y  *width   + x;
				
					pix_n[pos_n] = pix[pos];
				}
			}
		}


		// neues Bild anzeigen
		neu.show();
		neu.updateAndDraw();
	}

	void showAbout() {
		IJ.showMessage("");
	}
}

