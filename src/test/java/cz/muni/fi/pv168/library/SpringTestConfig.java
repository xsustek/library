package cz.muni.fi.pv168.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

/**
 * Created by robert on 15.4.2016.
 */
@Configuration
@EnableTransactionManagement
public class SpringTestConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(DERBY)
                .addScript("classpath:createTables.sql")
                .build();
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
