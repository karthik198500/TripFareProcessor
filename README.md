# TripFareProcessor
Trip Fare Processor

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