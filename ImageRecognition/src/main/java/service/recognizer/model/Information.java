package service.recognizer.model;

public class Information {

	private String label;
	private double latitude;
	private double longitude;
	private String description;
	private String extract;
	
	public Information() {
	}
	
	public Information(String label, double latitude, double longitude, String description, String extract) {
		this.label = label;
		this.latitude = latitude;
		this.longitude = longitude;
		this.description = description;
		this.extract = extract;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setExtract(String extract) {
		this.extract = extract;
	}
	
	public String getExtract() {
		return extract;
	}
}
