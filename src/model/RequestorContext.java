package model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import util.utility;

public class RequestorContext extends MyContext {

	private static final long serialVersionUID = 1L;
	
	private PhysicalContext physicalContext;
	private ComputingContext computingContext;
	private UserContext userContext;
	
	//modellazione di uno dei tipi di contesto definiti nelle politiche
	//di esempio  prodotte da Barbara Giardina attraverso OWL.
	//In particolare questa classe ammette sotto contesti quindi
	//è caratterizzato da una ridefinizione per motivi semantici
	//del metodo getDistance.
	public RequestorContext()
	{
		super();
	}
	
	public UserContext getUserContext() {
		return userContext;
	}

	public void setUserContext(UserContext userContext) {
		this.userContext = userContext;
	}

	
	public ComputingContext getComputingContext() {
		return computingContext;
	}

	public void setComputingContext(ComputingContext computingContext) {
		this.computingContext = computingContext;
	}

	public PhysicalContext getPhysicalContext() {
		return physicalContext;
	}

	public void setPhysicalContext(PhysicalContext physicalContext) {
		this.physicalContext = physicalContext;
	}
	
	@Override
	public double getDistance(MyContext fromUser) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
		//controllo che l'argomento sia del tipo corretto altrimenti invio valore max
		if (!(fromUser instanceof RequestorContext))
            return 1;
		
		double distanceTot=0;
		for(Field f : this.getClass().getDeclaredFields())
		{
			if(f.getName()!= "serialVersionUID")
			{
				double distance=0;
				//Field toExamine= rc.getClass().getDeclaredField(f.getName());
				double weight;
				Method getSubContexts= this.getClass().getMethod("get"+utility.firstToUpper(f.getName()),null);
				MyContext fromPolicySubC= (MyContext) getSubContexts.invoke(this, null);
				MyContext fromUserSubC= (MyContext) getSubContexts.invoke(fromUser, null);
				Method getWeight= Class.forName("model."+utility.firstToUpper(f.getName())).getMethod("getWeight", null);
				if(fromPolicySubC != null && fromUserSubC != null )
				{
					System.out.println(fromPolicySubC.getContextType()+ " "+ fromUserSubC.getContextType() );
					Method getDistance= fromPolicySubC.getClass().getMethod("getDistance",MyContext.class);
					weight=(double)getWeight.invoke(fromPolicySubC, null);
					System.out.println("req 1");
					distance =((double)getDistance.invoke(fromPolicySubC,fromUserSubC ));
				}
				//se la politica non specifica nulla su un contesto allora lascio passare
				else if(fromPolicySubC == null) 
				{
					System.out.println("req 2");
					distance= 0;
					weight=0;
				}
				else if(fromUserSubC ==null)
				{
					distance=1;
					System.out.println("req 4");
					if(fromPolicySubC != null)
						weight=(double)getWeight.invoke(fromPolicySubC, null);
				}
				//*getWeight()*((double)getWeight.invoke(fromUserSubC, null));
				System.out.println("distanza parziale "+ distance);
				if(distance> distanceTot) distanceTot=distance;
				//else if(distanceTot< (getWeight()*weight)) 
				//distanceTot= (getWeight()*weight);
			}
			System.out.println("distanza alla fine del campo "+f.getName()+" "+distanceTot);
		}
		return distanceTot;
	}

}
