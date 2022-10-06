/**
 * Create by xy
 * 2022-05-10 17:26
 */

package com.itheima.reggie.common;

public class CustomException extends RuntimeException { //继承运行时异常
    public CustomException(String message){
        super(message);
    }
}
