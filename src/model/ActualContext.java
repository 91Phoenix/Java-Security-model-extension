package model;

import java.io.FilePermission;
import java.util.HashMap;
import java.util.Map;

import security.ContextPermission;
import util.MyArrayList;

public class ActualContext {
	
	private static ActualContext instance;
	private Map<String,MyContext> contexts;
	private Session current;
	private ResourceContext resCon;
	private PhysicalContext phyCon;
	private RequestorContext reqCon;
	private FilePermission filePermission;
	private ContextPermission contextPermission;
	
	//singleton che mantiene le info di contesto del client in cui esegue
	//attribuzione temporanea di valori fittizzi ai vari attributi
	private ActualContext()
	{
		contexts= new HashMap<String,MyContext>();
	    current= new Session(contexts);
	    resCon= new ResourceContext("Non_Important_File_Patient_ABC123"
	        		,"Patient_ABCD123","DoctorRed","Mario","Bianchi",
	        		"Level2_also_in_the_presence_of_civilian","Data");
	    //ComputingContext comCon= new ComputingContext()
	    //versione corretta
	    //risorgimeno con lettera minuscola per provare equals ignorcase e gli spazi per provare trim
	    phyCon= new PhysicalContext("Lab1","Informatica","Viale_Risorgimento_25 ",20);
	    //versione sbgliata per provare che implies non rispetta politica 7
	    //phyCon= new PhysicalContext("Lab1","Informatica","Viale_25 ",0);
	    // TimeContext timCon= new TimeContext();
	    //UserContext useCon= new UserContext();
	    reqCon= new RequestorContext();
	    reqCon.setPhysicalContext(phyCon);
	    current.addContext(resCon);
	    current.addContext(reqCon);
	    MyArrayList mal= new MyArrayList();
	    filePermission= new FilePermission("", "read");
	    current.setPermissions(mal);
	    current.getPermissions().add(0,filePermission);
	    //debug stupido
	    //System.out.println(current.getContext(contexts.get(0).getContextType()));
		contextPermission = new ContextPermission(current);
	}
	
	public Map<String, MyContext> getContexts() {
		return contexts;
	}

	public void setContexts(Map<String, MyContext> contexts) {
		this.contexts = contexts;
	}

	public Session getCurrent() {
		return current;
	}

	public void setCurrent(Session current) {
		this.current = current;
	}

	public ResourceContext getResCon() {
		return resCon;
	}

	public void setResCon(ResourceContext resCon) {
		this.resCon = resCon;
	}

	public PhysicalContext getPhyCon() {
		return phyCon;
	}

	public void setPhyCon(PhysicalContext phyCon) {
		this.phyCon = phyCon;
	}

	public RequestorContext getReqCon() {
		return reqCon;
	}

	public void setReqCon(RequestorContext reqCon) {
		this.reqCon = reqCon;
	}

	public FilePermission getFilePermission() {
		return filePermission;
	}

	public void setFilePermission(FilePermission filePermission) {
		this.current.getPermissions().remove(0); 
		this.current.getPermissions().add(0,filePermission);
	}

	public ContextPermission getContextPermission() {
		return contextPermission;
	}

	public void setContextPermission(ContextPermission c) {
		this.contextPermission = c;
	}

	public static ActualContext getInstance()
	{
		if(instance==null)
		{
			instance = new ActualContext();
		}
		return instance;
	}

}
