package searchengine.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchengine.Application;

public class LemmaErrorLogger {

   public LemmaErrorLogger(String message){
       Logger logger = LoggerFactory.getLogger(Application.class);
       logger.error(message);
   }

}
