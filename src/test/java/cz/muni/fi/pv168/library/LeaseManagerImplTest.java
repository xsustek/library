package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author Robert Duriancik, 445363
 */
public class LeaseManagerImplTest {

    private LeaseManagerImpl leaseManager;
    private BookManagerImpl bookManager;
    private CustomerManagerImpl customerManager;
    private DataSource dataSource;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, LeaseManager.class.getResource("createTables.sql"));

        leaseManager = new LeaseManagerImpl();
        leaseManager.setDataSource(dataSource);
        bookManager = new BookManagerImpl();
        bookManager.setDataSource(dataSource);
        customerManager = new CustomerManagerImpl();
        customerManager.setDataSource(dataSource);

        prepareTestData();
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, LeaseManager.class.getResource("dropTables.sql"));

    }

    private void prepareTestData() {

    }

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:leasemgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }
}
