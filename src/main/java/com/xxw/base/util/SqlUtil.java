package com.xxw.base.util;

import com.xxw.annotation.Table;
import com.xxw.base.dao.SqlAndParamBean;
import com.xxw.base.query.Condition;
import com.xxw.base.query.Order;
import com.xxw.base.query.Page;
import com.xxw.base.query.Query;
import com.xxw.http.ExceptionUtil;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class SqlUtil {

    private static final String TABLE_PREFIX = "tbl";

    private static final String PRIMARY_KEY_FILED_NAME = "id";

    /**
     * 生成插入的sql语句以及参数
     *
     * @param tableName 表名 表名
     * @param dataMap   数据实体的Map对象
     * @return SqlAndParamBean
     */
    public SqlAndParamBean generateInsertSqlAndParam(String tableName, Map<String, Object> dataMap) {
        StringBuilder builder = new StringBuilder("insert into");
        builder.append(" ");
        builder.append(tableName);
        builder.append("(");

        String[] key = new String[dataMap.keySet().size()];
        dataMap.keySet().toArray(key);
        StringBuilder paramBuilder = new StringBuilder();
        Object[] params = new Object[dataMap.keySet().size()];
        for (int i = 0; i < key.length; i++) {
            builder.append(key[i]);
            builder.append(",");
            paramBuilder.append("?");
            paramBuilder.append(",");
            params[i] = dataMap.get(key[i]);
        }
        builder.deleteCharAt(builder.length() - 1);
        paramBuilder.deleteCharAt(paramBuilder.length() - 1);
        builder.append(") values (");
        builder.append(paramBuilder).append(")");
        return new SqlAndParamBean(builder.toString(), params, null);
    }

    /**
     * 根据实体对象获取插入Sql语句以及参数
     *
     * @param object 实体对象
     * @return SqlAndParamBean
     * @throws IllegalAccessException 没有访问权限的异常
     */
    public SqlAndParamBean generateInsertSqlAndParam(Object object) throws Exception {
        String tableName = getTableNameByClass(object.getClass());
        Map<String, Object> dataMap = getAllFields(object, false);
        return generateInsertSqlAndParam(tableName, dataMap);
    }

    /**
     * 生成批量插入的sql语句以及参数
     *
     * @param tableName 表名 表名
     * @param objects   实体对象集合
     * @return SqlAndParamBean
     * @throws IllegalArgumentException 不合法的参数异常
     * @throws IllegalAccessException   没有访问权限的异常
     */
    public SqlAndParamBean generateBatchInsertSqlAndParam(String tableName, List<?> objects) throws IllegalArgumentException, IllegalAccessException {
        StringBuilder builder = new StringBuilder("insert into ");
        builder.append(tableName).append(" (");
        Class<?> clazz = objects.get(0).getClass();
        StringBuilder paramBuilder = new StringBuilder();
        List<Object[]> parameters = new ArrayList<>();

        while (clazz != Object.class) {

            List<Field> fieldList = getAllEntityToDBFields(clazz, objects.get(0));

            for (Field field : fieldList) {
                field.setAccessible(true);
                List<Object> values = new ArrayList<>();
                for (Object object : objects) {
                    Object value = field.get(object);
                    values.add(value);
                }
                Object[] objects2 = values.toArray();
                parameters.add(objects2);

                builder.append(field.getName());
                builder.append(",");
                paramBuilder.append("?");
                paramBuilder.append(",");

            }
            clazz = clazz.getSuperclass();
        }

        builder.deleteCharAt(builder.length() - 1);
        paramBuilder.deleteCharAt(paramBuilder.length() - 1);
        builder.append(") values (");
        builder.append(paramBuilder).append(")");

        return new SqlAndParamBean(builder.toString(), null, transForm(parameters));
    }


    public SqlAndParamBean generateBatchInsertSqlAndParam(List<?> objects) throws IllegalArgumentException, IllegalAccessException {
        String tableName = getTableNameByClass(objects.get(0).getClass());
        return generateBatchInsertSqlAndParam(tableName, objects);
    }

    /**
     * 生成基本的查询sql语句
     *
     * @param fields           需要查询的字段
     * @param tableName        表名 表名
     * @param dataMap          数据实体的Map对象
     * @param pagingConditions 分页语句 例如： limit 10
     * @param sortConditionS   排序语句 id desc, age asc
     * @return Sql语句
     */
    private String generateQueryBaseSql(String fields, String tableName, Map<String, Object> dataMap, String pagingConditions, String sortConditionS) {
        StringBuilder builder = new StringBuilder("select ");
        builder.append(fields);
        builder.append(" from ");
        builder.append(tableName);
        if (dataMap != null && dataMap.size() != 0) {
            builder.append(" where ");
            String[] keys = new String[dataMap.size()];
            dataMap.keySet().toArray(keys);
            for (int i = 0; i < keys.length; i++) {
                builder.append(keys[i]);
                builder.append(" = ?");
                if (i != keys.length - 1) {
                    builder.append(" and ");
                }
            }
        }
        if (StringUtils.isNotEmpty(sortConditionS)) {
            builder.append(" order by ").append(sortConditionS);
        }
        if (StringUtils.isNotEmpty(pagingConditions)) {
            builder.append(" ").append(pagingConditions);
        }
        return builder.toString();
    }

    /**
     * 生成总记录数sql语句
     *
     * @param tableName 表名 表名
     * @param dataMap   数据实体的Map对象
     * @return Sql语句
     */
    public String generateQueryByTotalRecordSql(String tableName, Map<String, Object> dataMap) {
        return generateQueryBaseSql("count(1)", tableName, dataMap, null, null);
    }

    /**
     * 生成分页sql语句
     *
     * @param tableName  表名
     * @param dataMap    数据实体的Map对象
     * @param pageNumber 第几页
     * @param pageSize   每页显示多少条
     * @return Sql语句
     */
    public String generateQueryByPageSql(String tableName, Map<String, Object> dataMap, int pageNumber, int pageSize, String sortConditions) {
        return generateQueryBaseSql("*", tableName, dataMap, " offset " + pageSize * (pageNumber - 1) + " limit " + pageSize, sortConditions);
    }

    public SqlAndParamBean generateQueryTotalRecordBySqlAndParamBean(Query query) {
        return generateQueryBySqlAndParamBean(query, true);
    }

    public SqlAndParamBean generateQueryBySqlAndParamBean(Query query) {
        return generateQueryBySqlAndParamBean(query, false);
    }

    /**
     * 根据Query 对象生成sql语句
     *
     * @param query 查询条件对象
     * @return SqlAndParamBean
     */
    private SqlAndParamBean generateQueryBySqlAndParamBean(Query query, Boolean isTotalRecord) {

        if (Objects.isNull(query)) {
            return null;
        }

        /*sql语句组装器**/
        StringBuilder sqlBuilder = new StringBuilder();

        /* sql参数 **/
        Vector<Object> paramList = new Vector<>();

        /* sql字段 **/
        StringBuilder fields = new StringBuilder();

        /* sql表名 **/
        StringBuilder tableNames = new StringBuilder();

        /* sql条件 **/
        StringBuilder wheres = new StringBuilder();

        /* sql分页语句 **/
        StringBuilder pagingConditions = new StringBuilder();

        /* sql排序语句**/
        StringBuilder sortConditions = new StringBuilder();

        if (null != query.getFields() && !query.getFields().isEmpty()) {
            fields.append("select ");
            for (String field : query.getFields()) {
                fields.append(field).append(",");
            }
            fields.deleteCharAt(fields.length() - 1);
        } else {
            fields.append("select * ");
        }

        if (null != query.getTableNames() && query.getTableNames().length > 0) {
            tableNames.append(" from ");
            for (String tableName : query.getTableNames()) {
                tableNames.append(tableName).append(",");
            }
            tableNames.deleteCharAt(tableNames.length() - 1);
        }

        if (null != query.getWheres() && query.getWheres().length > 0) {
            wheres.append(" where ").append(buildCondition(null, query.getWheres()));
            for (Condition condition : query.getWheres()) {
                if (Objects.nonNull(condition) && Objects.nonNull(condition.getValue())) {
                    paramList.add(condition.getValue());
                }
            }
        }

        if (null != query.getPage() && query.getPage().getStartItems() >= 0 && query.getPage().getEndItems() > 0) {
            pagingConditions.append(" limit ").append(query.getPage().getEndItems()).append(" offset ").append(query.getPage().getStartItems());
        }

        if (null != query.getOrders() && query.getOrders().length > 0) {
            sortConditions.append(" order by ");
            for (Order order : query.getOrders()) {
                sortConditions.append(order.getField()).append(" ").append(order.getDirection().toString());
            }
        }

        if (isTotalRecord) {
            sqlBuilder.append("select count(1)").append(tableNames).append(wheres).append(pagingConditions);
        } else {
            sqlBuilder.append(fields).append(tableNames).append(wheres).append(sortConditions).append(pagingConditions);
        }

        return new SqlAndParamBean(sqlBuilder.toString(), paramList.toArray(), null);
    }

    /**
     * 根据Query对象生成sql语句和参数
     *
     * @param query 查询条件对象
     * @return SqlAndParamBean
     */
    public SqlAndParamBean generateWhereSqlAndParamByQuery(Query query) {
        StringBuilder wheres = new StringBuilder();
        Vector<Object> paramList = new Vector<>();
        if (null != query.getWheres() && query.getWheres().length > 0) {
            wheres.append(" where ").append(buildCondition(null, query.getWheres()));
            for (Condition condition : query.getWheres()) {
                paramList.add(condition.getValue());
            }
        }
        return new SqlAndParamBean(wheres.toString(), paramList.toArray(), null);
    }

    /**
     * @param tableName 表名
     * @param dataMap   数据实体的Map对象
     * @return SqlAndParamBean
     * @description dataMap 数据只做and操作，or操作请自行编写sql语句
     */
    public SqlAndParamBean generateSearchSqlAndParam(String tableName, Map<String, Object> dataMap, String pagingConditions, String sortConditions) {
        Object[] params = null;

        if (dataMap != null && dataMap.size() != 0) {
            String[] keys = new String[dataMap.size()];
            dataMap.keySet().toArray(keys);
            params = new Object[dataMap.keySet().size()];
            for (int i = 0; i < keys.length; i++) {
                params[i] = dataMap.get(keys[i]);
            }
        }
        String sql = generateQueryBaseSql("*", tableName, dataMap, pagingConditions, sortConditions);
        return new SqlAndParamBean(sql, params, null);
    }

    /**
     * @param tableName 表名
     * @param dataMap   数据实体的Map对象
     * @return SqlAndParamBean
     * @description dataMap数据只做and操作，or操作请自行编写sql语句
     */
    public SqlAndParamBean generateSearchSqlAndParam(String tableName, Map<String, Object> dataMap, String sortConditions) {
        return generateSearchSqlAndParam(tableName, dataMap, null, sortConditions);
    }

    public SqlAndParamBean generateSearchSqlAndParam(String tableName, Map<String, Object> dataMap) {
        return generateSearchSqlAndParam(tableName, dataMap, null, null);
    }

    /**
     * 根据实体对象获取查询Sql语句以及参数
     *
     * @param object 实体对象
     * @return SqlAndParamBean
     * @throws IllegalAccessException 没有访问权限的异常
     */
    public SqlAndParamBean generateSearchSqlAndParam(Object object) throws IllegalAccessException {
        String tableName = getTableNameByClass(object.getClass());
        Map<String, Object> dataMap = getAllFields(object, false);
        return generateSearchSqlAndParam(tableName, dataMap, null);
    }

    /**
     * 根据实体对象（属性赋值后自动带查询条件） ，页码、每页大小返回对应的 sql与参数对象，pageNumber pageSize <= 0 的时候 返回的说去总是sql
     *
     * @param object 实体对象 实体对象
     * @param page   入参的Page实体参数
     * @return Sql语句以及参数的实体对象
     * @throws IllegalAccessException 没有访问权限的异常
     */
    public <T> SqlAndParamBean getObjectPageSqlAndParamBean(Object object, Page<T> page, String sortConditions) throws IllegalAccessException {
        Map<String, Object> condition = getAllFields(object, false);
        String sql = generateQueryByPageSql(SqlUtil.getTableNameByClass(object.getClass()), condition, page.getPageNumber(), page.getPageSize(), sortConditions);
        if (page.getPageNumber() <= 0 && page.getPageSize() <= 0) {
            sql = SqlUtil.generateQueryByTotalRecordSql(SqlUtil.getTableNameByClass(object.getClass()), condition);
        }
        return new SqlAndParamBean(sql, condition.values().toArray(), null);
    }

    /**
     * 生成批量删除的Sql以及参数的实体对象
     *
     * @param tableName 表名
     * @param dataMaps  数据对象集合
     * @return Sql语句
     * @description dataMap数据只做and操作，or操作请自行编写sql语句
     */
    private String generateBatchDeleteSql(String tableName, String idFieldName, List<String> dataMaps) {
        StringBuilder builder = new StringBuilder("delete from ");
        builder.append(tableName);
        if (dataMaps != null && dataMaps.size() != 0) {
            builder.append(" where ").append(idFieldName).append(" in(");
            Iterator<String> iterator = dataMaps.iterator();
            while (iterator.hasNext()) {
                builder.append(" ? ,");
                iterator.next();
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(")");
        }
        return builder.toString();
    }

    /**
     * 生成删除的Sql以及参数的实体对象
     *
     * @param tableName 表名
     * @param dataMap   数据实体的Map对象
     * @return SqlAndParamBean
     * @description dataMap数据只做and操作，or操作请自行编写sql语句
     */
    public SqlAndParamBean generateDeleteSqlAndParam(String tableName, Map<String, Object> dataMap) {
        Object[] params = null;
        StringBuilder builder = new StringBuilder("delete from");
        builder.append(" ");
        builder.append(tableName);
        if (dataMap != null && dataMap.size() != 0) {
            builder.append(" where ");

            String[] keys = new String[dataMap.size()];
            dataMap.keySet().toArray(keys);
            params = new Object[dataMap.keySet().size()];
            for (int i = 0; i < keys.length; i++) {
                builder.append(keys[i]);
                builder.append("=");
                builder.append("?");
                if (i != keys.length - 1) {
                    builder.append(" and ");
                }
                params[i] = dataMap.get(keys[i]);
            }
        }

        return new SqlAndParamBean(builder.toString(), params, null);
    }


    /**
     * 根据实体对象获取删除的Sql语句以及参数
     *
     * @param object 实体对象
     * @return SqlAndParamBean
     * @throws IllegalAccessException 没有访问权限的异常
     */
    public SqlAndParamBean generateDeleteSqlAndParam(Object object) throws Exception {
        String tableName = getTableNameByClass(object.getClass());
        Map<String, Object> dataMap = getAllFields(object, false);
        return generateDeleteSqlAndParam(tableName, dataMap);
    }

    /**
     * 根据实体对象获取删除的Sql语句以及参数
     *
     * @param clazz 实体对象的类型
     * @param ids   主键集合
     * @return Sql 语句
     */
    public <T> String generateBatchDeleteSql(Class<T> clazz, String idFieldName, List<String> ids) {
        String tableName = getTableNameByClass(clazz);
        return generateBatchDeleteSql(tableName, idFieldName, ids);
    }

    public String generateBatchDeleteSqlForTableName(String tableName, String idFieldName, List<String> ids) {
        return generateBatchDeleteSql(tableName, idFieldName, ids);
    }

    private SqlAndParamBean generateUpdateSqlAndParam(String tableName, Map<String, Object> dataMap, String primaryName, boolean isFilterNullValue) throws IllegalArgumentException {
        StringBuilder whereBuilder = new StringBuilder(" where ");
        StringBuilder updateBuilder = new StringBuilder("update ").append(tableName).append(" set ");
        String[] keys = new String[dataMap.size()];
        dataMap.keySet().toArray(keys);
        List<Object> objList = new ArrayList<>();
        List<Object> whereList = new ArrayList<>();

        String primaryFiledName = StringUtils.isNotEmpty(primaryName) ? primaryName : PRIMARY_KEY_FILED_NAME;

        for (String key : keys) {
            Object object = dataMap.get(key);
            if (!primaryFiledName.equals(key)) {
                if (isFilterNullValue && object != null && StringUtils.isNotNull(object.toString())) {
                    updateBuilder.append(key);
                    updateBuilder.append(" = ? ,");
                    objList.add(object);
                } else {
                    updateBuilder.append(key);
                    updateBuilder.append(" = ? ,");
                    objList.add(object);
                }
            } else {
                whereBuilder.append(key);
                whereBuilder.append(" = ? ");
                whereList.add(object);
            }
        }
        if (updateBuilder.toString().endsWith(",")) {
            updateBuilder.deleteCharAt(updateBuilder.length() - 1);
        }
        objList.addAll(whereList);
        return new SqlAndParamBean(updateBuilder.append(whereBuilder).toString(), objList.toArray(), null);
    }

    /**
     * 根据实体对象、参数生成（不包括参数值为null）更新的Sql以及参数对象
     *
     * @param tableName   表名
     * @param dataMap     数据实体的Map对象
     * @param primaryName 主键名称
     * @return SqlAndParamBean
     * @throws IllegalArgumentException 不合法的参数异常
     */
    public SqlAndParamBean generateUpdateSqlAndParam(String tableName, Map<String, Object> dataMap, String primaryName) throws IllegalArgumentException {
        return generateUpdateSqlAndParam(tableName, dataMap, primaryName, true);
    }

    /**
     * 根据实体对象、参数生成全参数（包括参数值为null）的更新Sql以及参数对象
     *
     * @param tableName   表名
     * @param dataMap     数据实体的Map对象
     * @param primaryName 主键名称
     * @return SqlAndParamBean
     * @throws IllegalArgumentException 不合法的参数异常
     */
    public SqlAndParamBean generateUpdateSqlAndAllParam(String tableName, Map<String, Object> dataMap, String primaryName) throws IllegalArgumentException {
        return generateUpdateSqlAndParam(tableName, dataMap, primaryName, false);
    }

    /**
     * 根据实体对象获取删除的sql语句以及参数
     *
     * @param object 实体对象
     * @return SqlAndParamBean
     * @throws IllegalAccessException 没有访问权限的异常
     */
    public SqlAndParamBean generateUpdateSqlAndParam(Object object, String primaryFiledName) throws Exception {
        String tableName = getTableNameByClass(object.getClass());
        Map<String, Object> dataMap = getAllFields(object, false);
        return generateUpdateSqlAndParam(tableName, dataMap, primaryFiledName);
    }

    public SqlAndParamBean generateUpdateSqlAndParam(Object object, String primaryFiledName, String tableName) throws Exception {
        Map<String, Object> dataMap = getAllFields(object, false);
        return generateUpdateSqlAndParam(tableName, dataMap, primaryFiledName);
    }

    public SqlAndParamBean generateUpdateSqlAndAllParam(Object object, String primaryFiledName, String tableName) throws Exception {
        Map<String, Object> dataMap = getNewAllFields(object);
        return generateUpdateSqlAndAllParam(tableName, dataMap, primaryFiledName);
    }

    public static SqlAndParamBean generateUpdateSqlAndParam(String tableName, Object obj, Map<String, Object> dataMap) throws IllegalArgumentException, IllegalAccessException {
        Object[] params;
        StringBuilder builder = new StringBuilder("update ");
        builder.append(tableName).append(" set ");
        Map<String, Object> allFieldValue = getAllFields(obj, false);
        String[] keys = new String[allFieldValue.size()];
        allFieldValue.keySet().toArray(keys);
        List<Object> objList = new ArrayList<>();
        for (String key : keys) {
            Object object = allFieldValue.get(key);
            if (object != null && StringUtils.isNotEmpty(object.toString())) {
                builder.append(key);
                builder.append(" = ? ,");
                objList.add(object);
            }
        }
        if (builder.toString().endsWith(",")) {
            builder.deleteCharAt(builder.length() - 1);
        }
        params = new Object[objList.size() + dataMap.size()];
        Object[] objArray = objList.toArray();
        System.arraycopy(objArray, 0, params, 0, objArray.length);
        if (dataMap.size() != 0) {
            builder.append(" where ");

            String[] dataMapKeys = new String[dataMap.size()];
            dataMap.keySet().toArray(dataMapKeys);
            for (int i = 0; i < dataMapKeys.length; i++) {
                builder.append(dataMapKeys[i]);
                builder.append(" = ?");
                if (i != dataMapKeys.length - 1) {
                    builder.append(" and ");
                }
                params[i + objArray.length] = dataMap.get(dataMapKeys[i]);
            }
        }

        return new SqlAndParamBean(builder.toString(), params, null);
    }

    public List<String> generateBatchUpdateSqlList(List<?> objects) throws IllegalArgumentException {
        final Class<?>[] clazz = {objects.get(0).getClass()};
        final String tableName = getTableNameByClass(clazz[0]);
        List<String> sqlStrings = new ArrayList<>();

        objects.forEach(object -> {
            clazz[0] = objects.get(0).getClass();
            StringBuilder updateBuilder = new StringBuilder();
            StringBuilder whereBuilder = new StringBuilder();

            List<Field> fieldList = getAllEntityToDBFields(clazz[0], object);
            if (CollectionUtil.isNotEmpty(fieldList)) {
                updateBuilder.append("update ").append(tableName).append(" set ");
            }

            while (clazz[0] != Object.class) {
                fieldList = getAllEntityToDBFields(clazz[0], object);
                if (CollectionUtil.isNotEmpty(fieldList)) {
                    for (Field field : fieldList) {
                        field.setAccessible(true);
                        Object fieldValue = null;
                        try {
                            fieldValue = field.get(object);
                        } catch (IllegalAccessException e) {
                            log.error("批量更新sql语句出错...{}", ExceptionUtil.buildErrorMessage(e));
                        }
                        if (!PRIMARY_KEY_FILED_NAME.equals(field.getName())) {
                            if (fieldValue != null && StringUtils.isNotEmpty(fieldValue.toString())) {
                                updateBuilder.append(field.getName());
                                updateBuilder.append("=");
                                if (field.getType().equals(String.class)) {
                                    updateBuilder.append("'");
                                    updateBuilder.append(fieldValue);
                                    updateBuilder.append("'");
                                } else {
                                    updateBuilder.append(fieldValue);
                                }
                                updateBuilder.append(",");
                            }
                        } else {
                            if (fieldValue == null || StringUtils.isEmpty(fieldValue.toString())) {
                                throw new RuntimeException("主键ID的值不能为空....");
                            }
                            whereBuilder.append(" where ");
                            whereBuilder.append(field.getName());
                            whereBuilder.append("='");
                            whereBuilder.append(fieldValue);
                            whereBuilder.append("' ;");
                        }
                    }
                }
                clazz[0] = clazz[0].getSuperclass();
            }

            if (updateBuilder.toString().endsWith(",")) {
                updateBuilder.deleteCharAt(updateBuilder.length() - 1);
            }

            updateBuilder.append(whereBuilder);
            sqlStrings.add(updateBuilder.toString());

        });
        return sqlStrings;
    }

    /**
     * 实体的属性必须都不为空或者数据统一
     *
     * @param objects 实体对象集合
     * @return SqlAndParamBean
     * @throws IllegalArgumentException 不合法的参数异常
     */
    public SqlAndParamBean generateBatchUpdateSqlAndParam(List<?> objects) throws IllegalArgumentException {
        Class<?> objectClass = objects.get(0).getClass();
        final String tableName = getTableNameByClass(objectClass);
        List<Object[]> parameters = new ArrayList<>();
        String sqlString = "";

        for (Object object : objects) {
            List<Object> values = new ArrayList<>();
            List<Object> whereCondition = new ArrayList<>();
            StringBuilder whereBuilder = new StringBuilder();
            StringBuilder updateBuilder = new StringBuilder();
            List<Field> fieldList = getAllEntityToDBFields(objectClass, object);

            if (CollectionUtil.isNotEmpty(fieldList)) {
                updateBuilder.append("update ").append(tableName).append(" set ");
            }

            while (objectClass != Object.class) {
                fieldList = getAllEntityToDBFields(objectClass, object);

                if (CollectionUtil.isNotEmpty(fieldList)) {
                    fieldList.forEach(field -> {
                        field.setAccessible(true);
                        Object fieldValue = null;
                        try {
                            fieldValue = field.get(object);
                        } catch (IllegalAccessException e) {
                            log.error("批量更新sql语句出错... {}", ExceptionUtil.buildErrorMessage(e));
                        }
                        if (!PRIMARY_KEY_FILED_NAME.equals(field.getName())) {
                            if (fieldValue != null && StringUtils.isNotEmpty(fieldValue.toString())) {
                                updateBuilder.append(field.getName());
                                updateBuilder.append("=?");
                                updateBuilder.append(",");
                                values.add(fieldValue);
                            }
                        } else {

                            if (Objects.isNull(fieldValue) || StringUtils.isEmpty(fieldValue.toString())) {
                                throw new RuntimeException("主键ID的值不能为空....");
                            }

                            whereBuilder.append(" where ");
                            whereBuilder.append(field.getName());
                            whereBuilder.append("=");
                            whereBuilder.append("?");
                            whereBuilder.append(" ;");
                            whereCondition.add(fieldValue);
                        }

                    });
                }
                objectClass = objectClass.getSuperclass();
            }

            if (updateBuilder.toString().endsWith(",")) {
                updateBuilder.deleteCharAt(updateBuilder.length() - 1);
            }
            updateBuilder.append(whereBuilder);
            sqlString = updateBuilder.toString();
            values.addAll(whereCondition);
            parameters.add(values.toArray());
        }

        return new SqlAndParamBean(sqlString, null, parameters);
    }

    /**
     * 组装对象所有字段
     *
     * @param objects 实体对象集合
     * @return SqlAndParamBean
     * @throws IllegalArgumentException 不合法的参数异常
     */
    public SqlAndParamBean generateBatchUpdateSqlAndAllParam(List<?> objects) throws IllegalArgumentException {
        final Class<?>[] clazz = {objects.get(0).getClass()};
        final String tableName = getTableNameByClass(clazz[0]);
        List<Object[]> parameters = new ArrayList<>();
        AtomicReference<String> sqlString = new AtomicReference<>("");

        objects.forEach(object -> {
            clazz[0] = objects.get(0).getClass();
            List<Object> values = new ArrayList<>();
            List<Object> whereCondition = new ArrayList<>();
            StringBuilder whereBuilder = new StringBuilder();
            StringBuilder updateBuilder = new StringBuilder();
            List<Field> fieldList = getAllEntityToDBFields(clazz[0], object);

            if (CollectionUtil.isNotEmpty(fieldList)) {
                updateBuilder.append("update ").append(tableName).append(" set ");
            }

            while (clazz[0] != Object.class) {
                fieldList = getAllEntityToDBFields(clazz[0], object);

                if (CollectionUtil.isNotEmpty(fieldList)) {
                    fieldList.forEach(field -> {
                        field.setAccessible(true);
                        Object fieldValue = null;
                        try {
                            fieldValue = field.get(object);
                        } catch (IllegalAccessException e) {
                            log.error("批量更新sql语句出错... {}", ExceptionUtil.buildErrorMessage(e));
                        }
                        if (!PRIMARY_KEY_FILED_NAME.equals(field.getName())) {
                            updateBuilder.append(field.getName());
                            updateBuilder.append("=?");
                            updateBuilder.append(",");
                            values.add(fieldValue);
                        } else {

                            if (fieldValue == null || StringUtils.isEmpty(fieldValue.toString())) {
                                throw new RuntimeException("主键ID的值不能为空....");
                            }

                            whereBuilder.append(" where ");
                            whereBuilder.append(field.getName());
                            whereBuilder.append("=");
                            whereBuilder.append("?");
                            whereBuilder.append(" ;");
                            whereCondition.add(fieldValue);
                        }

                    });
                }
                clazz[0] = clazz[0].getSuperclass();
            }

            if (updateBuilder.toString().endsWith(",")) {
                updateBuilder.deleteCharAt(updateBuilder.length() - 1);
            }
            updateBuilder.append(whereBuilder);
            sqlString.set(updateBuilder.toString());

            values.addAll(whereCondition);
            parameters.add(values.toArray());
        });

        return new SqlAndParamBean(sqlString.get(), null, parameters);
    }

    /**
     * 创建LIKE语句中的值，创建的结果为：
     *
     * <pre>
     * 1、LikeType.StartWith: %value
     * 2、LikeType.EndWith: value%
     * 3、LikeType.Contains: %value%
     * </pre>
     * <p>
     * 如果withLikeKeyword为true，则结果为：
     *
     * <pre>
     * 1、LikeType.StartWith: LIKE %value
     * 2、LikeType.EndWith: LIKE value%
     * 3、LikeType.Contains: LIKE %value%
     * </pre>
     *
     * @param value           被查找值
     * @param likeType        LIKE值类型 {@link Condition.LikeType}
     * @param withLikeKeyword 是否包含LIKE关键字
     * @return 拼接后的like值
     */
    public String buildLikeValue(String value, Condition.LikeType likeType, boolean withLikeKeyword) {
        if (null == value) {
            return null;
        }

        StringBuilder likeValue = StringUtils.builder(withLikeKeyword ? "LIKE " : "");
        switch (likeType) {
            case StartWith:
                likeValue.append(value).append('%');
                break;
            case EndWith:
                likeValue.append('%').append(value);
                break;
            case Contains:
                likeValue.append('%').append(value).append('%');
                break;

            default:
                break;
        }
        return likeValue.toString();
    }


    /**
     * 构建组合条件<br>
     * 例如：name = ? AND type IN (?, ?) AND other LIKE ?
     *
     * @param conditions 条件对象
     * @return 构建后的SQL语句条件部分
     */
    private String buildCondition(String logicalOperator, Condition... conditions) {
        if (null == conditions || conditions.length <= 0) {
            return "";
        }
        if (null == logicalOperator) {
            logicalOperator = "and";
        }

        final StringBuilder conditionStrBuilder = new StringBuilder();
        boolean isFirst = true;
        for (Condition condition : conditions) {

            if (Objects.isNull(condition) && Objects.nonNull(condition.getValue()))
                continue;

            // 添加逻辑运算符
            if (isFirst) {
                isFirst = false;
            } else {
                // " AND " 或者 " OR "
                conditionStrBuilder.append(" ").append(logicalOperator).append(" ");
            }

            // 构建条件部分："name = ?"、"name IN (?,?,?)"、"name BETWEEN ？AND ？"、"name LIKE ?"
            conditionStrBuilder.append(condition.toString(null));
        }

        return conditionStrBuilder.toString();
    }

    /**
     * 创建Blob对象
     *
     * @param conn {@link Connection}
     * @param data 数据
     * @return {@link Blob}
     * @since 4.5.13
     */
    public Blob createBlob(Connection conn, byte[] data) {
        Blob blob;
        try {
            blob = conn.createBlob();
            blob.setBytes(0, data);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return blob;
    }

    /**
     * 转换为{@link java.sql.Date}
     *
     * @param date {@link java.util.Date}
     * @return {@link java.sql.Date}
     * @since 3.1.2
     */
    public java.sql.Date toSqlDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * 转换为{@link java.sql.Timestamp}
     *
     * @param date {@link java.util.Date}
     * @return {@link java.sql.Timestamp}
     * @since 3.1.2
     */
    public java.sql.Timestamp toSqlTimestamp(java.util.Date date) {
        return new java.sql.Timestamp(date.getTime());
    }


    /**
     * @param object 实体对象
     * @return 返回保存属性名和值得map
     * @throws IllegalArgumentException 不合法的参数异常
     * @throws IllegalAccessException   没有访问权限的异常
     * @decription 通过object反射获取字段名和和字段值，用map一一对应
     */
    public static Map<String, Object> getAllFields(Object object) throws IllegalArgumentException, IllegalAccessException {
        Map<String, Object> fieldAndValue = new HashMap<>();
        Class<?> class1 = object.getClass();
        while (class1 != Object.class) {
            Field[] fields = class1.getDeclaredFields();
            for (Field field : fields) {
                String name = field.getName();
                field.setAccessible(true);
                Object value = field.get(object);
                fieldAndValue.put(name, value);
            }
            class1 = class1.getSuperclass();
        }
        return fieldAndValue;
    }

    /**
     * 根据实体对象获取对应属性和值的Map对象
     *
     * @param object             实体对象
     * @param isnullValuesReturn 是否返回空值属性
     * @return Map结合
     * @throws IllegalArgumentException 不合法的参数异常
     * @throws IllegalAccessException   没有访问权限的异常
     */
    public Map<String, Object> getAllFields(Object object, boolean isnullValuesReturn) throws IllegalArgumentException, IllegalAccessException {
        Map<String, Object> fieldAndValue = new HashMap<>();
        Class<?> class1 = object.getClass();
        while (class1 != Object.class) {
            Field[] fields = class1.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())
                        && ReflectionUtil.isBaseType(field) && null == field.getAnnotation(Ignore.class) && null != field.get(object)) {
                    String name = field.getName();
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if (isnullValuesReturn) {
                        fieldAndValue.put(name, value);
                    } else if (null != value) {
                        fieldAndValue.put(name, value);
                    }
                }
            }
            class1 = class1.getSuperclass();
        }
        return fieldAndValue;
    }

    /**
     * 根据实体对象获取对应属性和值的Map对象
     * @param object 实体对象
     * @return Map结合
     * @throws IllegalArgumentException 不合法的参数异常
     * @throws IllegalAccessException 没有访问权限的异常
     */
    public Map<String, Object> getNewAllFields(Object object) throws IllegalArgumentException, IllegalAccessException {
        Map<String, Object> fieldAndValue = new HashMap<>();
        Class<?> class1 = object.getClass();
        while (class1 != Object.class) {
            Field[] fields = class1.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())
                        && ReflectionUtil.isBaseType(field) && null == field.getAnnotation(Ignore.class)) {
                    String name = field.getName();
                    field.setAccessible(true);
                    Object value = field.get(object);
                    fieldAndValue.put(name, value);
                }
            }
            class1 = class1.getSuperclass();
        }
        return fieldAndValue;
    }

    /**
     * 获取属性值不为null的数据库字段集合
     *
     * @param clazz  实体对象的类型
     * @param object 实体对象
     * @return List
     */
    public List<Field> getAllEntityToDBFields(Class<?> clazz, Object object) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> fieldList = Arrays.stream(fields).collect(Collectors.toList());
        return fieldList.stream().filter(field -> {
            try {
                field.setAccessible(true);
                return ReflectionUtil.isBaseType(field) && !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) && Modifier.isPrivate(field.getModifiers()) && null == field.getAnnotation(Ignore.class) && null != field.get(object);
            } catch (IllegalAccessException e) {
                log.error("根据实体获取数据的字段错误：{}", ExceptionUtil.buildErrorMessage(e));
            }
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * 跟实体类返回对那个的表名，只支持表名下划线并且前缀是tbl_对应的实体是驼峰命名规则
     *
     * @param clazz 实体对象的类型
     * @return 表名
     */
    public String getTableNameByClass(Class<?> clazz) {
        String tableName = "";

        Table table = clazz.getAnnotation(Table.class);
        if (table != null) {
            tableName = table.name();
        }
        if (StringUtils.isEmpty(tableName)) {
            if (table != null) {
                Method[] methods = table.annotationType().getDeclaredMethods();
                for (Method method : methods) {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    try {
                        tableName = (String) method.invoke(table);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        log.error(ExceptionUtil.buildErrorMessage(e));
                    }
                }
            }
        }

        if (StringUtils.isEmpty(tableName)) {
            tableName = TABLE_PREFIX + StringUtils.HumpToUnderline(clazz.getSimpleName());
        }

        return tableName;
    }

    /**
     * @param objects 实体对象集合
     * @return 二维数组
     * @description 将二维数组的行列对换
     */
    public List<Object[]> transForm(List<Object[]> objects) {
        List<Object[]> result = new ArrayList<>();
        Object[] objectsTmp = objects.get(0);
        for (int i = 0; i < objectsTmp.length; i++) {
            result.add(new Object[objects.size()]);
        }

        for (int i = 0; i < objects.size(); i++) {
            Object[] objects2 = objects.get(i);
            for (int j = 0; j < objects2.length; j++) {
                result.get(j)[i] = objects2[j];
            }
        }
        return result;
    }

    public String generateInCon(List<?> cons) {
        if (cons == null) {
            return "''";
        }
        StringBuilder result = new StringBuilder();
        for (Object object : cons) {
            if (object != null) {
                result.append("'").append(object.toString()).append("',");
            }
        }
        return "".equals(result.toString().trim()) ? "''" : result.substring(0, result.length() - 1);
    }
}
