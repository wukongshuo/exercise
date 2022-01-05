package com.xxw.base.query;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 查询对象，用于传递查询所需的字段值<br>
 * 查询对象根据表名（可以多个），多个条件 {@link Condition} 构建查询对象完成查询。<br>
 * 如果想自定义返回结果，则可在查询对象中自定义要查询的字段名，分页{@link PageResult}信息来自定义结果。
 *
 * @author ivan
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Query {

    /** 查询的字段名列表 */
    Collection<String> fields;

    /** 查询的表名 */
    String[] tableNames;

    /** 查询的条件语句 */
    Condition[] wheres;

    /** 分页对象 */
    Page page;

    Order[] orders;

    public static void main(String[] args) {
        Query query = new Query();

        List fileds = new ArrayList<>();

        fileds.add("*");

        query.setFields(fileds);

        Order[] orders = new Order[1];
        orders[0] = new Order("sequence", Direction.ASC);

        query.setOrders(orders);

        query.setPage(new Page(1,10));

        //query.setTableNames("tbl_");

        Condition[] conditions = new Condition[5];
        conditions[0] = new Condition("status", "=", "1");
        conditions[1] = new Condition("resourceName", "1", Condition.LikeType.Contains);
        conditions[2] = new Condition("type", "=", "1");
        conditions[3] = new Condition("createTime", ">=", "2018-09-10 10:10:10");
        conditions[4] = new Condition("createTime", "<=", "2020-09-10 10:10:10");

        query.setWheres(conditions);

        String  json = JSON.toJSONString(query);

        System.out.println(json);

    }

}
