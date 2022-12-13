package org.cosmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.cosmin.scheduler",
        "org.cosmin.generator",
        "org.cosmin.producer",
        "org.cosmin.consumer"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
