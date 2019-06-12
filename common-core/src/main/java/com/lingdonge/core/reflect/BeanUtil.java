package com.lingdonge.core.reflect;

import cn.hutool.core.exceptions.UtilException;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.TypeUtils;
import com.esotericsoftware.reflectasm.MethodAccess;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ClassUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * java.token.Date是不被支持的，而它的子类java.sql.Date是被支持的。因此如果对象包含时间类型的属性，且希望被转换的时候，一定要使用java.sql.Date类型。否则在转换时会提示argument mistype异常。
 * 可以使用： ReflectASM https://github.com/EsotericSoftware/reflectasm 提高性能，相关类型可以参考
 * https://www.cnblogs.com/Frank-Hao/p/5839096.html 性能对比
 */
public class BeanUtil {

    private static Map<Class, MethodAccess> methodMap = new HashMap<Class, MethodAccess>();

    private static Map<String, Integer> methodIndexMap = new HashMap<String, Integer>();

    private static Map<Class, List<String>> fieldMap = new HashMap<Class, List<String>>();


    /**
     * 找出所有标注了该annotation的公共属性，循环遍历父类.
     * <p>
     * 暂未支持Spring风格Annotation继承Annotation
     * <p>
     * copy from org.unitils.token.AnnotationUtils
     */
    public static <T extends Annotation> Set<Field> getAnnotatedPublicFields(Class<? extends Object> clazz,
                                                                             Class<T> annotation) {
        if (Object.class.equals(clazz)) {
            return Collections.emptySet();
        }

        Set<Field> annotatedFields = new HashSet<Field>();
        Field[] fields = clazz.getFields();

        for (Field field : fields) {
            if (field.getAnnotation(annotation) != null) {
                annotatedFields.add(field);
            }
        }

        return annotatedFields;
    }

