package searchengine.logger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchengine.Application;

public class ExceptionLogger {

   public ExceptionLogger(String message){
       Logger logger = LoggerFactory.getLogger(Application.class);
       logger.error(message);
   }

}
