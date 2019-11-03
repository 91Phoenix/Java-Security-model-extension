package controller;

import java.io.FilePermission;
import java.security.Policy;

import model.ActualContext;

public class MyController extends AbstractController {


	@Override
	public void setFileName(String fileName) {
	
		ActualContext a=ActualContext.getInstance();
		a.setFilePermission(new FilePermission(fileName, "read"));
		
	}

}
