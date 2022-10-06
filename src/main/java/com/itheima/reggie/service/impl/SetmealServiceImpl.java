/**
 * Create by xy
 * 2022-05-10 17:06
 */

package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional //要操作两张表，保证事务的一致性，要么全成功，要么全失败
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal,执行insert操作
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes); //批量插入
    }

    /**
     * 删除套餐，同时删除关联的菜品数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //大概是这：select count(*) from setmeal where id in (1,2,3....) and status = 1,正在售卖

        //查询套餐的状态，确定是否可以删除，
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        //如果不能删除，抛出业务异常
        int count = this.count(queryWrapper);//框架方法,得到count值
        if (count>0){
            throw new CustomException("套餐正在售卖不能删除");
        }

        //如果可以删除，先删除套餐表中的数据，--setmeal
        this.removeByIds(ids);//框架方法

        //再删除关系表中的数据--setmeal_dish
//        setmealDishService.removeByIds()
        //delete from setmeal_dish where setmeal_id in (1,2,3....)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(lambdaQueryWrapper); //批量删除了


    }

    /**
     * 修改套餐状态
     * @param status
     * @param ids
     */
    @Override
    public void editStatus(int status, List<Long> ids) {
        for (Long id:ids){
            UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id",id).set("status",status);
            this.update(null,updateWrapper);
        }
    }

}
