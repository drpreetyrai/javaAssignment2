package com.tymoshenko.controller.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Simple Java Spring configuration to be used for the Spring example application. This configuration is mainly
 * composed of a database configuration and initial population via the script "products.sql" of the database for
 * querying by our Spring service bean.
 * <p>
 * The Spring service bean and repository are scanned for via @EnableJpaRepositories and @ComponentScan annotations
 *
 * @author Yakiv Tymoshenko
 * @since 15.03.2016
 */
@Configuration
@ComponentScan("com.tymoshenko.controller")
@EnableJpaRepositories(basePackages = {"com.tymoshenko.controller.repository"})
@PropertySource(value = {"classpath:jdbc.properties"})
public class SpringConfig {

    public static final String PKG_TO_SCAN = "com.tymoshenko.model";

    public static final String INIT_DB_SQL = "init-db.sql";

    public static final String JDBC_DRIVER_CLASS_NAME = "jdbc.driverClassName";
    public static final String JDBC_URL = "jdbc.url";
    public static final String JDBC_USERNAME = "jdbc.username";
    public static final String JDBC_PASSWORD = "jdbc.password";

    public static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    public static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";

    @Autowired
    private Environment jdbcProperties;

    @Bean
    @Autowired
    @SuppressWarnings("unused")
    public DataSource dataSource(DatabasePopulator populator) {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcProperties.getProperty(JDBC_DRIVER_CLASS_NAME));
        dataSource.setUrl(jdbcProperties.getProperty(JDBC_URL));
        dataSource.setUsername(jdbcProperties.getProperty(JDBC_USERNAME));
        dataSource.setPassword(jdbcProperties.getProperty(JDBC_PASSWORD));
        DatabasePopulatorUtils.execute(populator, dataSource);
        return dataSource;
    }

    @Bean
    @Autowired
    @SuppressWarnings("unused")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(Boolean.TRUE);
        vendorAdapter.setShowSql(Boolean.TRUE);
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(PKG_TO_SCAN);
        Properties jpaProperties = new Properties();
        jpaProperties.put(HIBERNATE_HBM2DDL_AUTO, jdbcProperties.getProperty(HIBERNATE_HBM2DDL_AUTO));
        jpaProperties.put(HIBERNATE_SHOW_SQL, jdbcProperties.getProperty(HIBERNATE_SHOW_SQL));
        factory.setJpaProperties(jpaProperties);
        return factory;
    }

    @Bean
    @Autowired
    @SuppressWarnings("unused")
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Bean
    @Autowired
    @SuppressWarnings("unused")
    public DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(false);
        populator.addScript(new ClassPathResource(INIT_DB_SQL));
        return populator;
    }
}