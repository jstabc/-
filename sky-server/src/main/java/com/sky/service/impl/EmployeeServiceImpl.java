package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;


    /**
     * 根据员工ID查询员工信息
     * @param id
     * @return
     */
    @Override
    public Employee selectById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        employee.setPassword("******");
        return employee;
    }

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        String passwordMD5 = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!passwordMD5.equals(employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }




        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工接口实现
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new  Employee();

        //属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);

        //初始密码设置
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //状态设置
        employee.setStatus(StatusConstant.ENABLE);

        //创建时间，修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //创建人，修改人
        //TODO 还没有完全解决这个问题！
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);

    }


    /**
     * 分页查询接口实现
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //把页数-从零开始， 页码传进去
        //开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> result = page.getResult();


        return new  PageResult(total, result);
    }


    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id){

        //update employee set status = ? where id = ?
        //建议把update语句写成一个动态的，然后修改什么值，传递什么值就好了。 - - 让update语句通用性更强一些！
        //动态的SQL语句写在哪里呢？

        //DATO 这个不完善，后面可能还要在进行修改！
        Employee employee = Employee.builder()
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .status(status)
                .build();
        //应该对应传递一个实体类进去
        employeeMapper.update(employee);
    }

    /**
     * 根据id修改信息- - -调用了动态SQL
     * @param employeeDTO
     * @return
     */
    @Override
    public Employee updateEmployeeMessage(EmployeeDTO employeeDTO) {
        Employee employee = Employee.builder()
                .id(employeeDTO.getId())
                .name(employeeDTO.getName())
                .idNumber(employeeDTO.getIdNumber())
                .phone(employeeDTO.getPhone())
                .sex(employeeDTO.getSex())
                .username(employeeDTO.getUsername())
                .build();
        employeeMapper.update(employee);
        employee.setPassword("******");
        return  employee;
    }


}
