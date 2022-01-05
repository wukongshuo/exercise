package com.xxw.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Slf4j
public class DatasourceUtil {

    private DataSource dataSource;

    public DatasourceUtil() {
        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getResourceAsStream("/datasource.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            log.error(ExceptionUtil.buildErrorMessage(e));
        }
        PoolConfiguration poolConfiguration = DataSourceFactory.parsePoolProperties(properties);
        dataSource = new DataSource(poolConfiguration);
    }

    private static class SingletonHolder {
        private static final DatasourceUtil INSTANCE = new DatasourceUtil();
    }

    public static final DatasourceUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected void finalize() throws Throwable {
        dataSource.close();
        super.finalize();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close(Connection conn, Statement st, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            log.error("关闭结果集失败:--> {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (Exception e) {
                log.error("关闭Statement失败:-->{}", ExceptionUtil.buildErrorMessage(e));
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (Exception e) {
                    log.error("关闭连接集失败:-->{}", ExceptionUtil.buildErrorMessage(e));
                }
            }
        }
    }

    public static void close(Connection conn) {
        close(conn, null, null);
    }

    public static void close(Statement st) {
        close(null, st, null);
    }

    public static void close(ResultSet rs) {
        close(null, null, rs);
    }
}
