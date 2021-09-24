package com.lmh.rpc.server.register;

public interface ServiceRegister {
    void register(ServiceObject so) throws Exception;
    ServiceObject getServiceObject(String name) throws Exception;
}
