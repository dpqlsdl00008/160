package connector;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import connector.netty.nettyDecoder;
import connector.netty.nettyEncoder;
import connector.netty.nettyHandler;
import server.Timer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class connectorServer {
    private static connectorServer instance = new connectorServer();
    private ServerBootstrap bootstrap;

    private final Map<String, connectorClient> clients = new ConcurrentHashMap<>();

    public connectorClient getClient(String ip) {
        return clients.get(ip);
    }

    public Collection<connectorClient> getClients() {
        return clients.values();
    }

    public void registerClient(String ip, connectorClient client) {
        clients.put(ip, client);
    }

    public void unregisterClient(String ip) {
        clients.remove(ip);
    }

    public static connectorServer getInstance() {
        return instance;
    }

    public void run_startup_configurations() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("decodeer", new nettyDecoder());
                    ch.pipeline().addLast("encodeer", new nettyEncoder());
                    ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(30, 5, 0));
                    ch.pipeline().addLast("handler", new nettyHandler());
                    ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(0xFFFF));
                    ch.config().setOption(ChannelOption.SO_RCVBUF, 0xFFFF);
                }
            }).option(ChannelOption.SO_BACKLOG, 128).option(ChannelOption.SO_RCVBUF, 0xFFFF).option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(0xFFFF)).childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture cf = bootstrap.bind(11620).sync();
            Timer.EtcTimer.getInstance().register(new connectorThread(), 1000);
            System.out.println("[Success] Connector Server");
        } catch (InterruptedException ex) {
            System.err.println("[Fail] Connector Server\r\n" + ex);
        }
    }
}
