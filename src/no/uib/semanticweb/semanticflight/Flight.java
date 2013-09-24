package no.uib.semanticweb.semanticflight;



public class Flight {

	private String flight_id;

	private String scheduledTime;

	private String arrOrDep;
	
	private String airport;
	
	private String statusCode;
	private String statusTime;

	private String airline;
	
	public Flight(){
		
	}
	
	public Flight(String airline, String flightID, String arrOrDep2,
			String airport2, String statusCode2, String scheduledTime2,
			String statusTime2) {
		this.airline = airline;
		this.flight_id = flightID;
		this.arrOrDep = arrOrDep2;
		this.airport = airport2;
		this.statusCode = statusCode2;
		this.statusTime = statusTime2;
		this.scheduledTime = scheduledTime2;
	}

	public String getAge() {
		return scheduledTime;
	}

	public String getName() {
		return flight_id;
	}

	public void setName(String name) {
		this.flight_id = name;
	}


	public String getType() {
		return arrOrDep;
	}

	public void setType(String type) {
		this.arrOrDep = type;
	}	
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.airline + " ");
		sb.append(this.flight_id+ " ");
		sb.append(this.arrOrDep+ " ");
		sb.append(this.airport+ " ");
		sb.append(this.scheduledTime+ " " + "\t");
		sb.append(this.statusTime+ " ");
		sb.append(this.statusCode+ " ");
		
		return sb.toString();
	}
}