package com.lindonge.core.reflect;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.lindonge.core.collection.CaseInsensitiveMap;
import com.lindonge.core.exceptions.UtilException;
import com.lindonge.core.util.JudgeUtil;
import com.lindonge.core.util.StringUtils;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

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
     * 判断是否为Bean对象<br>
     * 判定方法是是否存在只有一个参数的setXXX方法
     *
     * @param clazz 待测试类
     * @return 是否为Bean对象
     */
    public static boolean isBean(Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getParameterTypes().length == 1 && method.getName().startsWith("set")) {
                    //检测包含标准的setXXX方法即视为标准的JavaBean
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasNull(Object bean, boolean ignoreError) {
        final Field[] fields = ClassUtil.getDeclaredFields(bean.getClass());

        Object fieldValue = null;
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                fieldValue = field.get(bean);
            } catch (Exception e) {

            }
            if (null == fieldValue) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得Bean字段描述数组
     *
     * @param clazz Bean类
     * @return 字段描述数组
     * @throws IntrospectionException 获取属性异常
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws IntrospectionException {
        return Introspector.getBeanInfo(clazz).getPropertyDescriptors();
    }

    /**
     * 获得字段名和字段描述Map。内部使用，直接获取Bean类的PropertyDescriptor
     *
     * @param clazz      Bean类
     * @param ignoreCase 是否忽略大小写
     * @return 字段名和字段描述Map
     * @throws IntrospectionException 获取属性异常
     */
    private static Map<String, PropertyDescriptor> internalGetPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws IntrospectionException {
        final PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
        final Map<String, PropertyDescriptor> map = ignoreCase ?
                new CaseInsensitiveMap<String, PropertyDescriptor>(propertyDescriptors.length, 1)
                : new HashMap<String, PropertyDescriptor>((int) (propertyDescriptors.length), 1);

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            map.put(propertyDescriptor.getName(), propertyDescriptor);
        }
        return map;
    }

    /**
     * 获得字段值，通过反射直接获得字段值，并不调用getXXX方法<br>
     * 对象同样支持Map类型，fieldName即为key
     *
     * @param bean      Bean对象
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getFieldValue(Object bean, String fieldName) {
        if (null == bean || StringUtils.isBlank(fieldName)) {
            return null;
        }

        if (bean instanceof Map) {
            return ((Map<?, ?>) bean).get(fieldName);
        } else if (bean instanceof List) {
            return ((List<?>) bean).get(Integer.parseInt(fieldName));
        } else if (bean instanceof Collection) {
            return ((Collection<?>) bean).toArray()[Integer.parseInt(fieldName)];
        } else if (JudgeUtil.isArray(bean)) {
            return Array.get(bean, Integer.parseInt(fieldName));
        } else {//普通Bean对象
            Field field;
            try {
                field = ClassUtil.getDeclaredField(bean.getClass(), fieldName);
                if (null != field) {
                    field.setAccessible(true);
                    return field.get(bean);
                }
            } catch (Exception e) {
                throw new UtilException(e);
            }
        }
        return null;
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

    /**
     * 对象转Map，不进行驼峰转下划线，不忽略值为空的字段
     *
     * @param <T>  Bean类型
     * @param bean bean对象
     * @return Map
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        return beanToMap(bean, false, false);
    }

    /**
     * 对象转Map
     *
     * @param <T>               Bean类型
     * @param bean              bean对象
     * @param isToUnderlineCase 是否转换为下划线模式
     * @param ignoreNullValue   是否忽略值为空的字段
     * @return Map
     */
    public static <T> Map<String, Object> beanToMap(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {

        if (bean == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            final PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(bean.getClass());
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class") && !key.equals("declaringClass")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(bean);
                    if (!ignoreNullValue || (null != value && false == value.equals(bean))) {
                        map.put(isToUnderlineCase ? NamingUtil.camelToUnderline(key) : key, value);
                    }
                }
            }
        } catch (Exception e) {
            throw new UtilException(e);
        }
        return map;
    }


}
