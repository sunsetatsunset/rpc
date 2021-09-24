package com.lmh.rpc.common.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RPCRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String serviceName;

    private String method;

    private Map<String, String> headers = new HashMap<>();

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
