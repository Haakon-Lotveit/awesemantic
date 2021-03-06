/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.uib.semanticweb.semanticflight.rdfstore ;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import no.uib.semanticweb.semanticflight.Flight;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.modify.request.UpdateLoad;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

/**
 * 
 * All communication with TDB should go through this. Maybe add a interface for modularity?
 *
 */
public class TDBwrapper extends Object {

	public TDBwrapper() {

	}

	public void loadAirportsDbpedia () {

		String sparqlUpdateString = "" +
				"PREFIX dbo: <http://dbpedia.org/ontology/>" +

						"INSERT {?airports dbo:iataLocationIdentifier ?iata} WHERE {" +

						"SERVICE <http://dbpedia.org/sparql> {" +

						"?airports dbo:iataLocationIdentifier ?iata ." +												

						"}}";


		// Haakon: StoreRDF er en enkel wrapper. Akkurat nå gjør den ikke så mye annet enn å la deg skrive litegranne mindre.
		TDBconnections store = TDBconnections.create();

		Dataset dataset = store.getDataset();
		Model model = store.getModel();
		System.out.println(model.size());
		dataset.begin(ReadWrite.WRITE) ;

		
		try {
		 GraphStore graphStore = GraphStoreFactory.create(dataset) ;
		 
		UpdateRequest request = UpdateFactory.create(sparqlUpdateString) ;
	     UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore) ;
	     proc.execute() ;
		


		//		model.write(System.out, "TURTLE");
		System.out.println(model.size());
		System.out.println(dataset.getDefaultModel().size());
//		dataset = DatasetFactory.create(model);
		dataset.commit();

		} finally { 
			dataset.end() ; 
		}

		TDBconnections s = TDBconnections.create();
		Dataset set = s.getDataset();
		set.begin(ReadWrite.READ);
		System.out.println(set.getDefaultModel().size());
		set.end();

	}

	/**
	 * Purges model before writing new triples.
	 * Makes sure no bloating of TDB because of old triples
	 * All in one transaction
	 * @param flights
	 */
	public void writeFlightsToTDB(List<Flight> flights) {
		
		String propertyURI = "http://awesemantic.com/properties/";
		String resourceURI = "http://awesemantic.com/resource/";
		
		TDBconnections store = TDBconnections.create();
		Dataset dataset = store.getDataset();
		dataset.begin(ReadWrite.WRITE) ;
		Model model = store.getModel();
		// Removes all triples from model.
		model.removeAll();
		
		// Write properties
		Property hasFlightID = model.createProperty(propertyURI + "hasFlightID");
		Property scheduledArrivalTime = model.createProperty(propertyURI + "schedueledArrivalTime");
		Property scheduledDepartureTime = model.createProperty(propertyURI + "schedueledDepartureTime");
		Property airline = model.createProperty(propertyURI + "airline");
		// To or from according to XML, not originating xml. Add arrOrDepValue after airport
		Property arrivingAirport = model.createProperty(propertyURI + "arrivingAirport");
		Property departingAirport = model.createProperty(propertyURI + "departingAirport");
		// Statuscodes and times default to emptystring in flight object
		Property statusCodeA = model.createProperty(propertyURI + "statusCodeA");
		Property statusTimeA = model.createProperty(propertyURI + "statusTimeA");
		Property statusCodeD = model.createProperty(propertyURI + "statusCodeD");
		Property statusTimeD = model.createProperty(propertyURI + "statusTimeD");
		
		
		for(int i = 0; i < flights.size() ; i++) {
			Flight f = flights.get(i);						
			
			// Makes sure the airports has correct direction
			if(f.getArrOrDep().equals("A")) {
				Resource flightRes  = model.createResource(resourceURI + f.getFlight_id())
			             .addProperty(hasFlightID, f.getFlight_id())
			             .addProperty(scheduledArrivalTime, f.getScheduledTime())
			             .addProperty(airline, model.createLiteral(f.getAirline(),"en"))
			             .addProperty(departingAirport, model.createLiteral(f.getAirport(), "en"))
			             .addProperty(statusCodeA, f.getStatusCode())
			             .addProperty(statusTimeA, f.getStatusTime());
			}
			
			if(f.getArrOrDep().equals("D")) {
				Resource flightRes  = model.createResource(resourceURI + f.getFlight_id())
			             .addProperty(hasFlightID, f.getFlight_id())
			             .addProperty(scheduledDepartureTime, f.getScheduledTime())
			             .addProperty(airline, model.createLiteral(f.getAirline(), "en"))
			             .addProperty(arrivingAirport, model.createLiteral(f.getAirport(), "en"))
			             .addProperty(statusCodeD, f.getStatusCode())
			             .addProperty(statusTimeD, f.getStatusTime());
			}
				
		}
		try {
			model.write(new FileOutputStream("triples.rdf"), "RDF/XML");
		} catch (FileNotFoundException e) {
			System.out.println("model.write failed!");
			e.printStackTrace();
		}
		
		
		// Cleanup. Docs recommend to set to null.
		dataset.close();
		store = null;
		dataset = null;
		model = null;
				
	}
	
	/**
	 * Updates server through sparql-update endpoint.
	 */
	public static void updateFusekiHTTP() {
		try {
		
		UpdateRequest request = UpdateFactory.create() ;
		request.add("DROP ALL")
					.add("LOAD <file:triples.rdf>");
		
		Ini ini = null;
		try {
			ini = new Ini(new File("Settings.ini"));			
		} catch (InvalidFileFormatException e) { /* Just prints for now */
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// gets the delaytime from the ini. Else default location.
		String dataset = "";

		if(null != ini){
			dataset = ini.get("Host", "URL", String.class);
		}
		
		
		// And perform the operations.
		UpdateExecutionFactory.createRemote(request, dataset+"update").execute();

		} catch (Exception i) {
			i.printStackTrace();
			System.out.println("FusekiUpdateFail!!");
		}
	}
}
