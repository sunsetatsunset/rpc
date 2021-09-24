package com.lmh.rpc.client.discovery;

import com.alibaba.fastjson.JSON;
import com.lmh.rpc.common.constants.ZkConstant;
import com.lmh.rpc.common.serializer.ZookeeperSerializer;
import com.lmh.rpc.common.service.Service;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZookeeperServiceDiscoverer implements ServiceDiscoverer {

    private ZkClient zkClient;

    public ZookeeperServiceDiscoverer(String zkAddress) {
        zkClient = new ZkClient(zkAddress);
        zkClient.setZkSerializer(new ZookeeperSerializer());
    }

    /**
     * 用Zookeeper客户端根据服务器名获取服务列表
     * @param name 服务名称
     * @return 服务名
     */
    @Override
    public List<Service> getService(String name) {
        String servicePath = ZkConstant.ZK_SERVICE_PATH + ZkConstant.PATH_DELIMITER + name + "/service";
        List<String> children = zkClient.getChildren(servicePath);
        return Optional.ofNullable(children).orElse(new ArrayList<>()).stream().map(s -> {
            String dech = null;
            try {
                dech = URLDecoder.decode(s, ZkConstant.UTF_8);
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return JSON.parseObject(dech, Service.class);
        }).collect(Collectors.toList());
    }
}
