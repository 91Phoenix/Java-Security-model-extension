package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import util.MyArrayList;

public class Session implements Serializable{
	
	private static final long serialVersionUID = 1L;
	//usata in un primo momento per la definizione dell'architettura dell'applicazione
	private Map<String,MyContext> contexts= new HashMap<String,MyContext>();
	//arrayList di permission modificata per garantire consistenza del modello
	private MyArrayList permissions= new MyArrayList();
	
	//gli attributi di contesto di un utente che chiede i permessi
	//sono associati ad una sessione di lavoro.
	public Session(Map<String,MyContext> contexts)
	{
		this.contexts=contexts;
	}

	public Session() {
		
	}

	public Map<String,MyContext> getContexts() {
		return contexts;
	}

	public void setContexts(Map<String,MyContext> contexts) {
		this.contexts = contexts;
	}
	
	public void addContext(MyContext c)
	{
		getContexts().put(c.getContextType(), c);
	}
	
	public MyContext getContext(String contexType)
	{
		return getContexts().get(contexType);
	}

	public MyArrayList getPermissions() {
		return permissions;
	}

	public void setPermissions(MyArrayList permissions) {
		this.permissions = permissions;
	}
	

}
