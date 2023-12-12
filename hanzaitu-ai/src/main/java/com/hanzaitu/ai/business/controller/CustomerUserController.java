package com.hanzaitu.ai.business.controller;

import com.hanzaitu.common.domain.SearchListDto;
import com.hanzaitu.ai.business.param.CustomerUserSaveParam;
import com.hanzaitu.ai.business.service.CustomerUserService;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hanzaitu.common.core.controller.BaseController;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author cr
 * @since 2023-05-27
 */
@Api(tags = "用户信息表")
@RestController
@RequestMapping("/customer")
public class CustomerUserController extends BaseController {

    @Autowired
    private CustomerUserService customerUserService;

    @ApiOperation(value = "用户列表", httpMethod = "POST")
    @PostMapping("pageList")
    public AjaxResult pageList() {
        return AjaxResult.success(customerUserService.getList());
    }


    @ApiOperation(value = "获取用户信息", httpMethod = "GET")
    @GetMapping("getUserInfo")
    public AjaxResult getUserInfo() {
        return AjaxResult.success(customerUserService.getUserInfo());
    }

    @ApiOperation(value = "编辑用户名", httpMethod = "POST")
    @PostMapping("editUserName")
    public AjaxResult editUserName(@RequestBody CustomerUserSaveParam param) {
        return AjaxResult.success(customerUserService.editUser(param));
    }

    @ApiOperation(value = "编辑手机号", httpMethod = "POST")
    @PostMapping("editPhoneNumber")
    public AjaxResult editPhoneNumber(@RequestBody CustomerUserSaveParam param) {
        return AjaxResult.success(customerUserService.editPhoneNumber(param));
    }

    @ApiOperation(value = "编辑头像", httpMethod = "POST")
    @PostMapping("editAvatar")
    public AjaxResult editAvatar(@RequestBody CustomerUserSaveParam param) {
        return AjaxResult.success(customerUserService.editAvatar(param));
    }

    @ApiOperation(value = "获取用户邀请明细信息", httpMethod = "POST")
    @PostMapping("listInviteUser")
    public AjaxResult listInviteUser(@RequestBody SearchListDto searchListDto) {
        return AjaxResult.success(customerUserService.listInviteUser(searchListDto));
    }

}
