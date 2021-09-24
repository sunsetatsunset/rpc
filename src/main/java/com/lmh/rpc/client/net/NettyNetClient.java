package com.lmh.rpc.client.net;

import com.lmh.rpc.client.handle.SendHandle;
import com.lmh.rpc.common.service.Service;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用Netty使用请求的具体细节
 */
public class NettyNetClient implements NetClient{
    private static Logger logger = LoggerFactory.getLogger(NettyNetClient.class);
    /**
     * 发送请求
     * @param data 请求数据
     * @param service 请求服务
     * @return 响应数据
     */
    @Override
    public byte[] sendRequest(byte[] data, Service service) throws InterruptedException {
        String[] addInfoArray = service.getAddress().split(":");
        String serviceIP = addInfoArray[0];
        String servicePort = addInfoArray[1];

        SendHandle sendHandle = new SendHandle(data);
        byte[] respData;

        //配置客户端
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline channelPipeline = ch.pipeline();
                    channelPipeline.addLast(sendHandle);
                }
            });
            //启动客户端连接
            bootstrap.connect(serviceIP,Integer.parseInt(servicePort)).sync();
            respData = (byte[]) sendHandle.respData();
            logger.info("SendRequest get reply: {}", respData);
        }finally {
            group.shutdownGracefully();
        }
        return respData;
    }
}
