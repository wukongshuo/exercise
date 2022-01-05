package com.xxw.base.util;

import lombok.experimental.UtilityClass;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @标题 CollectionUtil
 * @作者 ivan
 * @描述 集合工具类
 * @注意
 * @时间 2020年3月17日 上午9:49:04
 */
@UtilityClass
public class CollectionUtil {
    /**
     * @param src
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @Description List集合的深度复制
     * @notice
     */
    public <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        return (List<T>) in.readObject();
    }

    /**
     * @param collection
     * @return
     * @Description 判断是否为空
     * @notice
     */
    public Boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    /**
     * @param collections
     * @return
     * @Description 判断是否都不为空
     * @notice
     */
    public Boolean isNoneEmpty(Collection<?>... collections) {
        for (Collection<?> collection : collections) {
            if (null == collection || collection.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param collections
     * @return
     * @Description 判断是否有一个为空
     * @notice
     */
    public Boolean isAnyEmpty(Collection<?>... collections) {
        return !isNoneEmpty(collections);
    }

    /**
     * @param collection
     * @return
     * @Description 判断是否非空
     * @notice
     */
    public Boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public boolean isArray(Object obj) {
        if (null == obj) {
            return false;
        }
        return obj.getClass().isArray();
    }


    /**
     * Map转List
     *
     * @param map
     * @param <T>
     * @return
     */
    public <T> List<T> mapToList(Map<String, T> map) {
        return new LinkedList<>(map.values());
    }

    /**
     * Map转数组
     *
     * @param map
     * @param <T>
     * @return
     */
    public <T> Object[] mapToArray(Map<String, T> map) {
        List<T> list = mapToList(map);
        Object[] arr = new Object[list.size()];
        return list.toArray(arr);
    }
}
