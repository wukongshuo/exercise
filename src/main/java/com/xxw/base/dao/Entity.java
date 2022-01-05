package com.xxw.base.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Entity<T> {

    /**
     * 主键id
     */
    private String id;

    /**
     * 创建者
     **/
    private String creater;

    /**
     * 创建时间
     **/
    private String createTime;

    /**
     * 最后一次修改人
     */
    private String updater;

    /**
     * 最后一次修改时间
     */
    private String updateTime;

    public Entity(String id) {
        this.id = id;
    }

}
