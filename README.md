# TripFareProcessor
Trip Fare Processor

# Technologies used
* OpenCSV

# Clarifications & Assumptions
* Can a bus break down and customer catching a different bus and continuing the journey- Assuming all are given in the
 same file.
* If the customer does not Touch off by the end of midnight it is considered as Touch Off. Any new touch after this is 
considered as starting a new journey or Touch On.
* I am assuming there are two input files - one which contains the cost information and other which contains the 
information about the trips.
* 

# Assumptions on the input file
* Input is formatted and validated.
* The file submitted covers the entire use cases for the customer.
* is the input file sorted based on date? - Assuming No. So had to sort the incoming file based on date.
* Is there a gaurantee that customer start journey and end journey bound to happen on the same incoming file - Assuming
  yes. If this condition changes we need to come up with a common datastore like database to store it and process the use
  case.
* What is the size of the incoming file - Assuming this can processed in memory to keep the solution simple. If the
  file becomes too large we process each line one at a time and have to store in a datastore.
* There is no cases of bus hoping. For example customer A tapped on bus1 and then hopped to bus2, without tapping off 
  bus1, then hopped back to Bus1 and tapped off.
* Assume customer travelled from stopId 1,2,3,4,5 where 1 is the start and 5 is the end, if customer starts from 
  1 (TAP ON) and goes to 5 and travels back in the same bus and reaches 1 again (TAP OFF). In this scenario customer 
  touches TAP ON and TAP OFF at the same location. As given in the input his fair is zero, though he travelled the whole
  trip. We assume these scenarios are handled appropriately and input is free of such kind of issues. It can be easily 
  handled by forcing to TAP OFF at the end of journey or no fare concepts is changed to based on time too.

# Scalability
* This is a spring boot application, we can easily scale this application by running the same
application on multiple nodes.
* We can even think about writing the entire thing as lambda and scale horizantally.
* If we can come up with a rest api which takes the input file as stream, we can convert this 
to rest api and process the incoming file. But we need proper UI for this to upload the file on another hand we can 
consider this as service which takes incoming events, each event consisting a input file.
 

# Further Enhancements
* Print customer journies for a month and date
* Introduce the concepts of Pass and the fares will change accordingly