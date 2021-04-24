package com.learnkafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local")
public class AutoCreateConfig {

    @Bean
    public NewTopic LibraryEvents() {
        return TopicBuilder.name("library-events")
                .partitions(1)  //depends on the number of browkers
                .replicas(1) //depends on the number of browkers
                .build();
    }
}
