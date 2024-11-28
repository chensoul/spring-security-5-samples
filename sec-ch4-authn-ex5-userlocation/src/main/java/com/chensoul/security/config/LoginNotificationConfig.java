package com.chensoul.security.config;

import com.maxmind.geoip2.DatabaseReader;
import java.io.File;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Configuration
public class LoginNotificationConfig {

    @Bean(name = "GeoIPCity")
    public DatabaseReader databaseReader() throws IOException {
        File database = ResourceUtils
                .getFile("classpath:maxmind/GeoLite2-City.mmdb");
        return new DatabaseReader.Builder(database)
                .build();
    }
}