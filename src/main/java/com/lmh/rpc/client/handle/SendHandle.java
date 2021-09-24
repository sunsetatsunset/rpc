package com.lmh.rpc.client.handle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 处理器类，定义Netty入站处理
 */
public class SendHandle extends ChannelInboundHandlerAdapter {

    public static Logger logger = LoggerFactory.getLogger(SendHandle.class);
    private CountDownLatch countDownLatch;
    private Object readMsg = null;
    private byte[] data;

    public SendHandle(byte[] data) {
        this.data = data;
        countDownLatch = new CountDownLatch(1);
    }

    /**
     * 连接建立事件。连接建立时该方法会执行
     * @param context
     */
    @Override
    public void channelActive(ChannelHandlerContext context) {
        logger.info("Successful connection to server：{}, context");
        ByteBuf reqBuf = Unpooled.buffer(data.length);
        reqBuf.writeBytes(data);
        logger.info("Client sends message：{}", reqBuf);
        //将数据写到channelPipeline中当前channelHandle的下一个channelHandle中
        context.writeAndFlush(reqBuf);
    }

    /**
     * 读取数据,释放CD锁
     * @param context
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {
        logger.info("Client reads message: {}", msg);
        ByteBuf msgBuf = (ByteBuf) msg;
        byte[] resp = new byte[msgBuf.readableBytes()];
        msgBuf.readBytes(resp);
        readMsg = resp;
        countDownLatch.countDown();
    }

    /**
     * 等待CD锁完成后返回数据
     * @return
     * @throws InterruptedException
     */
    public Object respData() throws InterruptedException {
        countDownLatch.await();
        return readMsg;
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
