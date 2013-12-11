This is a very simple command-line program that looks up airplanes from a given airport.
Simply call the program like this:
"flyfra [IATA code]" and it will list flights from the RDFS database.
If you don't know the airport's IATA code, use the flyplass, use flyplasser like this to find it:
"flyplasser | grep [flyplassnavn]"
Please note that flyplasser visits dbpedia for information, and can therefore be rather slow.

The point of these small scripts is to demonstrate how to use the service in an external program.
