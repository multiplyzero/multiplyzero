package xyz.multiplyzero.zipkin.client.sample;

import java.net.InetAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptorV2;

import xyz.multiplyzero.util.CommonUtils;
import xyz.multiplyzero.zipkin.client.TraceKeys;
import xyz.multiplyzero.zipkin.client.ZeroZipkin;
import zipkin.Endpoint;

/**
 * spring.xml
 *
 * <pre>
 * {@code
< bean class="package.ZipkinMySQLInterceptorManagementBean" destroy-method="close">
  < constructor-argvalue="#{brave.clientTracer()}"/>
< /bean>
 * }
 * </pre>
 *
 * jdbc.properties 连接字符串添加
 *
 * <pre>
 * ?statementInterceptors=package.ZipkinMySQLInterceptor
 * </pre>
 */
public class ZipkinMySQLInterceptor implements StatementInterceptorV2 {
    private final static String SERVICE_NAME_KEY = "zipkinServiceName";

    private static volatile ZeroZipkin zeroZipkin;
    private static volatile String serviceName;

    public static void setZeroZipkin(final ZeroZipkin zipkin) {
        zeroZipkin = zipkin;
    }

    public static void setServiceName(final String service) {
        serviceName = service;
    }

    @Override
    public ResultSetInternalMethods preProcess(final String sql, final Statement interceptedStatement,
            final Connection connection) throws SQLException {
        if (zeroZipkin != null) {
            final String sqlToLog;
            if (interceptedStatement instanceof PreparedStatement) {
                sqlToLog = ((PreparedStatement) interceptedStatement).getPreparedSql();
            } else {
                sqlToLog = sql;
            }

            beginTrace(sqlToLog, connection);
        }

        return null;
    }

    @Override
    public ResultSetInternalMethods postProcess(final String sql, final Statement interceptedStatement,
            final ResultSetInternalMethods originalResultSet, final Connection connection, final int warningCount,
            final boolean noIndexUsed, final boolean noGoodIndexUsed, final SQLException statementException)
            throws SQLException {
        if (zeroZipkin == null) {
            return null;
        }
        Endpoint endpoint = this.createEndpoint(connection);
        zeroZipkin.sendAnnotation(TraceKeys.CLIENT_RECV, endpoint);
        try {
            if (warningCount > 0) {
                zeroZipkin.sendBinaryAnnotation("warning.count", warningCount + "", endpoint);
            }
            if (statementException != null) {
                zeroZipkin.sendBinaryAnnotation("error.code", statementException.getErrorCode() + "", endpoint);
            }
        } finally {
            zeroZipkin.finishSpan();
        }
        return null;
    }

    private void beginTrace(final String sql, final Connection connection) throws SQLException {
        Endpoint endpoint = this.createEndpoint(connection);
        zeroZipkin.startSpan("query");
        zeroZipkin.sendAnnotation(TraceKeys.CLIENT_SEND, endpoint);
        zeroZipkin.sendBinaryAnnotation(TraceKeys.SQL_QUERY, sql, endpoint);
    }

    private Endpoint createEndpoint(Connection connection) {
        try {
            InetAddress address = InetAddress.getByName(connection.getHost());
            int ipv4 = ByteBuffer.wrap(address.getAddress()).getInt();
            URI url = URI.create(connection.getMetaData().getURL().substring(5));
            int port = url.getPort() <= 0 ? 3306 : url.getPort();
            if (serviceName == null || "".equals(serviceName.trim())) {
                Properties props = connection.getProperties();
                serviceName = props.getProperty(SERVICE_NAME_KEY);
                if (serviceName == null || "".equals(serviceName.trim())) {
                    serviceName = "mysql";
                    String databaseName = connection.getCatalog();
                    if (databaseName != null && !"".equals(databaseName)) {
                        serviceName += "-" + databaseName;
                    }
                }
            }
            return Endpoint.create(serviceName, ipv4, port);
        } catch (Exception e) {
            e.printStackTrace();
            return Endpoint.create("mysql", CommonUtils.ipToInt("127.0.0.1"), 3306);
        }
    }

    @Override
    public boolean executeTopLevelOnly() {
        return true;
    }

    @Override
    public void init(final Connection connection, final Properties properties) throws SQLException {
    }

    @Override
    public void destroy() {
    }
}
