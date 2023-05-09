package it.uniroma3.siw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
public class SiwMovieProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiwMovieProjectApplication.class, args);
    }

}
