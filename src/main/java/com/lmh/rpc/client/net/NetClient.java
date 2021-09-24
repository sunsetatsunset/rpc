package com.lmh.rpc.client.net;

import com.lmh.rpc.common.service.Service;

/**
 * 通信层发送网络请求
 */
public interface NetClient {
    byte[] sendRequest(byte[] data, Service service) throws InterruptedException;
}
