package com.xxw.base.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * java常用对象工具类
 *
 * @author ivan
 */
@Slf4j
@UtilityClass
public class JavaBeanUtil {

    public Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null) return null;

        Object obj = beanClass.newInstance();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }

            field.setAccessible(true);
            field.set(obj, map.get(field.getName()));
        }

        return obj;
    }

    public Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }

        return map;
    }

    /**
     * 实体类转map
     *
     * @param obj 实体对象
     * @return Map
     */
    public Map<String, Object> convertBeanToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    if (null == value) {
                        map.put(key, "");
                    } else {
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            log.error("convertBean2Map Error {}", ExceptionUtil.buildErrorMessage(e));
        }
        return map;
    }


    /**
     * map 转实体类
     *
     * @param clazz 实体类
     * @param map   Map对象
     * @param <T>   实体类型
     * @return 实体对象
     */
    public <T> T convertMapToBean(Class<T> clazz, Map<String, Object> map) {
        T obj = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            obj = clazz.newInstance(); // 创建 JavaBean 对象


            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();
                if (map.containsKey(propertyName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    Object value = map.get(propertyName);
                    if ("".equals(value)) {
                        value = null;
                    }
                    Object[] args = new Object[1];
                    args[0] = value;
                    descriptor.getWriteMethod().invoke(obj, args);


                }
            }
        } catch (IllegalAccessException e) {
            log.error("convertMapToBean 实例化JavaBean失败 Error{}", ExceptionUtil.buildErrorMessage(e));
        } catch (IntrospectionException e) {
            log.error("convertMapToBean 分析类属性失败 Error{}", ExceptionUtil.buildErrorMessage(e));
        } catch (IllegalArgumentException e) {
            log.error("convertMapToBean 映射错误 Error{}", ExceptionUtil.buildErrorMessage(e));
        } catch (InstantiationException e) {
            log.error("convertMapToBean 实例化 JavaBean 失败 Error{}", ExceptionUtil.buildErrorMessage(e));
        } catch (InvocationTargetException e) {
            log.error("convertMapToBean字段映射失败 Error{}", ExceptionUtil.buildErrorMessage(e));
        } catch (Exception e) {
            log.error("convertMapToBean Error{}", ExceptionUtil.buildErrorMessage(e));
        }
        return obj;
    }

    //将map通过反射转化为实体
    public Object MapToModel(Map<String, Object> map, Object o) throws Exception {
        if (!map.isEmpty()) {
            for (String k : map.keySet()) {
                Object v = null;
                if (!k.isEmpty()) {
                    v = map.get(k);
                }
                Field[] fields;
                fields = o.getClass().getDeclaredFields();
                String clzName = o.getClass().getSimpleName();
                for (Field field : fields) {
                    int mod = field.getModifiers();
                    if (field.getName().toUpperCase().equals(k.toUpperCase())) {
                        field.setAccessible(true);
                        //region--进行类型判断
                        String type = field.getType().toString();
                        if (type.endsWith("String")) {
                            if (v != null) {
                                v = String.valueOf(v);
                            } else {
                                v = "";
                            }
                        }
                        if (type.endsWith("Date")) {
                            v = DateUtil.strToDate(String.valueOf(v), DateUtil.STRIPING_YMD_COLON_HM);
                        }
                        if (type.endsWith("Boolean")) {
                            v = Boolean.getBoolean(String.valueOf(v));
                        }
                        if (type.endsWith("int")) {
                            v = new Integer(String.valueOf(v));
                        }
                        if (type.endsWith("Long")) {
                            v = new Long(String.valueOf(v));
                        }
                        //endregion
                        field.set(o, v);
                    }
                }


            }
        }
        return o;
    }

    /**
     * 实体对象转成Map
     *
     * @param obj 实体对象
     * @return Map
     */
    public Map<String, Object> object2Map(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Map转成实体对象
     *
     * @param map   map实体对象包含属性
     * @param clazz 实体对象类型
     * @return Object
     */
    public Object map2Object(Map<String, Object> map, Class<?> clazz) {
        if (map == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = clazz.newInstance();


            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public <T> List<T> resultSetToList(Class<T> clazz, ResultSet resultSet) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        // 创建一个对应的空的泛型集合
        List<T> list = new ArrayList<>();

        // 获得该类所有自己声明的字段，不问访问权限.所有。所有。所有
        Field[] fields = clazz.getDeclaredFields();
        // 大家熟悉的操作，不用多说
        if (resultSet != null) {
            while (resultSet.next()) {
                // 创建实例
                T t = clazz.newInstance();
                // 赋值
                for (int i = 1; i <= fields.length; i++) {
                    // 获得列的标题(列名）
                    String columnLabel = resultSet.getMetaData().getColumnLabel(i);
                    /*
                     * fs[i].getName()：获得字段名
                     *
                     * f:获得的字段信息
                     */
                    Field field = t.getClass().getDeclaredField(fields[i].getName());
                    // 参数true 可跨越访问权限进行操作
                    field.setAccessible(true);

                    //根据此属性的类型来调用相应的结果集方法，
                    // 如：String ename --> resultSet.getString()
                    Object value = null;
                    Class<?> fieldType = field.getType();

                    if (fieldType == String.class) { //Character
                        value = resultSet.getString(columnLabel);

                    } else if (fieldType == Byte.class || fieldType == Byte.TYPE) { // int
                        value = resultSet.getByte(columnLabel);

                    } else if (fieldType == Short.class || fieldType == Short.TYPE) { //
                        value = resultSet.getShort(columnLabel);

                    } else if (fieldType == Integer.class || fieldType == Integer.TYPE) { //
                        value = resultSet.getInt(columnLabel);

                    } else if (fieldType == Long.class || fieldType == Long.TYPE) { //
                        value = resultSet.getLong(columnLabel);

                    } else if (fieldType == Double.class || fieldType == Double.TYPE) { //
                        value = resultSet.getDouble(columnLabel);

                    } else if (fieldType == Float.class || fieldType == Float.TYPE) { //
                        value = resultSet.getFloat(columnLabel);

                    } else if (fieldType == java.util.Date.class || fieldType == java.sql.Date.class) { //
                        value = resultSet.getDate(columnLabel);

                    } else if (fieldType == java.sql.Time.class) { //
                        value = resultSet.getTime(columnLabel);

                    } else if (fieldType == Boolean.class || fieldType == Boolean.TYPE) { //
                        value = resultSet.getBoolean(columnLabel);
                    }
                    field.set(t, value);

                }

                list.add(t);
            }
        }
        // 返回结果
        return list;
    }

    public <T> List<T> resultSetToList(ResultSet rs, Class<T> clazz) throws Exception {
        List<T> tList = new ArrayList<>();
        T t;
        while (rs.next()) {
            t = clazz.newInstance();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String name = rsMetaData.getColumnName(i);
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                field.set(t, rs.getObject(name));
            }
            tList.add(t);
        }
        return tList;
    }

}
