package com.lxj.nocode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxj.nocode.annotation.AuthCheck;
import com.lxj.nocode.common.BaseResponse;
import com.lxj.nocode.common.DeleteRequest;
import com.lxj.nocode.common.ResultUtils;
import com.lxj.nocode.constant.AppConstant;
import com.lxj.nocode.constant.UserConstant;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.exception.ThrowUtils;
import com.lxj.nocode.model.dto.app.AppAddRequest;
import com.lxj.nocode.model.dto.app.AppAdminUpdateRequest;
import com.lxj.nocode.model.dto.app.AppQueryRequest;
import com.lxj.nocode.model.dto.app.AppUpdateRequest;
import com.lxj.nocode.model.entity.App;
import com.lxj.nocode.model.entity.User;
import com.lxj.nocode.model.vo.AppVO;
import com.lxj.nocode.service.AppService;
import com.lxj.nocode.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class AppController {

    @Resource
    private AppService appService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR, "应用不能为空");
        return ResultUtils.success(appService.addApp(appAddRequest, request));
    }

    /**
     * 更新应用(用户只能更新自己的应用名称)
     *
     * @param appUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appUpdateRequest == null, ErrorCode.PARAMS_ERROR, "应用不能为空");
        return ResultUtils.success(appService.updateApp(appUpdateRequest, request));
    }

    /**
     * 删除应用(用户只能删除自己的应用)
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR, "应用不能为空");
        return ResultUtils.success(appService.deleteApp(deleteRequest, request));
    }

    /**
     * 根据 id 获取应用信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppVO> getAppVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        //查数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        //返回封装类
        return ResultUtils.success(appService.getAppVO(app));
    }

    /**
     * 获取我的应用列表（分页）
     * @param appQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AppVO>> getMyAppListPageVO(@RequestBody AppQueryRequest appQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        //限制每页最多 20个
        ThrowUtils.throwIf(appQueryRequest.getPageSize() > 20, ErrorCode.PARAMS_ERROR, "每页最多20条数据");
        //只查询当前用户应用
        User loginUser = userService.getLoginUser(request);
        appQueryRequest.setUserId(loginUser.getId());
        return ResultUtils.success(appService.getAppListPageVO(appQueryRequest));
    }

    /**
     * 获取精选应用列表（分页）
     * @param appQueryRequest
     * @return
     */
    @PostMapping("/good/list/page/vo")
    public BaseResponse<Page<AppVO>> getGoodAppListPageVO(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        //限制每页最多 20个
        ThrowUtils.throwIf(appQueryRequest.getPageSize() > 20, ErrorCode.PARAMS_ERROR, "每页最多20条数据");
        //只查精选的应用
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        return ResultUtils.success(appService.getAppListPageVO(appQueryRequest));
    }

    /**
     * 管理员删除应用
     * @param deleteRequest
     * @return
     */
    @AuthCheck(MustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> adminDeleteApp(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(appService.adminDeleteApp(deleteRequest));
    }

    /**
     * 管理员更新应用
     * @param appAdminUpdateRequest
     * @return
     */
    @PostMapping("/admin/update")
    @AuthCheck(MustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> adminUpdateApp(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
        ThrowUtils.throwIf(appAdminUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(appService.adminUpdateApp(appAdminUpdateRequest));
    }

    /**
     * 管理员获取应用列表（分页）
     * @param appQueryRequest
     * @return
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(MustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AppVO>> adminGetAppListPageVO(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(appService.getAppListPageVO(appQueryRequest));
    }

    /**
     * 管理员根据 id 获取应用信息
     *
     * @param id
     * @return
     */
    @GetMapping("/admin/get/vo")
    @AuthCheck(MustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AppVO> getAppVOByIdByAdmin(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        //查数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        //返回封装类
        return ResultUtils.success(appService.getAppVO(app));
    }



}
