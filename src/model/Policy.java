package model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Policy {
	
	private List<MyContext> contexts;
	private String name;
	private double tollerateDistance;
	
	//politica caratterizzata da una certo numero di contesti,
	//un nome, è un valore numerico.Quest'ultimo rappresenta la massima
	//distanza permessa, tra i contesti di tale istanza e il contesto
	//con cui accede l'utente,affinchè la politica stessa possa essere
	//considerata rispettata.
	public Policy()
	{
		contexts= new ArrayList<MyContext>();
	}

	public List<MyContext> getContexts() {
		return contexts;
	}

	public void setContexts(List<MyContext> contexts) {
		this.contexts = contexts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTollerateDistance() {
		return tollerateDistance;
	}
	
	//converte il tipo type del parametro di ingresso con lo specifico tipo di contesto considerato
	public MyContext getContextFromType(Type t)
	{
		for(MyContext c: contexts)
		{
			if(c.getClass().getTypeName().equals(t.getTypeName()))
			{
				return c;
			}
		}
		return null;
	}

	public void setTollerateDistance(double tollerateDistance) {
		this.tollerateDistance = tollerateDistance;
	}

}
