package com.lxj.nocode.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lxj.nocode.model.dto.user.UserQueryRequest;
import com.lxj.nocode.model.entity.User;
import com.lxj.nocode.model.vo.LoginUserVO;
import com.lxj.nocode.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author lxj
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-10-17 23:03:50
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 获取脱敏的已登录用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 根据用户实体获取用户信息
     */
    public UserVO getUserVO(User user);

    /**
     * 根据用户实体列表获取用户信息列表
     *
     * @param userList
     * @return
     */
    public List<UserVO> getUserVOList(List<User> userList);

    /**
     * 根据用户查询请求获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 根据用户查询请求获取分页对象
     * @param userQueryRequest
     * @return
     */
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);

    /**
     * 密码加密
     * @param password
     * @return
     */
    String Encrypt(String password);
}
