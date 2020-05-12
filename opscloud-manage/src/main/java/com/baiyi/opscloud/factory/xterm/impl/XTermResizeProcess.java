package com.baiyi.opscloud.factory.xterm.impl;

import com.baiyi.opscloud.common.base.XTermRequestStatus;
import com.baiyi.opscloud.factory.xterm.IXTermProcess;
import com.baiyi.opscloud.xterm.handler.RemoteInvokeHandler;
import com.baiyi.opscloud.xterm.message.BaseXTermWSMessage;
import com.baiyi.opscloud.xterm.message.XTermResizeWSMessage;
import com.baiyi.opscloud.xterm.model.JSchSession;
import com.baiyi.opscloud.xterm.model.JSchSessionMap;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.ChannelShell;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

/**
 * @Author baiyi
 * @Date 2020/5/12 10:43 上午
 * @Version 1.0
 */
@Component
public class XTermResizeProcess extends BaseXTermProcess implements IXTermProcess {

    /**
     * XTerm改变形体
     * @return
     */

    @Override
    public String getKey() {
        return XTermRequestStatus.RESIZE.getCode();
    }

    @Override
    public void xtermProcess(String message, Session session) {
        XTermResizeWSMessage resizeMessage = ( XTermResizeWSMessage) getXTermMessage(message);
        try {
            JSchSession jSchSession = JSchSessionMap.getBySessionId(session.getId(), resizeMessage.getInstanceId());
            RemoteInvokeHandler.invokeChannelPtySize((ChannelShell)jSchSession.getChannel(),resizeMessage);
        } catch (Exception e) {
        }
    }

    @Override
    protected BaseXTermWSMessage getXTermMessage(String message) {
        XTermResizeWSMessage baseMessage = new GsonBuilder().create().fromJson(message, XTermResizeWSMessage.class);
        return  baseMessage;
    }
}

