package com.lmh.rpc.client.discovery;

import com.lmh.rpc.common.service.Service;

import java.util.List;

/**
 *  服务发现接口
 */
public interface ServiceDiscoverer {
    /**
     * 根据名称获取服务列表
     * @param name 服务名称
     * @return 服务列表
     */
    List<Service> getService(String name);
}
