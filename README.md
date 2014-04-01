##stream-consumption-workshop

An ingestion client written for the twitter API in Java.

###Features
* Logging & metrics
* Connection to a stream with reconnect logic
* Writing analysis data to Redis
* Use of properties
* Asynchronous reading off the stream, parsing JSON and handling of messages


###Required Software
* Java 7: http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html
* Maven: http://maven.apache.org/download.cgi
* Redis: Server http://redis.io/download


###Suggested Software
* Java IDE

###Setting Up Credentials for Twitter APU
* Twitter credentials are required to be setup with

###Running the code
* mvn install
* mvn java:exec