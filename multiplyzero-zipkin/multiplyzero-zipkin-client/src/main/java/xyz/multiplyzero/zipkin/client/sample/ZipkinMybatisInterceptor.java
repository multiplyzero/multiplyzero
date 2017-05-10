package xyz.multiplyzero.zipkin.client.sample;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import lombok.Setter;
import xyz.multiplyzero.util.CommonUtils;
import xyz.multiplyzero.zipkin.client.TraceKeys;
import xyz.multiplyzero.zipkin.client.ZeroZipkin;
import zipkin.Endpoint;

@Intercepts(value = {
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class }) })
public class ZipkinMybatisInterceptor implements Interceptor {

    @Setter
    private ZeroZipkin zeroZipkin;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object result = null;
        if (target instanceof Executor) {
            Executor executor = (Executor) target;
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Configuration configuration = ms.getConfiguration();
            DynamicContext context = new DynamicContext(configuration, args[1]);
            String originSql = context.getSql();
            // BoundSql boundSql = ms.getBoundSql(parameterObject);
            // boundSql.getSql();

            Connection connection = executor.getTransaction().getConnection();
            Endpoint endpoint = this.createEndpoint(connection);
            zeroZipkin.startSpan("query");
            zeroZipkin.sendAnnotation(TraceKeys.CLIENT_SEND, endpoint);
            zeroZipkin.sendBinaryAnnotation(TraceKeys.SQL_QUERY, originSql, endpoint);

            long start = System.currentTimeMillis();
            try {
                result = invocation.proceed();
            } catch (Exception e) {
                zeroZipkin.sendAnnotation(TraceKeys.CLIENT_RECV, endpoint);
                zeroZipkin.sendBinaryAnnotation("query" + TraceKeys.SUFFIX_ERROR, CommonUtils.errorToString(e),
                        endpoint);
                zeroZipkin.finishSpan();
                throw e;
            }

            long end = System.currentTimeMillis();

            zeroZipkin.sendAnnotation(TraceKeys.CLIENT_RECV, endpoint);
            zeroZipkin.sendBinaryAnnotation("query.back", end - start + "", endpoint);
            zeroZipkin.finishSpan();
        }
        return result;
    }

    private static Pattern PATTERN = Pattern.compile(".?+(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d{1,5})\\.*");

    private Endpoint createEndpoint(Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseType = metaData.getDatabaseProductName();
            String url = metaData.getURL();
            Matcher m = PATTERN.matcher(url);
            String host = "";
            String port = "";
            if (m.find()) {
                host = m.group(1);
                port = m.group(2);
            }
            return Endpoint.create(databaseType, CommonUtils.ipToInt(host), Integer.parseInt(port));
        } catch (SQLException e) {
            e.printStackTrace();
            return Endpoint.create("mysql", CommonUtils.ipToInt("127.0.0.1"), 3306);
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
