package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.service.PermissionService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class PermissionController extends BaseController {
    @Resource
    private PermissionService permissionService;
}
