package com.xxw.base.util;

import com.alibaba.fastjson.JSONArray;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.jdbc.PgArray;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version: V1.0
 * @author: ivan
 * @className: DatabaseUtil
 * @packageName: com.sutpc.base.util
 * @description: 数据操作工具类
 * @data: 2020-02-28 12:20
 */
@Slf4j
@UtilityClass
public class DatabaseUtil {

    private static class SingletonHolder {
        private static final DatabaseUtil INSTANCE = new DatabaseUtil();
    }

    public static DatabaseUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 执行带参数的查询方法获取List结果集合
     *
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    public static List<Map> executeSearch(Connection conn, String sql, Object[] params) {
        PreparedStatement state = null;
        ResultSet object = null;
        List<Map> result = null;
        try {
            state = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    state.setObject(i + 1, params[i]);
                }
            }
            object = state.executeQuery();
            if (object != null) {
                result = new ArrayList<>();
                ResultSetMetaData md = object.getMetaData();
                int columnCount = md.getColumnCount();
                while (object.next()) {
                    Map<String, Object> rowData = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        Object value = object.getObject(i);
                        if(value instanceof PgArray) {
                            PgArray pgArrayValue =  (PgArray)value;
                            Object array = pgArrayValue.getArray();
                            rowData.put(md.getColumnName(i), array);
                        } else {
                            rowData.put(md.getColumnName(i), value);
                        }

                    }
                    result.add(rowData);
                }
            }
        } catch (Exception e) {
            log.error("executeSearch异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(null, state, object);
        }
        return result;
    }

    /**
     * @param conn
     * @param sql
     * @param params
     * @param clazz
     * @param <T>
     * @return
     * @deprecated
     */
    public static <T> List<T> executeSearch(Connection conn, String sql, Object[] params, Class<T> clazz) {
        PreparedStatement state = null;
        ResultSet resultSet = null;
        List<T> result = null;
        try {
            state = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    state.setObject(i + 1, params[i]);
                }

            }
            resultSet = state.executeQuery();
            if (null != resultSet) {
                result = JavaBeanUtil.resultSetToList(clazz, resultSet);
            }
        } catch (Exception e) {
            log.error("executeSearch异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(null, state, resultSet);
        }
        return result;
    }

    /**
     * 执行带参数的查询方法获取总条数
     *
     * @param conn
     * @param sql
     * @param args
     * @return
     */
    public static int executeQueryByIntSql(Connection conn, String sql, Object[] args) {
        PreparedStatement state = null;
        ResultSet resultSet = null;
        int object = -1;
        try {
            state = conn.prepareStatement(sql);
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    state.setObject(i + 1, args[i]);
                }
            }
            resultSet = state.executeQuery();
            resultSet.next();
            object = resultSet.getInt(1);
        } catch (Exception e) {
            log.error("executeQueryByIntSql异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(null, state, resultSet);
        }
        return object;

    }

    /**
     * 执行待参数的增加修改方法
     *
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    public static int executeSql(Connection conn, String sql, Object[] params) {

        log.debug("sql---->{} params----{}", sql, JsonUtil.objectToJson(params));

        PreparedStatement state = null;
        int object = -1;
        try {
            state = conn.prepareStatement(sql);
            if (null != params && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    Object parameter = params[i];
                    if(parameter instanceof JSONArray) {
                        JSONArray transformParameter = (JSONArray)parameter;
                        Object[] objects = transformParameter.toArray();
                        Object obj = objects[0];
                        Array sqlArray = null;
                        if (obj instanceof Boolean) {
                            sqlArray = conn.createArrayOf("BOOLEAN", objects);
                        } else if (obj instanceof Byte) {
                            sqlArray = conn.createArrayOf("BYTE", objects);
                        } else if (obj instanceof Character) {
                            sqlArray = conn.createArrayOf("CHARACTER", objects);
                        } else if (obj instanceof Double) {
                            sqlArray = conn.createArrayOf("DOUBLE", objects);
                        } else if (obj instanceof Float) {
                            sqlArray = conn.createArrayOf("FLOAT", objects);
                        } else if (obj instanceof Integer) {
                            sqlArray = conn.createArrayOf("INTEGER", objects);
                        } else if (obj instanceof Long) {
                            sqlArray = conn.createArrayOf("LONG", objects);
                        } else if (obj instanceof Short) {
                            sqlArray = conn.createArrayOf("SHORT", objects);
                        }
                        state.setObject(i + 1, sqlArray);
                    } else {
                        state.setObject(i + 1, parameter);
                    }
                }
            }
            object = state.executeUpdate();
        } catch (SQLException e) {
            log.error("executeSql异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(state);
        }
        return object;

    }

    /**
     * 执行增删改的方法
     *
     * @param conn
     * @param sql
     * @return
     */
    public static int executeSql(Connection conn, String sql) {
        return executeSql(conn, sql, null);
    }


    /**
     * 批量执行添加、修改的方法
     *
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    public static int executeBatchSql(Connection conn, String sql, List<Object[]> params) {
        PreparedStatement state = null;
        int[] object;
        try {
            state = conn.prepareStatement(sql);
            if (params != null && params.size() != 0) {
                for (Object[] objects : params) {
                    for (int i = 0; i < objects.length; i++) {
                        state.setObject(i + 1, objects[i]);
                    }
                    state.addBatch();
                }
            }
            object = state.executeBatch();
        } catch (Exception e) {
            log.error(ExceptionUtil.buildErrorMessage(e));
            return -1;
        } finally {
            DatasourceUtil.close(state);
        }
        return object.length;

    }

    /**
     * 批量执行添加、修改的方法
     *
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    public static int executeBatchSqlByTransaction(Connection conn, String sql, List<Object[]> params) {
        PreparedStatement state;
        int[] object = {};
        try {
            // 关闭自动提交，即开启事务
            conn.setAutoCommit(false);
            state = conn.prepareStatement(sql);
            if (params != null && params.size() != 0) {
                for (Object[] objects : params) {
                    for (int i = 0; i < objects.length; i++) {
                        state.setObject(i + 1, objects[i]);
                    }
                    state.addBatch();
                }
            }
            object = state.executeBatch();
            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception e2) {
                log.error("更新失败，事务回滚错误... {}", ExceptionUtil.buildErrorMessage(e));
            }
            log.error("executeBatchSqlByTransaction异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                log.error(ExceptionUtil.buildErrorMessage(e));
            }
            DatasourceUtil.close(conn);
        }
        return object.length;

    }

    /**
     * 批量删除的方法
     *
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    public static int executeBatchDelete(Connection conn, String sql, List<?> params) {
        PreparedStatement state = null;
        int[] object;
        try {
            state = conn.prepareStatement(sql);
            if (params != null && params.size() != 0) {
                for (int i = 0; i < params.size(); i++) {
                    state.setObject(i + 1, params.get(i));
                }
                state.addBatch();
            }
            object = state.executeBatch();
        } catch (Exception e) {
            log.error("executeBatchDelete异常: {}", ExceptionUtil.buildErrorMessage(e));
            return -1;
        } finally {
            DatasourceUtil.close(state);

        }
        return null != object && object.length > 0 ? object[0] : 0;
    }

}
