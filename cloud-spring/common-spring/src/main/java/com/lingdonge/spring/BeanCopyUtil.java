package com.lingdonge.spring;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.lingdonge.spring.bean.token.JwtUserInfo;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿里代码规范推荐：避免用Apache Beanutils进行属性的copy，Apache BeanUtils性能较差，可以使用其他方案比如Spring BeanUtils, Cglib BeanCopier
 * <p>
 * 评测：
 * https://github.com/arey/java-object-mapper-benchmark
 * https://github.com/yangtu222/BeanUtils#performance
 * <p>
 * 参考：
 * https://github.com/yangtu222/BeanUtils
 * <p>
 * 常用的BeanCopy类
 * 1、org.springframework.beans.BeanUtils.copyProperties
 * 2、net.sf.ezmorph.bean.BeanMorpher
 * 3、org.apache.commons.beanutils.BeanUtil.copyProperties 反射机制
 * 4、org.apache.commons.beanutils.PropertyUtils.copyProperties 反射机制
 * 5、org.springframework.beans.BeanUtils.copyProperties 反射机制
 * 6、org.springframework.cglib.beans.BeanCopier.create 动态代理，效率高
 * 7、org.dozer.DozerBeanMapper.map XML配置映射，性能最低下
 * <p>
 * Cglib copy 问题
 * <p>
 * 不支持链式 bean，mybatis-plus 生成的 Model 中默认添加了 @Accessors(chain = true) 注解默认为链式。
 * 不支持 原始类型和封装类型 copy int <-> Integer。
 * 类型转换不够智能，设置 useConverter 为 true 和重写 Converter，类型相同也会走转换的逻辑。
 * <p>
 * 简单的复制拷贝，这种情况下，BeanCopyer>PropertyUtils>Dozer
 */
public class BeanCopyUtil {

    public static void main(String[] args) {
        BeanCopier beanCopier = BeanCopier.create(JwtUserInfo.class, JwtUserInfo.class, false);
//        beanCopier.copy(source, target);

    }

    private static final Map<String, BeanCopier> BEAN_COPIERS = new HashMap<String, BeanCopier>();

    /**
     * 使用BeanCopier复制对象
     *
     * @param srcObj
     * @param destObj
     */
    public static void copy(Object srcObj, Object destObj) {
        String key = genKey(srcObj.getClass(), destObj.getClass());
        BeanCopier copier = null;
        if (!BEAN_COPIERS.containsKey(key)) {
            copier = BeanCopier.create(srcObj.getClass(), destObj.getClass(), false);
            BEAN_COPIERS.put(key, copier);
        } else {
            copier = BEAN_COPIERS.get(key);
        }
        copier.copy(srcObj, destObj, null);
    }

    /**
     * 生成缓存的Key
     *
     * @param srcClazz
     * @param destClazz
     * @return
     */
    private static String genKey(Class<?> srcClazz, Class<?> destClazz) {
        return srcClazz.getName() + destClazz.getName();
    }

    /**
     * MyBatisPlus的Map列表转对象
     *
     * @param mapList
     * @param elementType
     * @param <T>
     * @return
     */
    public <T> List<T> mapToList(List<Map<String, Object>> mapList, Class<T> elementType) {
        List<T> listResult = Lists.newArrayList();
        if (CollectionUtils.isEmpty(mapList)) {
            return null;
        }
        mapList.forEach(mapItem -> {
            listResult.add(BeanUtil.mapToBean(mapItem, elementType, true));
        });
        return listResult;
    }

    /**
     * Map转到一个对象里面
     *
     * @param mapList
     * @param elementType
     * @param <T>
     * @return
     */
    public <T> T mapToEntity(List<Map<String, Object>> mapList, Class<T> elementType) {

        if (CollectionUtils.isEmpty(mapList)) {
            return null;
        }

        T result = BeanUtil.mapToBean(mapList.get(0), elementType, true);
        return result;
    }

}
