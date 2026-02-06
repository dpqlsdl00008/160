package database;

import java.sql.SQLException;
import java.sql.Connection;

import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.ObjectPool;

public class PoolableConnectionFactoryAE extends PoolableConnectionFactory {

    private final int validationTimeout;

    /**
     * Create a new <tt>PoolableConnectionFactoryAE</tt>.
     *
     * @param connFactory the {@link ConnectionFactory} from which to obtain
     * base {@link Connection}s
     * @param pool the {@link ObjectPool} in which to pool those
     * {@link Connection}s
     * @param stmtPoolFactory the {@link KeyedObjectPoolFactory} to use to
     * create {@link KeyedObjectPool}s for pooling
     * {@link java.sql.PreparedStatement}s, or <tt>null</tt> to disable
     * {@link java.sql.PreparedStatement} pooling
     * @param validationTimeout a timeout value in seconds used to {@link #validateObject
     *            validate} {@link Connection}s. Value of <tt>0</tt> means no timeout.
     * Using <tt>-1</tt> turns off validation.
     * @param defaultReadOnly the default "read only" setting for borrowed
     * {@link Connection}s
     * @param defaultAutoCommit the default "auto commit" setting for returned
     * {@link Connection}s
     */
    public PoolableConnectionFactoryAE(ConnectionFactory connFactory, ObjectPool pool, KeyedObjectPoolFactory stmtPoolFactory, int validationTimeout, boolean defaultReadOnly, boolean defaultAutoCommit) {
        super(connFactory, pool, stmtPoolFactory, null, defaultReadOnly, defaultAutoCommit);
        this.validationTimeout = validationTimeout;
    }

    @Override
    public void validateConnection(Connection conn) throws SQLException {
        if (conn.isClosed()) {
            throw new SQLException("validateConnection: connection closed");
        }
        if (validationTimeout >= 0 && !conn.isValid(validationTimeout)) {
            throw new SQLException("validateConnection: connection invalid");
        }
    }
}
