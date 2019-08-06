package com.lingdonge.db.dynamic.sessionFactory;

/**
 * 使用进程上下文保存Session名称列表
 */
public abstract class ThreadLocalUtil {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    /**
     * @param customerType
     */
    public static void setCustomerType(String customerType) {
        contextHolder.set(customerType);
    }

    /**
     * @return
     */
    public static String getCustomerType() {
        return contextHolder.get();
    }

    /**
     *
     */
    public static void clearCustomerType() {
        contextHolder.remove();
    }

}