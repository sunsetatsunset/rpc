package com.lmh.rpc.common.service;

import lombok.Getter;
import lombok.Setter;

/**
 * 发现的服务信息
 */
@Getter
@Setter
public class Service {
    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务协议
     */
    private String protocol;

    /**
     * 服务地址，格式：IP:Port
     */
    private String address;
}
