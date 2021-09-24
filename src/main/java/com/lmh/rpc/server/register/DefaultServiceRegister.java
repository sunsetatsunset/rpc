package com.lmh.rpc.server.register;

import java.util.HashMap;
import java.util.Map;

public class DefaultServiceRegister implements ServiceRegister{

    private Map<String, ServiceObject> serviceMap = new HashMap<>();

    protected String protocol;

    protected Integer port;

    @Override
    public void register(ServiceObject so) throws Exception {
        if (so == null) {
            throw new IllegalArgumentException("Parameter cannot be empty.");
        }

        this.serviceMap.put(so.getName(), so);
    }

    @Override
    public ServiceObject getServiceObject(String name) throws Exception {
        return this.serviceMap.get(name);
    }
}
