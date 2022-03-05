# TripFareProcessor
Trip Fare Processor

# Run Program
* Build the program
* Grab the jar from the target folder and run using the below command.
* Please provide the absolute paths of the Tap Information CSV, Price Information CSV and the Output file.
* Assumption - All Files including output file has to exist and paths has to be valid. Not doing too much validation here.
* java -jar TripFareProcessor-0.1.jar <tap-info.csv> <route-prices.csv> <trips-customer.csv>
* Ex: java -jar TripFareProcessor-0.1.jar /Users/kkasiraju/dev/MyMusings/TripFareProcessor/src/main/resources/input/tap-info.csv /Users/kkasiraju/dev/MyMusings/TripFareProcessor/src/main/resources/input/route-prices.csv /Users/kkasiraju/dev/MyMusings/TripFareProcessor/src/main/resources/output/trips-customer.csv
* I have followed two ways to run the program, you can also run the program by configuring the file names in the application.properties
# Technologies used
* Java 8
* OpenCSV
* Spring Boot

# Design
* Please refer the TripFare.pdf in the resources/design folder for a high level architecture.
 

# Clarifications & Assumptions
* Can a bus break down and customer catching a different bus and continuing the journey- Assuming all are given in the
 same file.
* I am assuming there are two input files - one which contains the cost information and other which contains the 
  information about the trips.
* For a successful Trip to be constructed io am considering busId,companyId,pan has to match with TAP ON and TAP OFF in 
  the order, then the tap info records will be merged to a Trip.

# Assumptions on the input file
* Input is formatted and validated.
* The file submitted covers the entire use cases for the customer.
* is the input file sorted based on date? - Assuming No. So had to sort the incoming file based on date.
* Is there a gaurantee that customer start journey and end journey bound to happen on the same incoming file - Assuming
  yes. If this condition changes we need to come up with a common datastore like database to store it and process the use
  case.
* There is no cases of bus hoping. For example customer A tapped on bus1 and then hopped to bus2, without tapping off 
  bus1, then hopped back to Bus1 and tapped off.
* Assume customer travelled from stopId 1,2,3,4,5 where 1 is the start and 5 is the end, if customer starts from 
  1 (TAP ON) and goes to 5 and travels back in the same bus and reaches 1 again (TAP OFF). In this scenario customer 
  touches TAP ON and TAP OFF at the same location. As given in the input his fair is zero, though he travelled the whole
  trip. We assume these scenarios are handled appropriately and input is free of such kind of issues. It can be easily 
  handled by forcing to TAP OFF at the end of journey or "No fare" concepts is changed to based on time too. This is
  handled by adding some addition logic, before judging the trip as cancelled.
* Input in the current CSV files has spaces after commas which is generally not the standard for CSV, to avoid any 
  issues we had to override some classes in OpenCSV to ignore the trailing spaces. Please refer to CustomMappingStrategy
  and TrimStringBeforeRead  classes.

# Validations
* There are lot of validations that can be performed on the system including on values in the CSV and empty and NULL
  values, In order to keep the exercise scope minimal and as provided in the problem statement "input file is
  well formed and is not missing data.", we are cutting down on the many possible cases of validation errors.
* There are RowValidator and LineValidator which can help us validate if the input line is empty.

# File Size and performance
* OPEN CSV provides a way to handle large file sizes by reading line by line instead of loading whole file in to memory
  please check below for more information.
* Time vs. memory: The classic trade-off. If memory is not a problem, read using CsvToBean.parse() or CsvToBean.stream()
 ,which will read all beans at once and are multi-threaded. If your memory is limited, use CsvToBean.iterator() and 
 iterate over the input. Only one bean is read at a time, making multi-threading impossible and slowing down reading, 
 but only one object is in memory at a time (assuming you process and release the object for the garbage collector 
 immediately).
* Current implementation reads it line by line to avoid OutOfMemoryError when file size is too large.

# Scalability
* This is a spring boot application, we can easily scale this application by running the same application on multiple 
  nodes.
* We can even think about writing the entire thing as lambda and scale horizantally.
* If we can come up with a rest api which takes the input file as stream, we can convert this 
  to rest api and process the incoming file. But we need proper UI for this to upload the file on another hand we can 
consider this as service which takes incoming events, each event consisting a input file.
 
# Design Principles
* I am following KISS principle and keeping it simple. At present we are reading/writing to a file, in case input/output
 ways changes, we can code to interface and write corresponding classes which override them and this can be controlled 
 using configuration. You can use something like reader/writer interfaces which can do this
* 

# Further Enhancements
* Print customer journies for a month and date
* Introduce the concepts of Pass and the fares will change accordingly