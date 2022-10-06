/**
 * Create by xy
 * 2022-05-10 17:05
 */

package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional  //事务控制（保证数据一致性），在启动类上开启事务的支持（ReggieApplication那里）用@EnableTransactionManagement
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品dish表
        this.save(dishDto);

        Long dishId = dishDto.getId(); //菜品id

        List<DishFlavor> flavors = dishDto.getFlavors();//菜品口味
        flavors = flavors.stream().map((item) ->{  //可以使用for循环，也可使用这种stream流，lambda表达式
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());


        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);  //如果直接写dishDto.getFlavors(),那么id没有封装上
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表的基本信息
        this.updateById(dishDto); //因为是dish的子类，所以更新他也没问题，会更新dish中的数据

        //清理当前菜品的口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据--dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) ->{  //可以使用for循环，也可使用这种stream流，lambda表达式
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors); //批量保存
    }

    /**
     * 根据id查询菜品信息以及口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();  //条件构造器
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 删除菜品，同时删除关联的口味数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //查询餐品的状态，看能否正常删除
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        int count = this.count(queryWrapper);
        //如果不能正常删除
        if (count>0){
            throw new CustomException("菜品正在售卖不能删除");
        }
        //如果可以删除，先删除菜品表中的数据
        this.removeByIds(ids);

        //再删除关系表中的数据--dish_flavor
//        dishFlavorService.removeByIds()
        //delete from dish_flavor where dish_id in (1,2,3....)
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);

        dishFlavorService.remove(lambdaQueryWrapper);
    }

    //修改菜品状态
    @Override
    public void editStatus(int status, List<Long> ids) {
        for (Long id : ids) {
            UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id",id).set("status",status);
            this.update(null,updateWrapper);
        }
        /**
         * 将用户名中包含a并且年龄大于20或邮箱为null()的用户信息修改
         * UpdateWrapper<User> updateWrapper...
         * updateWrapper.like("user_name",a).and(i->i.gt("age",20).or().isNull("email"));
         * updateWrapper.set("user_name","小明").set("email","111@qq.com");
         * userMapper.update(null,updateWrapper); //这里因为不知道具体的实体类，所有写null
         *
         *
         *
         */

    }
}
