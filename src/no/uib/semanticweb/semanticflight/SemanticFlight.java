package no.uib.semanticweb.semanticflight;

import java.util.ArrayList;

public class SemanticFlight {
	
	public static void main(String[] args){
		//create an instance
		SemanticXMLParser dpe = new SemanticXMLParser();
		XmlSingle single = new XmlSingle();
		ArrayList<ArrayList<String>> li = new ArrayList<ArrayList<String>>();
		ArrayList<String> fly = new ArrayList<String>();
		fly.add("BGO");
		li.add(fly);
		//call run example
		dpe.runExample("OSL.xml");
		single.connections(li, 0, 5);
		
		RDFLoader rdfLoader = new RDFLoader();
		rdfLoader.loadAirportsDbpedia();
	}

}
