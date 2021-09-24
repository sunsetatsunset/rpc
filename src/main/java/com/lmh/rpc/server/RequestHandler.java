package com.lmh.rpc.server;

import com.lmh.rpc.common.enums.ResultCode;
import com.lmh.rpc.common.utils.ProtostuffUtils;
import com.lmh.rpc.common.utils.RPCRequest;
import com.lmh.rpc.common.utils.RPCResponse;
import com.lmh.rpc.server.register.ServiceObject;
import com.lmh.rpc.server.register.ServiceRegister;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    private ServiceRegister serviceRegister;

    public RequestHandler( ServiceRegister serviceRegister) {
        super();
        this.serviceRegister = serviceRegister;
    }
    public byte[] handleRequest(byte[] data) throws Exception{
        //解组请求
        RPCRequest request = ProtostuffUtils.deserialize(data, RPCRequest.class);

        //查找服务对象
        ServiceObject so = this.serviceRegister.getServiceObject(request.getServiceName());
        //响应
        RPCResponse rsp = null;
        if(so == null) {
            rsp = new RPCResponse(ResultCode.NOT_FOUND);
        }
        //通过放射调用请求的方法
        else {
            try {
                Method method = so.getClazz().getMethod(request.getMethod(),request.getParameterTypes());
                Object returnValue = method.invoke(so.getObj(), request.getParameters());
                rsp = new RPCResponse(ResultCode.SUCCESS);
                rsp.setReturnValue(returnValue);
            }catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e)
            {
                rsp = new RPCResponse(ResultCode.ERROR);
                rsp.setException(e);
            }
        }
        // 4、编组响应消息
        return ProtostuffUtils.serialize(rsp);
    }
}
