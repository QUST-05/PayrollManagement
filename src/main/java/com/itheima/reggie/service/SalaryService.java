package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Salary;

public interface SalaryService extends IService<Salary> {

    /**
     * 用户下单
     * @param Salary
     */
    public void submit(Salary Salary);

    /**
     * 更新派送订单信息
     * @param Salary
     */
    public String updateStatus(Salary Salary);
}
