package no.uib.semanticweb.semanticflight;

/**
 * Represents a flight.
 *
 */
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

	public String getFlight_id() {
		return flight_id;
	}

	public String getScheduledTime() {
		return scheduledTime;
	}

	public String getArrOrDep() {
		return arrOrDep;
	}

	public String getAirport() {
		return airport;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public String getStatusTime() {
		return statusTime;
	}

	public String getAirline() {
		return airline;
	}


}