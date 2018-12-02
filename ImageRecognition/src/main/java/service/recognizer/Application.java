package service.recognizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
* Main Spring Boot Application
*/
@SpringBootApplication
@ComponentScan("service.recognizer")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
