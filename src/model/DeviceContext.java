package model;


public class DeviceContext extends MyContext  {
	
	private static final long serialVersionUID = 1L;
	public String deviceType;
	
	//modellazione di uno dei tipi di contesto definiti nelle politiche
	//di esempio  prodotte da Barbara Giardina attraverso OWL.
	public DeviceContext()
	{
		deviceType="none";
	}
	public DeviceContext(String deviceType)
	{
		this.deviceType=deviceType;
	}
	
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	@Override
	public String toString() {
		return "DeviceContext [deviceType=" + deviceType + "]";
	}

}
