package controller;

import java.security.AccessController;
import java.security.Permission;

public abstract class AbstractController implements Controller {
	
	@Override
	//azione comune a tutti i possibili Controller futuri
	public void checkPermission(Permission c) {
		AccessController.checkPermission(c);	    
		System.out.println("requisiti di sessione soddisfatti");

	}

}
