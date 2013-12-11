package no.uib.semanticweb.semanticflight.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import no.uib.semanticweb.semanticflight.Flight;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parse xml and create Flight objects representing them.
 * @author ken
 *
 */
public class SemanticXMLParser {

	List<Flight> flights;
	Document dom;


	public SemanticXMLParser(){
		//create a list to hold the employee objects
		flights = new ArrayList<Flight>();
	}

	public void parse(String fileURI) {

		//parse the xml file and get the dom object
		parseXmlFile(fileURI);

		//get each flight element and create a Flight object
		parseDocument();

	}


	/**
	 * Reads a file and sets the Document object.
	 * @param fileURI
	 */
	private void parseXmlFile(String fileURI){
	
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			dom = builder.parse(fileURI);

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Parse and add all <flight> to list of flights
	 */
	private void parseDocument(){
		//get the root elememt
		Element root = dom.getDocumentElement();

		//get a nodelist of <flight> elements
		NodeList nlist = root.getElementsByTagName("flight");
		if(nlist != null && nlist.getLength() > 0) {
			for(int i = 0 ; i < nlist.getLength() ; i++) {

				//get the flight element
				Element el = (Element)nlist.item(i);

				//get the Flight object
				Flight e = getFlight(el);

				//add it to list of flights
				flights.add(e);
			}
		}
	}


	/**
	 * Takes a flight element, create
	 * an Flight object and return it
	 * @param flightEl
	 * @return
	 */
	private Flight getFlight(Element flightEl) {

		//Each flights information from the xml
		String airline = getTextValue(flightEl,"airline");
		String flightID = getTextValue(flightEl, "flight_id");
		String scheduledTime = getTextValue(flightEl,"schedule_time");
		String arrOrDep = getTextValue(flightEl,"arr_dep");
		String airport = getTextValue(flightEl, "airport");

		// Defaults to emptystring if not existent
		String statusCode = "";
		String statusTime = "";

		NodeList nl = flightEl.getElementsByTagName("status");

		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				//get the statuscode and timestamp
				Element el = (Element)nl.item(i);
				statusCode = el.getAttribute("code");
				statusTime = el.getAttribute("time");
			}
		}

		//Create a new Employee with the values read from the xml nodes
		Flight e = new Flight(airline, flightID, arrOrDep, airport,
				statusCode, scheduledTime, statusTime);				

		return e;
	}


	/**
	 * Takes a element and return the value as text  
	 * @param element
	 * @param tag
	 * @return
	 */
	private String getTextValue(Element element, String tag) {
		String textVal = null;
		NodeList nl = element.getElementsByTagName(tag);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}


	/**
	 * Iterate through the list and print the 
	 * content to console
	 */
	private void printData(){

		System.out.println("No of flights '" + flights.size() + "'.");

		Iterator<Flight> it = flights.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}

	public List<Flight> getFlights() {
		return flights;
	}
}