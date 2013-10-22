package no.uib.semanticweb.semanticflight.rdfstore;
import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * Dette er en enkel klasse som skal i teorien wrappe TDBFactory og slikt, slik at vi kan få mindre konfigurasjon og sånn... :D 
 * @author haakon
 *
 */
public class TDBconnections {
	// Private deler
	private Dataset ds = null;
	
	// konstruktør
	private TDBconnections(String location){
		this.ds = TDBFactory.createDataset(location);
	}
	
	// statiske metoder for å lage storeRDF objekter
	public static TDBconnections create() {
		Ini ini = null;
		try {
			ini = new Ini(new File("Settings.ini"));			
		} catch (InvalidFileFormatException e) { /* Det er ikke så veldig mye vi kan gjøre dersom .ini filen er feilformattert eller lignende, så vi bare skriver ut for nå. */
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * Ja, dette er totalt dust. Men det virker jo sånn ca.
		 * standard location er tatt fra Ken-Thomas Nilsens ærverdige streng i RDFLoader.
		 */
		String location = "tdb/flightSTORE";
		
		if(null != ini){
			location = ini.get("StoreRDF", "Location", String.class);
			System.out.printf("[DEBUG] Location: %s%n", location);
		}
		
		File loc = new File(location);
		if(!(loc.exists())){
			loc.mkdirs();
		}
		return new TDBconnections(location);
	}
	public static TDBconnections create(String location){
		return new TDBconnections(location);
	}
	
	public Model getModel(){
		return ds.getDefaultModel();
	}
	
	public Dataset getDataset(){
		return ds;
	}
}
