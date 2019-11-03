package model;

public class PhysicalContext extends MyContext {

	private static final long serialVersionUID = 1L;
	
	private String place,department,road;
	private double utilizzatori=0;
	private String emergency;
	
	//modellazione di uno dei tipi di contesto definiti nelle politiche
	//di esempio  prodotte da Barbara Giardina attraverso OWL.
	public PhysicalContext(String place, String department, String road,
			double utilizzatori) {
		super();
		this.place = place;
		this.department = department;
		this.road = road;
		this.utilizzatori = utilizzatori;
	}
	public PhysicalContext()
	{
		super();
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}

	public double getUtilizzatori() {
		return utilizzatori;
	}

	public void setUtilizzatori(double utilizzatori) {
		this.utilizzatori = utilizzatori;
	}

	public String getEmergency() {
		return emergency;
	}

	public void setEmergency(String emergency) {
		this.emergency = emergency;
	}	

}
