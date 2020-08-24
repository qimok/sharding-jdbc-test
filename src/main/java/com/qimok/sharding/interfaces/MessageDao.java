package com.qimok.sharding.interfaces;

import db.tables.pojos.MessagePo;
import java.lang.reflect.Method;

/**
 * @author qimok
 * @since 2020-08-20
 */
public interface MessageDao<T> {

    /**
     * 发送消息
     *
     * @param messagePo
     */
    void sendMessage(MessagePo messagePo);

    /**
     * 动态调用实现了 MessageDao 的所有实例方法
     *
     * @param invokeMethodName 要调用的方法名
     * @param parameterTypes 方法形参的 class
     * @param parameter 方法形参
     */
    default void dynamicInvokeMethod(String invokeMethodName, Class[] parameterTypes, T parameter) {
        if (invokeMethodName != null) {
            Class clazz = this.getClass();
            Method[] methods = clazz.getMethods();
            for (int i = 0; i < methods.length; i++) {
                String methodName = methods[i].getName();
                if (methodName.equals(invokeMethodName)) {
                    try {
                        Method method = clazz.getMethod(invokeMethodName, parameterTypes);
                        if ("MessageNoShardRepo".equals(clazz.getSimpleName())) {
                            // 是否执行？
                            // 同步 or 异步？
                            method.invoke(this, parameter);
                        } else if ("MessageShardRepo".equals(clazz.getSimpleName())) {
                            // 是否执行？
                            // 同步 or 异步？
                            method.invoke(this, parameter);
                        }
                    } catch (Exception ex) {
                        System.out.println(invokeMethodName + " not found!");
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

}
