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

import java.io.InputStream;
import java.util.Iterator;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.VCARD;

/** Tutorial 2 resources as property values
 */
public class RDFLoader extends Object {

	public RDFLoader() {

	}

	public void loadAirportsDbpedia () {

		
//		String queryString =
//				"PREFIX dbo: <http://dbpedia.org/ontology/>" +
//
//						"SELECT DISTINCT ?airports ?iata WHERE {" +
//						
//						"?airports dbo:iataLocationIdentifier ?iata ."+
//						
//
//						"}";
		
		String queryString =
				"PREFIX dbo: <http://dbpedia.org/ontology/>" +

						"CONSTRUCT {?airports dbo:iataLocationIdentifier ?iata} WHERE {" +
						
						"?airports dbo:iataLocationIdentifier ?iata ." +
						
						

						"}";
		
		
		Dataset dataset = TDBFactory.createDataset("tdb/flightSTORE") ;
		Model model = dataset.getDefaultModel();
		System.out.println(model.size());

		Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		model = qe.execConstruct();

//		model.write(System.out, "TURTLE");
		System.out.println(model.size());
		// Output query results	
//		ResultSetFormatter.out(System.out, results, query);
		
		
//		dataset = (Dataset) results;
		
		
		dataset.close();

	}
}
