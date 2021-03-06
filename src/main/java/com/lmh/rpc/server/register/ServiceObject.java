package com.lmh.rpc.server.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceObject {
    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务Class
     */
    private Class<?> clazz;

    /**
     * 具体服务
     */
    private Object obj;

    public ServiceObject(String name, Class<?> clazz, Object obj) {
        super();
        this.name = name;
        this.clazz = clazz;
        this.obj = obj;
    }

}
