package no.uib.semanticweb.semanticflight.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * 
 * @author Erik
 *
 */
public class XmlSingle {

	private static Queue<File> xmlQueue = new LinkedList<File>();

	// Rekursivt brukt metode for å laste ned xml-filer fra Avinor.
	/**
	 * 
	 * @param iata Listen over iatakoder som man skal finne koblinger til og laste ned.
	 * @param round Hvor dypt man er kommet i rekursjonen
	 * @param depth Ønsket rekursjonsdybde.
	 */
	public void connections(ArrayList<ArrayList<String>> iata, int round, int depth){
		if(round == depth){
			// Ingenting blir gjort om man har nådd ønsket rekursjonsdybde.
		}else{
			//Hvis en xml fil av den ønskede iatakoden ikke allerede eksisterer generes denne.
			for(int i = 0; i< iata.get(round).size(); i++){
				String airport = iata.get(round).get(i); 
				File f = new File("xml/xmlA/" + iata.get(round).get(i)+ "A.xml");
				File f2 = new File("xml/xmlD/" + iata.get(round).get(i)+ "D.xml");
				// Adding the files to list used to keep track of downloaded xmls
				if(!xmlQueue.contains(f)) {
					xmlQueue.add(f);
				}
				if(!xmlQueue.contains(f2)){
					xmlQueue.add(f2);
				}
				if(f.isFile()|f2.isFile()){
					// Hvis filen allerede eksisterer gjøres ingenting.
				}
				else{
					URL url;
					//Om filen ikke finnes kopieres den fra avinor.
					//Arrivals
					try {
						url = new URL("http://flydata.avinor.no/XmlFeed.asp?airport="+ airport + "&direction=A");
						Scanner s = new Scanner(url.openStream());
						File outfile = new File("xml/xmlA/" + airport+ "A.xml");
						if(!outfile.exists()){
							if(!outfile.createNewFile()){
							System.err.printf("Filen \"%s\" Kunne ikke lages.%nTODO: Fiks slik at den blir laget automatisk om den ikke finnes.%n",
											  outfile.getAbsolutePath());
							System.exit(1);
							}
						}
						BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
						while(s.hasNext()){
							writer.append(s.nextLine());
							writer.newLine();
						}
						s.close();
						writer.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						//Departures
						url = new URL("http://flydata.avinor.no/XmlFeed.asp?airport="+ airport + "&direction=D");
						Scanner s = new Scanner(url.openStream());
						BufferedWriter writer = new BufferedWriter(new FileWriter("xml/xmlD/" + airport+ "D.xml"));
						while(s.hasNext()){
							writer.append(s.nextLine());
							writer.newLine();
						}
						s.close();
						writer.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					

					BufferedReader reader;
					try {
						//går igjennom de nye xml-filene for å finne ut hvilke andre iata koder som er tilknyttet.
						reader = new BufferedReader(new FileReader("xml/xmlD/" + airport+ "D.xml"));
						String xml = "";
						while(reader.readLine() != null){
							xml = xml + reader.readLine();
						}
						reader.close();
						reader = new BufferedReader(new FileReader("xml/xmlA/" + airport+ "A.xml"));
						while(reader.readLine() != null){
							xml = xml + reader.readLine();
						}
						reader.close();
						String[]parts = xml.split("<airport>|<via_airport>|,");
						String[]iAta = new String[parts.length-1];
						ArrayList<String> sublist = new ArrayList<String>();
						//lager en ny liste med iata koder for neste kall av metoden.
						for(int j = 1; j < parts.length; j++){
							String sub = parts[j].substring(0,3);
							sublist.add(sub);
							iAta[j-1] = sub;
						}
						iata.add(sublist);
						//nytt rekursivt kall som øker rundetallet.
						connections(iata, round+1, depth);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	public Queue<File> getXmlQueue() {
		return xmlQueue;
	}
}
