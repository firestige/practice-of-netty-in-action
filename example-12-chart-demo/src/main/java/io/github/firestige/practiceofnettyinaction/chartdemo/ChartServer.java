package io.github.firestige.practiceofnettyinaction.chartdemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ChartServer {

    private static final Logger LOG = LoggerFactory.getLogger(ChartServer.class);

    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new CharServerInitializer(channelGroup));
        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    public void destory() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            LOG.error("Usage: {} <port>", ChartServer.class.getSimpleName());
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        final ChartServer server = new ChartServer();
        final ChannelFuture future = server.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread(server::destory));
        future.channel().closeFuture().syncUninterruptibly();
    }
}
