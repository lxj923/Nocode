package com.lxj.nocode.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxj.nocode.common.DeleteRequest;
import com.lxj.nocode.model.dto.app.AppAddRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lxj.nocode.model.dto.app.AppAdminUpdateRequest;
import com.lxj.nocode.model.dto.app.AppQueryRequest;
import com.lxj.nocode.model.dto.app.AppUpdateRequest;
import com.lxj.nocode.model.entity.App;
import com.lxj.nocode.model.vo.AppVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author lxj
 * @description 针对表【app(应用)】的数据库操作Service
 * @createDate 2025-10-24 10:51:50
 */
public interface AppService extends IService<App> {

    Long addApp(AppAddRequest appAddRequest, HttpServletRequest request);

    Boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request);

    Boolean deleteApp(DeleteRequest deleteRequest, HttpServletRequest request);

    AppVO getAppVO(App app);

    QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVOList(List<App> appList);

    Page<AppVO> getAppListPageVO(AppQueryRequest appQueryRequest);

    Boolean adminDeleteApp(DeleteRequest deleteRequest);

    Boolean adminUpdateApp(AppAdminUpdateRequest appAdminUpdateRequest);
}
