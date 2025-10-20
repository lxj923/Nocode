package com.lxj.nocode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxj.nocode.constant.UserConstant;
import com.lxj.nocode.exception.BusinessException;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.exception.ThrowUtils;
import com.lxj.nocode.model.dto.user.UserQueryRequest;
import com.lxj.nocode.model.entity.User;
import com.lxj.nocode.model.enums.UserRoleEnum;
import com.lxj.nocode.model.vo.LoginUserVO;
import com.lxj.nocode.model.vo.UserVO;
import com.lxj.nocode.service.UserService;
import com.lxj.nocode.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lxj
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-10-17 23:03:50
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 3 || userAccount.length() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户长度 3～10");
        }
        ThrowUtils.throwIf(userPassword.length() < 8 || checkPassword.length() < 8
                , ErrorCode.PARAMS_ERROR, "密码长度要大于 8");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次密码输入不一致");

        //检验是否重复
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("userAccount", userAccount));
        if (ObjUtil.isNotEmpty(user)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户重复");
        }
        //加密
        String encryptPassword = Encrypt(userPassword);
        //插入数据
        User newUser = new User();
        newUser.setUserAccount(userAccount);
        newUser.setUserPassword(encryptPassword);
        newUser.setUserName("无名");
        newUser.setUserRole(UserRoleEnum.USER.getValue());
        boolean result = this.save(newUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return newUser.getId();
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (ObjUtil.isEmpty(user)) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 3 || userAccount.length() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户长度 3～10");
        }
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码长度要大于 8");
        //加密
        String encrypt = Encrypt(userPassword);
        //查询是否存在
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("userAccount", userAccount)
                .eq("userPassword", encrypt));
        //用户不存在
        if (ObjUtil.isEmpty(user)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        //记录登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        //获取脱敏信息
        return getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        //判断是否已经登录
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (ObjUtil.isEmpty(user)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //从数据库中查询
        User selected = userMapper.selectById(user.getId());
        if (ObjUtil.isEmpty(selected)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return selected;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        //判断是否已经登录
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (ObjUtil.isEmpty(user)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (ObjUtil.isEmpty(user)) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (ObjUtil.isEmpty(userList)) {
            return List.of();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();

        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id)
                .eq(ObjUtil.isNotNull(userRole), "userRole", userRole)
                .like(ObjUtil.isNotNull(userName), "userName", userName)
                .like(ObjUtil.isNotNull(userAccount), "userAccount", userAccount)
                .like(ObjUtil.isNotNull(userProfile), "userProfile", userProfile);

        queryWrapper.orderBy(ObjUtil.isNotNull(sortField), StrUtil.equals(sortOrder, "ascend"), sortField);

        return queryWrapper;
    }

    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<User> queryWrapper = getQueryWrapper(userQueryRequest);
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = this.page(new Page<>(current, pageSize), queryWrapper);

        List<User> records = userPage.getRecords();
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        userVOPage.setRecords(getUserVOList(records));
        return userVOPage;
    }

    @Override
    public String Encrypt(String password) {
        final String salt = "lxjzjw4159123";
        return DigestUtils.md5DigestAsHex((password + salt).getBytes());
    }
}