    /**
     * 找出所有标注了该annotation的属性，循环遍历父类，包含private属性.
     * <p>
     * 暂未支持Spring风格Annotation继承Annotation
     * <p>
     * copy from org.unitils.token.AnnotationUtils
     */
    public static <T extends Annotation> Set<Field> getAnnotatedFields(Class<? extends Object> clazz,
                                                                       Class<T> annotation) {


        if (Object.class.equals(clazz)) {
            return Collections.emptySet();
        }
        Set<Field> annotatedFields = new HashSet<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(annotation) != null) {
                annotatedFields.add(field);
            }
        }
        annotatedFields.addAll(getAnnotatedFields(clazz.getSuperclass(), annotation));
        return annotatedFields;
    }

    /**
     * 找出所有标注了该annotation的公共方法(含父类的公共函数)，循环其接口.
     * <p>
     * 暂未支持Spring风格Annotation继承Annotation
     * <p>
     * 另，如果子类重载父类的公共函数，父类函数上的annotation不会继承，只有接口上的annotation会被继承.
     */
    public static <T extends Annotation> Set<Method> getAnnotatedPublicMethods(Class<?> clazz, Class<T> annotation) {
        // 已递归到Objebt.class, 停止递归
        if (Object.class.equals(clazz)) {
            return Collections.emptySet();
        }

        List<Class<?>> ifcs = ClassUtils.getAllInterfaces(clazz);
        Set<Method> annotatedMethods = new HashSet<Method>();

        // 遍历当前类的所有公共方法
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            // 如果当前方法有标注，或定义了该方法的所有接口有标注
            if (method.getAnnotation(annotation) != null || searchOnInterfaces(method, annotation, ifcs)) {
                annotatedMethods.add(method);
            }
        }

        return annotatedMethods;
    }

    private static <T extends Annotation> boolean searchOnInterfaces(Method method, Class<T> annotationType,
                                                                     List<Class<?>> ifcs) {
        for (Class<?> iface : ifcs) {
            try {
                Method equivalentMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                if (equivalentMethod.getAnnotation(annotationType) != null) {
                    return true;
                }
            } catch (NoSuchMethodException ex) { // NOSONAR
                // Skip this interface - it doesn't have the method...
            }
        }
        return false;
    }

    /**
     * 是否是基本类型、包装类型、String类型
     *
     * @param field
     * @return
     */
    public static boolean isWrapType(Field field) {

        String[] types = {"java.lang.Integer", "java.lang.Double", "java.lang.Float", "java.lang.Long",
                "java.lang.Short", "java.lang.Byte", "java.lang.Boolean", "java.lang.Char", "java.lang.String", "int",
                "double", "long", "short", "byte", "boolean", "char", "float"};
        List<String> typeList = Arrays.asList(types);
        return typeList.contains(field.getType().getName()) ? true : false;
    }

    /**
     * 获取Class里面包含的字段列表
     *
     * @param clazz
     * @return
     */
    public static Set<Field> getFieldsIncludeSuperClass(Class clazz) {
        Set<Field> fields = new LinkedHashSet<Field>();
        Class current = clazz;
        while (current != null) {
            Field[] currentFields = current.getDeclaredFields();
            for (Field currentField : currentFields) {
                fields.add(currentField);
            }
            current = current.getSuperclass();
        }
        return fields;
    }

    /**
     * 复制对象
     *
     * @param desc
     * @param orgi
     */
    public static void copyProperties(Object desc, Object orgi) {

        MethodAccess descMethodAccess = methodMap.get(desc.getClass());
        if (descMethodAccess == null) {
            descMethodAccess = cache(desc);
        }
        MethodAccess orgiMethodAccess = methodMap.get(orgi.getClass());
        if (orgiMethodAccess == null) {
            orgiMethodAccess = cache(orgi);
        }

        List<String> fieldList = fieldMap.get(orgi.getClass());
        for (String field : fieldList) {
            String getKey = orgi.getClass().getName() + "." + "get" + field;
            String setkey = desc.getClass().getName() + "." + "set" + field;
            Integer setIndex = methodIndexMap.get(setkey);
            if (setIndex != null) {
                int getIndex = methodIndexMap.get(getKey);
                descMethodAccess.invoke(desc, setIndex.intValue(), orgiMethodAccess.invoke(orgi, getIndex));
            }
        }

    }

    /**
     * 缓存object的字段到map里面备用，用于批量反射的时候提高性能
     *
     * @param orgi
     * @return
     */
    private static MethodAccess cache(Object orgi) {

        synchronized (orgi.getClass()) {
            // 使用reflectasm生产访问类
            MethodAccess methodAccess = MethodAccess.get(orgi.getClass());
            Field[] fields = orgi.getClass().getDeclaredFields();
            List<String> fieldList = new ArrayList<String>(fields.length);
            for (Field field : fields) {
                if (Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    //非公共私有变量
                    String fieldName = org.apache.commons.lang3.StringUtils.capitalize(field.getName());
                    int getIndex = methodAccess.getIndex("get" + fieldName);
                    int setIndex = methodAccess.getIndex("set" + fieldName);
                    methodIndexMap.put(orgi.getClass().getName() + "." + "get" + fieldName, getIndex);
                    methodIndexMap.put(orgi.getClass().getName() + "." + "set" + fieldName, setIndex);
                    fieldList.add(fieldName);
                }
            }
            fieldMap.put(orgi.getClass(), fieldList);
            methodMap.put(orgi.getClass(), methodAccess);
            return methodAccess;
        }
    }

    /**
     * map转对象Bean
     *
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        }

        Object obj = beanClass.newInstance();
        BeanUtils.populate(obj, map);
        return obj;
    }

    /**
     * Bean转为Map
     *
     * @param obj
     * @return
     */
    public static Map<?, ?> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }

        return new BeanMap(obj);
    }

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

    //16进制字符集
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    /**
     * Byte转Hex16进制
     *
     * @param bytes
     * @return
     */
    public static String byteToHex(byte[] bytes) {

        // 方法一：
//        return HEX_DIGITS[(bt & 0xf0) >> 4] + "" + HEX_DIGITS[bt & 0xf];

        // 方法二：
//        StringBuilder sign = new StringBuilder();
//        for (int i = 0; i < bytes.length; i++) {
//            String hex = Integer.toHexString(bytes[i] & 0xFF);
//            if (hex.length() == 1) {
//                sign.append("0");
//            }
//            sign.append(hex.toUpperCase());
//        }
//        return sign.toString();

        // 方法三：
        return Hex.encodeHexString(bytes);
    }

    /**
     * 支持的基本类型
     */
    private static final List<Class<?>> primitiveClasses = new ArrayList<Class<?>>();

    static {
        primitiveClasses.add(boolean.class);
        primitiveClasses.add(Boolean.class);

        primitiveClasses.add(char.class);
        primitiveClasses.add(Character.class);

        primitiveClasses.add(byte.class);
        primitiveClasses.add(Byte.class);

        primitiveClasses.add(short.class);
        primitiveClasses.add(Short.class);

        primitiveClasses.add(int.class);
        primitiveClasses.add(Integer.class);

        primitiveClasses.add(long.class);
        primitiveClasses.add(Long.class);

        primitiveClasses.add(float.class);
        primitiveClasses.add(Float.class);

        primitiveClasses.add(double.class);
        primitiveClasses.add(Double.class);

        primitiveClasses.add(BigInteger.class);
        primitiveClasses.add(BigDecimal.class);

        primitiveClasses.add(String.class);
        primitiveClasses.add(java.util.Date.class);
        primitiveClasses.add(java.sql.Date.class);
        primitiveClasses.add(java.sql.Time.class);
        primitiveClasses.add(java.sql.Timestamp.class);
    }

    /**
     * 是否支持传入的类型
     *
     * @param clazz
     * @return
     */
    public static boolean isSuport(Class<?> clazz) {
        return primitiveClasses.contains(clazz);
    }

    private static String castToString(Object obj) {
        if (null == obj) {
            return "";
        }

        if (obj.getClass() == Date.class) {
            return ((Date) obj).getTime() + "";
        }
        return obj.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }

    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();

        // 如果是基本类型，则转换为string
        if (isSuport(cls)) {
            return castToString(obj).getBytes();
        }

        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T castTo(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }

        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }

        if (clazz == obj.getClass()) {
            return (T) obj;
        }

        if (clazz.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }

        if (clazz == boolean.class || clazz == Boolean.class) {
            return (T) TypeUtils.castToBoolean(obj);
        }

        if (clazz == byte.class || clazz == Byte.class) {
            return (T) TypeUtils.castToByte(obj);
        }

        if (clazz == short.class || clazz == Short.class) {
            return (T) TypeUtils.castToShort(obj);
        }

        if (clazz == int.class || clazz == Integer.class) {
            return (T) TypeUtils.castToInt(obj);
        }

        if (clazz == long.class || clazz == Long.class) {
            return (T) TypeUtils.castToLong(obj);
        }

        if (clazz == float.class || clazz == Float.class) {
            return (T) TypeUtils.castToFloat(obj);
        }

        if (clazz == double.class || clazz == Double.class) {
            return (T) TypeUtils.castToDouble(obj);
        }

        if (clazz == String.class) {
            return (T) TypeUtils.castToString(obj);
        }

        if (clazz == BigDecimal.class) {
            return (T) TypeUtils.castToBigDecimal(obj);
        }

        if (clazz == BigInteger.class) {
            return (T) TypeUtils.castToBigInteger(obj);
        }

        if (clazz == Date.class) {
            return (T) TypeUtils.castToDate(obj);
        }

        if (clazz == java.sql.Date.class) {
            return (T) TypeUtils.castToSqlDate(obj);
        }

        if (clazz == java.sql.Timestamp.class) {
            return (T) TypeUtils.castToTimestamp(obj);
        }

        if (Calendar.class.isAssignableFrom(clazz)) {
            Date date = TypeUtils.castToDate(obj);
            Calendar calendar;
            if (clazz == Calendar.class) {
                calendar = Calendar.getInstance();
            } else {
                try {
                    calendar = (Calendar) clazz.newInstance();
                } catch (Exception e) {
                    throw new JSONException("can not cast to : " + clazz.getName(), e);
                }
            }
            calendar.setTime(date);
            return (T) calendar;
        }

        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0) {
                return null;
            }
        }

        throw new JSONException("can not cast to : " + clazz.getName());
    }

    public static <T> T unserialize(byte[] data, Class<T> cls) {
        if (data == null || data.length == 0) {
            return null;
        }

        // 如果是基本类型，则转换为string
        if (isSuport(cls)) {
            return castTo(new String(data), cls);
        }

        try {
            T message = cls.newInstance();
            Schema<T> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static <T> List<T> unserialize(List<byte[]> data, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        for (byte[] itemBytes : data) {
            result.add(unserialize(itemBytes, clazz));
        }
        return result;
    }


    /**
     * 加一个缓存，便于多处同时读取时提高效率
     */
    private static final Map<Class, BeanInfo> classCache = Collections.synchronizedMap(new WeakHashMap<Class, BeanInfo>());

    /**
     * 获取类本身的BeanInfo，不包含父类属性
     *
     * @param clazz
     * @return
     */
    public static BeanInfo getBeanInfo(Class<?> clazz) {
        try {
            BeanInfo beanInfo;
            if (classCache.get(clazz) == null) {
                beanInfo = Introspector.getBeanInfo(clazz, clazz.getSuperclass());
                classCache.put(clazz, beanInfo);
                // Immediately remove class from Introspector cache, to allow for proper
                // garbage collection on class loader shutdown - we cache it here anyway,
                // in a GC-friendly manner. In contrast to CachedIntrospectionResults,
                // Introspector does not use WeakReferences as values of its WeakHashMap!
                Class classToFlush = clazz;
                do {
                    Introspector.flushFromCaches(classToFlush);
                    classToFlush = classToFlush.getSuperclass();
                } while (classToFlush != null);
            } else {
                beanInfo = classCache.get(clazz);
            }

            return beanInfo;
        } catch (IntrospectionException e) {
            throw new UtilException(e);
        }
    }
}
