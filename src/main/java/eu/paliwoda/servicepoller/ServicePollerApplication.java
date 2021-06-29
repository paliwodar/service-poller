package eu.paliwoda.servicepoller;

import eu.paliwoda.servicepoller.domain.Pollee;
import eu.paliwoda.servicepoller.repository.PolleeRepository;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class ServicePollerApplication {

    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));

        return initializer;
    }

    public static void main(String[] args) {
        SpringApplication.run(ServicePollerApplication.class, args);
    }

    @Bean
    @Profile("!integration_test")
    public CommandLineRunner demo(PolleeRepository repository) {
        return (args) -> {
            Thread.sleep(3000);
            repository.saveAll(Arrays.asList(Pollee.builder().serviceUrl("httpbin.org/status/200").name("s1").build(),
                                             Pollee.builder().serviceUrl("httpbin.org/status/404").name("s2").build(),
                                             Pollee.builder().serviceUrl("httpbin.org/delay/5").name("s3").build()))
                      .blockLast();
        };
    }

}
