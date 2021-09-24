package com.lmh.rpc.server.register;

import com.alibaba.fastjson.JSON;
import com.lmh.rpc.common.constants.ZkConstant;
import com.lmh.rpc.common.serializer.ZookeeperSerializer;
import com.lmh.rpc.common.service.Service;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;

public class ZookeeperExportServiceRegister extends DefaultServiceRegister{

    private Logger logger = LoggerFactory.getLogger(ZookeeperExportServiceRegister.class);

    private ZkClient zkClient;

    public ZookeeperExportServiceRegister(String zkAddress, Integer port, String protocol) {
        zkClient = new ZkClient(zkAddress);
        zkClient.setZkSerializer(new ZookeeperSerializer());
        this.port = port;
        this.protocol = protocol;
    }

    /**
     * 服务注册
     * @param so 服务对象
     * @throws Exception
     */
    public void register(ServiceObject so) throws Exception {
        super.register(so);
        Service service = new Service();
        String host = InetAddress.getLocalHost().getHostAddress();
        String address = host + ":" + port;
        service.setAddress(address);
        service.setName(so.getClazz().getName());
        service.setProtocol(protocol);
        this.exportService(service);
    }

    public void exportService(Service service) {
        String serviceName = service.getName();
        String uri = JSON.toJSONString(service);
        try {
            //使用URLEncoder对地址参数编码
            uri = URLEncoder.encode(uri, ZkConstant.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String servicePath = ZkConstant.ZK_SERVICE_PATH + ZkConstant.PATH_DELIMITER + serviceName + "/service";
        if(!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath,true);
        }
        String uriPath = servicePath + ZkConstant.PATH_DELIMITER + uri;
        if(zkClient.exists(uriPath)) {
            zkClient.delete(uriPath);
        }
        zkClient.createEphemeral(uriPath);
    }
}
