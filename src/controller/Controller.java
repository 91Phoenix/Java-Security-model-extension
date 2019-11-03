package controller;

import java.security.Permission;

//interfaccia per disaccoppiare controller con grafica
public interface Controller {
	
	void checkPermission(Permission c);
	
	//per settare il nome del File sul Singleton ActualContext
	void setFileName(String fileName);
}
