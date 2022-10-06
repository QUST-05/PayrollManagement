package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dishFlavor
    public void saveWithFlavor(DishDto dishDto);

    //更新菜品，同时更新菜品对应的口味数据，需要操作两张表：dish、dishFlavor
    public void updateWithFlavor(DishDto dishDto);

    //根据id查询菜品信息以及对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //删除菜品
    public void removeWithFlavor(List<Long> ids);

    //修改菜品状态
    public void editStatus(int status,List<Long> id);
}
