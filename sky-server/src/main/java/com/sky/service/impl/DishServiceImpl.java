package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 新增菜品与口味接口实现-对多个数据库中表进行操作，要添加事务注解，一致性！！
     * @param dishDTO
     */
    @Override
    @Transactional
    public void save(DishDTO dishDTO) {


        //向菜品表添加一个数据--涉及实体的转变 - -数据传输对象 到 数据库实体对象的转变
        // 1. DTO 转 Dish Entity 并保存
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish); // 执行完这一步，dish 的 ID 会被自动回填（如果配置了主键回填）


        //向口味表添加多个数据

        // 2. 获取生成的菜品 ID
        Long dishId = dish.getId();

        // 3. 处理口味数据：DTO 里的 flavors 列表需要关联这个 dishId
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId); // 为每个口味设置所属的菜品 ID
            });
            // 4. 批量插入保存口味数据
            dishFlavorMapper.insertBatch(flavors);
        }



    }
}
