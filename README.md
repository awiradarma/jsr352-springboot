# jsr352-springboot

An example of how to run a JSR-352 batch job using Spring Boot.  
In order to allow the job to reference to a batch job XML outside of the Spring Boot uber jar, the Maven plugin needs to be configured to use a ZIP layout.
  
Once the jar is built, you can include external jars or an external META-INF/batch-jobs folder containing custom batch XML config file using the loader path argument.  

Example:  
Given a folder structure of:  
/test  
   |  
   +-> /META-INF  
   |         |  
   |         +--> /batch-jobs  
   |                   |  
   |                   +--> alternateJob.xml  
   +--> extralib.jar  
   |  
   +--> batch-0.0.1-SNAPSHOT.jar  
  
Then you can utilize classes in the extralib.jar as well as referencing alternateJob.xml config:  
java -Dloader.path=/test -jar batch-0.0.1-SNAPSHOT.jar alternateJob  