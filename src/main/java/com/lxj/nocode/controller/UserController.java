package com.lxj.nocode.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.server.HttpServerRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxj.nocode.annotation.AuthCheck;
import com.lxj.nocode.common.BaseResponse;
import com.lxj.nocode.common.DeleteRequest;
import com.lxj.nocode.common.ResultUtils;
import com.lxj.nocode.constant.UserConstant;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.exception.ThrowUtils;
import com.lxj.nocode.model.dto.user.UserAddRequest;
import com.lxj.nocode.model.dto.user.UserQueryRequest;
import com.lxj.nocode.model.dto.user.UserRegisterRequest;
import com.lxj.nocode.model.dto.user.UserUpdateRequest;
import com.lxj.nocode.model.entity.User;
import com.lxj.nocode.model.enums.UserRoleEnum;
import com.lxj.nocode.model.vo.LoginUserVO;
import com.lxj.nocode.model.vo.UserVO;
import com.lxj.nocode.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.userRegister(userRegisterRequest.getUserAccount()
                , userRegisterRequest.getUserPassword()
                , userRegisterRequest.getCheckPassword()));
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> login(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.userLogin(userRegisterRequest.getUserAccount(),
                userRegisterRequest.getUserPassword(), request));
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.userLogout(request));
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(MustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        if (userAddRequest.getUserRole() == null) {
            user.setUserRole(UserRoleEnum.USER.getValue());
        }
        user.setUserPassword(userService.Encrypt("12345678"));
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建失败");
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据 id 获取用户
     */
    @GetMapping("/get")
    @AuthCheck(MustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(MustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除失败");
        return ResultUtils.success(true);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(MustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表
     *
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(MustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.listUserVOByPage(userQueryRequest));
    }
}
