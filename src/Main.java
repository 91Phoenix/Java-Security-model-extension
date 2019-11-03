
import view.MyFrame;

import java.io.IOException;


public class Main {
	
	public static void main(String[] args) throws IOException {
		
		//creo oggetto myFrame che implementa l'interfaccia grafica dell'applicazione.
		MyFrame mf= new MyFrame("Context Aware");
		//lo mostro
		mf.setVisible(true);
		
	}
}