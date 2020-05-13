package com.baiyi.opscloud.xterm.model;

import com.jcraft.jsch.Channel;
import lombok.Data;

import java.io.OutputStream;
import java.io.PrintStream;

@Data
public class JSchSession {

    private String sessionId;
    private String instanceId;
    private PrintStream commander;
    private OutputStream inputToChannel;
    private Channel channel;
    private HostSystem hostSystem;

}
