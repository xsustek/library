package cz.muni.fi.pv168.common;

import cz.muni.fi.pv168.library.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created by robert on 15.4.2016.
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:myconf.properties")
public class SpringConfig {

    @Autowired
    Environment env;

    @Bean
    public DataSource dataSource() {
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName(env.getProperty("jdbc.driver"));
        bds.setUrl(env.getProperty("jdbc.url"));
        bds.setUsername(env.getProperty("jdbc.user"));
        bds.setPassword(env.getProperty("jdbc.password"));
        return bds;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public CustomerManager customerManager() {
        CustomerManagerImpl customerManager = new CustomerManagerImpl();
        customerManager.setSources(dataSource());
        return customerManager;
    }

    @Bean
    public BookManager bookManager() {
        BookManagerImpl bookManager = new BookManagerImpl();
        bookManager.setSources(dataSource());
        return bookManager;
    }

    @Bean
    public LeaseManager leaseManager() {
        LeaseManagerImpl leaseManager = new LeaseManagerImpl();
        leaseManager.setBookManager(bookManager());
        leaseManager.setCustomerManager(customerManager());
        leaseManager.setSources(dataSource());
        return leaseManager;
    }
}
