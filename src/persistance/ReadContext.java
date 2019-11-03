package persistance;

import model.Policies;

//interfaccia per disaccopiare i componenti che implementano
// la persistenza con il modello
public interface ReadContext {
	
	public Policies Read();

}
