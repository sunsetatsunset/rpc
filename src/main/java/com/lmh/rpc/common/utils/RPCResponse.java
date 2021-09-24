package com.lmh.rpc.common.utils;

import com.lmh.rpc.common.enums.ResultCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RPCResponse implements Serializable {
//    private static final long serialVersionUID = -4317845782629589997L;

    private ResultCode status;

    private Map<String, String> headers = new HashMap<>();

    private Object returnValue;

    private Exception exception;

    public RPCResponse(ResultCode code) {
        this.status = code;
    }
}
