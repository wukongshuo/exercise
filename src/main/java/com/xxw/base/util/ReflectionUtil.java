package com.xxw.base.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 反射的 Utils 函数集合
 * 提供访问私有变量, 获取泛型类型 Class, 提取集合中元素属性等 Utils 函数
 *
 * @author ivan
 */
@Slf4j
@UtilityClass
public class ReflectionUtil {
    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     *
     * @param object
     * @param fieldName
     * @return
     */
    public Object getFieldValue(Object object, String fieldName) {
        Field field = getDeclaredField(object, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }

        makeAccessible(field);

        Object result = null;

        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            log.error("getFieldValue: {}", ExceptionUtil.buildErrorMessage(e));
        }

        return result;
    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     *
     * @param object
     * @param fieldName
     * @param value
     */
    public void setFieldValue(Object object, String fieldName, Object value) {
        Field field = getDeclaredField(object, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }

        makeAccessible(field);

        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            log.error("setFieldValue: {}", ExceptionUtil.buildErrorMessage(e));
        }
    }


    /**
     * 通过反射, 获得定义 Class 时声明的父类的泛型参数的类型
     * 如: public EmployeeDao extends BaseDao<Employee, String>
     *
     * @param clazz
     * @param index
     * @return
     */
    public Class getSuperClassGenricType(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }

        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }

    /**
     * 通过反射, 获得 Class 定义中声明的父类的泛型参数类型
     * 如: public EmployeeDao extends BaseDao<Employee, String>
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public <T> Class<T> getSuperGenericType(Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredMethod
     *
     * @param object
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {

        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                //Method 不在当前类定义, 继续向上转型
            }
        }

        return null;
    }

    /**
     * 使 filed 变为可访问
     *
     * @param field
     */
    public void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param object
     * @param filedName
     * @return
     */
    public Field getDeclaredField(Object object, String filedName) {

        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(filedName);
            } catch (NoSuchFieldException e) {
                //Field 不在当前类定义, 继续向上转型
            }
        }
        return null;
    }

    /**
     * 根据属性名获取属性值
     *
     * @param fieldName
     * @param object
     * @return
     */
    public String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            Field field = getDeclaredField(object, fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            return  (String)field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected)
     *
     * @param object
     * @param methodName
     * @param parameterTypes
     * @param parameters
     * @return
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     */
    public Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws InvocationTargetException {

        Method method = getDeclaredMethod(object, methodName, parameterTypes);

        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
        }

        method.setAccessible(true);

        try {
            return method.invoke(object, parameters);
        } catch (IllegalAccessException e) {
            log.error("invokeMethod: {}", ExceptionUtil.buildErrorMessage(e));
        }

        return null;
    }

    /**
     * 获取包名
     *
     * @return 包名【String类型】
     */
    public String getPackage(Class<?> clazz) {
        Package pck = clazz.getPackage();
        if (null != pck) {
            return pck.getName();
        } else {
            return "没有包！";
        }
    }

    /**
     * 获取继承的父类的全类名
     *
     * @return 继承的父类的全类名【String类型】
     */
    public String getSuperClassName(Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        if (null != superClass) {
            return superClass.getName();
        } else {
            return "没有父类！";
        }
    }

    /**
     * 获取全类名
     *
     * @return 全类名【String类型】
     */
    public String getClassName(Class<?> clazz) {
        return clazz.getName();
    }

    /**
     * 获取实现的接口名
     *
     * @return 所有的接口名【每一个接口名的类型为String，最后保存到一个List集合中】
     */
    public List<String> getInterfaces(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        int len = interfaces.length;

        List<String> list = new ArrayList<String>();
        for (Class<?> iffy : interfaces) {
            // 接口名
            String interfaceName = iffy.getSimpleName();

            list.add(interfaceName);
        }

        return list;
    }

    /**
     * 获取所有属性
     *
     * @return 所有的属性【每一个属性添加到StringBuilder中，最后保存到一个List集合中】
     */
    public List<StringBuilder> getFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        return getStringBuilders(fields);
    }

    private static List<StringBuilder> getStringBuilders(Field[] fields) {
        int len = fields.length;

        List<StringBuilder> list = new ArrayList<>();
        StringBuilder sb = null;
        for (int i = 0; i < len; i++) {
            Field field = fields[i];
            sb = new StringBuilder();

            // 修饰符
            String modifier = Modifier.toString(field.getModifiers());
            sb.append(modifier + " ");

            // 数据类型
            Class<?> type = field.getType();
            String typeName = type.getSimpleName();
            sb.append(typeName + " ");

            // 属性名
            String fieldName = field.getName();
            sb.append(fieldName + ";");

            list.add(sb);
        }

        return list;
    }

    /**
     * 获取所有公共的属性
     *
     * @return 所有公共的属性【每一个属性添加到StringBuilder中，最后保存到一个List集合中】
     */
    public List<StringBuilder> getPublicFields(Class<?> clazz) {
        Field[] fields = clazz.getFields();
        return getStringBuilders(fields);
    }

    /**
     * 获取所有构造方法
     *
     * @return 所有的构造方法【每一个构造方法添加到StringBuilder中，最后保存到一个List集合中】
     */
    public List<StringBuilder> getConstructors(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        int len = constructors.length;

        List<StringBuilder> list = new ArrayList<>();
        StringBuilder sb = null;
        for (int i = 0; i < len; i++) {
            Constructor<?> constructor = constructors[i];
            sb = new StringBuilder();

            // 修饰符
            String modifier = Modifier.toString(constructor.getModifiers());
            sb.append(modifier + " ");

            // 方法名（类名）
            String constructorName = clazz.getSimpleName();
            sb.append(constructorName).append(" (");

            // 形参列表
            getParameterTypes(list, sb, constructor.getParameterTypes());
        }

        return list;
    }

    private static void getParameterTypes(List<StringBuilder> list, StringBuilder sb, Class<?>[] parameterTypes2) {
        Class<?>[] parameterTypes = parameterTypes2;
        int length = parameterTypes.length;
        for (int j = 0; j < length; j++) {
            Class<?> parameterType = parameterTypes[j];

            String parameterTypeName = parameterType.getSimpleName();

            if (j < length - 1) {
                sb.append(parameterTypeName + ", ");
            } else {
                sb.append(parameterTypeName);
            }

        }

        sb.append(") {}");

        list.add(sb);
    }

    /**
     * 获取所有自身的方法
     *
     * @return 所有自身的方法【每一个方法添加到StringBuilder中，最后保存到一个List集合中】
     */
    public List<StringBuilder> getMethods(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        return getStringBuilders(methods);
    }

    private static List<StringBuilder> getStringBuilders(Method[] methods) {
        int len = methods.length;

        List<StringBuilder> list = new ArrayList<>();
        StringBuilder sb;
        for (int i = 0; i < len; i++) {
            Method method = methods[i];
            sb = new StringBuilder();

            // 修饰符
            String modifier = Modifier.toString(method.getModifiers());
            sb.append(modifier + " ");

            // 返回值类型
            Class<?> returnClass = method.getReturnType();
            String returnType = returnClass.getSimpleName();
            sb.append(returnType + " ");

            // 方法名
            String methodName = method.getName();
            sb.append(methodName + " (");

            // 形参列表
            getParameterTypes(list, sb, method.getParameterTypes());
        }

        return list;
    }

    /**
     * 获取所有公共的方法
     *
     * @return 所有公共的方法【每一个方法添加到StringBuilder中，最后保存到一个List集合中】
     */
    public List<StringBuilder> getPublicMethods(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        return getStringBuilders(methods);
    }

    /**
     * 获取所有的注解名
     *
     * @return 所有的注解名【每一个注解名的类型为String，最后保存到一个List集合中】
     */
    public List<String> getAnnotations(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        int len = annotations.length;

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            Annotation annotation = annotations[i];

            String annotationName = annotation.annotationType().getSimpleName();
            list.add(annotationName);
        }

        return list;
    }

    /**
     * 获取父类的泛型
     *
     * @return 父类的泛型【Class类型】
     */
    public Class<?> getSuperClassGenericParameterizedType(Class<?> clazz) {
        Type genericSuperClass = clazz.getGenericSuperclass();

        Class<?> superClassGenericParameterizedType = null;

        // 判断父类是否有泛型
        if (genericSuperClass instanceof ParameterizedType) {
            // 向下转型，以便调用方法
            ParameterizedType pt = (ParameterizedType) genericSuperClass;
            // 只取第一个，因为一个类只能继承一个父类
            Type superClazz = pt.getActualTypeArguments()[0];
            // 转换为Class类型
            superClassGenericParameterizedType = (Class<?>) superClazz;
        }

        return superClassGenericParameterizedType;
    }

    /**
     * 获取接口的所有泛型
     *
     * @return 所有的泛型接口【每一个泛型接口的类型为Class，最后保存到一个List集合中】
     */
    public List<Class<?>> getInterfaceGenericParameterizedTypes(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        int len = genericInterfaces.length;

        List<Class<?>> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            Type genericInterface = genericInterfaces[i];

            // 判断接口是否有泛型
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;

                // 得到所有的泛型【Type类型的数组】
                Type[] interfaceTypes = pt.getActualTypeArguments();

                int length = interfaceTypes.length;

                for (int j = 0; j < length; j++) {
                    // 获取对应的泛型【Type类型】
                    Type interfaceType = interfaceTypes[j];
                    // 转换为Class类型
                    Class<?> interfaceClass = (Class<?>) interfaceType;
                    list.add(interfaceClass);
                }

            }

        }

        return list;
    }

    /**
     * 根据Class类型，获取对应的实例【要求必须有无参的构造器】
     *
     * @return 对应的实例【Object类型】
     */
    public Object getNewInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }

    /**
     * 根据传入的类的Class对象，以及构造方法的形参的Class对象，获取对应的构造方法对象
     *
     * @param clazz          类的Class对象
     * @param parameterTypes 构造方法的形参的Class对象【可以不写】
     * @return 构造方法对象【Constructor类型】
     */
    public Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        return clazz.getDeclaredConstructor(parameterTypes);
    }

    /**
     * 根据传入的构造方法对象，以及，获取对应的实例
     *
     * @param constructor 构造方法对象
     * @param initargs    传入构造方法的实参【可以不写】
     * @return 对应的实例【Object类型】
     */
    public Object getNewInstance(Constructor<?> constructor, Object... initargs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        constructor.setAccessible(true);
        return constructor.newInstance(initargs);
    }

    /**
     * 根据传入的属性名字符串，修改对应的属性值
     *
     * @param clazz 类的Class对象
     * @param name  属性名
     * @param obj   要修改的实例对象
     * @param value 修改后的新值
     */
    public void setField(Class<?> clazz, String name, Object obj, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, value);
    }

    /**
     * 根据传入的方法名字符串，获取对应的方法
     *
     * @param clazz          类的Class对象
     * @param name           方法名
     * @param parameterTypes 方法的形参对应的Class类型【可以不写】
     * @return 方法对象【Method类型】
     */
    public Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        return clazz.getDeclaredMethod(name, parameterTypes);
    }

    /**
     * 根据传入的方法对象，调用对应的方法
     *
     * @param method 方法对象
     * @param obj    要调用的实例对象【如果是调用静态方法，则可以传入null】
     * @param args   传入方法的实参【可以不写】
     * @return 方法的返回值【没有返回值，则返回null】
     */
    public Object invokeMethod(Method method, Object obj, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.setAccessible(true);
        return method.invoke(obj, args);
    }


    /**
     * 根据属性，获取get方法
     *
     * @param ob   对象
     * @param name 属性名
     * @return
     * @throws Exception
     */
    public Object getGetMethod(Object ob, String name) throws Exception {
        Method[] m = ob.getClass().getMethods();
        for (int i = 0; i < m.length; i++) {
            if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                return m[i].invoke(ob);
            }
        }
        return null;
    }

    /**
     * 根据属性，获取get方法
     *
     * @param ob   对象
     * @param name 属性名
     * @return
     * @throws Exception
     */
    public Object getSetMethod(Object ob, String name) throws Exception {
        Method[] m = ob.getClass().getMethods();
        for (int i = 0; i < m.length; i++) {
            if (("set" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                return m[i].invoke(ob);
            }
        }
        return null;
    }


    /**
     * 根据属性，拿到set方法，并把值set到对象中
     *
     * @param obj       对象
     * @param clazz     对象的class
     * @param filedName  需要设置值得属性
     * @param typeClass
     * @param value
     */
    public void setValue(Object obj, Class<?> clazz, String filedName, Class<?> typeClass, Object value) {
        filedName = removeLine(filedName);
        String methodName = "set" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1);
        try {
            Method method = clazz.getDeclaredMethod(methodName, typeClass);
            method.invoke(obj, getClassTypeValue(typeClass, value));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 通过class类型获取获取对应类型的值
     * @param typeClass class类型
     * @param value 值
     * @return Object
     */
    private Object getClassTypeValue(Class<?> typeClass, Object value){
        if(typeClass == int.class  || value instanceof Integer){
            if(null == value){
                return 0;
            }
            return value;
        }else if(typeClass == short.class){
            if(null == value){
                return 0;
            }
            return value;
        }else if(typeClass == byte.class){
            if(null == value){
                return 0;
            }
            return value;
        }else if(typeClass == double.class){
            if(null == value){
                return 0;
            }
            return value;
        }else if(typeClass == long.class){
            if(null == value){
                return 0;
            }
            return value;
        }else if(typeClass == String.class){
            if(null == value){
                return "";
            }
            return value;
        }else if(typeClass == boolean.class){
            if(null == value){
                return true;
            }
            return value;
        }else if(typeClass == BigDecimal.class){
            if(null == value){
                return new BigDecimal(0);
            }
            return new BigDecimal(value+"");
        }else {
            return typeClass.cast(value);
        }
    }

    /**
     * 处理字符串  如：  abc_dex ---> abcDex
     *
     * @param str
     * @return
     */
    private String removeLine(String str) {
        if (null != str && str.contains("_")) {
            int i = str.indexOf("_");
            char ch = str.charAt(i + 1);
            char newCh = (ch + "").substring(0, 1).toUpperCase().toCharArray()[0];
            String newStr = str.replace(str.charAt(i + 1), newCh);
            return newStr.replace("_", "");
        }
        return str;
    }

    /**
     * 判断object是否为基本类型
     *
     * @param object
     * @return
     */
    public boolean isBaseType(Object object) {
        if(object == null){
            return false;
        }
        return isBaseType(object.getClass());
    }

    /**
     * 判断对象的属性是否时基础数据类型
     * @param field
     * @return
     */
    public boolean isBaseType(Field field){
        if(field == null){
            return false;
        }
        return isBaseType(field.getType());
    }

    private boolean isBaseType(Class<?> clazz){
        return  isBaseType(clazz, true);
    }

    /**
     * 判断对象属性是否是基本数据类型,包括是否包括String
     * @param clazz
     * @param incString 是否包括String判断,如果为true就认为string也是基本数据类型
     * @return
     */
    public static boolean isBaseType(Class<?> clazz, boolean incString) {
        if (incString && clazz.equals(String.class)) {
            return true;
        }
        return clazz.equals(Integer.class) ||
                clazz.equals(int.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(byte.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(long.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(double.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(float.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(char.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(short.class) ||
                clazz.equals(Boolean.class) ||
                clazz.equals(boolean.class) ||
                clazz.equals(String.class) ||
                clazz.equals(Date.class) ||
                clazz.equals(BigDecimal.class);
    }

    /**
     * 判断是否为基本类型的默认值
     *
     * @param object
     * @return
     */
    public boolean isBaseDefaultValue(Object object) {
        Class<?> className = object.getClass();
        String strClassName = "" + className;
        if (className.equals(Integer.class)) {
            return (int) object == 0;
        } else if (className.equals(Byte.class)) {
            return (byte) object == 0;
        } else if (className.equals(Long.class)) {
            return (long) object == 0L;
        } else if (className.equals(Double.class)) {
            return (double) object == 0.0d;
        } else if (className.equals(Float.class)) {
            return (float) object == 0.0f;
        } else if (className.equals(Character.class)) {
            return (char) object == '\u0000';
        } else if (className.equals(Short.class)) {
            return (short) object == 0;
        } else if (className.equals(Boolean.class)) {
            return !((boolean) object);
        }
        return false;
    }
}
