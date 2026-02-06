package connector.netty;

import connector.connectorClient;
import connector.connectorHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import connector.connectorServer;
import connector.connectorWalker;
import tools.data.LittleEndianAccessor;

public class nettyHandler extends SimpleChannelInboundHandler<LittleEndianAccessor> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final String IP = ctx.channel().remoteAddress().toString().split(":")[0];
        System.out.println("[Connector] Log In : " + IP);
        final connectorClient cli = new connectorClient(ctx.channel());
        cli.sendPacket(packetCreator.Ping());
        connectorServer.getInstance().registerClient(cli.getIP(), cli);
        ctx.channel().attr(connectorClient.CLIENTKEY).set(cli);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connectorClient cli = ctx.channel().attr(connectorClient.CLIENTKEY).get();
        final String IP = ctx.channel().remoteAddress().toString().split(":")[0];
        System.out.println("[Connector] Log Out : " + IP);
        cli.closeSession();
        connectorServer.getInstance().unregisterClient(cli.getIP());
        if(!cli.getId().equalsIgnoreCase("")) { //logged in
            connectorWalker.setAlive(cli.getId(), false);
        }
        ctx.channel().attr(connectorClient.CLIENTKEY).set(null);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            channelInactive(ctx);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LittleEndianAccessor slea) throws Exception {
        final connectorClient client = ctx.channel().attr(connectorClient.CLIENTKEY).get();
        slea.skip(2);
        final short header = slea.readShort();
        for (final recvOpcode recv : recvOpcode.values()) {
            if (recv.getValue() == header) {
                connectorHandler.HandlePacket(recv, slea, client);
                break;
            }
        }
        return;
    }
}
