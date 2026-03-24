package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品ID查询对应的套餐ID
     */
    //动态SQL: delete from dish where di in (?,?,?)
    //  <foreach collection="dishIds" item="dishId" separator="," open="(" close=")">
    //      #{dishId}
    //  </foreach>
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
