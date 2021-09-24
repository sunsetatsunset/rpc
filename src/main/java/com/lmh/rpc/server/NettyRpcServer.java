package com.lmh.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyRpcServer extends RpcServer{

    private Logger logger = LoggerFactory.getLogger(NettyRpcServer.class);

    public NettyRpcServer(int port, String protocol, RequestHandler handler) {
        super(port, protocol, handler);
    }

    private Channel channel;

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline channelPipeline = ch.pipeline();
                    channelPipeline.addLast(new ChannelRequestHandler());
                }
            });
            //启动服务
            ChannelFuture future = serverBootstrap.bind(port).sync();
            logger.info("Server started successfully.");
            channel = future.channel();
            future.channel().closeFuture().sync();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    @Override
    public void stop() {
        this.channel.close();
    }

    private class ChannelRequestHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext context) {
            logger.info("Channel active：{}",context);
        }

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
            logger.info("The server receives a message: {}", msg);
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] req = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(req);
            byte[] res = handler.handleRequest(req);
            logger.info("Send response：{}", msg);
            ByteBuf respBuf = Unpooled.buffer(res.length);
            respBuf.writeBytes(res);
            context.write(respBuf);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            cause.printStackTrace();
            logger.error("Exception occurred：{}", cause.getMessage());
            ctx.close();
        }
    }
}
