package com.lmh.rpc.client;

import com.lmh.rpc.client.discovery.ServiceDiscoverer;
import com.lmh.rpc.client.net.NetClient;
import com.lmh.rpc.common.utils.ProtostuffUtils;
import com.lmh.rpc.common.service.Service;
import com.lmh.rpc.exception.RpcException;
import com.lmh.rpc.common.utils.RPCRequest;
import com.lmh.rpc.common.utils.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.reflect.Proxy.newProxyInstance;

public class ClientProxyFactory {


    private ServiceDiscoverer serviceDiscoverer;

    private Map<Class<?>, Object> objectCache = new HashMap<>();

    private NetClient netClient;

    public void setSid(ServiceDiscoverer serviceDiscoverer) {
        this.serviceDiscoverer = serviceDiscoverer;
    }

    public void setNetClient(NetClient netClient) {
        this.netClient = netClient;
    }
    public ServiceDiscoverer getServiceDiscoverer() {
        return serviceDiscoverer;
    }
    public NetClient getNetClient() {
        return netClient;
    }

    /**
     * 通过Java动态代理生成代理对象
     * @param clazz 被代理对象
     * @param <T>
     * @return 代理对象
     */
    public <T> T getProxy(Class<T> clazz) {
        //为接口生成动态代理对象
        return (T) this.objectCache.computeIfAbsent(clazz,
                cls -> newProxyInstance(cls.getClassLoader(), new Class<?>[] {cls}, new ClientInvocationHandler(cls)) );
    }

    private class ClientInvocationHandler implements InvocationHandler {

        private Random random = new Random();

        private Class<?> clazz;

        private Logger logger = LoggerFactory.getLogger(ClientInvocationHandler.class);

        ClientInvocationHandler(Class clazz) {
            super();
            this.clazz = clazz;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (method.getName().equals("toString")) {
                return proxy.getClass().toString();
            }

            if (method.getName().equals("hashCode")) {
                return 0;
            }
            //通过服务名称获取服务列表
            String serviceName = this.clazz.getName();
            List<Service> services = serviceDiscoverer.getService(serviceName);
            if(services == null || services.isEmpty()) {
                throw new RpcException("No provider found");
            }
            //从服务列表中随机获取一个服务
            Service service = services.get(random.nextInt(services.size()));
            //构造请求对象
            RPCRequest request = new RPCRequest();
            request.setServiceName(service.getName());
            request.setMethod(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);


            //编组请求
            byte[] data = ProtostuffUtils.serialize(request);

            //发送请求
            byte[] respData = netClient.sendRequest(data,service);

            //解组收到的响应
            RPCResponse rsp = ProtostuffUtils.deserialize(respData, RPCResponse.class);
            // 6、结果处理
            if (rsp.getException() != null) {
                throw rsp.getException();
            }
            return rsp.getReturnValue();
        }
    }
}

