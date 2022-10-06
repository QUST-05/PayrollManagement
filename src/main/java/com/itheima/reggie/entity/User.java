package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
/**
 * 用户信息
 */
@Data
//@TableName("user") //对应数据库表是user,因为实体类命名为User，这里可以不写这个注解
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * mybatis-plus会默认把id设为主键
     * 如果，主键不叫uid，如何设置主键？
     * 用@TableId注解
     */
    @TableId(value = "id")  //将属性对应的字段设置为主键，但是如果属性对应的字段是uid,该如何处理
    //需要用到value属性,数据库表主键名为id，这里value就写id。
    //mybatis-plus默认生成id的就是雪花算法，如果想要id是自增，该如何处理？用type属性
    //这里需要在数据库设置自增才能使用type = IdType.AUTO(也是一种主键生成策略)
    private Long id;


    //姓名
    private String name;


    //手机号
    private String phone;


    //性别 0 女 1 男
    private String sex;


    //身份证号
    private String idNumber;


    //头像
    private String avatar;


    //状态 0:禁用，1:正常
    private Integer status;
}
