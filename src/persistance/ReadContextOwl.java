package persistance;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import model.ComputingContext;
import model.MyContext;
import model.PhysicalContext;
import model.Policies;
import model.Policy;
import model.RequestorContext;
import model.UserContext;

public class ReadContextOwl implements ReadContext {
	
	private static String NS="http://www.semanticweb.org/barbara/ontologies/2013/11/contesto#";
	private static OntModel m;
	private List<String> foglie= new ArrayList<String>();

	public ReadContextOwl(String nameFile) {

		//creo un modello vuoto;
		init();
		m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		try
		{
			System.out.println("------------------->lettura file");
			System.out.println("------------------->ATTENZIONE: Non inserire informazioni con \":\" o \" \" (spazi vuoti)");
			//carico politica
			InputStream in = new FileInputStream(nameFile);
			m.read(in, null);
			System.out.println("------------------->file letto con successo");
			in.close();
		}
		catch(Exception exc)
		{
			System.out.println("------------------->non riesco a leggere il file");
			exc.printStackTrace();
		}
		m.setStrictMode(false);
	}

	//legge un file OWL e a partire da esso costruisce il modello definito per attuare
	//delle politiche di protezione
	@Override
	public Policies Read() {

		//creo oggetto da restituire
		Policies policies = new Policies();

		//carico il contesto di protezione
		OntClass pc=m.createClass(NS+"Protection_Context");
		//ottengo un iteratore  sul contesto di protezione
		ResIterator pc_iter = m.listResourcesWithProperty(RDF.type, pc);
		//lo converto in lista
		List<Resource> pc_list=pc_iter.toList();
		//ottengo nodo activatedBy
		OntProperty activatedBy = m.getObjectProperty( NS + "activatedBy" );
		for (Resource res: pc_list)
		{
			//creo un oggetto politica per andare a popolare la lista di politiche dell'oggetto session
			Policy p= new Policy();

			OntClass protection_context=m.createClass(NS + res.getLocalName());
			ResIterator po_iter = m.listResourcesWithProperty(activatedBy, protection_context); 
			//ottengo policy corrente
			Resource policy=po_iter.nextResource();
			//setto nome politica
			System.out.println();
			System.out.println("-----------------------");
			System.out.println(policy.getLocalName());
			System.out.println("-----------------------");
			p.setName(policy.getLocalName());
			//ottengo nodo corrispondente alla distanza tollerata dalla policy corrente
			DatatypeProperty tolerates_distance=m.createDatatypeProperty(NS+"tolerates_Context_Distance");   	
			NodeIterator accepted_distance=m.listObjectsOfProperty(policy, tolerates_distance);
			if (accepted_distance.hasNext()) 
			{
				//se in corrispondenza di tale nodo era presente un valore viene settato all'interno della politica
				p.setTollerateDistance(accepted_distance.nextNode().asLiteral().getDouble());
			}
			else 
				p.setTollerateDistance(0); // se non è presente la distanza tollerata è 0
			//ottengo l'oggetto individual della policy corrente
			Individual individualPC=m.getIndividual(NS+ res.getLocalName());
			//ottengo i nodi relativi ai possibili contesti presenti in tale politica
			OntProperty has_Requestor_Context = m.getObjectProperty( NS + "has_Requestor_Context" );
			OntProperty has_Resource_Context = m.getObjectProperty( NS + "has_Resource_Context" );
			OntProperty has_Time_Context = m.getObjectProperty( NS + "has_Time_Context" );
			//--------------------------->popolamento Resource Context
			NodeIterator resource_Context_Ottimo=m.listObjectsOfProperty(individualPC, has_Resource_Context);
			//--------------------------->popolamento Requestor Context
			NodeIterator requestor_Context_Ottimo=m.listObjectsOfProperty(individualPC, has_Requestor_Context);
			//--------------------------->popolamento Time Context
			NodeIterator time_Context_Ottimo=m.listObjectsOfProperty(individualPC, has_Time_Context);
			//invoco getContext che mi aggiunge alla politica il tipo di contesto specificato già popolato con i suoi attributi
			try 
			{
				MyContext temp=null;
				if((temp = getContext(resource_Context_Ottimo,"ResourceContext",policies,p)) != null)
				{
					p.getContexts().add(temp);
				}
				if((temp = getContext(time_Context_Ottimo,"TimeContext",policies,p)) != null)
				{
					p.getContexts().add(temp);
				}
				if((temp = getContext(requestor_Context_Ottimo,"RequestorContext",policies,p)) != null)
				{
					p.getContexts().add(temp);
				}
			} 
			catch (Exception e) {
				System.out.println("errore nel caricamento dei contesti :");
				e.printStackTrace();
				return null;
			}
			policies.getPolicies().add(p);
		}
		return policies;
	}

