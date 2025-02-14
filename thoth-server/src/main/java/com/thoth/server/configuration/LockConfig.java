package com.thoth.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class LockConfig {

    @Bean
    public DefaultLockRepository lockRepository(DataSource dataSource) {
        return new DefaultLockRepository(dataSource);
    }

    @Bean
    public JdbcLockRegistry lockRegistry(DefaultLockRepository lockRepository) {
        return new JdbcLockRegistry(lockRepository);
    }
}