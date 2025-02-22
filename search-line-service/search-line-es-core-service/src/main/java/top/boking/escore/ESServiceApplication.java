package top.boking.escore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ESServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ESServiceApplication.class, args);
    }

}
