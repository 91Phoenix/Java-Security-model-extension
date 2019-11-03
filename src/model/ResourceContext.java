package model;

public class ResourceContext extends MyContext {
	

	private static final long serialVersionUID = 1L;
	
	private String refers_to;
	private String has_owner;
	private String hasDoctor;
	public String firstName;
	private String lastName;
	private String has_security;
	private String has_Integrity;
	private String has_Confidentiality;
	private String has_Availability;
	private String has_Authenticity;
	private String is_type_of;
	private double Weight=0;
	private double importance=0;
	
	//modellazione di uno dei tipi di contesto definiti nelle politiche
	//di esempio  prodotte da Barbara Giardina attraverso OWL.
	public ResourceContext(String refers_to, String has_owner,String hasDoctor, String firstName, String lastName,
			String has_security, String is_type_of) 
	{
		super();
		this.refers_to = refers_to;
		this.has_owner = has_owner;
		this.hasDoctor = hasDoctor;
		this.firstName = firstName;
		this.lastName = lastName;
		this.has_security = has_security;
		this.is_type_of = is_type_of;
	}
	
	public ResourceContext()
	{
		super();
	}
	
	public double getImportance() {
		return importance;
	}

	public void setImportance(double importance) {
		this.importance = importance;
	}

	
	public String getHas_owner() {
		return has_owner;
	}

	public void setHas_owner(String has_owner) {
		this.has_owner = has_owner;
	}

	public String getHasDoctor() {
		return hasDoctor;
	}

	public void setHasDoctor(String hasDoctor) {
		this.hasDoctor = hasDoctor;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getHas_security() {
		return has_security;
	}

	public void setHas_security(String has_security) {
		this.has_security = has_security;
	}

	public String getHas_Integrity() {
		return has_Integrity;
	}

	public void setHas_Integrity(String has_Integrity) {
		this.has_Integrity = has_Integrity;
	}

	public String getHas_Confidentiality() {
		return has_Confidentiality;
	}

	public void setHas_Confidentiality(String has_Confidentiality) {
		this.has_Confidentiality = has_Confidentiality;
	}

	public String getHas_Availability() {
		return has_Availability;
	}

	public void setHas_Availability(String has_Availability) {
		this.has_Availability = has_Availability;
	}

	public String getHas_Authenticity() {
		return has_Authenticity;
	}

	public void setHas_Authenticity(String has_Authenticity) {
		this.has_Authenticity = has_Authenticity;
	}

	public String getIs_type_of() {
		return is_type_of;
	}

	public void setIs_type_of(String is_type_of) {
		this.is_type_of = is_type_of;
	}

	
	public String getRefers_to() {
		return refers_to;
	}
	public void setRefers_to(String refers_to) {
		this.refers_to = refers_to;
	}

	public double getWeight() {
		return Weight;
	}

	public void setWeight(double weight) {
		Weight = weight;
	}
	@Override
	public String toString() {
		return "ResourceContext [refers_to=" + refers_to + ", has_owner="
				+ has_owner + ", hasDoctor=" + hasDoctor + ", firstName="
				+ firstName + ", lastName=" + lastName + ", has_security="
				+ has_security + ", has_Integrity=" + has_Integrity
				+ ", has_Confidentiality=" + has_Confidentiality
				+ ", has_Availability=" + has_Availability
				+ ", has_Authenticity=" + has_Authenticity + ", is_type_of="
				+ is_type_of + ", Weight=" + Weight + ", importance="
				+ importance + "]";
	}
}
