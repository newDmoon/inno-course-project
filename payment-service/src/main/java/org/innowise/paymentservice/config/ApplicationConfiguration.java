package org.innowise.paymentservice.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Value("${spring.liquibase.change-log}")
    private String changeLog;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public ApplicationRunner liquibaseRunner() {
        return args -> {
            Database database = DatabaseFactory.getInstance()
                    .openDatabase(mongoUri, null, null, null, new ClassLoaderResourceAccessor());

            if (!(database instanceof MongoLiquibaseDatabase)) {
                throw new IllegalStateException("Database is not MongoLiquibaseDatabase");
            }

            try (Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(), database)) {
                liquibase.clearCheckSums();
                liquibase.update((String) null);
            }
        };
    }
}
