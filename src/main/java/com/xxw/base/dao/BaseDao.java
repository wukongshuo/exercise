package com.xxw.base.dao;

import com.xxw.base.query.Page;
import com.xxw.base.query.PageResult;
import com.xxw.base.query.Query;
import com.alibaba.fastjson.JSON;
import com.xxw.base.util.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@UtilityClass
public class BaseDao {

    /**
     * 单表插入，只支持表名下划线并且前缀是tbl_对应的实体是驼峰命名规则
     *
     * @param object
     * @return
     */
    public <T> int saveObject(T object) {
        int result = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            SqlAndParamBean sqlAndParamBean = SqlUtil.generateInsertSqlAndParam(object);
            conn.setAutoCommit(Boolean.TRUE);
            result = DatabaseUtil.executeSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
        } catch (Exception e) {
            log.error("saveObject异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return result;
    }

    public <T> int saveObject(T object, String tableName) {
        int result = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(Boolean.TRUE);
            result = save(conn, tableName, object);
        } catch (Exception e) {
            log.error("saveObject异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return result;
    }

    /**
     * 批量保存实体对象
     *
     * @param obj
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> int batchSave(List<T> obj) throws SQLException {
        int result = -1;
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(Boolean.TRUE);
            result = insertBatch(conn, SqlUtil.getTableNameByClass(obj.get(0).getClass()), obj);
        } catch (Exception e) {
            log.error("batchSave异常: {]", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return result;
    }
    /**
     * 批量保存实体对象
     *
     * @param obj
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> int batchSave(Connection conn,List<T> obj) throws Exception {
        return insertBatch(conn, SqlUtil.getTableNameByClass(obj.get(0).getClass()), obj);
    }

    /**
     * 单条插入
     *
     * @param conn
     * @param tableName
     * @param obj
     * @return
     */
    public int save(Connection conn, String tableName, Object obj) {
        Map<String, Object> parameterMap = (Map<String, Object>) JSON.parse(JSON.toJSONString(obj));
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateInsertSqlAndParam(tableName, parameterMap);
        return DatabaseUtil.executeSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
    }

    /**
     * 覆盖历史方法
     *
     * @param conn
     * @param tableName
     * @param obj
     * @return
     */
    public int insertOne(Connection conn, String tableName, Object obj) {
        return save(conn, tableName, obj);
    }

    /**
     * 批量插入
     *
     * @param conn
     * @param tableName
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public int insertBatch(Connection conn, String tableName, List<?> obj) throws IllegalArgumentException, IllegalAccessException {
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateBatchInsertSqlAndParam(tableName, obj);
        return DatabaseUtil.executeBatchSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getBatchParams());
    }

    public int insertBatch(List<?> obj, String tableName) throws IllegalArgumentException, IllegalAccessException {
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateBatchInsertSqlAndParam(tableName, obj);
        Connection conn = getConnection();
        int batchSql = -1;
        try {
            batchSql = DatabaseUtil.executeBatchSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getBatchParams());
        } catch (Exception e) {
            log.error(ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return batchSql;
    }

    /**
     * 批量插入
     *
     * @param conn
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public int insertBatch(Connection conn, List<?> obj) throws IllegalArgumentException, IllegalAccessException {
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateBatchInsertSqlAndParam(obj);
        return DatabaseUtil.executeBatchSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getBatchParams());
    }

    /**
     * 更新数据
     *
     * @param conn
     * @param tableName
     * @param conditions
     * @param primaryFileName
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public int update(Connection conn, String tableName, Map<String, Object> conditions, String primaryFileName) {
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateUpdateSqlAndParam(tableName, conditions, primaryFileName);
        return DatabaseUtil.executeSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
    }

    public static int update(Connection conn, String tableName, Object object, Map<String, Object> conditions) throws Exception {
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateUpdateSqlAndParam(tableName, object, conditions);
        return DatabaseUtil.executeSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
    }

    /**
     * 根据sql更新数据
     *
     * @param conn
     * @param sql
     * @return
     */
    public int updateBySql(Connection conn, String sql) {
        return DatabaseUtil.executeSql(conn, sql);
    }

    /**
     * @param sql
     * @return
     */
    public int updateBySql(String sql) {
        int result = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(Boolean.TRUE);
            result = updateBySql(conn, sql);
        } catch (Exception e) {
            log.error("updateBySql异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return result;
    }

    /**
     * 更新实体对象，只支持主键名称为id的
     *
     * @param object
     * @param <T>
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     */
    public <T> int updateObject(T object, String primaryFiledName) {
        int result = -1;
        Connection conn = getConnection();
        try {
            SqlAndParamBean sqlAndParamBean = SqlUtil.generateUpdateSqlAndParam(object, primaryFiledName);
            // 注意在内部的话
            conn.setAutoCommit(Boolean.TRUE);
            result = DatabaseUtil.executeSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
        } catch (Exception e) {
            log.error("updateObject异常: {]", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return result;
    }

    public <T> int updateObject(T object, String primaryFiledName, String tableName) {
        int result = -1;
        Connection conn = getConnection();
        try {
            SqlAndParamBean sqlAndParamBean = SqlUtil.generateUpdateSqlAndParam(object, primaryFiledName, tableName);
            result = DatabaseUtil.executeSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
        } catch (Exception e) {
            log.error("updateObject异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return result;
    }

    public <T> int updateObject(Connection connection, T object, String primaryFiledName, String tableName) {
        int result = -1;
        try {
            SqlAndParamBean sqlAndParamBean = SqlUtil.generateUpdateSqlAndParam(object, primaryFiledName, tableName);
            result = DatabaseUtil.executeSql(connection, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
        } catch (Exception e) {
            log.error("updateObject异常: {}", ExceptionUtil.buildErrorMessage(e));
        }
        return result;
    }

    public <T> int updateObjectAllField(T object, String primaryFiledName, String tableName) {
        int result = -1;
        Connection conn = getConnection();
        try {
            SqlAndParamBean sqlAndParamBean = SqlUtil.generateUpdateSqlAndAllParam(object, primaryFiledName, tableName);
            result = DatabaseUtil.executeSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
        } catch (Exception e) {
            log.error("updateObject异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return result;
    }

    /**
     * 更新实体对象，只支持主键名称为id的
     *
     * @param object
     * @param <T>
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     */
    public <T> int updateObject(T object) {
        return updateObject(object, null);
    }

    /**
     * 批量更新，要求所有有的字段值不能为空
     *
     * @param objects
     * @param <T>
     * @return
     */
    @Deprecated
    public <T> int batchUpdateByPreparedStatement(List<T> objects) {
        int result = -1;
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(Boolean.TRUE);
            result = batchUpdate(conn, objects);
        } catch (Exception e) {
            log.error("batchUpdateByPreparedStatement异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return result;
    }


    /**
     * 批量更新
     *
     * @param conn
     * @param objects
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public int batchUpdate(Connection conn, List<?> objects) throws IllegalArgumentException, IllegalAccessException {
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateBatchUpdateSqlAndParam(objects);
        return DatabaseUtil.executeBatchSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getBatchParams());
    }

    public int batchAllUpdate(Connection conn, List<?> objects) throws IllegalArgumentException, IllegalAccessException {
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateBatchUpdateSqlAndAllParam(objects);
        return DatabaseUtil.executeBatchSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getBatchParams());
    }

    /**
     * 批量更新
     *
     * @param objects
     * @param <T>
     * @return
     */
    public <T> int batchUpdate(List<T> objects) {
        return batchUpdateByStatement(objects);
    }

    /**
     * 批量保存或者更新
     *
     * @param objects
     * @param <T>
     * @return
     */
    public <T extends Entity> List<T> batchSaveOrUpdate(List<T> objects) {

        if (CollectionUtil.isEmpty(objects))
            return null;

        int saveResult = -1;
        int updateResult = -1;

        AtomicReference<Boolean> saveFlag = new AtomicReference<>(false);
        AtomicReference<Boolean> updateFlag = new AtomicReference<>(false);

        List<T> saveObjects = new ArrayList<>();
        List<T> updateObjects = new ArrayList<>();

        try {
            objects.forEach(object -> {

                if (Objects.isNull(object))
                    return;

                if (object.getId() == null || "".equals(object.getId().trim())) {
                    object.setId(IDUtil.generateUUID());
                    object.setCreateTime(DateUtil.now());
                    saveFlag.set(true);
                    saveObjects.add(object);
                } else {
                    object.setCreater(null);
                    object.setCreateTime(null);
                    object.setUpdateTime(DateUtil.now());
                    updateObjects.add(object);
                    updateFlag.set(true);
                }
            });

            if (saveFlag.get()) {
                saveResult = BaseDao.batchSave(saveObjects);
            }
            if (updateFlag.get()) {
                updateResult = BaseDao.batchUpdate(updateObjects);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.buildErrorMessage(e));
        }

        /**新增、修改成功**/
        if (saveResult > 0 && updateResult > 0) {
            saveObjects.addAll(updateObjects);
            return saveObjects;
        }

        /**新增成功**/
        if (saveResult > 0 && updateResult <= 0) {
            return saveObjects;
        }

        /**修改成功**/
        if (saveResult <= 0 && updateResult > 0) {
            return updateObjects;
        }

        return null;
    }

    /**
     * @return int    返回执行的行数
     * @Title: batchUpdateByStatement
     * @Description: Statement批量更新
     */
    public int batchUpdateByStatement(List objects) {
        int[] object = {};
        Statement stm = null;
        List<String> sqlStringList = SqlUtil.generateBatchUpdateSqlList(objects);
        Connection conn = getConnection();
        try {
            stm = conn.createStatement();
            // 关闭自动提交，即开启事务
            conn.setAutoCommit(false);
            for (String sql : sqlStringList) {
                stm.addBatch(sql);
            }
            // 最后执行
            object = stm.executeBatch();
            // 执行完后，手动提交事务
            conn.commit();
        } catch (SQLException e) {
            try {
                // 发生异常，事务回滚！
                conn.rollback();
            } catch (Exception e2) {
                log.error("更新失败，事务回滚错误... {}", ExceptionUtil.buildErrorMessage(e));
            }
            log.error("batchUpdateByStatement异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                log.error("batchUpdateByStatement异常: {}", ExceptionUtil.buildErrorMessage(e));
            }
            DatasourceUtil.close(conn, stm, null);
        }
        return object.length;
    }


    public <T> int delete(T object) {
        int result = 0;
        Connection connection = getConnection();
        try {
            result = deleteHard(connection, SqlUtil.getTableNameByClass(object.getClass()), SqlUtil.getAllFields(object, false));
        } catch (Exception e) {
            log.error("delete异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(connection);
        }
        return result;
    }

    /**
     * 批量删除
     *
     * @param clazz
     * @param idFieldName
     * @param objects
     * @return
     */
    public <T> int batchDelete(Class<T> clazz, String idFieldName, List<String> objects) {
        int result = 0;
        Connection connection = getConnection();
        try {
            result = deleteBatchHard(connection, SqlUtil.generateBatchDeleteSql(clazz, idFieldName, objects), objects);
        } catch (Exception e) {
            log.error("batchDelete异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(connection);
        }
        return result;
    }

    /**
     * 删除数据
     *
     * @param conn
     * @param tableName
     * @param conditions
     * @return
     */
    public int deleteHard(Connection conn, String tableName, Map<String, Object> conditions) {
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateDeleteSqlAndParam(tableName, conditions);
        return DatabaseUtil.executeSql(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
    }

    public int deleteHard(String tableName, Map<String, Object> conditions) {
        int result = -1;
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(Boolean.TRUE);
            result = deleteHard(conn, tableName, conditions);
        } catch (Exception e) {
            log.error("deleteHard异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return result;
    }

    /**
     * 批量删除
     *
     * @param conn
     * @param sql
     * @param divisionIds
     * @return
     */
    public int deleteBatchHard(Connection conn, String sql, List<String> divisionIds) {
        return DatabaseUtil.executeBatchDelete(conn, sql, divisionIds);
    }

    /**
     * 根据sql删除数据
     *
     * @param conn
     * @param sql
     * @return
     */
    public int deleteBySqlHard(Connection conn, String sql) {
        return DatabaseUtil.executeSql(conn, sql, null);
    }

    /**
     * 单表查询
     *
     * @param conn
     * @param tableName
     * @param condition
     * @return
     */
    public List<?> search(Connection conn, String tableName, Map<String, Object> condition) {
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateSearchSqlAndParam(tableName, condition, null);
        return DatabaseUtil.executeSearch(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
    }

    /**
     * @param tableName
     * @param condition
     * @return
     */
    public List<Map> search(String tableName, Map<String, Object> condition) {
        List<Map> result = null;
        Connection conn = getConnection();
        try {
            result = (List<Map>) search(conn, tableName, condition);
        } catch (Exception e) {
            log.error("search异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return result;
    }

    public <T> List<T> search(String tableName, Map<String, Object> condition, Class<T> clazz) {
        List list = search(tableName, condition);
        if (CollectionUtil.isNotEmpty(list)) {
            return JSON.parseArray(JSON.toJSONString(list), clazz);
        }
        return null;
    }

    /**
     * 根据sql、参数、第几页，每页大小，查询 PageResult
     *
     * @param sql
     * @param params
     * @param clazz
     * @param pageSize
     * @param pageNumber
     * @param <T>
     * @return
     */
    public <T> PageResult<T> searchPageBySql(String sql, Object[] params, Class<T> clazz, Integer pageSize, Integer pageNumber) {
        return new PageResult(pageSize, getTotalRecord(sql, params), searchBySql(sql, params, clazz, pageSize, pageNumber));
    }

    public <T> PageResult<T> searchPageBySql(SqlAndParamBean sqlAndParamBean, Class<T> clazz, Integer pageSize, Integer pageNumber) {
        return new PageResult(pageSize, getTotalRecord(sqlAndParamBean), searchBySql(sqlAndParamBean, clazz, pageSize, pageNumber));
    }

    /**
     * 根据sql查询list集合
     *
     * @param sql
     * @param params
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> searchBySql(String sql, Object[] params, Class<T> clazz) {
        return searchBySql(sql, params, clazz, null, null);
    }

    public <T> List<T> searchBySql(SqlAndParamBean sqlAndParamBean, Class<T> clazz) {
        return searchBySql(sqlAndParamBean.getSql(), sqlAndParamBean.getParams(), clazz);
    }

    /**
     * 根据sql、参数、第几页，每页大小，查询list集合
     *
     * @param sql
     * @param params
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> searchBySql(String sql, Object[] params, Class<T> clazz, Integer pageSize, Integer pageNumber) {
        List<T> list = null;
        try {
            list = mapToList(searchBySql(sql, params, pageSize, pageNumber), clazz);
        } catch (Exception e) {
            log.error(ExceptionUtil.buildErrorMessage(e));
        }
        return list;
    }

    private <T> List<T> mapToList(List<Map> dataList, Class<T> clazz) {
        List<T> list = null;
        if (CollectionUtil.isNotEmpty(dataList)) {
            list = new ArrayList<>();
            for (Map data : dataList) {
                if (null != data) {
                    T obj = null;
                    try {
                        obj = JSON.parseObject(JSON.toJSONString(data), clazz);
                    } catch (Exception e) {
                        log.error("mapToList异常: {}", ExceptionUtil.buildErrorMessage(e));
                    }
                    if (null != obj) {
                        list.add(obj);
                    }
                }
            }
        }
        return list;
    }

    public <T> List<T> searchBySql(SqlAndParamBean sqlAndParamBean, Class<T> clazz, Integer pageSize, Integer pageNumber) {
        return searchBySql(sqlAndParamBean.getSql(), sqlAndParamBean.getParams(), clazz, pageSize, pageNumber);
    }

    /**
     * 根据sql查询list集合
     *
     * @param sql
     * @param params
     * @return
     */
    /**
     * 根据sql、参数、第几页、每页多少查询分页结果,暂不支持排序
     *
     * @param sql
     * @param params
     * @param pageSize
     * @param pageNumber
     * @return
     */
    public List<Map> searchBySql(String sql, Object[] params, Integer pageSize, Integer pageNumber) {
        List<Map> dataList = null;
        Connection conn = null;
        try {
            if (null != pageSize && null != pageNumber && pageSize > 0 && pageNumber > 0) {
                sql += " offset ? limit ?";
                params = concat(params, new Object[]{(pageNumber - 1) * pageSize, pageNumber * pageSize});
            }
            conn = getConnection();
            dataList = DatabaseUtil.executeSearch(conn, sql, params);
        } catch (Exception e) {
            log.error("searchBySql异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return dataList;
    }

    public List<Map> searchBySql(SqlAndParamBean sqlAndParamBean, Integer pageSize, Integer pageNumber) {
        return searchBySql(sqlAndParamBean.getSql(), sqlAndParamBean.getParams(), pageSize, pageNumber);
    }

    public <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * 根据sql查询list集合
     *
     * @param sql
     * @param params
     * @return
     */
    public List<Map> searchBySql(String sql, Object[] params) {
        return searchBySql(sql, params, null, null);
    }


    public static List<?> searchBySql(String sql) {
        List list = null;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(Boolean.TRUE);
            list = DatabaseUtil.executeSearch(conn, sql, null);
        } catch (Exception e) {
            log.error("saveOrUpdate异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return list;
    }

    public static List<?> searchBySql(Connection conn, String sql) {
        return DatabaseUtil.executeSearch(conn, sql, null);
    }

    public List<Map> searchBySql(SqlAndParamBean sqlAndParamBean) {
        return searchBySql(sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
    }

    /**
     * 带参数的增删改sql查询方法
     *
     * @param sql
     * @param params
     * @return
     */
    public int saveOrUpdate(String sql, Object[] params) {
        int object = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(Boolean.TRUE);
            object = DatabaseUtil.executeSql(conn, sql, params);
        } catch (Exception e) {
            log.error("saveOrUpdate异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return object;
    }

    /**
     * 根据条件获取单个实体对象
     *
     * @param object
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T getObject(T object) {
        List<T> list = getObjectAll(object);
        if (null != list && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取当前对象（属性赋值后自动带查询条件） 的所有数据，带条件，需要把参数封装到对象属性里面，只支持表名下划线并且前缀是tbl_对应的实体是驼峰命名规则
     *
     * @param object 实体对象
     * @param <T>    泛型
     * @return 返回带泛型的List对象
     * @throws Exception
     */
    public <T> List<T> getObjectAll(T object, String sortConditions) {
        Class<T> clazz = (Class<T>) object.getClass();
        List<T> list = null;
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, Object> condition = SqlUtil.getAllFields(object, false);
            SqlAndParamBean sqlAndParamBean = SqlUtil.generateSearchSqlAndParam(SqlUtil.getTableNameByClass(clazz), condition, sortConditions);
            List<Map> dataList = DatabaseUtil.executeSearch(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
            list = mapToList(dataList, clazz);
        } catch (Exception e) {
            log.error("getObjectAll异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return list;
    }

    /**
     * 根据条件获取单表的集合
     *
     * @param object
     * @param <T>
     * @return
     */
    public <T> List<T> getObjectAll(T object) {
        return getObjectAll(object, null);
    }

    /**
     * 根据查询条件获取分页集合
     *
     * @param query
     * @param clazz
     * @return
     * @throws SQLException
     */
    public <T> PageResult<T> getObjectPage(Query query, Class<T> clazz) {
        setTableName(query, clazz);
        int totalRecord = getTotalRecord(SqlUtil.generateQueryTotalRecordBySqlAndParamBean(query));
        if (totalRecord <= 0) {
            return null;
        }
        query.setPage(new Page<>(query.getPage().getPageNumber(), query.getPage().getPageSize()));
        return new PageResult(query.getPage().getPageSize(), totalRecord, getList(SqlUtil.generateQueryBySqlAndParamBean(query), clazz), query.getPage().getPageNumber());
    }

    /**
     * 根据查询条件获取分页集合
     *
     * @param pageSize
     * @param sqlAndParamBean
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> PageResult<T> getPage(Integer pageSize, SqlAndParamBean sqlAndParamBean, Class<T> clazz) {
        return new PageResult(pageSize, getTotalRecord(sqlAndParamBean), getList(sqlAndParamBean, clazz));
    }

    /**
     * 根据查询条件获取List集合
     *
     * @param query
     * @param clazz
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> List<T> getList(Query query, Class<T> clazz) {
        setTableName(query, clazz);
        SqlAndParamBean sqlAndParamBean = SqlUtil.generateQueryBySqlAndParamBean(query);

        if (StringUtils.isEmpty(sqlAndParamBean.getSql())) {
            return null;
        }
        return getList(sqlAndParamBean, clazz);
    }

    /**
     * 根据查询条件获取List集合
     *
     * @param sqlAndParamBean
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> List<T> getList(SqlAndParamBean sqlAndParamBean, Class<T> clazz) {
        List<T> list = null;
        Connection conn = getConnection();
        try {
            List<Map> dataList = DatabaseUtil.executeSearch(conn, sqlAndParamBean.getSql(), sqlAndParamBean.getParams());

            if (CollectionUtil.isEmpty(dataList)) {
                return null;
            }

            list = mapToList(dataList, clazz);
        } catch (Exception e) {
            log.error("getList异常: {}", ExceptionUtil.buildErrorMessage(e));
        } finally {
            DatasourceUtil.close(conn);
        }
        return list;
    }

    /**
     * 根据当前实体对象（属性赋值后自动带查询条件） 获取当前条件下的总记录数
     *
     * @param sqlAndParamBean 实体对象
     * @return 总记录数
     * @throws IllegalAccessException
     * @throws SQLException
     */
    public int getTotalRecord(SqlAndParamBean sqlAndParamBean) {
        return getTotalRecord(sqlAndParamBean.getSql(), sqlAndParamBean.getParams());
    }

    /**
     * 根据条件查询数据的总和
     *
     * @param sql
     * @param params
     * @return
     */
    public int getTotalRecord(String sql, Object[] params) {
        int totalRecord = 0;
        Connection conn = getConnection();
        try {
            if (null != params && params.length > 0) {
                params = getCountParams(sql, params);
            }
            sql = getCountSql(sql);
            totalRecord = DatabaseUtil.executeQueryByIntSql(conn, sql, params);
        } catch (Exception e) {
            log.error("getTotalRecord异常: {}", ExceptionUtil.buildErrorMessage(e));
            throw e;
        } finally {
            DatasourceUtil.close(conn);
        }
        return totalRecord;
    }

    public static void main(String[] args) {

        String sql = "select * from tbl_user where id = ? and username = ? offset ? limit ? order ? asc ? desc";
        Object[] params = new Object[]{"1", "2", 1, 10, "3", "4"};

        Object[] newParams = getCountParams(sql, params);

        String newSql = getCountSql(sql);

        System.out.println(newSql);

        System.out.println(Arrays.toString(newParams));
    }


    private Object[] getCountParams(String sql, Object[] params) {
        return Arrays.copyOf(params, params.length - getOrderByParamsCount(sql));
    }

    private int getOrderByParamsCount(String sql) {
        int paramsCount = 0;

        int offsetIndex = sql.lastIndexOf("offset");

        if (offsetIndex > 0) {
            sql = sql.substring(offsetIndex);
            paramsCount = findCharCount(sql);
        } else {
            int orderIndex = sql.lastIndexOf("order");
            if (orderIndex > 0) {
                sql = sql.substring(orderIndex);
                paramsCount = findCharCount(sql);
            }
        }
        return paramsCount;
    }

    private int findCharCount(String sql) {
        int index = 0;
        char[] arr = sql.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if ("?".equals(String.valueOf(arr[i]))) {
                index += 1;
            }
        }
        return index;
    }

    private String getCountSql(String sql) {
        int fromIndex = sql.indexOf("from");

        int orderIndex = 0;
        int offsetIndex = 0;

        if (fromIndex > 0) {
            sql = sql.substring(fromIndex);
        }

        orderIndex = sql.lastIndexOf("order");

        if (orderIndex > 0) {
            sql = sql.substring(0, orderIndex);
        }

        offsetIndex = sql.lastIndexOf("offset");

        if (offsetIndex > 0) {
            sql = sql.substring(0, offsetIndex);
        }
        return new StringBuilder("select count(1) ").append(sql).toString();
    }


    /**
     * 获取当前条件的的数据集合总数
     *
     * @param object
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     */
    public int getObjectTotalRecord(Object object) throws SQLException, IllegalAccessException {
        return getTotalRecord(SqlUtil.getObjectPageSqlAndParamBean(object, new Page(-1, -1), null));
    }

    /**
     * 如果query不传查询的tableName，则根据class自动获取对应的tableName
     *
     * @param query
     * @param clazz
     * @param <T>
     */
    private <T> void setTableName(Query query, Class<T> clazz) {
        if (null != query && (null == query.getTableNames() || query.getTableNames().length <= 0)) {
            query.setTableNames(new String[]{SqlUtil.getTableNameByClass(clazz)});
        }
    }

    /**
     * 获取数据连接
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DatasourceUtil.getInstance().getDataSource().getConnection();
        } catch (SQLException e) {
            log.error(ExceptionUtil.buildErrorMessage(e));
        }
        return connection;
    }

    public <T extends Entity> List<T> batchSaveOrUpdateWithTransactional(Connection conn, List<T> objects) throws Exception {
        if (CollectionUtil.isEmpty(objects))
            return null;

        int saveResult = -1;
        int updateResult = -1;

        AtomicReference<Boolean> saveFlag = new AtomicReference<>(false);
        AtomicReference<Boolean> updateFlag = new AtomicReference<>(false);

        List<T> saveObjects = new ArrayList<>();
        List<T> updateObjects = new ArrayList<>();

        objects.forEach(object -> {

            if (Objects.isNull(object))
                return;

            if (object.getId() == null || "".equals(object.getId().trim())) {
                object.setId(IDUtil.generateUUID());
                object.setCreateTime(DateUtil.now());
                saveFlag.set(true);
                saveObjects.add(object);
            } else {
                object.setCreater(null);
                object.setCreateTime(null);
                object.setUpdateTime(DateUtil.now());
                updateObjects.add(object);
                updateFlag.set(true);
            }
        });

        if (saveFlag.get()) {
            saveResult = BaseDao.batchSave(conn, saveObjects);
        }
        if (updateFlag.get()) {
            updateResult = BaseDao.batchUpdate(conn, updateObjects);
        }

        /**新增、修改成功**/
        if (saveResult > 0 && updateResult > 0) {
            saveObjects.addAll(updateObjects);
            return saveObjects;
        }

        /**新增成功**/
        if (saveResult > 0 && updateResult <= 0) {
            return saveObjects;
        }

        /**修改成功**/
        if (saveResult <= 0 && updateResult > 0) {
            return updateObjects;
        }

        return null;
    }

}
