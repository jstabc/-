package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

    @Autowired
    private SetmealDishMapper setmealDishMapper;

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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuer(dishPageQueryDTO);

        return new  PageResult(page.getTotal(), page.getResult());

    }


    /**
     * 菜品批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        log.info("菜品批量删除,{}",ids.size());

        //1.判断当前菜品是否能够删除 - - 是否存起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new RuntimeException(MessageConstant.DISH_ON_SALE);
            }
        }


        //2.                         - - 是否被套餐关联了
        List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIdsByDishIds != null && setmealIdsByDishIds.size() > 0) {
            //关联套餐 - - 不删除
            throw new RuntimeException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


        //3.                           删除菜品表中的菜品数据
        //ids.for  可以进行for循环！
        for (Long id : ids) {
            dishMapper.deleteById(id);
            //传过去的是形参，在另外的地方可以随意改变！！！
            dishFlavorMapper.deleteByDishId(id);
        }
        //4.                            删除餐品关联的口味数据
        //这个删除操作可以并入 第三个条件，不管有没有直接删除就行了！

    }


    /**
     * 菜品数据回显
     *
     * @param id
     * @return
     */
    @Override
    public DishVO selectDish(Long id) {
        log.info("通过菜品id查询菜品信息{}",id);
        //这是一个返回拼接信息的逻辑
        //1 通过id 查询 菜品信息
        //2 通过菜品里面的数据 查询 口味数据库中的数据
        //3 拼接 两个 信息 进行返回

        // 通过 id 查询 菜品信息
        Dish dish = dishMapper.getById(id);
        //找到 两个数据库表格的联系
     //   Long dishId = dish.getId();
        //通过联系 查找另外一个数据库中的信息
        List<DishFlavor> dishFlavor = dishFlavorMapper.getById(id);

        // 进行拼接！ - - 属性拷贝， 分别创建出所需要的 表格类！
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);

        dishVO.setFlavors(dishFlavor);

        return dishVO;

    }

    /**
     * 菜品信息的更新
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateDishWithFlavor(DishDTO dishDTO) {
        //我需要对所给信息进行拆分
        //然后把拆分出来的数据 更新到数据库！
        //这里-菜品口味数据，因为不知道要不要修改
        //有好多条，所以 - 可以先删除口味数据，然后在插入口味数据！

        //修改菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        //删除所有口味数据
        dishFlavorMapper.deleteByDishId(dish.getId());

        //插入口味数据
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
