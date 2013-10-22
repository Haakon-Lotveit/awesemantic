package no.uib.semanticweb.semanticflight;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import no.uib.semanticweb.semanticflight.rdfstore.TDBconnections;
import no.uib.semanticweb.semanticflight.rdfstore.TDBwrapper;
import no.uib.semanticweb.semanticflight.xml.SemanticXMLParser;
import no.uib.semanticweb.semanticflight.xml.XmlSingle;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class SemanticFlight {

	public static void main(String[] args){

		long time = System.currentTimeMillis();

		Queue<File> xmlFileQueue = pullAvinorXML();

		long timeEnd = System.currentTimeMillis() - time;		
		System.out.println("Downloading xml took: " + timeEnd/1000 + " XMLs: " + xmlFileQueue.size());
		time = System.currentTimeMillis();

		// Parse XML and then persist triples
		parseAndPersistXMLQueue(xmlFileQueue);
		timeEnd = System.currentTimeMillis() - time;
		System.out.println("Parsing took: " + timeEnd/1000);		
		time = System.currentTimeMillis();

		debugQuerys();
		System.out.println("sjekk om persistent");

		timeEnd = System.currentTimeMillis() - time;
		System.out.println("Loading model took: " + timeEnd/1000);

		//		rdfLoader.loadAirportsDbpedia();
	}

	private static void debugQuerys() {
		TDBconnections s = TDBconnections.create();
		Dataset set = s.getDataset();
		set.begin(ReadWrite.READ);
		Model mod = set.getDefaultModel();
		System.out.println(mod.size());

		Query query = QueryFactory.create(""
				+ "PREFIX avi: <http://awesemantic.com/property/>"        		        	
				+ "PREFIX avires: <http://awesemantic.com/resource/>"
				+ "SELECT ?pred ?subject WHERE {"
				+ "avires:SK263 ?pred ?subject ."
				+ "}");

		QueryExecution queryExecution = QueryExecutionFactory.create(query, set);

		ResultSet res = queryExecution.execSelect();
		System.out.println("out");
		while (res.hasNext()){
			System.out.println(res.next().toString());
		}

		set.end();

	}

	/**
	 * Takes a list of xml-files, parse them and persist the resulting triples
	 * @param xmlFileQueue
	 */
	private static void parseAndPersistXMLQueue(Queue<File> xmlFileQueue) {
		SemanticXMLParser dpe = new SemanticXMLParser();
		TDBwrapper rdfLoader = new TDBwrapper();
		try {
			while(xmlFileQueue.peek() != null) {
				File xmlFile = xmlFileQueue.poll();
				dpe.parse(xmlFile.getPath());
				List<Flight> flights = dpe.getFlights();
				// Write flight objects to tdb
				rdfLoader.writeFlightsToTDB(flights);
				if(xmlFile.delete()){
					System.out.println("deleted xmlFile");
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
			//				rdfLoader.writeFlightsToTDB(flights);
		}

	}

	/**
	 * Pulls xmls from all airports connecting to avinor airports
	 * @return Queue<File> queue
	 */
	private static Queue<File> pullAvinorXML() {
		XmlSingle single = new XmlSingle();
		ArrayList<ArrayList<String>> li = new ArrayList<ArrayList<String>>();
		ArrayList<String> fly = new ArrayList<String>();

		String[] avinorAirPorts = {"AES","KSU","NVK","FBU","OSL","BVG","BOO","MQN",
				"MJF","OSY","SDN","ALF","ANX","BJF","BDU","BNN",
				"FDE","VDB","FRO","HFT","EVE","HAU","HVG","KKN",
				"KRS","LKL","LKN","MEH","MOL","RRS","RVK","RET",
				"SOJ","SSJ","SOG","SKN","LYR","SVJ","TOS","TRD",
				"VRY","VDS","VAW","HOV","BGO","HAA","SVG"};
		List<String> aviList = Arrays.asList(avinorAirPorts);

		for(int i = 0 ; i < aviList.size() ; i++) {
			fly.add(aviList.get(i));
			li.add(fly);

			// Downloads all airports connected to airports in li.
			single.connections(li, 0, 5);
		}
		return single.getXmlQueue();
	}

}
