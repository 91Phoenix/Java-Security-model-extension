package security;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.Permission;
import java.util.Date;

import persistance.ReadContext;
import persistance.ReadContextOwl;
import model.MyContext;
import model.Policies;
import model.Policy;
import model.RequestorContext;
import model.Session;

public class ContextPermission extends Permission implements Serializable {

	private static final long serialVersionUID = 1L;

	//attributo che permette al cliente di caricare le informazioni necessarie a verificare il rispetto
	//delle politiche di protezione dell'applicazione
	private Session session;
	//attributo che ingloba le politiche di protezione dell'applicazione
	private Policies policies;
	//per verificare freschezza attributi in memoria
	private File contesto_versione2;
	private String nomeFile;
	private Date ultimaModificaRilevata;


	//Costructor for user's istance
	public ContextPermission(Session s) {
		super(s.getPermissions().get(0).getName());
		this.setSession(s);
	}

	//costruttore che carica le policies dal file OWL
	public ContextPermission(String nameFile) throws ClassNotFoundException, NoSuchFieldException,
	SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, IOException
	{
		//caricando il tipo di permesso da file OWL abbiamo la possiilità di agire su più risorse
		//quindi il costruttore della superClasse perde il suo significato
		//saranno i diversi permission associati alle diverse politiche ad implementare questa funzionalità
		super("temp");
		setNomeFile(nameFile);
		//setto la data di ultima modifica del file dal quale vengono caricare le politiche
		contesto_versione2= new File(getNomeFile());
		setUltimaModificaRilevata(new Date(contesto_versione2.lastModified()));
		//carico politiche
		init(nameFile);
	}

	public Date getUltimaModificaRilevata() {
		return ultimaModificaRilevata;
	}

	public void setUltimaModificaRilevata(Date ultimaModificaRilevata) {
		this.ultimaModificaRilevata = ultimaModificaRilevata;
	}

