package model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import util.utility;


public abstract class MyContext implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String contextType;
	private double weight;
	private final int tolerance_measurable_value=20;
	private Map<String,Double> attributesWeight= new HashMap<String, Double>();

	//Classe astratta che ingloba i comportamenti comuni ai diversi tipi di contesto
	public MyContext()
	{
		contextType=getClass().getSimpleName();
	}
	
	public String getContextType() {
		return contextType;
	}		

	@SuppressWarnings("rawtypes")
	public boolean equals(MyContext mine) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, NoSuchMethodException
	{
		//recupero il tipo di MyContext corrente e quello da verificare
		Class This = this.getClass();
		Class that = mine.getClass();
		
		//verifico che siano dello stesso tipo
		if(!(This.getSimpleName().equals(that.getSimpleName()))) return false;
		
		//recupero i campi del MyContext corrente
		for(Field f : This.getFields())
		{
			Field current= This.getField(f.getName());
			Field toExamine= that.getField(f.getName());
			//recupero il metodo equals dello specifico tipo di dato del Field corrente
			Method equals= current.get(this).getClass().getMethod("equals", new Class[]{new Object().getClass()});
			boolean b= false;
			try 
			{
				//invoco equals sui due campi delle istanze di interesse
				 b= (boolean) equals.invoke(current.get(this),toExamine.get(mine));
			} 
			catch (InvocationTargetException e) 
			{
				e.printStackTrace();
			}
			//se i campi non erano uguali ritorno false
			if(!(b))return false;	
		}//se nessun campo ritorna false, ritorno true
		return true;
	}
	

	//peso dell'attributo in questione
	public Map<String, Double> getAttributesWeight() {
		return attributesWeight;
	}
	
	public double getAttributesWeight(String attributeName) {
		if(! getAttributesWeight().containsKey(attributeName)) return 1;
		return getAttributesWeight().get(attributeName);
	}
	

	public void setMapElement(String f,double d)
	{
		System.out.println("********************** "+f+" "+ d);
		getAttributesWeight().put(f, d);
	}
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@SuppressWarnings("rawtypes")
	//Calcola la differenza in termini numerici tra due tipi di contesto.
	//si usa un algoritmo che effettua un calcolo pesato in base all'importanza
	//di un certo attributo
	public double getDistance(MyContext fromUser) throws Exception {
		
		//recupero il tipo di MyContext corrente e quello da verificare
		Class This = this.getClass();
		Class that = fromUser.getClass();	
		double distance=0;
		//recupero i campi del MyContext corrente
		if(This.getDeclaredFields()!= null)
		{		
			for(Field f : This.getDeclaredFields())
			{
				double distanceTemp=0;
				//se il campo corrente non è un attributo irrilevante al calcolo continuo
				if(!f.getName().equals("serialVersionUID") && !f.getName().equals("Weight"))
				{	
					//ottengo il campo corrente
					Field current= This.getDeclaredField(f.getName());
					Field toExamine= that.getDeclaredField(f.getName());
					
					//ottengo i metodi getter per estrarne il valore
					//non potendo accedervi direttamente poichè sono campi privati
					Method getPolicy=utility.takeGetter(this, f.getName());
					Method getFromUser=utility.takeGetter(fromUser, f.getName());
					
					//comportamento differenziato in caso di attributi di tipi double
					if(current.getType()== double.class)
					{
						//stampe di debug
						System.out.println("double: peso di "+ f.getName()+": "+getAttributesWeight(f.getName()));
						System.out.println("valore campi "+getPolicy.invoke(this, null)+" "+getFromUser.invoke(fromUser, null) );
						
						//algoritmo di Barbara Giardina per ottenere la distanza tra i campi
						distanceTemp=Math.abs((((double)getPolicy.invoke(this, null))-
						((double)getFromUser.invoke(fromUser, null))
						/(((double)getPolicy.invoke(this,null)))
						/(100*tolerance_measurable_value)));
						
						//se è maggiore di 1(massima distanza tollerata)l' Actual Context risulta violante
						distanceTemp= Math.min(distance, 1);
						
						//ritorna la distanza moltiplicata per il peso dell'attributo del contesto
						System.out.println("dist parziale "+ distanceTemp);
						distance=Math.max((distance*(getAttributesWeight(f.getName()))),distance);
					}			
					else
					{
						//recupero il metodo equals dello specifico tipo (Sarà string) di dato del Field corrente
						Method equals= String.class.getMethod("equalsIgnoreCase",String.class);
						try 
						{
							System.out.println("peso di "+ f.getName()+": "+getAttributesWeight(f.getName()));
							if(getPolicy.invoke(this, null)!= null && getFromUser.invoke(fromUser, null)!= null)
							{
								System.out.println("valore campi "+getPolicy.invoke(this, null)+" "+getFromUser.invoke(fromUser, null) );
								//ottengo valori delle stringhe correnti
								
								String pol=(String) getPolicy.invoke(this, null);
								String usr= (String) getFromUser.invoke(fromUser, null);
								System.out.println(" lunghezza stringhe 2 "+ pol.length() + " " + usr.length());
								
								//invoco il metodo equals ottenuto tramite introspezione
								if(!(boolean) equals.invoke(pol.trim(),usr.trim())) 
							    distance=1*getAttributesWeight(f.getName());	 
							}
							else if(getPolicy.invoke(this, null)== null)//attributo non presente nella polica
							{
								//non faccio niente
								System.out.println("debug1");
							}
							//stringhe diverse oppure una delle due null
							else  
							{
								System.out.println("debug2");
								distance= (1*(getAttributesWeight(f.getName())));
							}
						} 
						catch (InvocationTargetException e) 
						{
							e.printStackTrace();
						}
					}
				}
				System.out.println("distanza alla fine del campo "+ f.getName()+" "+distance);
			}
			return distance;
		}
		//qualcosa va storto quindi per precauzione mandiamo fuori uso il sistema
		throw new Exception("non sono riuscito a confrontare i contesti");
	}
}
