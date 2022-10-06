/**
 * Create by xy
 * 2022-05-10 12:01
 */

package com.itheima.reggie.common;

import com.sun.org.apache.regexp.internal.RE;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
