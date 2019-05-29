package com.lindonge.core.reflect;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Optional增强
 */
public class OptionalUtil {

    /**
     * 解析对象是否有Null存在，缺点是根据异常来处理的，性能会有损耗
     * 使用时：
     * 1、复杂用法
     * Optional<Integer> optionalActive = resolve(()->properties.getJedis().getPool().getMaxActive());
     * if (optionalActive.isPresent()) {
     * jedisPoolConfig.setMaxTotal(optionalActive.get());// 最大连接数, 默认8个，控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
     * }
     * 2、简单用法
     * resolve(() -> obj.getNested().getInner().getFoo());
     *     .ifPresent(System.out::println);
     * 来源：https://stackoverflow.com/questions/10391406/java-avoid-checking-for-null-in-nested-classes-deep-null-checking
     *
     * 原生方案：
     * 判断：product.getLatestVersion().getProductData().getTradeItem().getInformationProviderOfTradeItem().getGln(); 能否使用时：
     * Optional.ofNullable(product).map(
     *             Product::getLatestVersion
     *         ).map(
     *             ProductVersion::getProductData
     *         ).map(
     *             ProductData::getTradeItem
     *         ).map(
     *             TradeItemType::getInformationProviderOfTradeItem
     *         ).map(
     *             PartyInRoleType::getGln
     *         ).orElse(null);
     *
     * @param resolver
     * @param <T>
     * @return
     */
    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
