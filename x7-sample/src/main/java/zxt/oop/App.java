package zxt.oop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Sample
 *
 */
@Configuration
@SpringBootApplication
@ServletComponentScan
public class App 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class);
    	
//    	RepositoryBooter.generateId(); // init the id generator, default for redis
    }
}
