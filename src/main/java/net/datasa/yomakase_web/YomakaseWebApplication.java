package net.datasa.yomakase_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class YomakaseWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(YomakaseWebApplication.class, args);
    }

}
