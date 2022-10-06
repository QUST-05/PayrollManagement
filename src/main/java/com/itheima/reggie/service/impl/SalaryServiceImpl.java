package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.SalaryMapper;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, Salary> implements SalaryService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param salary
     */
    @Transactional
    public void submit(Salary salary) {
        /**
         * 将这些业务逻辑翻译成Java代码即可
         * 1.获得当前用户id
         * 2.查询当前用户的购物车数据
         * 3.向订单表插入数据，一条数据
         * 4.向订单明细表插入数据，多条数据
         * 5.清空购物车数据
         */
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId); //查询该用户的购物车数据
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }
        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = salary.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);//查询到地址信息

        if (addressBook == null) {//一般不会为空的，因为id地址都传过来了（这样比较保险）
            throw new CustomException("用户地址信息有误，不能下单");
        }
        //向订单表插入数据，一条数据
        long orderId = IdWorker.getId();//订单号，mybatis-plus提供的一个方法

        AtomicInteger amount = new AtomicInteger(0);//给他的初始值设为0，后面总金额累加就可以
        //因为这是一个原子操作，可以保证线程安全的。因为可能遇到高并发之类的事情，导致计算出错，这里使用这个类比较好


        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {//把订单明细数据封装出来（这里做了两件事，一个计算总金额，另外整出来这个集合对象）
            OrderDetail orderDetail = new OrderDetail(); //订单明细实体
            orderDetail.setOrderId(orderId);  //设置订单编号
            orderDetail.setNumber(item.getNumber()); //数量
            orderDetail.setDishFlavor(item.getDishFlavor());  //口味
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());  //单份金额
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());//从0开始累加 multiply×份数
            //遍历完之后，总金额就是amount

            return orderDetail;

        }).collect(Collectors.toList());


        //设置订单相应的属性
        salary.setNumber(String.valueOf(orderId)); //基本数据类型转换成String型
        salary.setId(orderId);
        salary.setOrderTime(LocalDateTime.now());
        salary.setCheckoutTime(LocalDateTime.now());
        salary.setStatus(2); //代表待派送  1，2，3，4，5五个状态，详见数据库
        salary.setAmount(new BigDecimal(amount.get()));//订单总金额
        salary.setUserId(userId);
//        salary.setNumber(String.valueOf(orderId));
        salary.setUserName(user.getName());
        salary.setConsignee(addressBook.getConsignee());
        salary.setPhone(addressBook.getPhone());
        salary.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())//地址表详细信息
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));


        this.save(salary);//直接保存orders属性不够，需要设置一些值

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }

    /**
     * 更新派送订单信息
     * @param salary
     */
    @Override
    public String updateStatus(Salary salary) {

        UpdateWrapper<Salary> updateWrapper = new UpdateWrapper<>();

        this.updateById(salary);
        switch (salary.getStatus()){
            case 1:
                updateWrapper.set("status",5); //2,3,4,5,1
                this.updateById(salary);
                return "派送完成";
            case 2:
                updateWrapper.set("status",5);
                this.updateById(salary);
                return "派送完成";
            case 3:
                updateWrapper.set("status",5);
                this.updateById(salary);
                return "派送完成";
            case 4:
                updateWrapper.set("status",5);
                this.updateById(salary);
                return "派送完成";
            case 5:
                updateWrapper.set("status",5);
                this.updateById(salary);
                return "派送完成";
        }
        return "未知错误";
    }
}