package no.uib.semanticweb.semanticflight;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Xmltest {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		String first = "http://flydata.avinor.no/XmlFeed.asp?airport=";
		String arrival = "&direction=A";
		String departure = "&direction=D";
		URL url;
		URL url2;
		String[] mid;
		String[] airports = new String[1];
		try {
			url = new URL("http://flydata.avinor.no/airportNames.asp");
			Scanner s = new Scanner(url.openStream());
			String a = "";
			while(s.hasNextLine()){
				a = a.concat(s.nextLine());
			}
			mid = a.split("airportName code=");
			airports = new String[mid.length];
			for(int i=0;i<mid.length;i++){
				airports[i] = mid[i].substring(1, 4);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=1;i<airports.length;i++){
			try {
				url2 = new URL("http://flydata.avinor.no/XmlFeed.asp?airport="+ airports[i] + "&direction=A");
				Scanner s = new Scanner(url2.openStream());
				BufferedWriter writer = new BufferedWriter(new FileWriter("xml/xmlA/" + airports[i]+ "A.xml"));
				while(s.hasNext()){
					writer.append(s.nextLine());
					writer.newLine();
					//				System.out.println(s.nextLine());
				}
				writer.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		for(int i=1;i<airports.length;i++){
			try {
				url2 = new URL("http://flydata.avinor.no/XmlFeed.asp?airport="+ airports[i] + "&direction=D");
				Scanner s = new Scanner(url2.openStream());
				BufferedWriter writer = new BufferedWriter(new FileWriter("xml/xmlD/" + airports[i]+ "D.xml"));
				while(s.hasNext()){
					writer.append(s.nextLine());
					writer.newLine();
					//				System.out.println(s.nextLine());
				}
				writer.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		long endTime = System.nanoTime();
		
		long duration = endTime - startTime;
		System.out.println(duration);
	}

}