	//recupera un particolare contesto passato come parametro
	@SuppressWarnings({ "rawtypes" })
	private MyContext getContext(NodeIterator node,String classe,Policies s,Policy pol) throws ClassNotFoundException, InstantiationException,
	IllegalAccessException, NoSuchFieldException, SecurityException, NoSuchMethodException,
	IllegalArgumentException, InvocationTargetException
	{
		System.out.println("popolamento di "+classe+":");
		System.out.println();
		MyContext mC= null;
		//se il contesto considerato è presente nella politica;
		if (node.hasNext())
		{
			//ottengo la classe rappresentativa il contesto corrente;
			Class currentContext = Class.forName("model."+ classe);
			//ne creo un'istanza
			mC =(MyContext) currentContext.newInstance();
			//ottengo il nodo del contesto per estrapolarne le info e caratterizzare quindi l'istanza
			RDFNode res_Cont_Ottimo=node.nextNode();
			List<Statement> oc_list = intSubContext(res_Cont_Ottimo, mC);	
			//invoco popolate policy che ottiene gli attributi di un contesto di complessità arbitraria
			//e quindi ne caratterizza la classe appena creata
			popolatePolicy(node, classe, s, mC, oc_list,pol);
		}
		return mC;
	}

	//ottiene gli attributi di un contesto e li mappa alla classe opportuna
	@SuppressWarnings("rawtypes")
	private void popolatePolicy(NodeIterator node, String classe, Policies s,MyContext mC, List<Statement> oc_list,Policy pol)
			throws ClassNotFoundException, InstantiationException,IllegalAccessException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException 
	{
		System.out.println("dimensione lista attributi "+oc_list.size());
		for (Statement a: oc_list)
		{	
			//ottengo l'oggetto Resource utile per ottenere il peso del nodo
			Resource a_res=a.getSubject();
			//ottengo property utile per ottenere alcune info come il nome dell'attributo
			Property a_prop=a.getPredicate();
			System.out.println(a_prop.getLocalName());
			//ottengo RDFNode utile per ottenere il valore dell'attributo
			RDFNode a_obj=a.getObject();
			//ottengo le propetietà diverse da Type(nella lettura del file OWL ne compaiono diversi,ma sono inutili)
			//e da weight già ricavato qualche passaggio prima per settare il peso del tipo di contesto corrente
			if(!a_prop.getLocalName().equalsIgnoreCase("type") && !a_prop.getLocalName().equalsIgnoreCase("Weight") )
			{
				//creo un oggetto subContext utile quando si ha un requestor context, il quale
				//è caratterizzato da sottocontesti
				MyContext subContext=null;
				boolean settedAction=false;
				Individual soggettoPc=null;
				//controllo per gli attributi di tipo Literal, indispensabile per non avere eccezioni
				if(!(a_obj instanceof Literal))
				{
					soggettoPc=m.getIndividual(((Resource)a_obj).toString());
				}
				//se l'attributo è di tipo literal lo stampo a console
				else System.out.println(a_obj.asLiteral().getString());
				//l'attributo control action viene tradotto in uno dei permessi dell'architettura
				//di sicurezza in java
				if(classe.equals("ResourceContext") && !settedAction && a_prop.getLocalName().equals("controls_action"))
				{
					settedAction=true; 
					s.getAction().put(pol.getName(),findPermission(soggettoPc.getLocalName()));
				}
				//control action quindi non verrà più considerato
				if( !a_prop.getLocalName().equals("controls_action"))
				{
					// 3 if per il popolamento dei sotto contesti di Requestor Context : 1° if
					if(classe.equals("RequestorContext")&& a_prop.getLocalName().equals("has_User_Context"))
					{	
						System.out.println("popolamento di UserContext :");
						subContext = extractClass("model.UserContext");
						List<Statement> subOc_list = intSubContext(a_obj,subContext);
						popolatePolicy(node, "UserContext",s,subContext,subOc_list,pol);
						if(subContext != null)
							((RequestorContext) mC).setUserContext((UserContext)subContext);
					}
					//2° if
					else if(classe.equals("RequestorContext")&& a_prop.getLocalName().equals("has_Physical_Context"))
					{
						System.out.println("popolamento di PhysicalContext :");
						subContext = extractClass("model.PhysicalContext");
						List<Statement> subOc_list = intSubContext(a_obj,subContext);
						popolatePolicy(node, "PhysicalContext",s,subContext,subOc_list,pol);
						if(subContext != null)
							((RequestorContext) mC).setPhysicalContext((PhysicalContext)subContext);
					}
					//3°if
					else if(classe.equals("RequestorContext")&& a_prop.getLocalName().equals("has_Computing_Context"))
					{
						System.out.println("popolamento di ComputingContext :");
						subContext= extractClass("model.ComputingContext");
						List<Statement> subOc_list = intSubContext(a_obj,subContext);
						popolatePolicy(node, "ComputingContext",s,subContext,subOc_list,pol);
						if(subContext != null)
							((RequestorContext) mC).setComputingContext((ComputingContext)subContext);
					}
					else 
					{
						if(a_prop.getLocalName().equals("place"))
						{
							System.out.println();
						}
						//per ottenere gli attributi di una paricolare classe tramite introspezione 
						//questi devono essere dichiarati public, poichè farlo non è considerata una buona pratica
						//adottiamo questo algoritmo alternativo che ci consente di aggiarare il problema
						//in modo elegante e senza comromettere l'estendibilità del programma a patto che
						//si mantega lo standard nella definizione dei metodi getter : getX (prima lettera maiuscola).
						//ottengo metodo get. I metodi appena descritti sono collocati nel package utility.
						Method get= takeGetter(mC,a_prop.getLocalName());
						//ottengo tipo restituito dal metodo get in modo da poter ottenere anche il metodo set
						Type t= get.getReturnType();
						Method set=takeSetter(mC, a_prop.getLocalName(),t);
						//se avessi avuto gli attributi public avrei dovuto fare semplicemente
						//f=mC.getClass().getField(a_prop.getLocalName());
						//setto peso del campo corrente
						mC.setMapElement(a_prop.getLocalName(),pesoNodo(a_res));

						//setto il valore del campo corrente nei 3 diversi casi dovuti alla
						//specifica sintassi OWL
						if(!(a_obj instanceof Literal))
						{
							System.out.println("con valore: "+soggettoPc.getLocalName());
							set.invoke(mC,soggettoPc.getLocalName());
						}
						else if(a_prop.getLocalName().equals("age") || a_prop.getLocalName().equals("utilizzatori") )
							set.invoke(mC, a_obj.asLiteral().getDouble());
						else 
						{
							System.out.println("sto settando: "+a_obj.asLiteral().getString());
							set.invoke(mC, a_obj.asLiteral().getString());
						}

						//mi fermo al popolamento dei contesti quando ho raggiunto le foglie
						if((classe.equals("ResourceContext") || classe.equals("PhysicalContext")) && !foglie.contains(a_prop.getLocalName()))		
						{
							if(!measurable(a))
							{
								//ottengo i sottonodi della politica che corrispondono a normali attributi
								//per le nostre classi MyContext
								immeasurable_values(node,classe,s,mC,a_obj,pol);
							}
						}
					}
				}
			}
		}
	}

