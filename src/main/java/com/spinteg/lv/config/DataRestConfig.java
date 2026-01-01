package com.spinteg.lv.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "spring.data.rest", name = "enabled", havingValue = "false")
@Import(RepositoryRestMvcAutoConfiguration.class)
public class DataRestConfig {
    // This class disables Spring Data REST auto-configuration
}
