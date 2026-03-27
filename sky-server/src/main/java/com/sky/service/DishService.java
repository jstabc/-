package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {


    /**
     * 新增菜品
     * @param dishDTO
     */
    void save(DishDTO dishDTO);


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 菜品批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);


    /**
     * 菜品数据回显
     *
     * @param id
     * @return
     */
    DishVO selectDish(Long id);

    /**
     * 菜品信息的更新
     * @param dishDTO
     */
    void updateDishWithFlavor(DishDTO dishDTO);
}
