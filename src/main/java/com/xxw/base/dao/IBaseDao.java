package com.xxw.base.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface IBaseDao<T> {

    /**
     * 批量插入
     * @param connection 条件
     * @param domains 要插入的数据
     * @return 返回被影响的行数
     * @throws Exception 异常
     */
    int insertBatch(Connection connection, List<T> domains) throws Exception;

    int insertBatch(List<T> domains) throws Exception;

    int insert(T domain) throws Exception;

    /**
     * 根据多个条件来删除
     * @param conditions  条件
     * @return 返回被影响的行数
     * @throws Exception 异常
     */
    int deleteByConditions(Connection connection, Map<String, Object> conditions) throws Exception;

    int deleteByConditions(Map<String, Object> conditions) throws Exception;

    /**
     * 根据多个条件来查询
     * @param conditions  条件
     * @return 返回数据
     * @throws Exception 异常
     */
    List<T> searchByConditionsWithoutConnection(Map<String, Object> conditions)throws Exception;

    T searchOneByConditionsWithoutConnection(Map<String, Object> conditions)throws Exception;

    /**
     * 根据某个字段在某个范围内进行查询
     * @param field 要查询的字段
     * @param fieldValues 要字段值集合
     * @return 返回数据
     * @throws Exception 异常
     */
    List<T> batchSearchByFieldWithoutConnection(String field, List<?> fieldValues)throws Exception;

    /**
     * 根据某个条件批量删除
     */
    int batchDeleteByParam(Connection connection, String field, List<String> params) throws Exception;

    int batchDeleteByParam(String field, List<String> params) throws Exception;

    /**
     * @param domain  实体bean
     * @param primaryFiledName  主键
     */
    int update(T domain, String primaryFiledName) throws Exception;

    /**
     * @param domain  实体bean
     * @param primaryFiledName  主键
     */
    int update(Connection connection, T domain, String primaryFiledName) throws Exception;

    /**
     * 主键为id
     * @param domain
     * @return
     * @throws Exception
     */
    int batchUpdate(List<T> domain) throws Exception;

    /**
     * 主键为id
     * @param domain
     * @return
     * @throws Exception
     */
    int batchUpdate(Connection connection, List<T> domain) throws Exception;


    /**
     * 主键为id
     */
    int batchUpdateAllField(Connection connection, List<T> domain) throws Exception;

    /**
     * 主键为id
     */
    int batchUpdateAllField(T domain, String primaryFiledName) throws Exception;
}
