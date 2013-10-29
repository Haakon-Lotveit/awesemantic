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

import java.util.List;

import no.uib.semanticweb.semanticflight.Flight;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.vocabulary.VCARD;

/**
 * 
 * @author ken
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
		//		model.write(System.out, "TURTLE");
		//		ResultSetFormatter.out(System.out, results, query);

		Query query2 = QueryFactory.create( "" +
				"PREFIX dbo: <http://dbpedia.org/ontology/>" +
				"SELECT ?airports ?iata WHERE {" +
				"?airports dbo:iataLocationIdentifier ?iata ." +
				"}LIMIT 10");


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
//		Property arrivingORDeparting = model.createProperty(propertyURI + "arrivingOrDeparting");
		Property airline = model.createProperty(propertyURI + "airline");
		// To or from according to XML, not originating xml. Add arrOrDepValue after airport
		Property arrivingAirport = model.createProperty(propertyURI + "arrivingAirport");
		Property departingAirport = model.createProperty(propertyURI + "departingAirport");
		// Create anonymous node if exists
		Property statusCodeA = model.createProperty(propertyURI + "statusCodeA");
		Property statusTimeA = model.createProperty(propertyURI + "statusTimeA");
		Property statusCodeD = model.createProperty(propertyURI + "statusCodeD");
		Property statusTimeD = model.createProperty(propertyURI + "statusTimeD");
		Property statusParent = model.createProperty(propertyURI + "statusParent");
		
		
		for(int i = 0; i < flights.size() ; i++) {
			Flight f = flights.get(i);						
			
			// Makes sure the airports has correct direction
			if(f.getArrOrDep().equals("A")) {
				Resource flightRes  = model.createResource(resourceURI + f.getFlight_id())
			             .addProperty(hasFlightID, f.getFlight_id())
			             .addProperty(scheduledArrivalTime, f.getScheduledTime())
//			             .addProperty(arrivingORDeparting, f.getArrOrDep())
			             .addProperty(airline, f.getAirline())
			             .addProperty(departingAirport, f.getAirport())
//			             .addProperty(statusParent, 
//			            		 model.createResource()
			            		 .addProperty(statusCodeA, f.getStatusCode())
			            		 .addLiteral(statusTimeA, f.getStatusTime());
			}
			
			if(f.getArrOrDep().equals("D")) {
				Resource flightRes  = model.createResource(resourceURI + f.getFlight_id())
			             .addProperty(hasFlightID, f.getFlight_id())
			             .addProperty(scheduledDepartureTime, f.getScheduledTime())
//			             .addProperty(arrivingORDeparting, f.getArrOrDep())
			             .addProperty(airline, f.getAirline())
			             .addProperty(arrivingAirport, f.getAirport())
//			             .addProperty(statusParent, 
//			            		 model.createResource(resourceURI+"statusParent"+f.getFlight_id())
			            		 .addProperty(statusCodeD, f.getStatusCode())
			            		 .addLiteral(statusTimeD, f.getStatusTime());
			}
				
		}
		
		dataset.commit();
		dataset.close();
		store = null;
		dataset = null;
		model = null;
				
	}
}
