package com.xxw.base.dao;

import com.xxw.base.util.DatasourceUtil;
import com.xxw.base.util.ExceptionUtil;
import com.xxw.base.util.SqlUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
public abstract class BaseDaoImpl<T> implements IBaseDao<T> {

    @Override
    public int insertBatch(Connection connection, List<T> domains) throws Exception {
        if (CollectionUtils.isEmpty(domains)) {
            return -1;
        }
        int batch = BaseDao.insertBatch(connection, getTableName(), domains);
        if (!Objects.equals(domains.size(), batch)) {
            return -1;
        }
        return BigInteger.ZERO.intValue();
    }

    @Override
    public int insert(T domain) throws Exception {
        if(Objects.isNull(domain)) {
            return -1;
        }
        int insert = BaseDao.saveObject(domain, getTableName());
        if (!Objects.equals(BigInteger.ONE.intValue(), insert)) {
            return -1;
        }
        return BigInteger.ZERO.intValue();
    }

    @Override
    public int insertBatch(List<T> domains) throws Exception {
        if (CollectionUtils.isEmpty(domains)) {
            return -1;
        }
        int insert = BaseDao.insertBatch(domains, getTableName());
        if (!Objects.equals(domains.size(), insert)) {
            return -1;
        }
        return BigInteger.ZERO.intValue();
    }

    @Override
    public int deleteByConditions(Connection connection, Map<String, Object> conditions) throws Exception {
        int id = BaseDao.deleteHard(connection, getTableName(), conditions);
        if (id > BigInteger.ZERO.intValue()) {
            return 0;
        }
        return -1;
    }

    @Override
    public int deleteByConditions(Map<String, Object> conditions) throws Exception {
        int id = BaseDao.deleteHard(getTableName(), conditions);
        if (id > BigInteger.ZERO.intValue()) {
            return 0;
        }
        return -1;
    }

    @Override
    public List<T> searchByConditionsWithoutConnection(Map<String, Object> conditions) throws Exception {
        return JSON.parseArray(JSON.toJSONString(BaseDao.search(getTableName(), conditions)), getActualTypeArgument(this.getClass()));
    }

    @Override
    public T searchOneByConditionsWithoutConnection(Map<String, Object> conditions) throws Exception {
        List<T> list = JSON.parseArray(JSON.toJSONString(BaseDao.search(getTableName(), conditions)), getActualTypeArgument(this.getClass()));
        if (list != null && list.size() != 0) {
            return list.get(0);
        }
        return null;
    }


    @Override
    public List<T> batchSearchByFieldWithoutConnection(String field, List<?> fieldValues) throws Exception {
        StringBuilder fieldValuesArr = new StringBuilder();
        fieldValues.forEach(id -> {
            fieldValuesArr.append("?");
            fieldValuesArr.append(" ");
            fieldValuesArr.append(",");
        });
        fieldValuesArr.deleteCharAt(fieldValuesArr.length() - 1);
        StringBuffer preSql = new StringBuffer();
        preSql.append("SELECT * FROM ")
                .append(getTableName())
                .append("  WHERE ")
                .append(field.toLowerCase())
                .append(" IN (")
                .append(fieldValuesArr)
                .append(")");
        return BaseDao.searchBySql(preSql.toString(), fieldValues.toArray(), getActualTypeArgument(this.getClass()));
    }

    /*
     * 获取泛型类Class对象，不是泛型类则返回null
     */
    private Class<T> getActualTypeArgument(Class<?> clazz) {
        Class<T> entitiClass = null;
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass)
                    .getActualTypeArguments();
            if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                entitiClass = (Class<T>) actualTypeArguments[0];
            }
        }

        return entitiClass;
    }

    @Override
    public int batchDeleteByParam(Connection connection, String field, List<String> params) throws Exception{
        return BaseDao.deleteBatchHard(connection, SqlUtil.generateBatchDeleteSqlForTableName(getTableName(), field, params), params);
    }

    @Override
    public int batchDeleteByParam(String field, List<String> params) throws Exception{
        return BaseDao.deleteBatchHard(getConnection(), SqlUtil.generateBatchDeleteSqlForTableName(getTableName(), field, params), params);
    }


    @Override
    public int update(T domain, String primaryFiledName) throws Exception {
        return BaseDao.updateObject(domain, primaryFiledName, getTableName());
    }

    @Override
    public int update(Connection connection, T domain, String primaryFiledName) throws Exception {
        return BaseDao.updateObject(connection, domain, primaryFiledName, getTableName());
    }

    @Override
    public int batchUpdate(List<T> domain) throws Exception {
        return BaseDao.batchUpdate(domain);
    }

    @Override
    public int batchUpdate(Connection connection, List<T> domain) throws Exception {
        return BaseDao.batchUpdate(connection, domain);
    }


    @Override
    public int batchUpdateAllField(Connection connection, List<T> domain) throws Exception {
        return BaseDao.batchAllUpdate(connection, domain);
    }

    @Override
    public int batchUpdateAllField(T domain, String primaryFiledName) throws Exception {
        return BaseDao.updateObjectAllField(domain, primaryFiledName, getTableName());
    }

    protected Connection getConnection() {
        Connection connection = null;
        try {
            connection = DatasourceUtil.getInstance().getDataSource().getConnection();
        } catch (SQLException e) {
            log.error(ExceptionUtil.buildErrorMessage(e));
        }
        return connection;
    }


    protected abstract String getTableName();
}
