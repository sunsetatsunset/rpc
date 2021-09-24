package com.lmh.rpc.config;

import com.lmh.rpc.client.ClientProxyFactory;
import com.lmh.rpc.client.discovery.ZookeeperServiceDiscoverer;
import com.lmh.rpc.client.net.NettyNetClient;
import com.lmh.rpc.properties.RpcProperties;
import com.lmh.rpc.server.NettyRpcServer;
import com.lmh.rpc.server.RequestHandler;
import com.lmh.rpc.server.RpcServer;
import com.lmh.rpc.server.register.DefaultRpcProcessor;
import com.lmh.rpc.server.register.ServiceRegister;
import com.lmh.rpc.server.register.ZookeeperExportServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration {

    @Bean
    public RpcProperties lmhRpcProperties() {
        return new RpcProperties();
    }

    @Bean
    public DefaultRpcProcessor defaultRpcProcessor() {
        return new DefaultRpcProcessor();
    }

    @Bean
    public ClientProxyFactory clientProxyFactory(@Autowired RpcProperties rpcProperties) {
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory();
        //设置服务发现者
        clientProxyFactory.setSid(new ZookeeperServiceDiscoverer(rpcProperties.getRegisterAddress()));


        //设置网络实现层
        clientProxyFactory.setNetClient(new NettyNetClient());
        return clientProxyFactory;
    }
    @Bean
    public ServiceRegister serviceRegister(@Autowired RpcProperties rpcProperty) {
        return new ZookeeperExportServiceRegister(
                rpcProperty.getRegisterAddress(),
                rpcProperty.getServerPort(),
                rpcProperty.getProtocol());
    }
    @Bean
    public RequestHandler requestHandler(@Autowired ServiceRegister serviceRegister) {
        return new RequestHandler(serviceRegister);
    }

    @Bean
    public RpcServer rpcServer(@Autowired RequestHandler requestHandler,
                               @Autowired RpcProperties rpcProperties) {
        return new NettyRpcServer(rpcProperties.getServerPort(),
                rpcProperties.getProtocol(), requestHandler);
    }

}