	public String getNomeFile() {
		return nomeFile;
	}

	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}


	//initializes ContextPermission's session from file
	private void init(String nameFile) throws ClassNotFoundException, NoSuchFieldException,
	SecurityException, IllegalArgumentException, IllegalAccessException,
	IOException, InstantiationException {

		ReadContext RC=null;

		RC = new ReadContextOwl(nameFile);
		setPolicies(RC.Read());	
	}

	//checks if this ContextPermission implies the passed ContextPermission
	@Override
	public boolean implies(Permission permission) 
	{

		//controllo che il permesso passato sia del tipo corretto
		if (!(permission instanceof ContextPermission))
			return false;
		contesto_versione2= new File(getNomeFile());
		Date ultimaModificaAttuale= new Date(contesto_versione2.lastModified());
		System.out.println("attuale: "+ ultimaModificaAttuale.toString()+" ultima rilevata: "+getUltimaModificaRilevata().toString());
		if(ultimaModificaAttuale.after(getUltimaModificaRilevata()))
		{
			setUltimaModificaRilevata(ultimaModificaAttuale);
			try {
				init(getNomeFile());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//ne faccio il cast
		ContextPermission that = (ContextPermission) permission;
		//ottengo da esso l'oggetto sessione
		Session thatSession= that.getSession();
		//contatore delle iterazioni lista policies
		int count=0;
		//ciclo le politiche
		for(Policy p : policies.getPolicies())
		{
			//salto prova_distanza_Policy perchè vuota, approverebbe tutte le politiche
			if(p.getName().equalsIgnoreCase("prova_distanza_Policy")) continue;
			System.out.println("\n\nverifico politica "+ p.getName());
			//incremento contatore iterazioni
			count++;
			//definisco un double che alla fine del prossimo for equivale alla massima distanza raggiunta 
			//nei confronti tra i contesti appartenenti a una certa politica e i contesti di sessione del client
			double distanceTot=0;
			//ciclo i contesti appartenenti alla politica corrente
			for(MyContext fromPolicy : p.getContexts())
			{
				//ogni contesto della sessione del client sarà distante un certo valore 
				//(numerico) dal contesto della politica corrente.Se uguali sarà 0
				double distance=0;
				//ottengo il contesto considerato temporaneamente 
				//nella politica di protezione dall'oggetto Session
				MyContext fromUser = thatSession.getContext(fromPolicy.getContextType());
				//verifico non sia nullo
				if(fromUser != null)
				{
					//comportamento diverso in base al contesto che si ha di fronte:
					// un RequestorContext ha dei sotto-contesti gli altri contesti no.
					if(!fromPolicy.getClass().getSimpleName().equals("RequestorContext"))
					{
						try 
						{
							//getDistance di MyContext confronta il contesto sul quale
							// è chiamato con quello passato come parametro
							distance=(fromPolicy.getDistance(fromUser)*fromPolicy.getWeight());
						} 
						catch (Exception e) 
						{
							System.out.println("impossibile confrontare i contesti : \n");
							e.printStackTrace();
						}
					}
					//Requestor Context ingloba dei sottocontesti quindi l'algoritmo è differente
					else
					{
						try 
						{
							//invoco getDistance ridefinito da RequestorContext
							distance=((RequestorContext)fromPolicy).getDistance(fromUser);
						} 
						catch (Exception e)
						{
							System.out.println("impossibile confrontare i contesti : \n");
							e.printStackTrace();
						}
					}
					//se siamo all'ultima politica da controllare ed ancora implies non ha
					// restituito valori viene controllato il valore di distanceTot se 
					//maggiore di 1(per convenzione distanza massima accettabile) -->politiche non rispettate
					if((distanceTot=Math.max(distanceTot, distance))>1 && count==policies.getPolicies().size()-1)
					{
						System.out.println("implies 1");
						return false;
					}
				}
			}
			System.out.println("distanceTot = " + distanceTot);
			//ricavata la distanza complessiva tra i contesti della politica di protezione e 
			//i contesti della richiesta corrente verifichiamo che non superi la soglia di accettabilità
			if(distanceTot>p.getTollerateDistance() && count==policies.getPolicies().size()-1 )
			{
				System.out.println("implies 2");
				return false;
			}

			//una volta controllati i valori dei campi context se l'azione (sottoforma di xPermission) della politica
			// implica quella che il cliente vuole svolgere allora la politica corrente è stata rispettata
			boolean implies= false;

			if(policies.getAction().containsKey(p.getName()))
			{
				implies=policies.getAction().get(p.getName()).implies(thatSession.getPermissions().get(0));
				System.out.println(thatSession.getPermissions().get(0).toString());
				System.out.println(policies.getAction().get(p.getName()));
			}

			System.out.println("implies : "+ implies);
			// xPermission non rispettato all'ultima iterazione
			if(!implies && count==policies.getPolicies().size()-1) 
			{
				System.out.println("implies 3");
				return false;
			}
			//i dati del cliente rispettano almeno una politica quindi consento l'accesso    
			if(distanceTot<= p.getTollerateDistance() && implies)
			{
				System.out.println("politica soddisfatta : "+p.getName() + " distanza totale = " + distanceTot + " distanza tollerata "+ p.getTollerateDistance());
				return true;
			}
			//nessuna politica nega i dati di sessione del cliente quindi consento l'accesso
			if(count==policies.getPolicies().size()-1) 
			{
				System.out.println("politica soddisfatta : "+p.getName() + " distanza totale = " + distanceTot + " distanza tollerata "+ p.getTollerateDistance());
				return true;
			}	        
			else 
			{
				System.out.println("..");
				continue;
			}
		}
		System.out.println("soddisfatti");
		return true; 

	}

	@Override
	public boolean equals(Object obj) {

		return false;
	}

	@Override
	public int hashCode() {

		return 0;
	}

	@Override
	public String getActions() {

		return null;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}


	public Policies getPolicies() {
		return policies;
	}

	public void setPolicies(Policies policies) {
		this.policies = policies;
	}


	public File getContesto_versione2() {
		return contesto_versione2;
	}

	public void setContesto_versione2(File contesto_versione2) {
		this.contesto_versione2 = contesto_versione2;
	}

}
