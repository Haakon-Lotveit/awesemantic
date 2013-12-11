package no.uib.semanticweb.semanticflight.rdfstore;
import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * Simple class wrapping TDBFactory, such that we get less configuration
 * @author haakon
 *
 */
public class TDBconnections {

	private Dataset ds = null;


	private TDBconnections(String location){
		this.ds = TDBFactory.createDataset(location);
	}

	// static method for creating storeRDF object
	public static TDBconnections create() {
		Ini ini = null;
		try {
			ini = new Ini(new File("Settings.ini"));			
		} catch (InvalidFileFormatException e) { /* Just prints for now */
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// gets the location from the ini. Else default location.
		String location = "tdb/flightSTORE";

		if(null != ini){
			location = ini.get("StoreRDF", "Location", String.class);
		}

		// Creates folders
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
