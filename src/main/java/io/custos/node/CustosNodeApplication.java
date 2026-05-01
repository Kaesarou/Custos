package io.custos.node;

import io.custos.node.config.CustosProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CustosProperties.class)
public class CustosNodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustosNodeApplication.class, args);
    }
}
