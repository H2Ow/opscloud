package com.baiyi.opscloud.serverChange;

import com.baiyi.opscloud.BaseUnit;
import com.baiyi.opscloud.common.base.ServerChangeType;
import com.baiyi.opscloud.common.util.UUIDUtils;
import com.baiyi.opscloud.domain.generator.opscloud.OcServer;
import com.baiyi.opscloud.domain.generator.opscloud.OcServerChangeTask;
import com.baiyi.opscloud.domain.param.server.ServerChangeParam;
import com.baiyi.opscloud.facade.ServerChangeFacade;
import com.baiyi.opscloud.factory.change.handler.ServerChangeHandler;
import com.baiyi.opscloud.server.ServerCenter;
import com.baiyi.opscloud.service.server.OcServerService;
import com.baiyi.opscloud.service.serverChange.OcServerChangeTaskService;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @Author baiyi
 * @Date 2020/5/29 1:29 下午
 * @Version 1.0
 */
public class ServerChangeTest extends BaseUnit {

    @Resource
    private OcServerService ocServerService;

    @Resource
    private ServerChangeFacade serverChangeFacade;

    @Resource
    private OcServerChangeTaskService ocServerChangeTaskService;

    @Resource
    private ServerChangeHandler serverChangeHandler;

    @Resource
    private ServerCenter serverCenter;

    @Test
    void testExecuteServerChangeOffline() {
        OcServer ocServer = ocServerService.queryOcServerByIp("192.168.1.108");
        // 192.168.1.108

        ServerChangeParam.ExecuteServerChangeParam param = new ServerChangeParam.ExecuteServerChangeParam();
        param.setChangeType(ServerChangeType.OFFLINE.getType());
        param.setServerGroupId(ocServer.getServerGroupId());
        param.setServerId(ocServer.getId());

        serverChangeFacade.executeServerChangeOffline(param);
    }

    @Test
    void testExecuteServerChangeOnline() {
        OcServer ocServer = ocServerService.queryOcServerById(4298);
        ServerChangeParam.ExecuteServerChangeParam param = new ServerChangeParam.ExecuteServerChangeParam();
        param.setChangeType(ServerChangeType.ONLINE.getType());
        param.setServerGroupId(ocServer.getServerGroupId());
        param.setServerId(ocServer.getId());
        param.setTaskId(UUIDUtils.getUUID());
        serverChangeFacade.executeServerChangeOnline(param);
    }


    @Test
    void testServerChangeHandler() {
        String taskId = "4040555cc0334b28a0b852b6d85a2781";
        OcServerChangeTask ocServerChangeTask = ocServerChangeTaskService.queryOcServerChangeTaskByTaskId(taskId);
        serverChangeHandler.executeChangeTask(ocServerChangeTask );
    }

    //         Boolean result = serverCenter.disable(ocServer);
    @Test
    void testServerDisable() {
        OcServer ocServer = ocServerService.queryOcServerByIp("192.168.1.108");
        Boolean result = serverCenter.disable(ocServer);
        System.err.println(result);
    }

}