	private MyContext extractClass(String classe) throws ClassNotFoundException,
	InstantiationException, IllegalAccessException {
		MyContext subContext;
		Class currentContext = Class.forName(classe);
		subContext=(MyContext) currentContext.newInstance();
		return subContext;
	}

	//se sono presenti sottonodi li recupera e provvede alla loro caratterizzazione
	private  void immeasurable_values(NodeIterator node, String classe, Policies s,
			MyContext mC,RDFNode a_obj,Policy pol) {
		Individual SoggettoPc=m.getIndividual(((Resource)a_obj).toString());
		StmtIterator pc_iter=SoggettoPc.listProperties();
		boolean hasAttr=false;
		while (pc_iter.hasNext() && !hasAttr) 
		{
			Statement attr_pc=pc_iter.nextStatement();
			Property prop_attr_pc=attr_pc.getPredicate();
			if (!prop_attr_pc.getLocalName().equals("type") && !prop_attr_pc.getLocalName().equals("Weight"))
			{
				hasAttr=true;
			}
		}				
		if (hasAttr)	
		{
			Individual OptimalContext=m.getIndividual(NS+SoggettoPc.asResource().asResource().getLocalName());
			StmtIterator oc_iter=OptimalContext.listProperties();
			List<Statement> oc_list= oc_iter.toList();
			try 
			{
				popolatePolicy(node,classe,s,mC,oc_list,pol);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			} 
		}
	}

