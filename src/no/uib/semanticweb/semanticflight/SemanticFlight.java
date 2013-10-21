package no.uib.semanticweb.semanticflight;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import no.uib.semanticweb.semanticflight.rdfstore.StoreRDF;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;

public class SemanticFlight {

	public static void main(String[] args){
		long time = System.currentTimeMillis();
		//create an instance
		XmlSingle single = new XmlSingle();
		ArrayList<ArrayList<String>> li = new ArrayList<ArrayList<String>>();
		ArrayList<String> fly = new ArrayList<String>();
		fly.add("BGO");
		li.add(fly);

		single.connections(li, 0, 5);
		Queue<File> ls = single.getXmlQueue();
		//		while(ls.peek() != null) {
		//			System.out.println(ls.poll().getPath());
		//		}
		long timeEnd = System.currentTimeMillis() - time;
		System.out.println("Downloading xml took: " + timeEnd/1000 );
		time = System.currentTimeMillis();

		//call run example
		System.out.println("semParser");
		SemanticXMLParser dpe = new SemanticXMLParser();
		RDFLoader rdfLoader = new RDFLoader();
		try {
			while(ls.peek() != null) {
				File xmlFile = ls.poll();
				dpe.runExample(xmlFile.getPath());
				List<Flight> flights = dpe.getFlights();
				// Write flight objects to tdb
				rdfLoader.writeFlightsToTDB(flights);
				if(xmlFile.delete()){
					System.out.println("deleted xmlFile");
				}
			}
			timeEnd = System.currentTimeMillis() - time;
			System.out.println("Parsing took: " + timeEnd/1000);
		}catch(Exception e) {
			e.printStackTrace();
			//				rdfLoader.writeFlightsToTDB(flights);
		}
		time = System.currentTimeMillis();

		System.out.println("sjekk om persistent");
		StoreRDF s = StoreRDF.create();
		Dataset set = s.getDataset();
		set.begin(ReadWrite.READ);
		Model mod = set.getDefaultModel();
		System.out.println(mod.size());
		//		mod.write(System.out, "TURTLE");
		set.end();

		timeEnd = System.currentTimeMillis() - time;
		System.out.println("Loading model took: " + timeEnd/1000);

		//		rdfLoader.loadAirportsDbpedia();
	}

}
