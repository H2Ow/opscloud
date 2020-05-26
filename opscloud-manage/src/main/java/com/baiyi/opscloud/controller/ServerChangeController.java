package com.baiyi.opscloud.controller;

import com.baiyi.opscloud.domain.HttpResult;
import com.baiyi.opscloud.domain.param.server.ServerChangeParam;
import com.baiyi.opscloud.facade.ServerChangeFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author baiyi
 * @Date 2020/5/26 4:07 下午
 * @Version 1.0
 */
@RestController
@RequestMapping("/server/change")
@Api(tags = "服务器变更管理")
public class ServerChangeController {

    @Resource
    private ServerChangeFacade serverChangeFacade;

    @ApiOperation(value = "服务器下线")
    @PostMapping(value = "/offline", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<Boolean> executeServerChangeOffline(@RequestBody @Valid ServerChangeParam.ExecuteServerChangeParam executeServerChangeParam) {
        return new HttpResult<>(serverChangeFacade.executeServerChangeOffline(executeServerChangeParam));
    }






}
