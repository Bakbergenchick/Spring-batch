package com.spring.springbatch.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.universitydatasource")
    public DataSource universityDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.postgredatasource")
    public DataSource postgreDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    public EntityManagerFactory postgreSqlEntityManagerFactory(){
        LocalContainerEntityManagerFactoryBean lem =
                new LocalContainerEntityManagerFactoryBean();
        lem.setDataSource(postgreDataSource());
        lem.setPackagesToScan("com.spring.springbatch.entity.postgre");
        lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        lem.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        lem.afterPropertiesSet();

        return lem.getObject();
    }

    @Bean
    @Primary
    public EntityManagerFactory mySqlEntityManagerFactory(){
        LocalContainerEntityManagerFactoryBean lem =
                new LocalContainerEntityManagerFactoryBean();
        lem.setDataSource(universityDataSource());
        lem.setPackagesToScan("com.spring.springbatch.entity.mysql");
        lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        lem.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        lem.afterPropertiesSet();

        return lem.getObject();
    }

    @Bean
    @Primary
    public JpaTransactionManager jpaTransactionManager(){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setDataSource(universityDataSource());
        jpaTransactionManager.setEntityManagerFactory(mySqlEntityManagerFactory());

        return jpaTransactionManager;
    }
}
