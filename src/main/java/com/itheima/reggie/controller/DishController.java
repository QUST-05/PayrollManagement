/**
 * Create by xy
 * 2022-05-11 18:12
 */

package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){  //注意不能Dish dish 因为前端传来的数据有flavor而Dish实体类中没有这个属性。
             // 我们可以封装例外一个类 用DTO 即Data Transfer Object(用于展示层与服务层之间传输数据)
        log.info(dishDto.toString());
        //因为操作多张表，所以有些复杂，详细见DishServiceImpl
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        /*
        难点在于，菜品的分类以及菜品的图片如何显示？（需要查询菜品的分类，以及图片）
        1、分类需要查询分页表
         */
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件（模糊查询）
        queryWrapper.like(name!=null,Dish::getName,name);
        /**
         * 查询用户名包含a,年龄在20-30之间，邮箱信息不为null的用户信息
         * QueryWrapper<User> queryWrapper = ....
         * queryWrapper.like("user_name","a").between("age",20,30).isNotNull("email");
         *
         * list.forEach()
         */
        //添加排序条件(根据降序时间来排)
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //进行分页查询
        dishService.page(pageInfo,queryWrapper);
//        return R.success(pageInfo);//直接这么写是不对的，因为返回的数据中没有分类名称
        //于是想到了dto

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");  //把pageInfo拷贝到dishDtoPage上
        //这里record不需要拷贝，因为列表中显示的已经就是List集合的数据了于是用 ...这第三个属性
        //我们想要DishDto这种泛型，所以要手动更改records
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{ //遍历records,map把每个元素拿出来，通过入表达式处理item
            DishDto dishDto = new DishDto();
            //对象拷贝
            BeanUtils.copyProperties(item,dishDto);//把拿到的item拷到dishDto上
            Long categoryId = item.getCategoryId(); //分类id
            //根据id查询分类
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();//顺利拿到分类名称
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

//        List<DishDto> list = null;
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息以及口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}") //固定写法
    public R<DishDto> get(@PathVariable Long id){ //id在请求url中

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 更新菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){  //注意不能Dish dish 因为前端传来的数据有flavor而Dish实体类中没有这个属性。
        log.info(dishDto.toString());
        //还是同新增，因为有两张表（多个风味表）的操作，更新比较复杂
        dishService.updateWithFlavor(dishDto);
        return R.success("更新菜品成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){ //可能多个菜品，用list集合 用Long categoryId没问题，只是Dish比较好(list可以更通用点，可能传来其他参数)
//        //构造查询条件对象
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);//查询状态为1的（起售状态）
//
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);  //如果sort相同，根据第二个字段，更新时间
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }
    //改造一下，显示移动端需要的口味数据(只是在原先的基础上追加一些数据，对原来的后台系统不会产生影响)
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){ //可能多个菜品，用list集合 用Long categoryId没问题，只是Dish比较好(list可以更通用点，可能传来其他参数)
        //构造查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);//查询状态为1的（起售状态）

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);  //如果sort相同，根据第二个字段，更新时间
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item)->{ //遍历records,map把每个元素拿出来，通过入表达式处理item
            DishDto dishDto = new DishDto();
            //对象拷贝
            BeanUtils.copyProperties(item,dishDto);//把拿到的item拷到dishDto上
            Long categoryId = item.getCategoryId(); //分类id
            //根据id查询分类
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();//顺利拿到分类名称
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);

            //查出来口味的集合 SQL:select *from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

//        List<DishDto> dishDtoList = null;
        return R.success(dishDtoList);
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids,{}",ids);
        dishService.removeWithFlavor(ids);
        return R.success("菜品删除成功");
    }

    /**
     * 切换菜品停售起售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> editStatus(@PathVariable int status,@RequestParam List<Long> ids){
        log.info("根据status查询该菜单状态,{}",status);
        dishService.editStatus(status,ids);
        return R.success("菜品状态切换成功");
    }
}
