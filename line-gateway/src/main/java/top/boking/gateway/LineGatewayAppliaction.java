package top.boking.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LineGatewayAppliaction {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(LineGatewayAppliaction.class, args);
    }

}
