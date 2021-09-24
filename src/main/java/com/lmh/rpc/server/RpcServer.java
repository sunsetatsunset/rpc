package com.lmh.rpc.server;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RpcServer {
    /**
     * 服务端口
     */
    protected int port;

    /**
     * 服务协议
     */
    protected String protocol;

    /**
     * 请求处理者
     */
    protected RequestHandler handler;

    public RpcServer(int port, String protocol, RequestHandler handler) {
        super();
        this.port = port;
        this.protocol = protocol;
        this.handler = handler;
    }

    /**
     * 开启服务
     */
    public abstract void start();

    /**
     * 停止服务
     */
    public abstract void stop();
}
