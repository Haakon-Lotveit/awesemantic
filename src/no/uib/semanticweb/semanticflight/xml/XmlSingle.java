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

public class XmlSingle {

	private static Queue<File> xmlQueue = new LinkedList<File>();
	/**
	 * @param args
	 */
	public void connections(ArrayList<ArrayList<String>> iata, int round, int depth){
		if(round == depth){

		}else{
			for(int i = 0; i< iata.get(round).size(); i++){
//				long startTime = System.nanoTime();
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

				}
				else{
					URL url;
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
						for(int j = 1; j < parts.length; j++){
							String sub = parts[j].substring(0,3);
							sublist.add(sub);
							iAta[j-1] = sub;
						}
						iata.add(sublist);
						connections(iata, round+1, depth);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

//					long endTime = System.nanoTime();

//					long duration = endTime - startTime;
					//					System.out.println(duration);
				}
			}
		}
	}
	public Queue<File> getXmlQueue() {
		return xmlQueue;
	}
}
