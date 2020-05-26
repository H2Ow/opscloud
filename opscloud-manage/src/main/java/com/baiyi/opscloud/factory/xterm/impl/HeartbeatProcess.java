package com.baiyi.opscloud.factory.xterm.impl;

import com.baiyi.opscloud.common.base.XTermRequestStatus;
import com.baiyi.opscloud.domain.generator.opscloud.OcTerminalSession;
import com.baiyi.opscloud.factory.xterm.IXTermProcess;
import com.baiyi.opscloud.xterm.message.BaseMessage;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

/**
 * @Author baiyi
 * @Date 2020/5/13 6:50 下午
 * @Version 1.0 HEARTBEAT
 */
@Slf4j
@Component
public class HeartbeatProcess extends BaseProcess implements IXTermProcess {

    /**
     * XTerm心跳
     *
     * @return
     */

    @Override
    public String getKey() {
        return XTermRequestStatus.HEARTBEAT.getCode();
    }

    @Override
    public void xtermProcess(String message, Session session, OcTerminalSession ocTerminalSession) {
        //  log.info("收到前端心跳");
        String key = Joiner.on("#").join(ocTerminalSession.getSessionId(), "heartbeat");
        redisUtil.set(key, true, 60 * 1000L);
    }

    @Override
    protected BaseMessage getXTermMessage(String message) {
        return null;
    }

}
