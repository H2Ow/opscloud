package com.baiyi.opscloud.factory.xterm.impl;

import com.baiyi.opscloud.common.base.BusinessType;
import com.baiyi.opscloud.common.base.XTermRequestStatus;
import com.baiyi.opscloud.domain.generator.opscloud.OcServer;
import com.baiyi.opscloud.domain.generator.opscloud.OcUser;
import com.baiyi.opscloud.domain.generator.opscloud.OcUserPermission;
import com.baiyi.opscloud.factory.xterm.IXTermProcess;
import com.baiyi.opscloud.xterm.handler.RemoteInvokeHandler;
import com.baiyi.opscloud.xterm.message.BaseMessage;
import com.baiyi.opscloud.xterm.message.InitialIpMessage;
import com.baiyi.opscloud.xterm.model.HostSystem;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

/**
 * @Author baiyi
 * @Date 2020/5/15 2:59 下午
 * @Version 1.0
 */
@Component
public class XTermInitialIpProcess extends BaseXTermProcess implements IXTermProcess {


    /**
     * 初始化XTerm
     *
     * @return
     */
    @Override
    public String getKey() {
        return XTermRequestStatus.INITIAL_IP.getCode();
    }

    @Override
    public void xtermProcess(String message, Session session) {
        InitialIpMessage xtermMessage = (InitialIpMessage) getXTermMessage(message);
        xtermMessage.setLoginUserType(1);
        OcUser ocUser =  userFacade.getOcUserBySession();
        String ip = xtermMessage.getIp();

        boolean isAdmin = isOps(ocUser);
        // 鉴权
        if(!isAdmin){
            OcServer ocServer = ocServerService.queryOcServerByIp(ip);
            OcUserPermission ocUserPermission = new OcUserPermission();
            ocUserPermission.setUserId(ocUser.getId());
            ocUserPermission.setBusinessId(ocServer.getServerGroupId());
            ocUserPermission.setBusinessType(BusinessType.SERVERGROUP.getType());
            OcUserPermission checkUserPermission = ocUserPermissionService.queryOcUserPermissionByUniqueKey(ocUserPermission);
            if(checkUserPermission == null)
                return;
        }

        HostSystem hostSystem = buildHostSystem(ocUser, ip, xtermMessage, isAdmin);
        RemoteInvokeHandler.openSSHTermOnSystem(session.getId(), xtermMessage.getInstanceId(), hostSystem);
    }


    @Override
    protected BaseMessage getXTermMessage(String message) {
        InitialIpMessage xtermMessage = new GsonBuilder().create().fromJson(message, InitialIpMessage.class);
        return xtermMessage;
    }

}
