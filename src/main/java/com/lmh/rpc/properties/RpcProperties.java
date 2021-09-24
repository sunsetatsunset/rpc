package com.lmh.rpc.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RpcProperties.class)
@ConfigurationProperties("lmh.rpc")
@Getter
@Setter
public class RpcProperties {
    /**
     * 服务注册中心
     */
    private String registerAddress = "127.0.0.1:2181";

    /**
     * 服务端暴露端口
     */
    private Integer serverPort = 2181;

    /**
     * 服务协议
     */
    private String protocol = "lmh";
}
