package com.xxw.base.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * SQL排序对象
 * @author ivan
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {


    private static final long serialVersionUID = 1837941818453048784L;

    /** 排序的字段 */
    private String field;

    /** 排序方式（正序还是反序） */
    private Direction direction;

}
