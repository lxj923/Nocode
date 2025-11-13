package com.lxj.nocode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxj.nocode.common.DeleteRequest;
import com.lxj.nocode.constant.UserConstant;
import com.lxj.nocode.exception.BusinessException;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.exception.ThrowUtils;
import com.lxj.nocode.model.dto.app.AppAddRequest;
import com.lxj.nocode.model.dto.app.AppAdminUpdateRequest;
import com.lxj.nocode.model.dto.app.AppQueryRequest;
import com.lxj.nocode.model.dto.app.AppUpdateRequest;
import com.lxj.nocode.model.entity.App;
import com.lxj.nocode.model.entity.User;
import com.lxj.nocode.model.enums.CodeGenTypeEnum;
import com.lxj.nocode.model.vo.AppVO;
import com.lxj.nocode.model.vo.UserVO;
import com.lxj.nocode.service.AppService;
import com.lxj.nocode.mapper.AppMapper;
import com.lxj.nocode.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lxj
 * @description 针对表【app(应用)】的数据库操作Service实现
 * @createDate 2025-10-24 10:51:50
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>
        implements AppService {

    @Resource
    private UserService userService;

    @Override
    public Long addApp(AppAddRequest appAddRequest, HttpServletRequest request) {
        //参数校验
        String initPrompt = appAddRequest.getInitPrompt();
        if (initPrompt == null || initPrompt.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "初始化提示不能为空");
        }

        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        App app = new App();
        app.setInitPrompt(initPrompt);
        app.setUserId(loginUser.getId());

        //设置默认应用名称 代码类型
        app.setAppName("我的应用" + RandomUtil.randomString(5) + initPrompt.substring(0, Math.min(initPrompt.length(), 5)));
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());
        //插入数据库
        boolean result = this.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "添加应用失败");

        return app.getId();
    }

    @Override
    public Boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        //校验
        Long appId = appUpdateRequest.getId();
        String appName = appUpdateRequest.getAppName();
        if (appId == null || appId < 0 || appName == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        if (appName.length() < 3 || appName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称长度必须在3-20之间");
        }

        //判断是否存在
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");

        //只有本人可以更新
        User loginUser = userService.getLoginUser(request);
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有权限更新应用");
        }

        //设置编辑时间
        app.setEditTime(new Date());
        app.setAppName(appName);

        //更新数据库
        boolean result = this.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "更新应用失败");

        return true;
    }

    @Override
    public Boolean deleteApp(DeleteRequest deleteRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long appId = deleteRequest.getId();
        //查看应用是否存在
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        //仅管理员或本人可以删除
        Long userId = app.getUserId();
        //不是管理员 不是本人
        if (!loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE) && !loginUser.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有权限删除应用");
        }
        boolean result = this.removeById(appId);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "删除应用失败");
        return true;
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        //关联用户信息
        User user = userService.getById(app.getUserId());
        UserVO userVO = userService.getUserVO(user);

        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        appVO.setUser(userVO);
        return appVO;
    }

    @Override
    public QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);

        QueryWrapper<App> queryWrapper = new QueryWrapper<>();
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();

        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id)
                .like(StrUtil.isNotBlank(appName), "appName", appName)
                .like(StrUtil.isNotBlank(cover), "cover", cover)
                .like(StrUtil.isNotBlank(initPrompt), "initPrompt", initPrompt)
                .eq(StrUtil.isNotBlank(codeGenType), "codeGenType", codeGenType)
                .eq(StrUtil.isNotBlank(deployKey), "deployKey", deployKey)
                .eq(ObjUtil.isNotNull(priority), "priority", priority)
                .eq(ObjUtil.isNotNull(userId), "userId", userId)
                .orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);

        return queryWrapper;
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (appList.isEmpty()) {
            return new ArrayList<>();
        }
        //搜集所有 userid 到集合
        Set<Long> userID = appList.stream().map(App::getUserId).collect(Collectors.toSet());
        //根据 userid 集合批量查询信息
        List<User> users = userService.listByIds(userID);
        Map<Long, UserVO> collect = users.stream()
                .collect(Collectors.toMap(User::getId, user -> userService.getUserVO(user)));
        //组件 map
        //一次性封装所有 apppvo
        return appList.stream().map(app -> {
            AppVO appVO = this.getAppVO(app);
            UserVO userVO = collect.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect((Collectors.toList()));
    }

    @Override
    public Page<AppVO> getAppListPageVO(AppQueryRequest appQueryRequest) {
        int current = appQueryRequest.getCurrent();
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(current < 0, ErrorCode.PARAMS_ERROR, "页码有误");

        QueryWrapper<App> queryWrapper = this.getQueryWrapper(appQueryRequest);
        Page<App> page = this.page(new Page<>(current, pageSize), queryWrapper);
        //数据封装
        Page<AppVO> appVOPage = new Page<>(current, pageSize, page.getTotal());
        List<AppVO> appVOList = this.getAppVOList(page.getRecords());
        appVOPage.setRecords(appVOList);

        return appVOPage;
    }

    @Override
    public Boolean adminDeleteApp(DeleteRequest deleteRequest) {
        Long id = deleteRequest.getId();
        //判断是否存在
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除应用失败");
        return true;
    }

    @Override
    public Boolean adminUpdateApp(AppAdminUpdateRequest appAdminUpdateRequest) {
        Long id = appAdminUpdateRequest.getId();
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "应用ID不能为空");

        App oldapp = this.getById(id);
        ThrowUtils.throwIf(oldapp == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        //更新
        App app = new App();
        BeanUtil.copyProperties(appAdminUpdateRequest, app);
        //设置编辑时间
        app.setEditTime(new Date());
        boolean result = this.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新应用失败");
        return true;
    }
}




