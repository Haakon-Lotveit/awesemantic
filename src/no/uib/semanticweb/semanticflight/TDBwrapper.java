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

package no.uib.semanticweb.semanticflight ;

import java.util.List;

import no.uib.semanticweb.semanticflight.rdfstore.StoreRDF;

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
 * All communication with TDB should go true this. Maybe add a interface for modularity?
 *
 */
public class TDBwrapper extends Object {

	public TDBwrapper() {

	}

	public void loadAirportsDbpedia () {

		//		String queryString =
		//				"PREFIX dbo: <http://dbpedia.org/ontology/>" +
		//
		//						"CONSTRUCT {?airports dbo:iataLocationIdentifier ?iata} WHERE {" +
		//
		//						"?airports dbo:iataLocationIdentifier ?iata ." +												
		//
		//						"}";
		String sparqlUpdateString = "" +
				"PREFIX dbo: <http://dbpedia.org/ontology/>" +

						"INSERT {?airports dbo:iataLocationIdentifier ?iata} WHERE {" +

						"SERVICE <http://dbpedia.org/sparql> {" +

						"?airports dbo:iataLocationIdentifier ?iata ." +												

						"}}";


		// Haakon: StoreRDF er en enkel wrapper. Akkurat nå gjør den ikke så mye annet enn å la deg skrive litegranne mindre.
		StoreRDF store = StoreRDF.create();

		Dataset dataset = store.getDataset();
		Model model = store.getModel();
		System.out.println(model.size());
		dataset.begin(ReadWrite.WRITE) ;
//		Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
//		QueryExecution qe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
//		model = qe.execConstruct();
		
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
//		dataset.end() ; 
		
//			dataset.commit() ;
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

//		QueryExecution queryExecution = QueryExecutionFactory.create(query2, dataset);
//
//		ResultSet res = queryExecution.execSelect();
//
//		while (res.hasNext()){
//			System.out.println(res.next().toString());
//		}

//		dataset.begin(ReadWrite.WRITE) ;


//		model.close();
//		dataset.close();
//
		StoreRDF s = StoreRDF.create();
		Dataset set = s.getDataset();
		set.begin(ReadWrite.READ);
		System.out.println(set.getDefaultModel().size());
		set.end();
//
//		queryExecution = QueryExecutionFactory.create(query2, set);
//
//		res = queryExecution.execSelect();
//
//		while (res.hasNext()){
//			System.out.println(res.next().toString());
//		}
//		dataset.close();
	}

	public void writeFlightsToTDB(List<Flight> flights) {
		
		String propertyURI = "http://awesemantic.com/properties/";
		String resourceURI = "http://awesemantic.com/resource/";
		
		StoreRDF store = StoreRDF.create();
		Dataset dataset = store.getDataset();
		dataset.begin(ReadWrite.WRITE) ;
		Model model = store.getModel();
		System.out.println(model.size());
		
		// Write properties
		Property hasFlightID = model.createProperty(propertyURI + "hasFlightID");
		Property scheduledTime = model.createProperty(propertyURI + "schedueledTime"); 
		Property arrivingORDeparting = model.createProperty(propertyURI + "arrivingOrDeparting");
		Property airline = model.createProperty(propertyURI + "airline");
		// To or from according to XML, not originating xml. Add arrOrDepValue after airport
		Property arrivingAirport = model.createProperty(propertyURI + "arrivingAirport");
		Property departingAirport = model.createProperty(propertyURI + "departingAirport");
		// Create anonymous node if exists
		Property statusCode = model.createProperty(propertyURI + "statusCode");
		Property statusTime = model.createProperty(propertyURI + "statusTime");
		
		
		for(int i = 0; i < flights.size() ; i++) {
			Flight f = flights.get(i);						
			
			// Makes sure the airports has correct direction
			if(f.getArrOrDep().equals("A")) {
				Resource flightRes  = model.createResource(resourceURI + f.getFlight_id())
			             .addProperty(hasFlightID, f.getFlight_id())
			             .addProperty(scheduledTime, f.getScheduledTime())
			             .addProperty(arrivingORDeparting, f.getArrOrDep())
			             .addProperty(airline, f.getAirline())
			             .addProperty(arrivingAirport, f.getAirport())
			             .addProperty(VCARD.N, 
			            		 model.createResource()
			            		 .addProperty(statusCode, f.getStatusCode())
			            		 .addLiteral(statusTime, f.getStatusTime()));
			}
			
			if(f.getArrOrDep().equals("D")) {
				Resource flightRes  = model.createResource(resourceURI + f.getFlight_id())
			             .addProperty(hasFlightID, f.getFlight_id())
			             .addProperty(scheduledTime, f.getScheduledTime())
			             .addProperty(arrivingORDeparting, f.getArrOrDep())
			             .addProperty(airline, f.getAirline())
			             .addProperty(departingAirport, f.getAirport())
			             .addProperty(VCARD.N, 
			            		 model.createResource()
			            		 .addProperty(statusCode, f.getStatusCode())
			            		 .addLiteral(statusTime, f.getStatusTime()));
			}
				
		}
		
		dataset.commit();
		dataset.close();
		store = null;
		dataset = null;
		model = null;
				
	}
}
