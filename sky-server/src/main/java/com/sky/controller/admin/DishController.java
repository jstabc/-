package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.util.List;

@RequestMapping("/admin/dish")
@RestController
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {


    @Autowired
    private DishService dishService;
    @Autowired
    private ResourceUrlProvider resourceUrlProvider;


    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.save(dishDTO);
        return  Result.success();
    }


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO  dishPageQueryDTO) {
        log.info("菜品分页展示,{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除,{}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @ApiOperation("根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable long id) {
        log.info("记录传过来的要修改的id值{}",id);
        DishVO dishvo = dishService.selectDish(id);
        return Result.success(dishvo);
    }


    /**
     * 修改菜品信息
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("更新菜品信息")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("传过来的菜品信息{}",dishDTO);
        dishService.updateDishWithFlavor(dishDTO);
        return Result.success();
    }








}
