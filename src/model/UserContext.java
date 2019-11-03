package model;

public class UserContext extends MyContext {

	private static final long serialVersionUID = 1L;
	private String medicalKnowledge;
	private String relativesOf;
	private String work;
	private double age=0;
	private String isDoctorFor;
	
	//modellazione di uno dei tipi di contesto definiti nelle politiche
	//di esempio  prodotte da Barbara Giardina attraverso OWL.
	public UserContext()
	{
		super();
	}

	public String getMedicalKnowledge() {
		return medicalKnowledge;
	}

	public void setMedicalKnowledge(String medicalKnowledge) {
		this.medicalKnowledge = medicalKnowledge;
	}

	public String getRelativesOf() {
		return relativesOf;
	}

	public void setRelativesOf(String relativesOf) {
		this.relativesOf = relativesOf;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public double getAge() {
		return age;
	}

	public void setAge(double age) {
		this.age = age;
	}

	public String getIsDoctorFor() {
		return isDoctorFor;
	}

	public void setIsDoctorFor(String isDoctorFor) {
		this.isDoctorFor = isDoctorFor;
	}
	

}
