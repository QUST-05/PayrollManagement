package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Salary;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.SalaryService;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 用户下单
     *
     * @param salary
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Salary salary) {//前端传来的数据是不需要传用户信息的，因为都可以通过getCurrentId去查相关信息
        log.info("订单数据：{}", salary);
        salaryService.submit(salary);
        return R.success("下单成功");
    }

    /**
     * 订单分页查询
     *
     * @param page
     * @param pageSize
     * @param input
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String consignee, String input, String beginTime, String endTime) {
        Page<Salary> pageInfo = new Page<>(page, pageSize); //分页构造器
        QueryWrapper<Salary> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(StringUtils.isNotEmpty(beginTime),"order_time",beginTime)
                .le(StringUtils.isNotEmpty(endTime),"order_time",endTime);

//        Page<OrdersDto> ordersDtoPage = new Page<>(page,pageSize);
        //查询订单号
        LambdaQueryWrapper<Salary> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(input != null, Salary::getNumber, input);
        queryWrapper1.orderByDesc(Salary::getOrderTime);

        //查询姓名
        LambdaQueryWrapper<Salary> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(consignee != null, Salary::getConsignee, consignee);
        queryWrapper2.orderByDesc(Salary::getOrderTime);


        //进行分页查询
        if (input==null&&consignee==null)
            salaryService.page(pageInfo,queryWrapper);
        else if (input!=null&&consignee==null){
            salaryService.page(pageInfo, queryWrapper1);
        }else if (input==null&&consignee!=null){
            salaryService.page(pageInfo,queryWrapper2);
        }





        return R.success(pageInfo);
    }

    @GetMapping("/userPage")
    public R<Page> UserPage(int page,int pageSize){
        Page<Salary> pageInfo = new Page<>(page, pageSize); //分页构造器
        LambdaQueryWrapper<Salary> queryWrapper = new LambdaQueryWrapper<>();
        Long currentId = BaseContext.getCurrentId();
        //查询数据库表中userId一致的表
        queryWrapper.eq(currentId!=null, Salary::getUserId,currentId);
        salaryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> sendFood(@RequestBody Salary salary){
//        LambdaQueryWrapper<Salary> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Salary::getId,salary.getId());
//
//        Salary order = salaryService.getOne(queryWrapper);
        log.info("派送情况，数据:{}", salary);
        String msg = salaryService.updateStatus(salary);
        return R.success(msg);
    }
}