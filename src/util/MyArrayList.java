package util;

import java.security.Permission;
import java.util.ArrayList;

import security.ContextPermission;

public class MyArrayList extends ArrayList<Permission> {

	private static final long serialVersionUID = 1L;
	//classe creata per garantire consistenza al modello che altrimenti permetterebbe ad un
	//oggetto session di aggiungere alla lista di permessi un ContextPermission
	public MyArrayList()
	{
		super();
	}
	
	@Override
	public boolean add(Permission p)
	{
		if(!(p instanceof ContextPermission))
		{
			return super.add(p);
		}
		else return false;
	}
	
	@Override
	public void add(int index, Permission element) {
		if(!(element instanceof ContextPermission))
		{
			 super.add(index,element);
		}
		else return;
	}
	

}
