package no.uib.semanticweb.semanticflight;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SemanticXMLParser {

	//No generics
	List<Flight> flights;
	Document dom;


	public SemanticXMLParser(){
		//create a list to hold the employee objects
		flights = new ArrayList<Flight>();
	}

	public void runExample(String fileURI) {

		//parse the xml file and get the dom object
		parseXmlFile(fileURI);

		//get each employee element and create a Employee object
		parseDocument();

		//Iterate through the list and print the data
		printData();

	}


	private void parseXmlFile(String fileURI){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(fileURI);


		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}


	private void parseDocument(){
		//get the root elememt
		Element docEle = dom.getDocumentElement();

		//get a nodelist of <employee> elements
		NodeList nl = docEle.getElementsByTagName("flight");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the employee element
				Element el = (Element)nl.item(i);

				//get the Employee object
				Flight e = getFlight(el);

				//add it to list
				flights.add(e);
			}
		}
	}


	/**
	 * I take an employee element and read the values in, create
	 * an Employee object and return it
	 * @param empEl
	 * @return
	 */
	private Flight getFlight(Element empEl) {

		//for each <employee> element get text or int values of 
		//name ,id, age and name
		String airline = getTextValue(empEl,"airline");
		String flightID = getTextValue(empEl, "flight_id");
		String scheduledTime = getTextValue(empEl,"schedule_time");
		String arrOrDep = getTextValue(empEl,"arr_dep");
		String airport = getTextValue(empEl, "airport");

		String statusCode = "";
		String statusTime = "";
		//		System.out.println(getTextValue(empEl, "status"));

		NodeList nl = empEl.getElementsByTagName("status");
		//		System.out.println(nl.item(0).getAttributes());
		//		NamedNodeMap attributes = nl.item(0).getAttributes();
		//		attributes.item(0).get
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				//get the employee element
				Element el = (Element)nl.item(i);
				statusCode = el.getAttribute("code");
				statusTime = el.getAttribute("time");
			}
		}

		//Create a new Employee with the value read from the xml nodes
		Flight e = new Flight(airline, flightID, arrOrDep, airport, statusCode, scheduledTime, statusTime);				

		return e;
	}


	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content 
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is name I will return John  
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}


	/**
	 * Calls getTextValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}

	/**
	 * Iterate through the list and print the 
	 * content to console
	 */
	private void printData(){

		System.out.println("No of flights '" + flights.size() + "'.");

		Iterator it = flights.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}
}