	//recupera il metodo get per un particolare attributo di una particolare classe
	public Method takeGetter(MyContext mC,String localName)
	{
		try 
		{
			return mC.getClass().getMethod("get"+localName.substring(0, 1).toUpperCase()+
					localName.substring(1,localName.length()),null);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}

	}

	//recupera il metodo set per un particolare attributo di una particolare classe
	@SuppressWarnings("rawtypes")
	public Method takeSetter(MyContext mC,String localName,Type c)
	{
		try 
		{
			Class current= null;
			try
			{
				current= Class.forName(c.getTypeName());
			}
			catch(Exception e)
			{
				current=Double.TYPE;
			}

			return mC.getClass().getMethod("set"+localName.substring(0,1).toUpperCase()+
					localName.substring(1, localName.length()),current);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	//algoritmo che a partire dallo Statemente "control action" ne ricava il tipo di permesso
	//al momento è un algoritmo fittizio, con qualche modifica alle istanze delle politiche
	//può essere realizzato in modo semplice e performante. In particolare usando lo stringTokenizer possiamo
	//andare a recuperare il valore target ed il valore action per poi mapparli nell'opportuno tipo di Permission.
	private Permission findPermission(String string) {
		if(string!= null)
		{
			StringTokenizer st= new StringTokenizer(string,"_");
			//l'azione si chiama open... definire una sintassi idonea nelle politiche
			String action= st.nextToken();
			//System.out.println(action);
			//modello da definire meglio due stringhe che vengono saltate per prove
			//corrispondono a non important
			st.nextToken();
			st.nextToken();
			//in questa prova file(si potrebbe generalizzare a più tipi di permission
			//con politiche definite diversamente) String typeOfPermission=
			st.nextToken();
			//salto il nome del paziente
			String nameResource=st.nextToken();
			//System.out.println(nameResource);
			//String nameResource=st.nextToken();
			return new FilePermission(nameResource, "read");
		}
		return null;
	}

	//metodo sviluppato da Barbara Giardina
	//recupera il peso di un nodo;
	private double pesoNodo(Resource risorsa) {
		DatatypeProperty weight=m.createDatatypeProperty(NS+"Weight");

		NodeIterator weight_iter=m.listObjectsOfProperty(risorsa, weight);
		double weight_result;
		if (weight_iter.hasNext()) 
			weight_result=weight_iter.nextNode().asLiteral().getDouble();
		else 
			weight_result=1;//deve essere 1 se non c'è scritta
		return weight_result;

	}

	//metodo sviluppato da Barbara Giardina
	//verifica che uno Statement sia numerico
	public static boolean measurable(Statement a)
	{
		boolean measurable=false;
		RDFNode a_obj=a.getObject();
		if (a_obj instanceof Literal)
		{

			String[] tipo=(((Literal) a_obj).toString()).split("#");

			if (tipo.length>1) 
			{
				if (tipo[1].equalsIgnoreCase("double") || tipo[1].equalsIgnoreCase("float") ||
						tipo[1].equalsIgnoreCase("int")|| tipo[1].equalsIgnoreCase("short")||
						tipo[1].equalsIgnoreCase("short")|| tipo[1].equalsIgnoreCase("long") )
					measurable=true;
			}
			else
				measurable=false;

		}
		else 
			measurable=false;
		return measurable;
	}

	//qualche istruzione inglobata in un metodo per evitare ripetizioni
	// e rendere più chiaro il codice
	private List<Statement> intSubContext(RDFNode a_obj, MyContext subContext) {
		subContext.setWeight(pesoNodo(a_obj.asResource()));
		Individual OptimalContext=m.getIndividual(NS+a_obj.asResource().getLocalName());	
		StmtIterator oc_iter=OptimalContext.listProperties();
		List<Statement> subOc_list=oc_iter.toList();
		return subOc_list;
	}

	//popolo la lista di "foglie" (il file OWL ha una struttura ad albero) per rendere più efficente  e leggibile l'algoritmo;
	//Per ogni politica in questa mappa vanno inseriti tutti i nodi foglia.
	//Volendo inserire una politica con un ulteriore nodo finale, va agiunto qui.
	private void init() {
		foglie.add("hasDoctor");
		foglie.add("lastName");
		foglie.add("firstName");
		foglie.add("has_security");
		foglie.add("is_type_of");
		foglie.add("MedicalKnowledge");
		foglie.add("Emergency");
		foglie.add("relativesOf");
		foglie.add("Work");
		foglie.add("age");
		foglie.add("isDoctorFor");
		foglie.add("road");
		foglie.add("utilizzatori");
	}

}
